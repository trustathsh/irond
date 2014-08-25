package de.fhhannover.inform.iron.mapserver.communication.http;

/*
 * #%L
 * ====================================================
 *   _____                _     ____  _____ _   _ _   _
 *  |_   _|_ __ _   _ ___| |_  / __ \|  ___| | | | | | |
 *    | | | '__| | | / __| __|/ / _` | |_  | |_| | |_| |
 *    | | | |  | |_| \__ \ |_| | (_| |  _| |  _  |  _  |
 *    |_| |_|   \__,_|___/\__|\ \__,_|_|   |_| |_|_| |_|
 *                             \____/
 * 
 * =====================================================
 * 
 * Fachhochschule Hannover 
 * (University of Applied Sciences and Arts, Hannover)
 * Faculty IV, Dept. of Computer Science
 * Ricklinger Stadtweg 118, 30459 Hannover, Germany
 * 
 * Email: trust@f4-i.fh-hannover.de
 * Website: http://trust.inform.fh-hannover.de/
 * 
 * This file is part of irond, version 0.4.2, implemented by the Trust@FHH
 * research group at the Fachhochschule Hannover.
 * 
 * irond is an an *experimental* IF-MAP 2.0 compliant MAP server written in
 * JAVA. irond supports both basic authentication and certificate-based 
 * authentication (using X.509 certificates) of MAP clients. irond is
 * maintained by the Trust@FHH group at the Fachhochschule Hannover, initial
 * developement was carried out during the ESUKOM research project.
 * %%
 * Copyright (C) 2010 - 2014 Trust@FHH
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;

import org.apache.log4j.Logger;

import de.fhhannover.inform.iron.mapserver.communication.ChannelIdentifier;
import de.fhhannover.inform.iron.mapserver.communication.bus.Queue;
import de.fhhannover.inform.iron.mapserver.communication.bus.messages.Event;
import de.fhhannover.inform.iron.mapserver.communication.ifmap.EventProcessor;
import de.fhhannover.inform.iron.mapserver.exceptions.ServerInitialException;
import de.fhhannover.inform.iron.mapserver.provider.BasicAuthProvider;
import de.fhhannover.inform.iron.mapserver.provider.LoggingProvider;
import de.fhhannover.inform.iron.mapserver.provider.ServerConfigurationProvider;
import de.fhhannover.inform.iron.mapserver.utils.NullCheck;
/**
 * Opens sockets for the basic-authentication and certificate-based authentication.
 * 
 * After a connect from a client, the socket is delegated to a {@link ChannelThread}. 
 *
 * @author tr, aw
 *
 */
public class ChannelAcceptor {
	
	private static final String sName = "ChannelAcceptor";
	
	/**
	 * The logger
	 */
	private static Logger sLogger;
	static {
		sLogger = LoggingProvider.getTheLogger();
	}

	/**
	 * Holds the configuration as specified in <code>ifmap-properties</code>
	 */
	private final ServerConfigurationProvider mConfReader;
	
	/**
	 * The SSL socket for basic authentication
	 */
	private SSLServerSocket mBasicSocket;

	/**
	 * The SSL socket for certificate-based authentication
	 */
	private SSLServerSocket mCertificateSocket;

	/**
	 * Counter for thread-channels
	 */
	private int mCounter = 0;

	/**
	 * Channel threads repository
	 */
	private final ChannelRep mChannelRep;
	
	/**
	 * new {@link ChannelThread} instances need to know about the event queue,
	 * this is the reference to it.
	 */
	private final Queue<Event> mEventQueue;
	
	/**
	 * factory to create{@link CertificateChannelAuth} instances
	 */
	private final CertificateChannelAuthFactory mCertificateAuthFactory;
	
	/**
	 * factory to create{@link BasicChannelAuth} instances
	 */
	private final BasicChannelAuthFactory mBasicAuthFactory;
	
	/**
	 * represents the factory to be used to create the sockets to listen on.
	 */
	private ServerSocketFactory mSecureSocketFactory;
	
	/**
	 * indicates whether the {@link ChannelAcceptor} was initialized.
	 */
	private boolean mInitialized;
	
	/**
	 * represents the {@link Executor} instance used for the {@link AcceptorTask}s
	 */
	private Executor mAcceptorExecutor;
	
	/**
	 * The executor used to run the {@link ChannelThread} instances.
	 * The implementation is as a private class at the end of this file.
	 */
	private Executor mChannelThreadExecutor;

