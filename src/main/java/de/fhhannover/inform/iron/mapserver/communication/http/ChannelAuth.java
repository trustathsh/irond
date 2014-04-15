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

import java.net.Socket;

import org.apache.http.HttpRequest;

import de.fhhannover.inform.iron.mapserver.communication.ClientIdentifier;
import de.fhhannover.inform.iron.mapserver.exceptions.ChannelAuthException;
import de.fhhannover.inform.iron.mapserver.trust.TrustService;
import de.fhhannover.inform.iron.mapserver.utils.NullCheck;

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
	
	private final TrustService mTrustService;
	
	protected ChannelAuth(Socket socket, TrustService trustService) {
		NullCheck.check(socket, "socket is null");
		mSocket = socket;
		mTrustService = trustService;
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
