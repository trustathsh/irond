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
 * This file is part of irond, version 0.5.6, implemented by the Trust@HsH
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


import de.hshannover.f4.trust.iron.mapserver.communication.ChannelIdentifier;
import de.hshannover.f4.trust.iron.mapserver.utils.NullCheck;

/**
 * Abstract {@link Event} implementation containing a {@link ChannelIdentifier}
 * object to identify the channel this {@link Event} belongs to.
 *
 * @author aw
 */
public abstract class ChannelEvent implements Event {

	/**
	 * Identifies the channel the {@link Action} belongs to.
	 */
	private final ChannelIdentifier mChannelIdentifier;

	/**
	 * Constructor with simple sanity check. A {@link ChannelIdentifier} is
	 * immutable, so we don't have to do any copies here.
	 *
	 * @param channelIdentifier
	 */
	public ChannelEvent(ChannelIdentifier channelIdentifier) {
		NullCheck.check(channelIdentifier, "channelIdentifier is null");
		mChannelIdentifier = channelIdentifier;
	}

	public ChannelIdentifier getChannelIdentifier() {
		return mChannelIdentifier;
	}
}
