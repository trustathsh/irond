/*
 * #%L
 * =====================================================
 *   _____                _     ____  _   _       _   _
 *  |_   _|_ __ _   _ ___| |_  / __ \| | | | ___ | | | |
 *    | | | '__| | | / __| __|/ / _` | |_| |/ __|| |_| |
 *    | | | |  | |_| \__ \ |_| | (_| |  _  |\__ \|  _  |
 *    |_| |_|   \__,_|___/\__|\ \__,_|_| |_||___/|_| |_|
 *                             \____/
 * 
 * =====================================================
 * 
 * Hochschule Hannover
 * (University of Applied Sciences and Arts, Hannover)
 * Faculty IV, Dept. of Computer Science
 * Ricklinger Stadtweg 118, 30459 Hannover, Germany
 * 
 * Email: trust@f4-i.fh-hannover.de
 * Website: http://trust.f4.hs-hannover.de/
 * 
 * This file is part of irond, version 0.5.3, implemented by the Trust@HsH
 * research group at the Hochschule Hannover.
 * %%
 * Copyright (C) 2010 - 2015 Trust@HsH
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
package de.hshannover.f4.trust.iron.mapserver.communication.http;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLSocket;

import org.apache.http.HeaderElement;
import org.apache.http.HeaderElementIterator;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.DefaultHttpServerConnection;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import de.hshannover.f4.trust.iron.mapserver.communication.ChannelIdentifier;
import de.hshannover.f4.trust.iron.mapserver.communication.ClientIdentifier;
import de.hshannover.f4.trust.iron.mapserver.communication.bus.Queue;
import de.hshannover.f4.trust.iron.mapserver.communication.bus.messages.BadChannelEvent;
import de.hshannover.f4.trust.iron.mapserver.communication.bus.messages.ClosedChannelEvent;
import de.hshannover.f4.trust.iron.mapserver.communication.bus.messages.Event;
import de.hshannover.f4.trust.iron.mapserver.communication.bus.messages.RequestChannelEvent;
import de.hshannover.f4.trust.iron.mapserver.communication.ifmap.EventProcessor;
import de.hshannover.f4.trust.iron.mapserver.exceptions.ChannelAuthException;
import de.hshannover.f4.trust.iron.mapserver.provider.LoggingProvider;
import de.hshannover.f4.trust.iron.mapserver.utils.NullCheck;

/**
 * Accepts the http-request from client, and if authenticated, creates an event
 * and put it in the event-queue.
 *
 * @author tr, aw
 *
 */
public class ChannelThread implements Runnable {

	private static final String sName = "ChannelThread";

	private static Logger sLogger;
	static {
		sLogger = LoggingProvider.getTheLogger();
	}


	/**
	 * indicates whether this is the first request, will always be false after
	 * the second one.
	 */
	private final ChannelIdentifier mChannelIdentifier;
	private final ChannelAuth mChannelAuth;
	private final SSLSocket mSocket;
	private final Queue<Event> mQueue;
	private volatile boolean mFirstRequest;
	private volatile boolean mDone;
	private volatile boolean mBroken;
	private volatile boolean mExpectResponse;
	private volatile boolean mUseGzip;
	private DefaultHttpServerConnection mHttpConnection;

	public ChannelThread(Socket socket, ChannelAuth auth, ChannelIdentifier cid,
			Queue<Event> queue) {
		NullCheck.check(socket, "socket is null");
		NullCheck.check(auth, ", auth is null");
		NullCheck.check(cid, "cid is null");
		NullCheck.check(queue, "queue is null");

		mSocket = (SSLSocket)socket;
		mChannelAuth = auth;
		mChannelIdentifier = cid;
		mQueue = queue;

		mFirstRequest = true;
		mUseGzip = mExpectResponse = mDone = mBroken = false;
		init();
	}

	private void init() {
		try {
			mHttpConnection = getDefaultHttpServerConnection(mSocket);
		} catch (IOException e) {
			sLogger.warn("ChannelThread.init: Cannot bind socket to DefaultHttpServerConnection");
		}
	}

	private DefaultHttpServerConnection getDefaultHttpServerConnection(Socket s)
			throws IOException {
		DefaultHttpServerConnection hsc = new DefaultHttpServerConnection();
		hsc.bind(s, new BasicHttpParams());
		return hsc;
	}

