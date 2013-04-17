package de.fhhannover.inform.iron.mapserver.messages;

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

import de.fhhannover.inform.iron.mapserver.communication.ChannelIdentifier;
import de.fhhannover.inform.iron.mapserver.communication.ClientIdentifier;
import de.fhhannover.inform.iron.mapserver.communication.ifmap.EventProcessor;
import de.fhhannover.inform.iron.mapserver.exceptions.AccessDeniedException;
import de.fhhannover.inform.iron.mapserver.utils.NullCheck;

/**
 * Simple superclass which already contains the sessionId
 * added to remove some code from the other Request classes.
 * 
 * @version 0.1
 * @author aw
 * 
 * created: 05.02.10
 * 
 * changes:
 *   05.02.10 aw - ...
 *   02.12.10 aw - Make it just a simple interface
 * 
 * 
 */
public abstract class Request {

	/**
	 * represents the client
	 */
	private ClientIdentifier mClientIdentifier;
	
	/**
	 * represents the used channel
	 */
	private ChannelIdentifier mChannelIdentifier;
	
	/**
	 * represents whether this request is the first request on the channel
	 */
	private boolean mFirstRequest;
	
	
	public Request() {
		mChannelIdentifier = null;
		mClientIdentifier = null;
		mFirstRequest = false;
	}

/*	
	public Request(ClientIdentifier clientId, ChannelIdentifier channelId,
			boolean first) {
		NullCheck.check(clientId, "clientId is null");
		NullCheck.check(channelId, "channelId is null");
		mClientIdentifier = clientId;
		mChannelIdentifier = channelId;
		mFirstRequest = first;
	}
	*/

	/**
	 * Dispatch to the appropriate method for this {@link Request}
	 * in the {@link EventProcessor} implementation.
	 * 
	 * @param ep
	 * @throws AccessDeniedException 
	 */
	public abstract void dispatch(EventProcessor ep) throws AccessDeniedException;
	
	

	public ClientIdentifier getClientIdentifier() {
		NullCheck.check(mClientIdentifier, "client identifier not set!");
		return mClientIdentifier;
	}
	
	public ChannelIdentifier getChannelIdentifier() {
		NullCheck.check(mChannelIdentifier, "channel identifier not set!");
		return mChannelIdentifier;
	}
	
	public boolean isFirstRequest() {
		return mFirstRequest;
	}


	public void setChannelId(ChannelIdentifier channelId) {
		NullCheck.check(channelId, "channelId is null");
		mChannelIdentifier = channelId;
	}


	public void setClientId(ClientIdentifier clientId) {
		NullCheck.check(clientId, "clientId is null");
		mClientIdentifier = clientId;
	}


	public void setFirst(boolean firstRequest) {
		mFirstRequest = firstRequest;
	}
}
