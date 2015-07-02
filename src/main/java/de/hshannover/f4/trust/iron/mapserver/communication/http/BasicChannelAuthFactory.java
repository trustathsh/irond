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
 * This file is part of irond, version 0.5.5, implemented by the Trust@HsH
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


import java.net.Socket;

import de.hshannover.f4.trust.iron.mapserver.provider.BasicAuthProvider;
import de.hshannover.f4.trust.iron.mapserver.utils.NullCheck;

/**
 * Factory class to create instances of class  {@link BasicChannelAuth}, to be used
 * if a MAPC uses basic authentication.
 *
 * This class needs a {@link BasicAuthProvider} instance to create {@link BasicChannelAuth}
 * instances.
 *
 * @author aw
 *
 */
public class BasicChannelAuthFactory implements ChannelAuthFactory {

	/**
	 * represents the {@link BasicAuthProvider} to be used by the constructed.
	 * {@link BasicChannelAuth} instances.
	 */
	private final BasicAuthProvider mBasicAuthProvider;

	/**
	 * Construct a {@link BasicChannelAuthFactory} given a {@link BasicAuthProvider}.
	 *
	 * @param basicAuthProvider
	 */
	public BasicChannelAuthFactory(BasicAuthProvider basicAuthProvider) {
		NullCheck.check(basicAuthProvider, "basicAuthProvider is null");
		mBasicAuthProvider = basicAuthProvider;
	}

	@Override
	public ChannelAuth createChannelAuth(Socket socket) {
		NullCheck.check(socket, "socket is null");
		return new BasicChannelAuth(socket,  mBasicAuthProvider);
	}
}