	public void setDone() {
		mDone = true;
	}

	public boolean isDone() {
		return mDone;
	}

	public ChannelIdentifier getChannelIdentifier() {
		return mChannelIdentifier;
	}

	public ClientIdentifier getClientIdentifier() {
		return mChannelAuth.getClientIdentifier();
	}

	/**
	 * Send a reply to the client for this channel.
	 *
	 * We use HTTP request/response. This method is called by the {@link ActionProcessor}
	 * after it was instructed by the {@link EventProcessor} to send out a
	 * reply. This can only happen after we received a request on this channel.
	 * <br/>
	 * Be aware, the {@link Thread} sending out the reply is not the {@link Thread}
	 * executing this {@link ChannelThread} instance.
	 * <br/>
	 *
	 * @param reply
	 */
	public void reply(InputStream reply) {
		sLogger.debug(sName + ": Sending reply to " + getClientIdentifier());
		try {

			// if we don't expect to send a response log it, but continue
			if (!isExpectResponse()) {
				sLogger.error(sName + ": UNEXPECTED: sending of response on "
						+ getChannelIdentifier() + " which is not expecting a " +
								"response");

				while (!isExpectResponse() && !isBroken() && isDone()) {
					try {
						this.wait();
					} catch (InterruptedException e) { /* ignore */ }
				}
			}

			if (isBroken()) {
				sLogger.warn(sName + ": " + getChannelIdentifier() +
						" is broken, can not send response");

				// try to abort again
				abort();
				return;
			}

			if (isDone()) {
				sLogger.error(sName + ": UNEXPECTED: sending of response on "
						+ getChannelIdentifier() + " which is in state 'DONE'");
				return;
			}

			BasicHeader header = null;



			HttpResponse response = new BasicHttpResponse(HttpVersion.HTTP_1_1,
					HttpStatus.SC_OK, "");
			header = new BasicHeader("Content-Type", "application/soap+xml");
			response.addHeader(header);

			if (mUseGzip) {
				sLogger.trace(sName + ": Client " + getClientIdentifier()
						+ " will get gzipped reply");
				sLogger.trace(sName + ": Length of reply before gzip="
						+ reply.available() + " bytes");
				response.addHeader(new BasicHeader("Content-Encoding", "gzip"));
				reply = compress(reply);
			}

			int size = reply.available();
			sLogger.trace(sName + ": Length of reply on wire=" + size + " bytes");
			header = new BasicHeader("Content-Length", "" + size);
			response.addHeader(header);


			synchronized (this) {
				HttpEntity respEntity = new InputStreamEntity(reply, size);
				response.setEntity(respEntity);
				mHttpConnection.sendResponseHeader(response);
				mHttpConnection.sendResponseEntity(response);
				//mHttpConnection.flush();
				EntityUtils.consume(respEntity);
				mExpectResponse = false;
				this.notify();
			}

		} catch (HttpException e) {
			sLogger.error(sName + ": Sending response failed: " + e.getMessage());
			sLogger.error(sName + ": Setting channel " +  getChannelIdentifier() +
					" into state 'BROKEN'");
			broken();
		} catch (IOException e) {
			if (e.getMessage().equals("Connection closed by remote host")) {
				int remainingBytest = 0;
				try {
					remainingBytest = reply.available();
					if (remainingBytest == 0) {
						// the are no bytes left in the the reply stream, that
						// doesn't guarantee receipt by the MAPC, but it's good
						// enough to simply take it as a "closed after sending"
						sLogger.trace(sName + ": " + getClientIdentifier() +
								" closed " + getChannelIdentifier() +
								" directly after sending response.");

						// we just set it to done, the ClosedChannelEvent
						// is created in run(), after the closed channel is
						// detected.
						setDone();
					} else {
						sLogger.error(sName + ": " + getClientIdentifier() +
								" closed channel while sending response");
						broken();
					}
				} catch (IOException e1) {
					// in this case we have a real problem, but we are working
					// with ByteArrayInputStream, so that shouldn't happen.
					sLogger.error(sName + ": Could not get remaining bytes");
					sLogger.error(sName + ": " + e.getMessage());
					sLogger.error(sName + ": Setting channel "
							+ getChannelIdentifier() + " into state 'BROKEN'");
					broken();
				}
			}
		}
	}


