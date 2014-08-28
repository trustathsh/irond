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
 * This file is part of irond, version 0.5.0, implemented by the Trust@HsH
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
package de.hshannover.f4.trust.iron.mapserver.contentauth;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import javax.xml.bind.JAXBException;

import oasis.names.tc.xacml._2_0.context.schema.os.RequestType;
import oasis.names.tc.xacml._2_0.context.schema.os.ResponseType;

import org.apache.log4j.Logger;

import com.sun.xacml.ctx.Result;

import de.hshannover.f4.trust.iron.mapserver.exceptions.ServerInitialException;
import de.hshannover.f4.trust.iron.mapserver.provider.LoggingProvider;

class RemoteSunXacml extends AbstractSunXacml {

	private final URL mUrl;
	private final boolean mRawLog;
	private static Logger sLogger = LoggingProvider.getRawDecisionRequestLogger();
	private AtomicLong mCounter;


	public RemoteSunXacml(String param, boolean rawLog) throws ServerInitialException {

		try {
			mUrl = new URL(param);
		} catch (MalformedURLException e) {
			throw new ServerInitialException(e.toString());
		}

		mRawLog = rawLog;
		mCounter = new AtomicLong();
	}

	@Override
	public Set<Result> doRequestHook(RequestType reqType) throws IOException, JAXBException {

		HttpURLConnection conn = null;
		long req = mCounter.incrementAndGet();

		try {
			InputStream is = SunXacmlGlue.request2Is(reqType);

			if (mRawLog) {
				is = log(is, false, req);
			}

			conn = (HttpURLConnection)mUrl.openConnection();
			conn.setRequestMethod("POST");
			conn.setDoOutput(true);
			conn.setFixedLengthStreamingMode(is.available());

			OutputStream cOs = conn.getOutputStream();
			int val = 0;
			ResponseType respType = null;

			while ((val = is.read()) >= 0) {
				cOs.write(val);
			}

			is.close();
			cOs.flush();
			cOs.close();

			InputStream cIs = conn.getInputStream();

			if (mRawLog) {
				cIs = log(cIs, true, req);
			}

			respType = SunXacmlGlue.is2ResponseType(cIs);
			cIs.close();

			return SunXacmlGlue.responseType2Results(respType);

		} catch (IOException e) {
			if (conn != null) {
				conn.disconnect();
			}
			throw e;
		} catch (JAXBException e) {
			throw e;
		}
	}

	private InputStream log(InputStream is, boolean in, long req) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int c;

		while ((c = is.read()) >= 0) {
			baos.write(c);
		}

		synchronized (this) {
			sLogger.trace(String.format("Request %d %s\n%s", req,
															 in ? "in" : "out",
															 baos.toString()));
		}

		return new ByteArrayInputStream(baos.toByteArray());
	}
}
