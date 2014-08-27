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
package de.hshannover.f4.trust.iron.mapserver.communication.ifmap;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import de.hshannover.f4.trust.iron.mapserver.communication.ChannelIdentifier;
import de.hshannover.f4.trust.iron.mapserver.communication.ClientIdentifier;
import de.hshannover.f4.trust.iron.mapserver.communication.bus.Queue;
import de.hshannover.f4.trust.iron.mapserver.communication.bus.messages.Action;
import de.hshannover.f4.trust.iron.mapserver.communication.bus.messages.ActionSeries;
import de.hshannover.f4.trust.iron.mapserver.communication.bus.messages.Event;
import de.hshannover.f4.trust.iron.mapserver.provider.ServerConfigurationProvider;
import de.hshannover.f4.trust.iron.mapserver.provider.StubProvider;

/**
 * Check some SSRC behavior if using old/new channels.
 * This is too pathological, and maybe we should just ignore
 * the {@link #testDenyIfClosed()} and {@link #testNewSessionOldChannel()}
 * cases, because not even the test-suite cares about them...
 *
 * @author aw
 *
 */
public class SsrcTest {
	private ServerConfigurationProvider mServerConf;
	private EventProcessor mEventProc = null;
	private Queue<Event> mEventQueue;
	private Queue<ActionSeries> mActionQueue;

	private ChannelIdentifier ssrc1, ssrc2;
	private ClientIdentifier clId;

	// this session-id is provided by the stub implementation
	private static final String SESSION_ID = "0";

	@Before
	public void setUp() {
		ssrc1 = new ChannelIdentifier("192.168.0.1", 8888, 0);
		ssrc2 = new ChannelIdentifier("192.168.0.1", 8888, 1);
		clId = new ClientIdentifier("theClient");
		mServerConf = StubProvider.getServerConfStub(500);
		mEventQueue = new Queue<Event>();
		mActionQueue = new Queue<ActionSeries>();
		mEventProc = EventProcessorSetup.setUpEventProcessor(mServerConf,
				mEventQueue, mActionQueue);
		mEventProc.start();
	}

	@After
	public void tearDown() {
		mEventProc.stop();
	}

	/**
	 * Run a newSession with ssrc1 and then a renewSession with ssrc2.
	 * Expect an error if renewSession is done with ssrc1 again.
	 * The session should stay active, however
	 *
	 * @throws InterruptedException
	 */
	@Test
	public void testDenyOldSsrc() throws InterruptedException {
		ActionSeries as;
		Action a;

		// new session using ssrc1
		mEventQueue.put(TestEventCreator.createNewSessionRequest(clId, ssrc1, true));
		as = mActionQueue.get();
		assertEquals(1, as.getActions().size());
		a = as.getActions().get(0);
		assertTrue(ResponseCheck.checkNewSessionResult(a, ssrc1));

		// switch to ssrc2
		mEventQueue.put(TestEventCreator.createRenewSessionRequest(clId, ssrc2, SESSION_ID, true));
		as = mActionQueue.get();
		assertEquals(1, as.getActions().size());
		a = as.getActions().get(0);
		assertTrue(ResponseCheck.checkRenewSessionResult(a, ssrc2));

		// try ssrc1 again
		mEventQueue.put(TestEventCreator.createRenewSessionRequest(clId, ssrc1, SESSION_ID, false));
		as = mActionQueue.get();
		assertEquals(1, as.getActions().size());
		a = as.getActions().get(0);
		assertTrue(ResponseCheck.checkErrorResponse("AccessDenied", a, ssrc1));

		// do endSession on ssrc2
		mEventQueue.put(TestEventCreator.createEndSessionRequest(clId, ssrc2, SESSION_ID, false));
		as = mActionQueue.get();
		assertEquals(1, as.getActions().size());
		a = as.getActions().get(0);
		assertTrue(ResponseCheck.checkEndSessionResult(a, ssrc2));
	}

	/**
	 * Run newSession, switch channel, close the current channel, try to
	 * use the channel which was used to do the newSession. to do another
	 * newSession. This should fail.
	 *
	 * @throws InterruptedException
	 */
	@Test
	@Ignore
	public void testDenyIfClosed() throws InterruptedException {
		ActionSeries as;
		Action a;

		// new session using ssrc1
		mEventQueue.put(TestEventCreator.createNewSessionRequest(clId, ssrc1, true));
		as = mActionQueue.get();
		assertEquals(1, as.getActions().size());
		a = as.getActions().get(0);
		assertTrue(ResponseCheck.checkNewSessionResult(a, ssrc1));

		// switch to ssrc2
		mEventQueue.put(TestEventCreator.createRenewSessionRequest(clId, ssrc2, SESSION_ID, true));
		as = mActionQueue.get();
		assertEquals(1, as.getActions().size());
		a = as.getActions().get(0);
		assertTrue(ResponseCheck.checkRenewSessionResult(a, ssrc2));

		// close ssrc2, this should not result in any response
		mEventQueue.put(TestEventCreator.createClosedChannelEvent(ssrc2));

		// give the other guy some time to remove the event from the queue, racy...
		Thread.sleep(10);
		assertTrue(mActionQueue.isEmpty());

		// try ssrc1 again
		mEventQueue.put(TestEventCreator.createNewSessionRequest(clId, ssrc1, false));
		as = mActionQueue.get();
		assertEquals(1, as.getActions().size());
		a = as.getActions().get(0);
		assertTrue(ResponseCheck.checkErrorResponse("AccessDenied", a, ssrc1));

		// do endSession on ssrc2
		mEventQueue.put(TestEventCreator.createEndSessionRequest(clId, ssrc2, SESSION_ID, false));
		as = mActionQueue.get();
		assertEquals(1, as.getActions().size());
		a = as.getActions().get(0);
		assertTrue(ResponseCheck.checkErrorResponse("AccessDenied", a, ssrc2));
	}

	/**
	 * Create a newSession, close this session using a new channel,
	 * try to create a newSesson with the first channel. This should fail.
	 * @throws InterruptedException
	 */
	@Test
	@Ignore
	public void testNewSessionOldChannel() throws InterruptedException {
		ActionSeries as;
		Action a;

		// new session using ssrc1
		mEventQueue.put(TestEventCreator.createNewSessionRequest(clId, ssrc1, true));
		as = mActionQueue.get();
		assertEquals(1, as.getActions().size());
		a = as.getActions().get(0);
		assertTrue(ResponseCheck.checkNewSessionResult(a, ssrc1));

		// do endSession on ssrc2
		mEventQueue.put(TestEventCreator.createEndSessionRequest(clId, ssrc2, SESSION_ID, true));
		as = mActionQueue.get();
		assertEquals(1, as.getActions().size());
		a = as.getActions().get(0);
		assertTrue(ResponseCheck.checkEndSessionResult(a, ssrc2));

		// new session using ssrc1, again (this is an old channel in this case)
		mEventQueue.put(TestEventCreator.createNewSessionRequest(clId, ssrc1, false));
		as = mActionQueue.get();
		assertEquals(1, as.getActions().size());
		a = as.getActions().get(0);
		assertTrue(ResponseCheck.checkErrorResponse("AccessDenied", a, ssrc1));
	}
}
