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
 * This file is part of irond, version 0.5.5, implemented by the Trust@HsH
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


import java.util.HashMap;
import java.util.Map;

import de.hshannover.f4.trust.iron.mapserver.communication.ChannelIdentifier;
import de.hshannover.f4.trust.iron.mapserver.communication.ClientIdentifier;
import de.hshannover.f4.trust.iron.mapserver.exceptions.AlreadyMappedException;
import de.hshannover.f4.trust.iron.mapserver.exceptions.AlreadyStoredException;
import de.hshannover.f4.trust.iron.mapserver.exceptions.NoMappingException;
import de.hshannover.f4.trust.iron.mapserver.exceptions.SessionNotFoundException;
import de.hshannover.f4.trust.iron.mapserver.exceptions.StillMappedException;
import de.hshannover.f4.trust.iron.mapserver.utils.NullCheck;

/**
 * Implementation of {@link SessionRepository} using three different
 * {@link HashMap}s.
 *
 * @author aw
 */
public class SessionRepositoryImpl implements SessionRepository {

	/**
	 * Holds mappings from {@link ClientIdentifier} to {@link Session}
	 */
	private Map<ClientIdentifier, Session> mClientIdSessionMap;


	/**
	 * Holds mappings from {@link ChannelIdentifier} to {@link Session}
	 */
	private Map<ChannelIdentifier, Session> mChannelIdSessionMap;

	/**
	 * Holds mappings from session-id to {@link Session}
	 */
	private Map<String, Session> mSessionIdSessionMap;

	/**
	 * Construct a {@link SessionRepository}
	 */
	public SessionRepositoryImpl() {
		mClientIdSessionMap = new HashMap<ClientIdentifier, Session>();
		mChannelIdSessionMap = new HashMap<ChannelIdentifier, Session>();
		mSessionIdSessionMap = new HashMap<String, Session>();
	}


	@Override
	public Session getBy(ClientIdentifier clientId) {
		NullCheck.check(clientId, "clientId is null");
		return mClientIdSessionMap.get(clientId);
	}

	@Override
	public Session getBy(ChannelIdentifier channelId) {
		NullCheck.check(channelId, "channelId is null");
		return mChannelIdSessionMap.get(channelId);
	}

	@Override
	public Session getBy(String sessionId) {
		NullCheck.check(sessionId, "sessionId is null");
		return mSessionIdSessionMap.get(sessionId);
	}

	@Override
	public void store(Session session) throws AlreadyStoredException {
		Session stored = getSessionWithChecks(session);
		if (stored != null) {
			throw new AlreadyStoredException(session.toString() + " already stored");
		}

		mClientIdSessionMap.put(session.getClientIdentifier(), session);
	}

	@Override
	public void drop(Session session) throws SessionNotFoundException, StillMappedException {
		sanityCheckBeforeDropOf(session);
		Session stored = getSessionWithChecksExpectExists(session);
		mClientIdSessionMap.remove(stored.getClientIdentifier());
	}


	@Override
	public void map(Session session, String sessionId)
			throws SessionNotFoundException, AlreadyMappedException {
		Session stored = getSessionWithChecksExpectExists(session);
		Session mapped = getBy(sessionId);
		if (mapped != null) {
			throw new AlreadyMappedException(stored.toString() + " already mapped");
		}

		mSessionIdSessionMap.put(sessionId, session);
	}

	@Override
	public void unmap(Session session, String sessionId)
			throws SessionNotFoundException, NoMappingException {

		Session stored = getSessionWithChecksExpectExists(session);
		Session mapped = getBy(sessionId);
		if (mapped == null) {
			throw new NoMappingException(stored.toString() + " not mapped");
		}

		mSessionIdSessionMap.remove(sessionId);
	}

	@Override
	public void map(Session session, ChannelIdentifier channelId)
			throws SessionNotFoundException, AlreadyMappedException {
		Session stored = getSessionWithChecksExpectExists(session);
		Session mapped = getBy(channelId);
		if (mapped != null) {
			throw new AlreadyMappedException(stored.toString() + " already mapped");
		}

		mChannelIdSessionMap.put(channelId, session);
	}

	@Override
	public void unmap(Session session, ChannelIdentifier channelId)
			throws SessionNotFoundException, NoMappingException {

		Session stored = getSessionWithChecksExpectExists(session);
		Session mapped = getBy(channelId);
		if (mapped == null) {
			throw new NoMappingException(stored.toString() + " not mapped");
		}

		mChannelIdSessionMap.remove(channelId);
	}

	private Session getSessionWithChecks(Session session) {
		NullCheck.check(session, "session is null");
		ClientIdentifier clientId = session.getClientIdentifier();
		Session stored = getBy(clientId);
		return stored;
	}

	private Session getSessionWithChecksExpectExists(Session session) throws SessionNotFoundException {
		Session ret = getSessionWithChecks(session);
		if (ret == null) {
			throw new SessionNotFoundException(session.toString() + " not found in repository");
		}

		return ret;
	}

	/**
	 * Helper method that checks if the session-id and {@link ChannelIdentifier}
	 * mappings do not contain a reference to the given session.
	 *
	 * This is O(n) where n is the number of mappings using {@link ChannelIdentifier}
	 * and session-ids together.
	 *
	 * @param session
	 * @throws SessionNotFoundException
	 */
	private void sanityCheckBeforeDropOf(Session session) throws StillMappedException, SessionNotFoundException {
		Session stored = getSessionWithChecksExpectExists(session);
		for (Session cur : mChannelIdSessionMap.values()) {
			if (cur == stored) {
				throw new StillMappedException(session.toString() + " still mapped with a channelId");
			}
		}

		for (Session cur : mSessionIdSessionMap.values()) {
			if (cur == stored) {
				throw new StillMappedException(session.toString() + " still mapped with a session-id");
			}
		}
	}
}
