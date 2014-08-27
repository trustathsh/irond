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
 * This file is part of irond, version 0.4.2, implemented by the Trust@HsH
 * research group at the Hochschule Hannover.
 * %%
 * Copyright (C) 2010 - 2014 Trust@HsH
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
package de.fhhannover.inform.iron.mapserver.communication.http;




import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.Socket;

import junit.framework.TestCase;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestFactory;
import org.apache.http.HttpVersion;
import org.apache.http.MethodNotSupportedException;
import org.apache.http.impl.DefaultHttpRequestFactory;
import org.apache.http.message.BasicRequestLine;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.fhhannover.inform.iron.mapserver.exceptions.ChannelAuthException;
import de.fhhannover.inform.iron.mapserver.exceptions.ProviderInitializationException;
import de.fhhannover.inform.iron.mapserver.provider.BasicAuthProvider;
import de.fhhannover.inform.iron.mapserver.provider.BasicAuthProviderPropImpl;
import de.fhhannover.inform.iron.mapserver.provider.ServerConfigurationProvider;
import de.fhhannover.inform.iron.mapserver.provider.StubProvider;

public class BasicAccessAuthenticationTest extends TestCase {

	private BasicChannelAuth mBasicAuth;
	private ServerConfigurationProvider mServerConf;

	private String testConf;

	@Override
	@Before
	public void setUp() {

		// ugly, create a properties file "somewhere" for testing
		try {
			File f;
			do {
				testConf = "irond_test_" + System.nanoTime();
				f = new File(testConf);
			} while (f.exists());

			FileWriter fw = new FileWriter(f);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write("test:test");
			bw.flush();
			fw.close();
		} catch (IOException e) {
			fail(e.getMessage());
		}

		mServerConf = StubProvider.getServerConfStub(testConf);
		BasicAuthProvider provider = null;
		try {
			provider = new BasicAuthProviderPropImpl(mServerConf);
		} catch (ProviderInitializationException e) {
			fail("Cannot initialize the provider!");
		}

		Socket s = new Socket();
		mBasicAuth = new BasicChannelAuth(s, provider);
	}

	@Override
	@After
	public void tearDown() {
		// delete the created file
		File f = new File(testConf);
		if (!f.delete()) {
			fail("Could not delete " + f.getAbsolutePath() + "!");
		}
	}

	@Test
	public void testBasicAuthWithValidCredentials(){
		HttpRequest req = getHttpRequest("test", "test");
		try {
			mBasicAuth.authenticate(req);
		} catch (ChannelAuthException e) {
			fail("AuthException with valid credentials!");
		}

	}

	@Test
	public void testBasicAuthWithInvalidCredentials(){
		HttpRequest req = getHttpRequest("bla", "blub");
		try {
			mBasicAuth.authenticate(req);
		} catch (ChannelAuthException e) {
			return;
		}
		fail("No AuthException with invalid credentials!");
	}

	private HttpRequest getHttpRequest(String user, String pass){
		HttpRequestFactory factory = new DefaultHttpRequestFactory();
		HttpRequest req = null;
		String base64 = new String(Base64.encodeBase64(
				user.concat(":").concat(pass).getBytes()));
		try {
			req = factory.newHttpRequest(
					new BasicRequestLine("POST", "https://localhost:8444/",
							HttpVersion.HTTP_1_1));
			req.addHeader("Accept-Encoding", "gzip,deflate");
			req.addHeader("Content-Type", "application/soap+xml;charset=UTF-8");
			req.addHeader("User-Agent", "IROND Testsuite/1.0");
			req.addHeader("Host", "localhost:8444");
			req.addHeader("Content-Length", "198");
			req.addHeader("Authorization", "Basic "+base64);
		} catch (MethodNotSupportedException e) {
			e.printStackTrace();
		}
		return req;
	}
}
