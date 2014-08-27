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
import de.fhhannover.inform.iron.mapserver.communication.bus.messages.ActionSeries;
import de.fhhannover.inform.iron.mapserver.communication.bus.messages.Event;
import de.fhhannover.inform.iron.mapserver.provider.ServerConfigurationProvider;
import de.fhhannover.inform.iron.mapserver.provider.StubProvider;

/**
 * Some simple tests to check what happens if a Poll is send on a SSRC
 * or a SSRC Op is send on an ARC.
 *
 * @author aw
 *
 */
public class ArcSsrcTest extends TestCase {

	private ServerConfigurationProvider mServerConf;
	private EventProcessor mEventProc = null;
	private Queue<Event> mEventQueue;
	private Queue<ActionSeries> mActionQueue;

	private ChannelIdentifier chSsrc, chArc1;
	private ClientIdentifier clId;

	// this session-id is provided by the stub implementation
	private static final String SESSION_ID = "0";


	@Override
	@Before
	public void setUp() {
		chSsrc = new ChannelIdentifier("192.168.0.1", 8888, 0);
		chArc1 = new ChannelIdentifier("192.168.0.1", 8889, 0);
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
	 * Create a new session. Run a poll on the SSRC. We expect a
	 * errorResult in the queue.
	 *
	 * @throws InterruptedException
	 */
	@Test
	public void testArcSsrc_PollOnSsrc() throws InterruptedException {
		Event newsession = TestEventCreator.createNewSessionRequest(clId, chSsrc, true);
		Event endsession = TestEventCreator.createEndSessionRequest(clId, chSsrc, SESSION_ID, false);
		Event poll1 = TestEventCreator.createPollRequest(clId, chSsrc, SESSION_ID, false);

		mEventQueue.put(newsession);
		ActionSeries as = mActionQueue.get();
		assertEquals(1, as.getActions().size());
		assertTrue(ResponseCheck.checkNewSessionResult(as.getActions().get(0), chSsrc));

		// this is the poll on the ssrc
		mEventQueue.put(poll1);

		as = mActionQueue.get();
		assertEquals(1, as.getActions().size());
		assertTrue(ResponseCheck.checkErrorResponse("errorResult",
				as.getActions().get(0), chSsrc));

		// should still work
		mEventQueue.put(endsession);
		as = mActionQueue.get();
		assertEquals(1, as.getActions().size());
		assertTrue(ResponseCheck.checkEndSessionResult(as.getActions().get(0), chSsrc));
	}

	/**
	 * Create a new session, a subscription, create a ARC and get the first
	 * poll result. Then run a renewSession on the ARC. Should result in a
	 * errorResult, but nothing more.
	 *
	 * @throws InterruptedException
	 */
	public void testArc_DoubleArcWhenNotPending() throws InterruptedException {
		Event newsessionreq = TestEventCreator.createNewSessionRequest(clId, chSsrc, true);
		String subUpdate = TestEventCreator.createSubscrbeUpdateElement(
				"testsub", "filter1", "5", null, null, "filter2", IDENTIFIER);
		String subDelete = TestEventCreator.createSubscrbeDeleteElement("testsub");
		Event subscribeUp = TestEventCreator.createSubscribeRequest(clId, chSsrc,
				SESSION_ID, subUpdate, false);
		Event subscribeDel = TestEventCreator.createSubscribeRequest(clId, chSsrc,
				SESSION_ID, subDelete, false);
		Event endsessionreq = TestEventCreator.createEndSessionRequest(clId, chSsrc,
				SESSION_ID, false);
		Event poll1 = TestEventCreator.createPollRequest(clId, chArc1, SESSION_ID, true);

		Event renewreq = TestEventCreator.createRenewSessionRequest(clId, chArc1,
				SESSION_ID, false);

		mEventQueue.put(newsessionreq);
		ActionSeries as = mActionQueue.get();
		assertEquals(1, as.getActions().size());
		assertTrue(ResponseCheck.checkNewSessionResult(as.getActions().get(0), chSsrc));

		// create a subscription
		mEventQueue.put(subscribeUp);
		as = mActionQueue.get();
		assertEquals(1, as.getActions().size());
		assertTrue(ResponseCheck.checkSubscribeReceived(as.getActions().get(0), chSsrc));

		// this should return us the first poll result including the search result
		mEventQueue.put(poll1);
		as = mActionQueue.get();
		assertEquals(1, as.getActions().size());
		assertTrue(ResponseCheck.checkPollResult(as.getActions().get(0), chArc1));


		// delete the subscription
		mEventQueue.put(subscribeDel);

		// subscribe received
		ResponseCheck.checkSubscribeReceived(mActionQueue.get().getActions().get(0),
				chSsrc);

		// put the renewSessionRequest on the ARC into the queue, we expect
		// a errorResult
		mEventQueue.put(renewreq);

		as = mActionQueue.get();

		assertEquals(1, as.getActions().size());
		assertTrue(ResponseCheck.checkErrorResponse("errorResult",
				as.getActions().get(0),  chArc1));

		mEventQueue.put(endsessionreq);
		as = mActionQueue.get();
		assertEquals(1, as.getActions().size());
		assertTrue(ResponseCheck.checkEndSessionResult(as.getActions().get(0),chSsrc));
	}

	private static final String IDENTIFIER =
		"<device>" + "<name>devName</name>" + "</device>";
}
