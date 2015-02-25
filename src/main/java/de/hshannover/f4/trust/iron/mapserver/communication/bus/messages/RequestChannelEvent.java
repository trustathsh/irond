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
 * This file is part of irond, version 0.5.2, implemented by the Trust@HsH
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
package de.hshannover.f4.trust.iron.mapserver.communication.bus.messages;


import java.io.InputStream;


import de.hshannover.f4.trust.iron.mapserver.communication.ChannelIdentifier;
import de.hshannover.f4.trust.iron.mapserver.communication.ClientIdentifier;
import de.hshannover.f4.trust.iron.mapserver.communication.ifmap.EventProcessor;
import de.hshannover.f4.trust.iron.mapserver.utils.NullCheck;

/**
 * A {@link Event} implementation representing an incoming request.
 * The channel used for this request is given by the {@link ChannelIdentifier}
 * object. The MAPC is identified by the given {@link ClientIdentifier} object.
 *
 * @author aw
 */
public class RequestChannelEvent extends ChannelEvent {

	/**
	 * Indicates the MAPC responsible for this request.
	 */
	private final ClientIdentifier mClientIdentifier;

	/**
	 * Contains the content of the request, i.e the HTTP body.
	 */
	private final InputStream mRequestContent;

	/**
	 * Indicates whether this is the first request on the channel.
	 */
	private final boolean mFirstRequest;

	/**
	 * Constructs a {@link RequestChannelEvent} with the needed parameters.
	 * Does some sanity checks.
	 *
	 * @param channelIdentifier
	 * @param clientIdentifier
	 * @param requestContent
	 * @param firstRequest true if this is the first request on the channel
	 */
	public RequestChannelEvent(ChannelIdentifier channelIdentifier,
			ClientIdentifier clientIdentifier, InputStream requestContent,
			boolean firstRequest) {
		super(channelIdentifier);
		NullCheck.check(clientIdentifier, "clientIdentifier is null");
		NullCheck.check(requestContent, "requestContent is null");
		mClientIdentifier = clientIdentifier;
		mRequestContent = requestContent;
		mFirstRequest = firstRequest;
	}


	public ClientIdentifier getClientIdentifier() {
		return mClientIdentifier;
	}


	public InputStream getRequestContent() {
		return mRequestContent;
	}

	public boolean isFirstRequest() {
		return mFirstRequest;
	}


	@Override
	public void dispatch(EventProcessor ep) {
		ep.processRequestChannelEvent(this);
	}
}