	/**
	 * Constructor expecting all the used parameters.
	 * 
	 * toDo:
	 * The {@link EventProcessor} uses setters...
	 * Maybe we should do that at some point too?
	 * 
	 * @param serverConf
	 * @param eventQueue
	 * @param channelRep
	 * @param basicAuthProvider
	 */
	public ChannelAcceptor(ServerConfigurationProvider serverConf, Queue<Event> eventQueue,
			ChannelRep channelRep, BasicAuthProvider basicAuthProvider) {
		
		NullCheck.check(serverConf, "serverConf is null");
		NullCheck.check(eventQueue, "eventQueue is null");
		NullCheck.check(channelRep, "channelRep is null");
		NullCheck.check(basicAuthProvider, "basicAuthProvider is null");
		
		mConfReader = serverConf;
		mEventQueue = eventQueue;
		mChannelRep = channelRep;
		
		mBasicAuthFactory = new BasicChannelAuthFactory(basicAuthProvider);
		mCertificateAuthFactory = new CertificateChannelAuthFactory();
		mInitialized = false;
	}
	
	/**
	 * Method to be called to set up everything
	 * Keystore and Truststore are set as system properties, sockets to listen
	 * on are created and the {@link Executor}s are initialized.
	 * Needs to be called before {@link #start()} is called!
	 * 
	 * @throws ServerInitialException
	 */
	public void setUp() throws ServerInitialException {
		setUpSsl();
		setUpPorts();
		setUpExecutors();
		
		mInitialized = true;
	}
	
	/**
	 * Simply set the given key and truststore as default Java properties.
	 * Create a default {@link SSLServerSocketFactory}. This factory is later
	 * used to create the ports.
	 */
	private void setUpSsl() {
		sLogger.debug(sName + ": Initializing SSL");
		sLogger.debug(sName + ": Using keystore path= "
				+ mConfReader.getKeyStoreFileName());
		sLogger.debug(sName + ": Using trustStore path= "
				+ mConfReader.getTrustStoreFileName());
		
		System.setProperty("javax.net.ssl.keyStore",
				mConfReader.getKeyStoreFileName());
		System.setProperty("javax.net.ssl.keyStorePassword",
				mConfReader.getKeyStorePasswort());
		System.setProperty("javax.net.ssl.trustStore",
				mConfReader.getTrustStoreFileName());					
		System.setProperty("javax.net.ssl.trustStorePassword",
				mConfReader.getTrustStorePasswort());
		
		mSecureSocketFactory = SSLServerSocketFactory.getDefault();
	}
	
	/**
	 * Creates ports to listen on for basic and certificate-based authentication
	 * methods.
	 * 
	 * @throws ServerInitialException
	 */
	private void setUpPorts() throws ServerInitialException {
		// get the port numbers to listen on
		int basicAuthPort = mConfReader.getBasicAuthPort();
		int credentialAuthPort = mConfReader.getCertAuthPort();

		try {
			if (basicAuthPort != 0) {			
				mBasicSocket = (SSLServerSocket) mSecureSocketFactory
				.createServerSocket(basicAuthPort);
			}
		
			if (credentialAuthPort != 0) {
				mCertificateSocket = (SSLServerSocket)mSecureSocketFactory
				.createServerSocket(credentialAuthPort);
				
				// force certificate-based authentication
				mCertificateSocket.setWantClientAuth(true);
				mCertificateSocket.setNeedClientAuth(true);
			}
		
		} catch (IOException e) {
			if (mBasicSocket != null) {
				try {
					mBasicSocket.close();
				} catch (IOException e1) {
					// sorry, can't do much about it then
				}
			}
			
			if (mCertificateSocket != null) {
				try {
					mCertificateSocket.close();
				} catch (IOException e1) {
					// sorry, can't do much about it then
				}
			}
			
			throw new ServerInitialException(sName + ": " + e.getMessage());
		}
	}

	/**
	 * Depending whether basic, certificate-based or both authentication methods
	 * are enabled, create a fixed size executor with the corresponding number
	 * of threads.
	 */
	private void setUpExecutors() {
		int acceptors = (mBasicSocket == null) ? 0 : 1;
		acceptors = (mCertificateSocket == null) ? acceptors : acceptors + 1;
		mAcceptorExecutor = Executors.newFixedThreadPool(acceptors);
		mChannelThreadExecutor = new ChannelThreadExecutor();
	}

	/**
	 * Start accepting connections.
	 * 
	 * Create {@link AcceptorTask} instances for basic and certificate-based
	 * authentication and execute them using the {@link #mAcceptorExecutor}
	 * instance.
	 */
	public void start() {
		if (!mInitialized)
			throw new RuntimeException(sName + ": Missing initialization!");
		
		if (mBasicSocket != null) {
			sLogger.info(sName + ": Listening on port " + mBasicSocket.getLocalPort()
					+ " for incoming basic authentication connections");
			mAcceptorExecutor.execute(new AcceptorTask(mBasicSocket,
					mBasicAuthFactory));
		}
			
		if (mCertificateSocket != null) {
			sLogger.info(sName + ": Listening on port " + mCertificateSocket.getLocalPort() 
					+ " for incoming certificate-based authentication connections");
			mAcceptorExecutor.execute(new AcceptorTask(mCertificateSocket,
					mCertificateAuthFactory));
		}
	}
	
	
	/**
	 * Helper method to create a good {@link ChannelIdentifier} object for the
	 * given socket.
	 * 
	 * @param s
	 * @return
	 */
	private ChannelIdentifier getChannelIdentifier(Socket s){
		return new ChannelIdentifier(
				s.getInetAddress().getHostAddress(),
				s.getPort(), 
				mCounter++);
	}
	
