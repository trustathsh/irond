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


import junit.framework.TestCase;
import de.hshannover.f4.trust.iron.mapserver.communication.ChannelIdentifier;
import de.hshannover.f4.trust.iron.mapserver.communication.ClientIdentifier;
import de.hshannover.f4.trust.iron.mapserver.exceptions.AlreadyMappedException;
import de.hshannover.f4.trust.iron.mapserver.exceptions.AlreadyStoredException;
import de.hshannover.f4.trust.iron.mapserver.exceptions.NoMappingException;
import de.hshannover.f4.trust.iron.mapserver.exceptions.SessionNotFoundException;
import de.hshannover.f4.trust.iron.mapserver.exceptions.StillMappedException;

public class SessionRepositoryTest extends TestCase {

	private SessionRepository mSessionRep;
	private ClientIdentifier clientIdent1;
	private ClientIdentifier clientIdent11; // the same as client1
	private ClientIdentifier clientIdent2;

	private ChannelIdentifier channelIdent1;
	private ChannelIdentifier channelIdent11;
	private ChannelIdentifier channelIdent2;
	private String sessionId1 = "sessionid01";
	private String sessionId11= "sessionid01";
	private String sessionId2 = "sessionid02";

	@Override
	public void setUp() {
		mSessionRep = new SessionRepositoryImpl();

		clientIdent1 = new ClientIdentifier("client1");
		clientIdent11 = new ClientIdentifier("client1");
		clientIdent2 = new ClientIdentifier("client2");

		channelIdent1 = new ChannelIdentifier("192.168.0.1", 4321, 0);
		channelIdent11 = new ChannelIdentifier("192.168.0.1", 4321, 0);
		channelIdent2 = new ChannelIdentifier("192.168.0.1", 5321, 1);

		sessionId1 = "sessionid01";
		sessionId11= "sessionid01";
		sessionId2 = "sessionid02";
	}


	/**
	 * Create a session and store/remove it in/from the repository
	 */
	public void testSessionRepository_SimpleStore() {

		Session session = new Session(clientIdent1, "ABCDEFGH");
		try {
			mSessionRep.store(session);
		} catch (AlreadyStoredException e) {
			fail();
		}

		try {
			mSessionRep.store(session);
			fail();
		} catch (AlreadyStoredException e) {
			// on purpose
		}

		assertSame(mSessionRep.getBy(clientIdent1), session);
		assertSame(mSessionRep.getBy(clientIdent11), session);
		assertSame(mSessionRep.getBy(clientIdent1), mSessionRep.getBy(clientIdent11));

		try {
			mSessionRep.drop(session);
		} catch (SessionNotFoundException e) {
			fail();
		} catch (StillMappedException e) {
			fail();
		}

		try {
			mSessionRep.drop(session);
			fail();
		} catch (SessionNotFoundException e) {
			// on purpose
		} catch (StillMappedException e) {
			fail();
		}
		assertNull(mSessionRep.getBy(clientIdent1));
	}

	public void testSessionRepository_StoreCollision() {
		Session session1 = new Session(clientIdent1, "ABCD");
		Session session2 = new Session(clientIdent11, "EFGH");

		try {
			mSessionRep.store(session1);
		} catch (AlreadyStoredException e) {
			fail();
		}

		try {
			mSessionRep.store(session2);
			fail();
		} catch (AlreadyStoredException e) {
			// on purpose
		}

		try {
			mSessionRep.drop(session2);
		} catch (SessionNotFoundException e) {
			fail();
		} catch (StillMappedException e) {
			fail();
		}

		try {
			mSessionRep.drop(session1);
			fail();
		} catch (SessionNotFoundException e) {
			// on purpose
		} catch (StillMappedException e) {
			fail();
		}
	}

