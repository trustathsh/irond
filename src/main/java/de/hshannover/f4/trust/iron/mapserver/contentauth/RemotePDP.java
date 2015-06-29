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
 * This file is part of irond, version 0.5.4, implemented by the Trust@HsH
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
package de.hshannover.f4.trust.iron.mapserver.contentauth;


import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.net.ServerSocketFactory;
import javax.xml.bind.JAXBException;

import oasis.names.tc.xacml._2_0.context.schema.os.RequestType;
import oasis.names.tc.xacml._2_0.context.schema.os.ResponseType;

import org.apache.http.ConnectionClosedException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.DefaultHttpServerConnection;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import com.sun.xacml.ctx.Result;

public class RemotePDP {

	private static SimpleDateFormat fmt = new SimpleDateFormat("HH:mm:ss:ms");

	static class HandleXacmlRequest implements Runnable {

		private final Socket mSocket;
		private final LocalSunXacml mPdp;
		private final DefaultHttpServerConnection mConn;

		public HandleXacmlRequest(Socket sock, LocalSunXacml pdp) throws IOException {
			mSocket = sock;
			mConn = new DefaultHttpServerConnection();
			HttpParams params = new BasicHttpParams();
			mConn.bind(mSocket, params);
			mPdp = pdp;
		}

		@Override
		public void run() {
			HttpRequest req = null;
			HttpEntityEnclosingRequest hreq = null;
			HttpEntity reqEntity = null;
			InputStream is = null;
			HttpResponse response = null;
			boolean error = false;
			boolean errorClose = false;
			BasicHeader hdr = null;
			String clientName = mSocket.getInetAddress().getHostAddress() + ":" + mSocket.getPort();

			try {

				while (!errorClose) {
					error = false;
					req = mConn.receiveRequestHeader();

					// Is there more for us?
					if (req instanceof HttpEntityEnclosingRequest) {
						hreq = (HttpEntityEnclosingRequest)req;
						mConn.receiveRequestEntity(hreq);

						// Receive the entity
						reqEntity = hreq.getEntity();

						if (reqEntity != null) {

							try {
								is = processEntity(reqEntity);
								response = new BasicHttpResponse(HttpVersion.HTTP_1_1, 200, "OK");
								hdr = new BasicHeader("Content-Length", "" + is.available());
								response.setEntity(new InputStreamEntity(is, is.available()));
								response.addHeader(hdr);
							} catch (JAXBException e) {
								System.err.println(nameDate() + e.toString());
								response = new BasicHttpResponse(HttpVersion.HTTP_1_1,
										400, "Bad Request");
								response.setEntity(new StringEntity(e.toString()));
								hdr = new BasicHeader("Content-Length", "" + e.toString().length());
								response.addHeader(hdr);

							} finally {
								EntityUtils.consume(reqEntity);
							}
						} else {
							System.err.println(nameDate() + "No Entity");
							error = true;
						}

					} else {
						System.err.println(nameDate() + "Non HttpEntityEnclosingRequst");
						error = errorClose = true;
					}

					if (error) {
						response = new BasicHttpResponse(HttpVersion.HTTP_1_1,
								400, "Bad Request");
						hdr = new BasicHeader("Content-Length", "" + 0);
						response.addHeader(hdr);
					}

					mConn.sendResponseHeader(response);
					mConn.sendResponseEntity(response);

					if (errorClose) {
						mConn.close();
					}
				}
			} catch (ConnectionClosedException e) {
				System.out.println(nameDate() + clientName + " gone");
			} catch (IOException e) {
				e.printStackTrace();
			} catch (HttpException e) {
				e.printStackTrace();
			}
		}

		private InputStream processEntity(HttpEntity reqEntity) throws IOException, JAXBException {

			InputStream is = reqEntity.getContent();

			RequestType reqType = SunXacmlGlue.is2Request(is);
			Set<Result> results = mPdp.doRequestHook(reqType);

			ResponseType resType = SunXacmlGlue.results2ResponseType(results);

			InputStream ret = SunXacmlGlue.responseType2Is(resType);
			return ret;
		}
	}

	private static void runServer(int port, LocalSunXacml pdp, int threads) {
		Executor clientExec = Executors.newFixedThreadPool(threads);
		ServerSocketFactory sockFac = ServerSocketFactory.getDefault();


		try {
			ServerSocket sock = sockFac.createServerSocket(port);
			while (true) {
				Socket clientSock = sock.accept();
				Runnable cmd = new HandleXacmlRequest(clientSock, pdp);
				System.out.println(nameDate() + "new client " + clientSock.getInetAddress());
				clientExec.execute(cmd);
			}
		} catch (IOException e) {
			System.err.println(nameDate() + e.toString());
			System.exit(1);
		}
	}

	private static LocalSunXacml loadPolicy(String string) {
		return new LocalSunXacml(string, true);
	}

	public static void main(String args[]) throws UnknownHostException, IOException {

		int port = -1;
		int threads = -1;
		LocalSunXacml pdp = null;

		if (args.length != 3) {
			System.err.println(String.format("Usage: RemotePDP <policyfile> <port> <threads>"));
			System.exit(1);
		}


		pdp = loadPolicy(args[0]);

		try {
			port = Integer.parseInt(args[1]);
		} catch (NumberFormatException e) {
			port = -1;
		}

		if (port <= 0 || port >= 16384) {
			System.err.println("Bad Port");
			System.exit(1);
		}

		try {
			threads = Integer.parseInt(args[2]);
		} catch (NumberFormatException e) {
			port = -1;
		}

		// You just don't want more!
		if (threads <= 0 || threads >= 256) {
			System.err.println("Bad thread count");
			System.exit(1);
		}

		runServer(port, pdp, threads);
	}


	private static String nameDate() {
		return fmt.format(new Date()) + " PDP: ";
	}
}
