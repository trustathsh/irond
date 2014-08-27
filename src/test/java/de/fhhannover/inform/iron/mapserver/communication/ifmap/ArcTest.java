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
 * This file is part of irond, version 0.4.2, implemented by the Trust@HsH
 * research group at the Hochschule Hannover.
 * %%
 * Copyright (C) 2010 - 2014 Trust@HsH
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
package de.fhhannover.inform.iron.mapserver.communication.ifmap;


import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.fhhannover.inform.iron.mapserver.communication.ChannelIdentifier;
import de.fhhannover.inform.iron.mapserver.communication.ClientIdentifier;
import de.fhhannover.inform.iron.mapserver.communication.bus.Queue;
import de.fhhannover.inform.iron.mapserver.communication.bus.messages.Action;
import de.fhhannover.inform.iron.mapserver.communication.bus.messages.ActionSeries;
import de.fhhannover.inform.iron.mapserver.communication.bus.messages.Event;
import de.fhhannover.inform.iron.mapserver.communication.bus.messages.SendResponseAction;
import de.fhhannover.inform.iron.mapserver.messages.ErrorCode;
import de.fhhannover.inform.iron.mapserver.provider.ServerConfigurationProvider;
import de.fhhannover.inform.iron.mapserver.provider.StubProvider;

public class ArcTest extends TestCase {

	private ServerConfigurationProvider mServerConf;
	private EventProcessor mEventProc = null;
	private Queue<Event> mEventQueue;
	private Queue<ActionSeries> mActionQueue;

	private ChannelIdentifier chSsrc1, chArc1, chArc2;
	private ClientIdentifier clId;

	// this session-id is provided by the stub implementation
	private static final String SESSION_ID = "0";


	@Override
	@Before
	public void setUp() {
		chSsrc1 = new ChannelIdentifier("192.168.0.1", 8888, 0);
		chArc1 = new ChannelIdentifier("192.168.0.1", 8890, 0);
		chArc2 = new ChannelIdentifier("192.168.0.1", 8891, 0);
		clId = new ClientIdentifier("theClient");
		mServerConf = StubProvider.getServerConfStub(1);
		mEventQueue = new Queue<Event>();
		mActionQueue = new Queue<ActionSeries>();
		mEventProc = EventProcessorSetup.setUpEventProcessor(mServerConf,
		mEventQueue, mActionQueue);
		mEventProc.start();

	}

	@Override
	@After
	public void tearDown() {
		mEventProc.stop();
	}

	/**
	 * Simulate a endSession due to opening a ARC twice.
	 *
	 * We expect a endSession result on the first ARC and a InvalidSessionId
	 * error on the second ARC.
	 * A endSession using the SSRC afterwards should fail with a InvalidSessionId
	 *
	 * @throws InterruptedException
	 */
	@Test
	public void testArc_DoubleArcWhenPending() throws InterruptedException {
		Event newsession = TestEventCreator.createNewSessionRequest(clId, chSsrc1, true);
		Event endsession = TestEventCreator.createEndSessionRequest(clId, chSsrc1, SESSION_ID, false);
		Event poll1 = TestEventCreator.createPollRequest(clId, chArc1, SESSION_ID, true);
		Event poll2 = TestEventCreator.createPollRequest(clId, chArc2, SESSION_ID, true);

		mEventQueue.put(newsession);
		ActionSeries as = mActionQueue.get();
		assertEquals(1, as.getActions().size());
		assertTrue(ResponseCheck.checkNewSessionResult(as.getActions().get(0), chSsrc1));

		mEventQueue.put(poll1);
		mEventQueue.put(poll2);

		as = mActionQueue.get();
		assertEquals(2, as.getActions().size());

		for (Action a : as.getActions()) {
			if (a instanceof SendResponseAction) {
				SendResponseAction sra = (SendResponseAction) a;
				if (sra.getChannelIdentifier().equals(chArc1)) {
					assertTrue(ResponseCheck.checkEndSessionResult(sra, chArc1));
				} else if (sra.getChannelIdentifier().equals(chArc2)) {
					assertTrue(ResponseCheck.checkErrorResponse(
							ErrorCode.InvalidSessionID.toString(), sra, chArc2));
				} else {
					fail();
				}
			} else {
				fail();
			}
		}

		mEventQueue.put(endsession);
		as = mActionQueue.get();
		assertEquals(1, as.getActions().size());
		assertTrue(ResponseCheck.checkErrorResponse(
				ErrorCode.InvalidSessionID.toString(), as.getActions().get(0), chSsrc1));
	}

	/**
	 * The ARC is closed while a poll is pending. This should end in an automatic
	 * endSession. So the endSession on the SSRC will fail.
	 *
	 * @throws InterruptedException
	 */
	@Test
	public void testArc_CloseWhenPending() throws InterruptedException {
		Event newsession = TestEventCreator.createNewSessionRequest(clId, chSsrc1,
				true);
		Event endsession = TestEventCreator.createEndSessionRequest(clId, chSsrc1,
				SESSION_ID, false);
		Event poll1 = TestEventCreator.createPollRequest(clId, chArc1, SESSION_ID,
				true);
		Event closedArc = TestEventCreator.createClosedChannelEvent(chArc1);

		mEventQueue.put(newsession);
		ActionSeries as = mActionQueue.get();
		assertEquals(1, as.getActions().size());
		assertTrue(ResponseCheck.checkNewSessionResult(as.getActions().get(0), chSsrc1));

		mEventQueue.put(poll1);

		mEventQueue.put(closedArc);

		mEventQueue.put(endsession);
		as = mActionQueue.get();
		assertEquals(1, as.getActions().size());
		assertTrue(ResponseCheck.checkErrorResponse(
				ErrorCode.InvalidSessionID.toString(), as.getActions().get(0),
				chSsrc1));
	}

