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
import de.fhhannover.inform.iron.mapserver.utils.LengthCheck;
import de.fhhannover.inform.iron.mapserver.utils.NullCheck;

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
		
		if ((maxPollResultSize != null) && !(maxPollResultSize >= 0))
				throw new RuntimeException("max-poll-result-size wrong: " +
						maxPollResultSize);
		
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
