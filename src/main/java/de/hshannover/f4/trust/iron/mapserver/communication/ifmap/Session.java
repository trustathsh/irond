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
 * This file is part of irond, version 0.5.3, implemented by the Trust@HsH
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
package de.hshannover.f4.trust.iron.mapserver.communication.ifmap;


import de.hshannover.f4.trust.iron.mapserver.communication.ChannelIdentifier;
import de.hshannover.f4.trust.iron.mapserver.communication.ClientIdentifier;
import de.hshannover.f4.trust.iron.mapserver.exceptions.SystemErrorException;
import de.hshannover.f4.trust.iron.mapserver.utils.NullCheck;

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
		if (mPollState == PollState.POLL_PENDING) {
			throw new SystemErrorException("Can't have PollResultAvailable "
					+ "and PollPending");
		}

			mPollState = PollState.POLL_AVAILABLE;
	}

	public boolean isPollPending() {
		return mPollState == PollState.POLL_PENDING;
	}

	public void setPollPending() {
		if (mPollState == PollState.POLL_AVAILABLE) {
			throw new SystemErrorException("Can't have PollResultAvailable "
					+ "and PollPending");
		}

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
	@Override
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