	/**
	 * After one successful poll we don't close the ARC but leave it open.
	 * Then start another poll on a second ARC. This is allowed. However,
	 * polling on the old connection is disallowed. Try this in the end.
	 *
	 * @throws InterruptedException
	 */
	public void testArc_DoubleArcWhenNotPending() throws InterruptedException {
		Event newsessionreq = TestEventCreator.createNewSessionRequest(clId,
				chSsrc1, true);
		String subUpdate = TestEventCreator.createSubscrbeUpdateElement(
				"testsub", "filter1", "5", null, null, "filter2", IDENTIFIER);
		Event subscribeUp1 = TestEventCreator.createSubscribeRequest(clId, chSsrc1,
				SESSION_ID, subUpdate, false);
		Event subscribeUp2 = TestEventCreator.createSubscribeRequest(clId, chSsrc1,
				SESSION_ID, subUpdate, false);
		Event subscribeUp3 = TestEventCreator.createSubscribeRequest(clId, chSsrc1,
				SESSION_ID, subUpdate, false);
		Event endsessionreq = TestEventCreator.createEndSessionRequest(clId, chSsrc1,
				SESSION_ID, false);
		Event poll1 = TestEventCreator.createPollRequest(clId, chArc1, SESSION_ID,
				true);
		Event poll1old = TestEventCreator.createPollRequest(clId, chArc1, SESSION_ID,
				false);
		Event poll2 = TestEventCreator.createPollRequest(clId, chArc2, SESSION_ID,
				true);

		mEventQueue.put(newsessionreq);
		ActionSeries as = mActionQueue.get();
		assertEquals(1, as.getActions().size());
		assertTrue(ResponseCheck.checkNewSessionResult(as.getActions().get(0), chSsrc1));

		// create a subscription
		mEventQueue.put(subscribeUp1);
		as = mActionQueue.get();
		assertEquals(1, as.getActions().size());
		assertTrue(ResponseCheck.checkSubscribeReceived(as.getActions().get(0), chSsrc1));

		// this should return us the first poll result including the search result
		mEventQueue.put(poll1);
		as = mActionQueue.get();
		assertEquals(1, as.getActions().size());
		assertTrue(ResponseCheck.checkPollResult(as.getActions().get(0), chArc1));

		// update the subscription
		mEventQueue.put(subscribeUp2);
		as = mActionQueue.get();
		assertEquals(1, as.getActions().size());
		assertTrue(ResponseCheck.checkSubscribeReceived(as.getActions().get(0), chSsrc1));

		// chArc1 is still opened (no closed channel event), we change arc to
		// chArc2
		mEventQueue.put(poll2);

		// first poll result for new subscription
		as = mActionQueue.get();
		assertEquals(1, as.getActions().size());
		assertTrue(ResponseCheck.checkPollResult(as.getActions().get(0), chArc2));

		// update the subscription once again
		mEventQueue.put(subscribeUp3);
		as = mActionQueue.get();
		assertEquals(1, as.getActions().size());
		assertTrue(ResponseCheck.checkSubscribeReceived(as.getActions().get(0), chSsrc1));


		// poll with old channel, this should fail
		mEventQueue.put(poll1old);
		as = mActionQueue.get();
		assertEquals(1, as.getActions().size());
		assertTrue(ResponseCheck.checkErrorResponse("errorResult",
				as.getActions().get(0), chArc1));

		mEventQueue.put(endsessionreq);
		as = mActionQueue.get();
		assertEquals(1, as.getActions().size());
		assertTrue(ResponseCheck.checkEndSessionResult(as.getActions().get(0),chSsrc1));
	}

	@Test
	public void testArc_BadEvenWhenPending() throws InterruptedException {
		Event newsession = TestEventCreator.createNewSessionRequest(clId, chSsrc1,
				true);
		Event endsession = TestEventCreator.createEndSessionRequest(clId, chSsrc1,
				SESSION_ID, false);
		Event poll1 = TestEventCreator.createPollRequest(clId, chArc1, SESSION_ID,
				true);

		Event badArc = TestEventCreator.createBadChannelEvent(chArc1);

		mEventQueue.put(newsession);
		ActionSeries as = mActionQueue.get();
		assertEquals(1, as.getActions().size());
		assertTrue(ResponseCheck.checkNewSessionResult(as.getActions().get(0), chSsrc1));

		mEventQueue.put(poll1);
		mEventQueue.put(badArc);

		mEventQueue.put(endsession);

		as = mActionQueue.get();
		assertEquals(1, as.getActions().size());
		assertTrue(ResponseCheck.checkErrorResponse(
				ErrorCode.InvalidSessionID.toString(), as.getActions().get(0),
				chSsrc1));
	}

	private static final String IDENTIFIER =
		"<device>" + "<name>devName</name>" + "</device>";
}
