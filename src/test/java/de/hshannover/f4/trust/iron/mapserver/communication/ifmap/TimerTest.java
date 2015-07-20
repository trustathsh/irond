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
package de.hshannover.f4.trust.iron.mapserver.communication.ifmap;


import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.hshannover.f4.trust.iron.mapserver.communication.ChannelIdentifier;
import de.hshannover.f4.trust.iron.mapserver.communication.ClientIdentifier;
import de.hshannover.f4.trust.iron.mapserver.communication.bus.Queue;
import de.hshannover.f4.trust.iron.mapserver.communication.bus.messages.Action;
import de.hshannover.f4.trust.iron.mapserver.communication.bus.messages.ActionSeries;
import de.hshannover.f4.trust.iron.mapserver.communication.bus.messages.Event;
import de.hshannover.f4.trust.iron.mapserver.communication.bus.messages.SendResponseAction;
import de.hshannover.f4.trust.iron.mapserver.messages.ErrorCode;
import de.hshannover.f4.trust.iron.mapserver.provider.ServerConfigurationProvider;
import de.hshannover.f4.trust.iron.mapserver.provider.StubProvider;

/**
 * Try to see if the timer stuff works as expected.
 * We use some Thread.sleep() here... might not work everywhere as expected :-/
 *
 */
public class TimerTest extends TestCase {

	private ServerConfigurationProvider mServerConf;
	private EventProcessor mEventProc = null;
	private Queue<Event> mEventQueue;
	private Queue<ActionSeries> mActionQueue;

	private ChannelIdentifier chSsrc;
	private ChannelIdentifier chArc;
	private ClientIdentifier clId;

	private static int TIME_OUT_MILIS = 200;
	private static int SPAN_TIME_MILIS = 100;

	private static final String SESSION_ID = "0";