	public void testSessionRepository_MapChannelId() {

		Session session1 = new Session(clientIdent1, "ABCDEF");
		Session session2 = new Session(clientIdent11, "ABCDEF");
		Session session3 = new Session(clientIdent2, "ABCDEF");

		try {
			mSessionRep.store(session1);
		} catch (AlreadyStoredException e) {
			fail();
		}

		try {
			mSessionRep.map(session1, channelIdent1);
		} catch (SessionNotFoundException e) {
			fail();
		} catch (AlreadyMappedException e) {
			fail();
		}

		// try same the mapping again
		try {
			mSessionRep.map(session1, channelIdent1);
			fail();
		} catch (SessionNotFoundException e) {
			fail();
		} catch (AlreadyMappedException e) {
		}

		// try to make the mapping again, but use session2 this time,
		// which is basically the same as session1
		try {
			mSessionRep.map(session2, channelIdent1);
			fail();
		} catch (SessionNotFoundException e) {
			fail();
		} catch (AlreadyMappedException e) {
		}

		// try again, but with session1 and channelIdent11 (same as 1),
		// should also fail.
		try {
			mSessionRep.map(session1, channelIdent11);
			fail();
		} catch (SessionNotFoundException e) {
			fail();
		} catch (AlreadyMappedException e) {
		}

		// try with a session which is not in the repository at all
		try {
			mSessionRep.map(session3, channelIdent2);
			fail();
		} catch (SessionNotFoundException e) {
		} catch (AlreadyMappedException e) {
		}

		Session recv1 = mSessionRep.getBy(channelIdent1);
		Session recv11 = mSessionRep.getBy(channelIdent11);
		Session recv2 = mSessionRep.getBy(channelIdent2);
		assertNotNull(recv1);
		assertSame(recv1, recv11);
		assertNull(recv2);

		try {
			mSessionRep.drop(session1);
			fail();
		} catch (SessionNotFoundException e) {
			fail();
		} catch (StillMappedException e) {
			// yes, we still have mappings
		}

		try {
			mSessionRep.drop(session2);
			fail();
		} catch (SessionNotFoundException e) {
			fail();
		} catch (StillMappedException e) {
			// yes, we still have mappings
		}

		// this one isn't in the rep at all...
		try {
			mSessionRep.drop(session3);
			fail();
		} catch (SessionNotFoundException e) {
		} catch (StillMappedException e) {
			fail();
		}

		try {
			mSessionRep.unmap(session1, channelIdent1);
		} catch (SessionNotFoundException e) {
			fail();
		} catch (NoMappingException e) {
			fail();
		}

		try {
			mSessionRep.drop(session1);
		} catch (SessionNotFoundException e) {
			fail();
		} catch (StillMappedException e) {
			fail();
		}

		try {
			mSessionRep.drop(session1);
			fail();
		} catch (SessionNotFoundException e) {
			// shouldn't be there anymore
		} catch (StillMappedException e) {
			fail();
		}
	}

	/**
	 * The same as for the ChannelId but this time with SessionId
	 */
	public void testSessionRepository_MapSessionId() {

		Session session1 = new Session(clientIdent1, "ABCDEF");
		Session session2 = new Session(clientIdent11, "ABCDEF");
		Session session3 = new Session(clientIdent2, "ABCDEF");

		try {
			mSessionRep.store(session1);
		} catch (AlreadyStoredException e) {
			fail();
		}

		try {
			mSessionRep.map(session1, sessionId1);
		} catch (SessionNotFoundException e) {
			fail();
		} catch (AlreadyMappedException e) {
			fail();
		}

		// try same the mapping again
		try {
			mSessionRep.map(session1, sessionId1);
			fail();
		} catch (SessionNotFoundException e) {
			fail();
		} catch (AlreadyMappedException e) {
		}

		// try to make the mapping again, but use session2 this time,
		// which is basically the same as session1
		try {
			mSessionRep.map(session2, sessionId1);
			fail();
		} catch (SessionNotFoundException e) {
			fail();
		} catch (AlreadyMappedException e) {
		}

		// try again, but with session1 and channelIdent11 (same as 1),
		// should also fail.
		try {
			mSessionRep.map(session1, sessionId11);
			fail();
		} catch (SessionNotFoundException e) {
			fail();
		} catch (AlreadyMappedException e) {
		}

		// try with a session which is not in the repository at all
		try {
			mSessionRep.map(session3, sessionId2);
			fail();
		} catch (SessionNotFoundException e) {
		} catch (AlreadyMappedException e) {
		}

		Session recv1 = mSessionRep.getBy(sessionId1);
		Session recv11 = mSessionRep.getBy(sessionId11);
		Session recv2 = mSessionRep.getBy(sessionId2);
		assertNotNull(recv1);
		assertSame(recv1, recv11);
		assertNull(recv2);

		try {
			mSessionRep.drop(session1);
			fail();
		} catch (SessionNotFoundException e) {
			fail();
		} catch (StillMappedException e) {
			// yes, we still have mappings
		}

		try {
			mSessionRep.drop(session2);
			fail();
		} catch (SessionNotFoundException e) {
			fail();
		} catch (StillMappedException e) {
			// yes, we still have mappings
		}

		// this one isn't in the rep at all...
		try {
			mSessionRep.drop(session3);
			fail();
		} catch (SessionNotFoundException e) {
		} catch (StillMappedException e) {
			fail();
		}

		try {
			mSessionRep.unmap(session1, sessionId1);
		} catch (SessionNotFoundException e) {
			fail();
		} catch (NoMappingException e) {
			fail();
		}

		try {
			mSessionRep.drop(session1);
		} catch (SessionNotFoundException e) {
			fail();
		} catch (StillMappedException e) {
			fail();
		}

		try {
			mSessionRep.drop(session1);
			fail();
		} catch (SessionNotFoundException e) {
			// shouldn't be there anymore
		} catch (StillMappedException e) {
			fail();
		}
	}
}
