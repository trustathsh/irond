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
 * This file is part of irond, version 0.4.1, implemented by the Trust@FHH
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

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HeaderElement;
import org.apache.http.HeaderElementIterator;
import org.apache.http.HttpRequest;
import org.apache.http.message.BasicHeaderElementIterator;

import de.fhhannover.inform.iron.mapserver.communication.ClientIdentifier;
import de.fhhannover.inform.iron.mapserver.exceptions.ChannelAuthException;
import de.fhhannover.inform.iron.mapserver.provider.BasicAuthProvider;
import de.fhhannover.inform.iron.mapserver.utils.NullCheck;

/**
 * Implements the basic access authentication described in RFC 1945, 2616 and 2617.
 * 
 * @author tr
 */
public class BasicChannelAuth extends ChannelAuth {

	private ClientIdentifier mClientId;
	private final BasicAuthProvider mBasicAuthProvider;
	
	public BasicChannelAuth(Socket socket, BasicAuthProvider basicAuthProv) {
		super(socket);
		NullCheck.check(basicAuthProv, "basic auth provider is null");
		mBasicAuthProvider = basicAuthProv;
	}
	
	@Override
	public void authenticate(HttpRequest request) throws ChannelAuthException {
		HeaderElementIterator it = 
			new BasicHeaderElementIterator(request.headerIterator("Authorization"));
		HeaderElement elem;
		
		if(it.hasNext()){
			elem = it.nextElement();
		} else {
			throw new ChannelAuthException("no authorization field found");
		}
	
		// split the value of the Authorization header. split[0] should be
		// basic and split[1] the base64 stuff.
		String split[] = elem.getName().split(" ");
		
		if (split.length != 2 || !split[0].equals("Basic")) {
			throw new ChannelAuthException("Bad Authorization header value!");
		}
		
		String base64 = split[1];
		String[] creds = new String(Base64.decodeBase64(base64)).split(":");
		
		if(creds.length != 2){
			throw new ChannelAuthException("Wrong credentials, not authenticated!");
		}
		
		String user = creds[0];
		String pass = creds[1];

		if(!mBasicAuthProvider.verify(user, pass)){
			throw new ChannelAuthException("Bad username/password");
		} else {
			ClientIdentifier newClId = new ClientIdentifier(user);

			// this checks, whether the username is the same as given on the
			// first call. If this is the first call, set the mClientId field
			// appropriately.
			if (mClientId != null) {
				if (!mClientId.equals(newClId)) {
					throw new ChannelAuthException("Username/password was changed");
				}
			} else {
				mClientId = newClId;
			}
		}
	}

	@Override
	public ClientIdentifier getClientIdentifier() {
		return mClientId;
	}

}
