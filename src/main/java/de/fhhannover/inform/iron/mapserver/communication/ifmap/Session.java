package de.fhhannover.inform.iron.mapserver.communication.ifmap;

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

import de.fhhannover.inform.iron.mapserver.communication.ChannelIdentifier;
import de.fhhannover.inform.iron.mapserver.communication.ClientIdentifier;
import de.fhhannover.inform.iron.mapserver.exceptions.SystemErrorException;
import de.fhhannover.inform.iron.mapserver.utils.NullCheck;

/**
 * Represents an IF-MAP session.
 * 
 * This class simply contains data. It shouldn't contain control code!
 * 
 * 
 * @author aw
 *
 */
class Session {
	
	/**
	 * Private enum to represent the state of polling.
	 */
	private enum PollState {
		POLL_PENDING, POLL_AVAILABLE, NONE;
	}
	
	/**
	 * Identifies the client.
	 */
	private final ClientIdentifier mClientIdentifier;

	/**
	 * The corresponding ifmap-publisher-id for the session.
	 */
	private final String mPublisherId;
	
	/**
	 * The corresponding session-id.
	 */
	private String mSessionId;
	
	/**
	 * Identifies the associated synchronous send receive channel, or
	 * null if none is currently active.
	 */
	private ChannelIdentifier mSsrc;
	
	
	/**
	 * Identifies the associated asynchronous receive channel, or null
	 * if none is currently active.
	 */
	private ChannelIdentifier mArc;


	/**
	 * If mSSRC is null, then a timer should run, this timer is represented
	 * by this field. It will be null if a SSRC is active.
	 */
	private SessionTimer mSessionTimer;
	
	private PollState mPollState;
	
	/**
	 * Constructs a session with a given clientIdentifier and publisherId.
	 * 
	 * @param clientIdentifier
	 * @param pubId
	 */
	public Session(ClientIdentifier clientIdentifier, String publisherId) {
		NullCheck.check(clientIdentifier, "clientIdentifier is null");
		NullCheck.check(publisherId, "publisherId is null");
		mClientIdentifier = clientIdentifier;
		mPublisherId = publisherId;
	}
	
	public String getSessionId() {
		return mSessionId;
	}

	public void setSessionId(String sessionId) {
		mSessionId = sessionId;
	}

	public ChannelIdentifier getSsrc() {
		return mSsrc;
	}

	public void setSsrc(ChannelIdentifier ssrc) {
		mSsrc = ssrc;
	}

	public ChannelIdentifier getArc() {
		return mArc;
	}

	public void setArc(ChannelIdentifier arc) {
		mArc = arc;
	}

	public SessionTimer getTimer() {
		return mSessionTimer;
	}

	public void setTimer(SessionTimer sessionTimer) {
		mSessionTimer = sessionTimer;
	}

	public boolean isPollResultAvailable() {
		return mPollState == PollState.POLL_AVAILABLE;
	}

	public void setPollResultAvailable() {
		if (mPollState == PollState.POLL_PENDING)
			throw new SystemErrorException("Can't have PollResultAvailable "
					+ "and PollPending");
		
			mPollState = PollState.POLL_AVAILABLE;
	}

	public boolean isPollPending() {
		return mPollState == PollState.POLL_PENDING;
	}

	public void setPollPending() {
		if (mPollState == PollState.POLL_AVAILABLE)
			throw new SystemErrorException("Can't have PollResultAvailable "
					+ "and PollPending");
		
		mPollState = PollState.POLL_PENDING;
	}

	public void unsetPollState() {
		mPollState = PollState.NONE;
	}

	public ClientIdentifier getClientIdentifier() {
		return mClientIdentifier;
	}

	public String getPublisherId() {
		return mPublisherId;
	}

	// TODO: implement this
	public String toString() {
		return "FIXME TODO Session.toString()";
	}

	public boolean hasArc() {
		return mArc != null;
	}
	
	public boolean hasSsrc() {
		return mSsrc != null;
	}

	public boolean hasTimer() {
		return mSessionTimer != null;
	}
}