	@Override
	public void run() {


		while (!isDone() && !isBroken()) {
			Event event = null;
			try {
				InputStream body = receiveRequestBodyContent();

				event = new RequestChannelEvent(getChannelIdentifier(),
						getClientIdentifier(), body,  mFirstRequest);
				mFirstRequest = false;

				synchronized (this) {
					if (mExpectResponse == true) {
						sLogger.error(sName + ": UNEXPECTED: Received next "
								+ "request before reply was sent to client. "
								+ getClientIdentifier() + " on " + getChannelIdentifier());

						// wait until something happens
						while (isExpectResponse() && !isBroken() && !isDone()) {
							try {
								this.wait();
							} catch (InterruptedException e) { /* ignore */ }
						}
					}
				}
				mExpectResponse = true;

				// put the created event into the queue.
				putIntoQueue(event);
			} catch (HttpException e) {
				sLogger.error(sName + ": Receiving request failed: "
						+ e.getMessage());
				sLogger.error(sName + ": Setting channel " +  getChannelIdentifier()
						+ " into state 'BROKEN'");
				// interpret this as an error and close channel
				broken();

			} catch (IOException e) {
				if (e.getMessage() != null && e.getMessage().equals("Client closed connection")) {
					sLogger.debug(sName + ": " + mChannelIdentifier + " closed");
					setDone();
				} else {
					sLogger.error(sName + ": Receiving request failed");

					if (e instanceof SSLHandshakeException) {
						sLogger.error(sName + ": SSLHandshakeException: Client "
								+ "doesn't know about our certificate (?)");
					}

					sLogger.error(sName + ": Setting channel " +  getChannelIdentifier() +
						" into state 'BROKEN'");
					broken();
				}

			} catch (ChannelAuthException e) {
				sLogger.warn(sName + ": Authentication on "
						+ mChannelIdentifier + " failed: " +  e.getMessage());
				sendUnauthorizedResponse();
			}
		} // end while (!isDone && isBroken)

		// If we end up with a broken channel, create a BadChannelEvent,
		// else a ClosedChannelEvent.
		if (isBroken()) {
			putIntoQueue(new BadChannelEvent(getChannelIdentifier()));
		} else {
			putIntoQueue(new ClosedChannelEvent(getChannelIdentifier()));
		}
	}

	private void putIntoQueue(Event event) {
		while (event != null) {
			try {
				mQueue.put(event);
				event = null;
			} catch (InterruptedException e) {
				sLogger.debug(sName + ": Interrupted while putting event into" +
						" queue for " + getChannelIdentifier());
				// try again!
			}
		}
	}

	private void sendUnauthorizedResponse() {
		String html = "<html><head><title>401 Unauthorized</title></head></html>";
		HttpResponse denied = new BasicHttpResponse(HttpVersion.HTTP_1_1,
				HttpStatus.SC_UNAUTHORIZED, "Unauthorized");
		BasicHeader header = new BasicHeader("WWW-Authenticate", "Basic realm=\""
					+ mSocket.getInetAddress().getHostAddress()+"\"");

		denied.addHeader(header);
		header = new BasicHeader("Content-length", "" + html.length());
		denied.addHeader(header);
		try {
			StringEntity entity = new StringEntity(html);
			denied.setEntity(entity);
			mHttpConnection.sendResponseHeader(denied);
			mHttpConnection.sendResponseEntity(denied);
			EntityUtils.consume(entity);
		} catch (HttpException e) {
			sLogger.error(sLogger + " Could not send unauthorized response");
			sLogger.error(sName + ": Dropping channel " +  mChannelIdentifier);
			abort();
		} catch (IOException e) {
			sLogger.error(sLogger + " Could not send unauthorized response");
			sLogger.error(sName + ": Dropping channel " +  mChannelIdentifier);
			abort();
		}
	}