	/**********************************************************************
	 * Inner Classes start here					   *
	 **********************************************************************/
	
	/**
	 * Task which blocking waits on the given socket and creates new
	 * {@link ChannelThread} instances with attaches {@link ChannelAuth}
	 * instances depending on the given {@link ChannelAuthFactory}.
	 */
	private class AcceptorTask implements Runnable {
		
		/**
		 * The socket to accept new connections from.
		 */
		private final SSLServerSocket mSocket;
		
		/**
		 * The {@link ChannelAuthFactory} to be used to create the right
		 * {@link ChannelAuth} instances for the {@link ChannelThread}.
		 */
		private final ChannelAuthFactory mChannelAuthFactory;
		
		private AcceptorTask(SSLServerSocket socket, ChannelAuthFactory authFactory) {
			NullCheck.check(socket, "socket is null");
			NullCheck.check(authFactory, "authFactory is null");
			mSocket = socket;
			mChannelAuthFactory = authFactory;
		}
	
		@Override
		public void run() {
			
			/*
			 * AcceptorTask infinite loop.
			 * 
			 * Try to accept() a client, create identifier and auth objects,
			 * create a ChannelThread instance.
			 * 
			 * Give this channelThread instance to the other executor to
			 * run.
			 */
			while (true) {
				StringBuffer sb = new StringBuffer();
				SSLSocket clientSocket = null;
				ChannelIdentifier channelIdentifier = null;
				ChannelAuth channelAuth = null;
				ChannelThread channelThread = null;
				try {
					clientSocket = (SSLSocket) mSocket.accept();
					clientSocket.setKeepAlive(mConfReader.getSocketKeepAlive());
					clientSocket.setSoTimeout(mConfReader.getSocketTimeout()); 
					if (sLogger.isDebugEnabled()) {
						sb.append(sName);
						sb.append(": New connection from ");
						sb.append(clientSocket.getInetAddress().getHostAddress());
						sb.append(":");
						sb.append(clientSocket.getPort());
						sb.append(" on port ");
						sb.append(mSocket.getLocalPort());
						sb.append(" (SO_KEEPALIVE = ");
						sb.append(clientSocket.getKeepAlive());
						sb.append(", SO_TIMEOUT = ");
						sb.append(clientSocket.getSoTimeout());
						sb.append(" ms)");
						sLogger.debug(sb.toString());
					}
					
					channelIdentifier = getChannelIdentifier(clientSocket);
					channelAuth = mChannelAuthFactory.createChannelAuth(clientSocket);
					
					channelThread = new ChannelThread(
							clientSocket,
							channelAuth,
							channelIdentifier,
							mEventQueue);

					mChannelThreadExecutor.execute(channelThread);
					
				} catch (IOException e) {
					sb.append(sName);
					sb.append(": Problem accepting client: ");
					sb.append(e.getMessage());
					sLogger.warn(sb.toString());
				}
			}
		}
	}
	
	/**
	 * Represents the {@link Executor} used for the {@link ChannelThread} runnables.
	 * 
	 * <b>NOTE:</b> beforeExecute() afterExecute() are used to add and remove the
	 * ChannelThread instances from the {@link ChannelRep}. You might not like it.
	 * <br/>
	 * <br/>
	 * The superclass {@link ThreadPoolExecutor} is initialized with the same
	 * values the method {@link Executors#newCachedThreadPool()} uses. The only
	 * difference is the non default implementation of beforeExecute() and
	 * afterExecute().
	 * 
	 * @author aw
	 *
	 */
	private class ChannelThreadExecutor extends ThreadPoolExecutor {

		/**
		 * The values are taken from the {@link Executors#newCachedThreadPool()}
		 * method. They may be customized if needed. Even using a config file
		 * if it is really needed.
		 */
		private ChannelThreadExecutor() {
			super(
					0,						// initial number of threads
					Integer.MAX_VALUE,		// maximum number of threads
					60L,					// idle time before a thread gets removed
					TimeUnit.SECONDS,		// unit of the parameter before	
					new SynchronousQueue<Runnable>());	// the queue to be used
		}
			
		@Override
		protected void beforeExecute(Thread t, Runnable r) {
			
			if (r instanceof ChannelThread)
				mChannelRep.add((ChannelThread) r);
			
			super.beforeExecute(t, r);
				
		}
		
		@Override
		protected void afterExecute(Runnable r, Throwable t) {
			
			if (r instanceof ChannelThread)
				mChannelRep.remove((ChannelThread) r);
			
			super.afterExecute(r, t);
			
		}
	}
}
