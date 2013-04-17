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
 * This file is part of irond, version 0.4.0, implemented by the Trust@FHH 
 * research group at the Fachhochschule Hannover.
 * 
 * irond is an an *experimental* IF-MAP 2.0 compliant MAP server written in
 * JAVA. irond supports both basic authentication and certificate-based 
 * authentication (using X.509 certificates) of MAP clients. irond is
 * maintained by the Trust@FHH group at the Fachhochschule Hannover, initial
 * developement was carried out during the ESUKOM research project.
 * %%
 * Copyright (C) 2010 - 2013 Trust@FHH
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

import de.fhhannover.inform.iron.mapserver.provider.BasicAuthProvider;
import de.fhhannover.inform.iron.mapserver.utils.NullCheck;

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