	/**
	 * Hackery
	 *
	 * @return the HTTP body as {@link InputStream}
	 * @throws HttpException
	 * @throws IOException
	 * @throws ChannelAuthException
	 */
	private InputStream receiveRequestBodyContent() throws HttpException, IOException,
			ChannelAuthException {
		HttpEntity reqEntity = null;
		boolean entityGzipped = false;
		HttpRequest req = mHttpConnection.receiveRequestHeader();

		if (req instanceof HttpEntityEnclosingRequest) {
			mHttpConnection
					.receiveRequestEntity((HttpEntityEnclosingRequest) req);
			reqEntity = ((HttpEntityEnclosingRequest) req).getEntity();
		}

		try {
			mChannelAuth.authenticate(req);
		} catch (ChannelAuthException e) {
			if (reqEntity != null) {
				EntityUtils.consume(reqEntity);
			}

			throw e;
		}

		sLogger.debug(sName + ": Client " + getClientIdentifier() +
				" authenticated successfully on channel " + mChannelIdentifier);

		// here we are authenticated

		// check the headers for Accept-Encoding: gzip
		mUseGzip = clientAcceptsGzip(req) ? true : mUseGzip;
		entityGzipped = isReqEntityGzipped(req);

		if (mUseGzip) {
			sLogger.trace(sName + ": Client " + getClientIdentifier()
					+ " wants gzipped encoding");
		}

		if (entityGzipped) {
			sLogger.trace(sName + ": Received request is gzipped");
		}

		if (reqEntity == null) {
			throw new HttpException();
		}

		// We bluntly copy the whole stream, but it makes life easier
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		reqEntity.writeTo(baos);
		baos.close();
		InputStream is = new ByteArrayInputStream(baos.toByteArray());


		if (is.available() <= 0) {
			throw new IOException("No content received");
		}

		if (entityGzipped) {
			is = uncompress(is);
		}

		return is;
	}

	/**
	 * just look for gzip somewhere and if it's there, use it. ugly.
	 *
	 * @param req
	 * @return
	 */
	private boolean clientAcceptsGzip(HttpRequest req) {
		HeaderElementIterator it =
			new BasicHeaderElementIterator(req.headerIterator("Accept-Encoding"));
		while (it.hasNext()) {
			HeaderElement element = it.nextElement();
			if (element.getName().contains("gzip")) {
				return true;
			}
		}
		return false;
	}

	/**
	 * just look for gzip somewhere and if it's there, use it. ugly.
	 */
	private boolean isReqEntityGzipped(HttpRequest req) {
		HeaderElementIterator it =
			new BasicHeaderElementIterator(req.headerIterator("Content-Encoding"));
		while (it.hasNext()) {
			HeaderElement element = it.nextElement();
			if (element.getName().contains("gzip")) {
				return true;
			}
		}
		return false;
	}

	private boolean isBroken() {
		return mBroken;
	}

	private InputStream uncompress(InputStream is) throws IOException {
		int c = 0;
		// wrap to uncompress
		is = new GZIPInputStream(is, is.available());
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		while ((c = is.read()) >= 0) {
			baos.write(c);
		}

		baos.close();
		is.close();

		return new ByteArrayInputStream(baos.toByteArray());
	}

	private InputStream compress(InputStream is) throws IOException {
		int read;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		GZIPOutputStream go = new GZIPOutputStream(baos);

		while ((read = is.read()) > 0) {
			go.write(read);
		}

		go.close();
		baos.close();
		return new ByteArrayInputStream(baos.toByteArray());
	}

	private boolean isExpectResponse() {
		return mExpectResponse;
	}


	/**
	 * Call this if the channe is most likely broken. Will set mBroken
	 * and then call abort.
	 */
	private void broken() {
		mBroken = true;
		abort();
	}

	/**
	 * Try to close the underlying socket of this {@link ChannelThread}.
	 * If the thread executing this {@link ChannelThread} is waiting on
	 * a read() call, it'll get an {@link IOException} and can quit.
	 */
	private void close() {
		try {
			if (mSocket != null) {
				mSocket.close();
			}
		} catch (IOException e) {
			sLogger.warn(sName + ": Exception closing socket: " + e.getMessage());
		}
	}

	/**
	 * Try to abort this {@link ChannelThread}. First set mDone to true,
	 * then try to close the socket. This should do the trick.
	 */
	public void abort() {
		setDone();
		close();
	}

}