	@Override
	@Before
	public void setUp() {
		chSsrc = new ChannelIdentifier("192.168.0.1", 8888, 0);
		chArc = new ChannelIdentifier("192.168.0.1", 8889, 0);
		clId = new ClientIdentifier("theClient");
		mServerConf = StubProvider.getServerConfStub(TIME_OUT_MILIS);
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
	 * Create a new session, wait some time longer than TIME_OUT_SEC,
	 * run endSession. Expect InvalidSessionID in the queue.
	 * @throws InterruptedException
	 */
	@Test
	public void testTimer_Timeout() throws InterruptedException {
		Event newsessionreq = TestEventCreator.createNewSessionRequest(clId,
				chSsrc, true);
		Event closechannel = TestEventCreator.createClosedChannelEvent(chSsrc);
		// hmm... we use the stub, so the first session-id is 0... don't worry...
		Event endsessionreq = TestEventCreator.createEndSessionRequest(clId,
				chSsrc, SESSION_ID, true);
		mEventQueue.put(newsessionreq);

		ActionSeries as = mActionQueue.get();
		assertEquals(1, as.getActions().size());
		assertTrue(ResponseCheck.checkNewSessionResult(as.getActions().get(0), chSsrc));

		// notify the eventprocessor that the channel was closed
		mEventQueue.put(closechannel);

		// sleep some time
		Thread.sleep(TIME_OUT_MILIS + SPAN_TIME_MILIS);
		mEventQueue.put(endsessionreq);

		// this should be a InvalidSessionID result, because a timeout happend
		as = mActionQueue.get();
		assertEquals(1, as.getActions().size());
		assertTrue(ResponseCheck.checkErrorResponse(ErrorCode.InvalidSessionID.toString(),
				as.getActions().get(0),chSsrc));
	}

	/**
	 * Create a new session, wait some time shorter than TIME_OUT_SEC,
	 * run endSession. Expect endSessionResult in the queue.
	 * @throws InterruptedException
	 */
	@Test
	public void testTimer_NoTimeout() throws InterruptedException {
		Event newsessionreq = TestEventCreator.createNewSessionRequest(clId,
				chSsrc, true);
		Event closechannel = TestEventCreator.createClosedChannelEvent(chSsrc);
		// hmm... we use the stub, so the first session-id is 0... don't worry...
		Event endsessionreq = TestEventCreator.createEndSessionRequest(clId,
				chSsrc, SESSION_ID, true);
		mEventQueue.put(newsessionreq);

		ActionSeries as = mActionQueue.get();
		assertEquals(1, as.getActions().size());
		assertTrue(ResponseCheck.checkNewSessionResult(as.getActions().get(0),
				chSsrc));

		// notify the eventprocessor that the channel was closed
		mEventQueue.put(closechannel);

		// sleep some time
		Thread.sleep(TIME_OUT_MILIS - SPAN_TIME_MILIS);
		mEventQueue.put(endsessionreq);

		// this should be a endSessionResult, because a no timeout should happen
		as = mActionQueue.get();
		assertEquals(1, as.getActions().size());
		assertTrue(ResponseCheck.checkEndSessionResult(as.getActions().get(0),chSsrc));
	}

	/**
	 * Run newsession, close channel, run a number of renewSession requests and
	 * close the channel after each renewSession. Simulates keeping a session
	 * alive using renewSession. At the end do an endSession and expect an
	 * endSessionResult.
	 *
	 * @throws InterruptedException
	 */
	@Test
	public void testTimer_RenewSession() throws InterruptedException {
		int c = 0;
		Event newsessionreq = TestEventCreator.createNewSessionRequest(clId,
				chSsrc, true);
		Event closechannel = TestEventCreator.createClosedChannelEvent(chSsrc);
		// hmm... we use the stub, so the first session-id is 0... don't worry...
		Event endsessionreq = TestEventCreator.createEndSessionRequest(
				clId, chSsrc, SESSION_ID, true);
		mEventQueue.put(newsessionreq);

		ActionSeries as = mActionQueue.get();
		assertEquals(1, as.getActions().size());
		assertTrue(ResponseCheck.checkNewSessionResult(as.getActions().get(0), chSsrc));

		// notify the eventprocessor that the channel was closed
		mEventQueue.put(closechannel);
		while (c < 3) {
			Thread.sleep(TIME_OUT_MILIS - SPAN_TIME_MILIS);
			Event renewsession = TestEventCreator.createRenewSessionRequest(
					clId, chSsrc, SESSION_ID, true);
			mEventQueue.put(renewsession);
			as = mActionQueue.get();
			assertEquals(1, as.getActions().size());
			assertTrue(ResponseCheck.checkRenewSessionResult(as.getActions().get(0),chSsrc));
			mEventQueue.put(closechannel);
			c++;
		}
		Thread.sleep(TIME_OUT_MILIS - SPAN_TIME_MILIS);
		// sleep some time
		mEventQueue.put(endsessionreq);
		// this should be a endSessionResult, because a no timeout should happen
		as = mActionQueue.get();
		assertEquals(1, as.getActions().size());
		assertTrue(ResponseCheck.checkEndSessionResult(as.getActions().get(0),chSsrc));
	}

	/**
	 * create a new session, close the channel, timer starts. cancel timer
	 * by doing one renewsession. don't close the channel again.
	 * Wait TIME_OUT_SEC + SPAN. Do endSession, expect endSessionResult.
	 */
	@Test
	public void testTimer_CancelOnce() throws InterruptedException {
		Event newsessionreq = TestEventCreator.createNewSessionRequest(clId,
				chSsrc, true);
		Event closechannel = TestEventCreator.createClosedChannelEvent(chSsrc);
		// hmm... we use the stub, so the first session-id is 0... don't worry...
		Event endsessionreq = TestEventCreator.createEndSessionRequest(clId,
				chSsrc, SESSION_ID, false);

		mEventQueue.put(newsessionreq);
		ActionSeries as = mActionQueue.get();
		assertEquals(1, as.getActions().size());
		assertTrue(ResponseCheck.checkNewSessionResult(as.getActions().get(0), chSsrc));

		// notify the eventprocessor that the channel was closed
		mEventQueue.put(closechannel);

		Thread.sleep(TIME_OUT_MILIS - SPAN_TIME_MILIS);
		Event renewsession = TestEventCreator.createRenewSessionRequest(clId,
				chSsrc, SESSION_ID, true);
		mEventQueue.put(renewsession);
		as = mActionQueue.get();
		assertEquals(1, as.getActions().size());
		assertTrue(ResponseCheck.checkRenewSessionResult(as.getActions().get(0),chSsrc));


		Thread.sleep(TIME_OUT_MILIS + SPAN_TIME_MILIS);

		mEventQueue.put(endsessionreq);
		// this should be a endSessionResult, because a no timeout should happen
		as = mActionQueue.get();
		assertEquals(1, as.getActions().size());
		assertTrue(ResponseCheck.checkEndSessionResult(as.getActions().get(0),chSsrc));
	}

	/**
	 * create a new session, start a poll, close the SSRC, wait for TIME_OUT + SPAN
	 * end the session using another SSRC request.
	 * Results in endSessionResult on SSRC and ARC
	 *
	 * @throws InterruptedException
	 */
	@Test
	public void testTimer_NoTimerWithActivePoll() throws InterruptedException {
		Event newsessionreq = TestEventCreator.createNewSessionRequest(
				clId, chSsrc, true);
		Event closechannel = TestEventCreator.createClosedChannelEvent(chSsrc);
		Event endsessionreq = TestEventCreator.createEndSessionRequest(
				clId, chSsrc, SESSION_ID, true);
		Event poll = TestEventCreator.createPollRequest(clId, chArc, SESSION_ID,
				true);

		mEventQueue.put(newsessionreq);
		ActionSeries as = mActionQueue.get();
		assertEquals(1, as.getActions().size());
		assertTrue(ResponseCheck.checkNewSessionResult(as.getActions().get(0), chSsrc));

		mEventQueue.put(poll);
		mEventQueue.put(closechannel);

		Thread.sleep(TIME_OUT_MILIS + SPAN_TIME_MILIS);

		mEventQueue.put(endsessionreq);

		as = mActionQueue.get();
		assertEquals(2, as.getActions().size());

		for (Action a : as.getActions()) {
			if (a instanceof SendResponseAction) {
				SendResponseAction sra = (SendResponseAction) a;
				if (sra.getChannelIdentifier().equals(chArc)) {
					assertTrue(ResponseCheck.checkEndSessionResult(sra, chArc));
				} else if (sra.getChannelIdentifier().equals(chSsrc)) {
					assertTrue(ResponseCheck.checkEndSessionResult(sra, chSsrc));
				} else {
					fail();
				}
			} else {
				fail();
			}
		}
	}

	/**
	 * If a client opened a ARC and used it for a poll, the TCP connection
	 * of this ARC may still be opened. We don't have to start a timer in
	 * this case.
	 *
	 * TODO: This is going to fail, because it is not implemented that way.
	 * @throws InterruptedException
	 */
	public void testTimer_NoTimerIfArcOpen() throws InterruptedException {
		Event newsessionreq = TestEventCreator.createNewSessionRequest(
				clId, chSsrc, true);
		String subContent = TestEventCreator.createSubscrbeUpdateElement(
				"testsub", "filter1", "5", null, null, "filter2", IDENTIFIER);
		Event subscribe = TestEventCreator.createSubscribeRequest(clId, chSsrc,
				SESSION_ID, subContent, false);
		Event closessrc = TestEventCreator.createClosedChannelEvent(chSsrc);
		Event endsessionreq = TestEventCreator.createEndSessionRequest(
				clId, chSsrc, SESSION_ID, true);
		Event poll = TestEventCreator.createPollRequest(clId, chArc, SESSION_ID,
				true);

		mEventQueue.put(newsessionreq);
		ActionSeries as = mActionQueue.get();
		assertEquals(1, as.getActions().size());
		assertTrue(ResponseCheck.checkNewSessionResult(as.getActions().get(0), chSsrc));

		// create a subscription
		mEventQueue.put(subscribe);
		as = mActionQueue.get();
		assertEquals(1, as.getActions().size());
		assertTrue(ResponseCheck.checkSubscribeReceived(as.getActions().get(0), chSsrc));

		// this should return us the first poll result including the search result
		mEventQueue.put(poll);
		as = mActionQueue.get();
		assertEquals(1, as.getActions().size());
		assertTrue(ResponseCheck.checkPollResult(as.getActions().get(0), chArc));

		// we don't have a poll pending, but the ARC is still open.
		// close the SSRC and wait TIME_OUT + SPAN and see whether the
		// session is still open
		mEventQueue.put(closessrc);

		Thread.sleep(TIME_OUT_MILIS + SPAN_TIME_MILIS);
		mEventQueue.put(endsessionreq);
		// this should be a endSessionResult, because a no timeout should happen
		// because of the ARC that was still open
		as = mActionQueue.get();
		assertEquals(1, as.getActions().size());
		assertTrue(ResponseCheck.checkEndSessionResult(as.getActions().get(0),chSsrc));
	}

	/**
	 * Same as above, but this time wait some time before closing the ARC:
	 * do a poll, leave the ARC open.
	 * close the SSRC.
	 * wait TIME_OUT + SPAN
	 * try renewsession (should work because the ARC is still open)
	 * close ARC
	 * wait TIME_OUT + SPAN
	 * try endSession (shouldn't work because no channel was open)
	 *
	 *
	 * @throws InterruptedException
	 */
	@Test
	public void testTimer_NoTimerUntilArcClosed() throws InterruptedException {
		Event newsessionreq = TestEventCreator.createNewSessionRequest(clId,
				chSsrc, true);
		String subContent = TestEventCreator.createSubscrbeUpdateElement(
				"testsub", "filter1", "5", null, null, "filter2", IDENTIFIER);
		Event subscribe = TestEventCreator.createSubscribeRequest(clId, chSsrc,
				SESSION_ID, subContent, false);
		Event closessrc = TestEventCreator.createClosedChannelEvent(chSsrc);
		Event closearc = TestEventCreator.createClosedChannelEvent(chArc);
		Event endsessionreq = TestEventCreator.createEndSessionRequest(
				clId, chSsrc, SESSION_ID, true);
		Event poll = TestEventCreator.createPollRequest(clId, chArc, SESSION_ID,
				true);
		Event renewsession = TestEventCreator.createRenewSessionRequest(clId,
				chSsrc, SESSION_ID, true);

		mEventQueue.put(newsessionreq);
		ActionSeries as = mActionQueue.get();
		assertEquals(1, as.getActions().size());
		assertTrue(ResponseCheck.checkNewSessionResult(as.getActions().get(0), chSsrc));

		// create a subscription
		mEventQueue.put(subscribe);
		as = mActionQueue.get();
		assertEquals(1, as.getActions().size());
		assertTrue(ResponseCheck.checkSubscribeReceived(as.getActions().get(0), chSsrc));

		// this should return us the first poll result including the search result
		mEventQueue.put(poll);
		as = mActionQueue.get();
		assertEquals(1, as.getActions().size());
		assertTrue(ResponseCheck.checkPollResult(as.getActions().get(0), chArc));

		// we don't have a poll pending, but the ARC is still open.
		// close the SSRC and wait TIME_OUT + SPAN and see whether the
		// session is still open
		mEventQueue.put(closessrc);

		Thread.sleep(TIME_OUT_MILIS + SPAN_TIME_MILIS);
		mEventQueue.put(renewsession);

		// no timeout should have occurred because ARC is still open
		as = mActionQueue.get();
		assertEquals(1, as.getActions().size());
		assertTrue(ResponseCheck.checkRenewSessionResult(as.getActions().get(0),chSsrc));

		// close the ARC and the SSRC
		mEventQueue.put(closearc);
		mEventQueue.put(closessrc);

		Thread.sleep(TIME_OUT_MILIS + SPAN_TIME_MILIS);
		mEventQueue.put(endsessionreq);

		// timer has expired
		as = mActionQueue.get();
		assertEquals(1, as.getActions().size());
		assertTrue(ResponseCheck.checkErrorResponse(ErrorCode.InvalidSessionID.toString(),
				as.getActions().get(0),chSsrc));
	}

	/**
	 * Same as with the ARC, but this time the SSRC is closed last.
	 *
	 * @throws InterruptedException
	 */
	@Test
	public void testTimer_NoTimerUntilSsrcClosed() throws InterruptedException {
		Event newsessionreq = TestEventCreator.createNewSessionRequest(clId, chSsrc,
				true);
		String subContent = TestEventCreator.createSubscrbeUpdateElement(
				"testsub", "filter1", "5", null, null, "filter2", IDENTIFIER);
		Event subscribe = TestEventCreator.createSubscribeRequest(clId, chSsrc,
				SESSION_ID, subContent, false);
		Event closessrc = TestEventCreator.createClosedChannelEvent(chSsrc);
		Event closearc = TestEventCreator.createClosedChannelEvent(chArc);
		Event endsessionreq = TestEventCreator.createEndSessionRequest(clId,
				chSsrc, SESSION_ID, true);
		Event poll = TestEventCreator.createPollRequest(clId, chArc, SESSION_ID, true);
		Event renewsession = TestEventCreator.createRenewSessionRequest(clId,
				chSsrc, SESSION_ID, true);

		mEventQueue.put(newsessionreq);
		ActionSeries as = mActionQueue.get();
		assertEquals(1, as.getActions().size());
		assertTrue(ResponseCheck.checkNewSessionResult(as.getActions().get(0), chSsrc));

		// create a subscription
		mEventQueue.put(subscribe);
		as = mActionQueue.get();
		assertEquals(1, as.getActions().size());
		assertTrue(ResponseCheck.checkSubscribeReceived(as.getActions().get(0), chSsrc));

		// this should return us the first poll result including the search result
		mEventQueue.put(poll);
		as = mActionQueue.get();
		assertEquals(1, as.getActions().size());
		assertTrue(ResponseCheck.checkPollResult(as.getActions().get(0), chArc));

		// close the ARC, this shouldn't start a timer, because the SSRC is
		// still open.
		mEventQueue.put(closearc);

		Thread.sleep(TIME_OUT_MILIS + SPAN_TIME_MILIS);
		mEventQueue.put(renewsession);

		// no timeout should have occurred because SSRC is still open
		as = mActionQueue.get();
		assertEquals(1, as.getActions().size());
		assertTrue(ResponseCheck.checkRenewSessionResult(as.getActions().get(0),chSsrc));

		// close the SSRC
		mEventQueue.put(closessrc);

		Thread.sleep(TIME_OUT_MILIS + SPAN_TIME_MILIS);
		mEventQueue.put(endsessionreq);

		// timer has expired
		as = mActionQueue.get();
		assertEquals(1, as.getActions().size());
		assertTrue(ResponseCheck.checkErrorResponse(ErrorCode.InvalidSessionID.toString(),
				as.getActions().get(0),chSsrc));
	}

	private static final String IDENTIFIER =
		"<device>" + "<name>devName</name>" + "</device>";
}
