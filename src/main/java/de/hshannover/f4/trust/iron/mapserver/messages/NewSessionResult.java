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
 * This file is part of irond, version 0.5.4, implemented by the Trust@HsH
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
package de.hshannover.f4.trust.iron.mapserver.messages;


import de.hshannover.f4.trust.iron.mapserver.communication.ChannelIdentifier;
import de.hshannover.f4.trust.iron.mapserver.communication.ClientIdentifier;
import de.hshannover.f4.trust.iron.mapserver.utils.LengthCheck;
import de.hshannover.f4.trust.iron.mapserver.utils.NullCheck;

/**
 *
 *
 * @author aw
 *
 */
public class NewSessionResult extends Result {

	private final String mSessionId;
	private final String mPublisherId;
	private final Integer mMaxPollResultSize;


	public NewSessionResult(ChannelIdentifier channelId, ClientIdentifier clientId,
			String sessionId, String publisherId, Integer maxPollResultSize) {
		super(channelId, clientId);

		NullCheck.check(sessionId, "session-id not set");
		NullCheck.check(publisherId, "publisher-id not set");
		LengthCheck.checkMin(sessionId, 1, "session-id length=0");
		LengthCheck.checkMin(publisherId, 1, "publisher-id length=0");

		if (maxPollResultSize != null && !(maxPollResultSize >= 0)) {
			throw new RuntimeException("max-poll-result-size wrong: " +
					maxPollResultSize);
		}

		mSessionId = sessionId;
		mPublisherId = publisherId;
		mMaxPollResultSize = maxPollResultSize;
	}

	public String getSessionId() {
		return mSessionId;
	}

	public String getPublisherId() {
		return mPublisherId;
	}

	public Integer getMaxPollResultSize() {
		return mMaxPollResultSize;
	}
}
