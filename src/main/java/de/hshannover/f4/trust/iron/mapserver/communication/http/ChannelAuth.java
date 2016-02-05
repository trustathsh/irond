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
 * This file is part of irond, version 0.5.7, implemented by the Trust@HsH
 * research group at the Hochschule Hannover.
 * %%
 * Copyright (C) 2010 - 2016 Trust@HsH
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

import org.apache.http.HttpRequest;

import de.hshannover.f4.trust.iron.mapserver.communication.ClientIdentifier;
import de.hshannover.f4.trust.iron.mapserver.exceptions.ChannelAuthException;
import de.hshannover.f4.trust.iron.mapserver.utils.NullCheck;

/**
 * Defines the authentication-methods used by {@link ChannelThread}
 *
 * @author tr
 */


public abstract class ChannelAuth {

	/**
	 * The socket, this {@link ChannelAuth} instance is used with.
	 * This is needed for the certificate-based authentication.
	 * It's useless for the basic authentication, but we can generalize
	 * basic and certificate-based authentication easier if we introduce it
	 * here.
	 */
	private final Socket mSocket;

	protected ChannelAuth(Socket socket) {
		NullCheck.check(socket, "socket is null");
		mSocket = socket;
	}

	/**
	 * Returns the client-identifier
	 * @return An unique ID for every client
	 */
	public abstract ClientIdentifier getClientIdentifier();

	/**
	 * Performs the authentication process.
	 *
	 * This method is called for each HTTP request. Whether the authentication
	 * is done each time or only once is up to the implementation.
	 * Basic Authentication, for example, should do the authentication for each
	 * HTTP request.
	 *
	 * Classes that implement this method must throw a {@link ChannelAuthException}
	 * when an error occurred
	 *
	 * @param request
	 */
	public abstract void authenticate(HttpRequest request) throws ChannelAuthException;


	/**
	 * @return the socket used for the channel.
	 */
	public Socket getSocket() {
		return mSocket;
	}
}
