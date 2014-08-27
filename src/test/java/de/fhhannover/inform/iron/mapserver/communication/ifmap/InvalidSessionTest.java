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
import de.fhhannover.inform.iron.mapserver.communication.bus.messages.RequestChannelEvent;
import de.fhhannover.inform.iron.mapserver.messages.ErrorCode;
import de.fhhannover.inform.iron.mapserver.provider.ServerConfigurationProvider;
import de.fhhannover.inform.iron.mapserver.provider.StubProvider;

/**
 * Run all possible requests with a invalid session id and
 * check whether we get a InvalidSessionId response back.
 *
 * Note: Only rudimentary checks for the response :-(
 *
 * @author awelzel
 *
 */
public class InvalidSessionTest extends TestCase {

	private ServerConfigurationProvider mServerConf;
	private EventProcessor mEventProc = null;
	private Queue<Event> mEventQueue;
	private Queue<ActionSeries> mActionQueue;

	private ChannelIdentifier chId;
	private ClientIdentifier clId;

	private static final String SESSION_ID = "123";


	private static final String IDENTIFIER =
		"<device>" + "<name>devName</name>" + "</device>";

	private static final String METADATA =
		"<metadata><somemetadata ifmap-cardinality=\"singleValue\"/></metadata>";

	private static final String PUBLISH_CONTENT =
		"<update lifetime=\"session\">" +
			IDENTIFIER + METADATA +
		"</update>";



	@Override
	@Before
	public void setUp() {

		chId = new ChannelIdentifier("192.168.0.1", 8888, 0);
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

	@Test
	public void testInvalidSessionID_endSession() throws InterruptedException {

			RequestChannelEvent endSession = TestEventCreator.createEndSessionRequest(
					clId, chId, SESSION_ID, true);
			mEventQueue.put(endSession);
			ActionSeries as = mActionQueue.get();
			assertEquals(1, as.getActions().size());
			assertTrue(ResponseCheck.checkErrorResponse(ErrorCode.InvalidSessionID.toString(),
					as.getActions().get(0),chId));
	}

	@Test
	public void testInvalidSessionID_renewSession() throws InterruptedException {
			RequestChannelEvent renewSession = TestEventCreator.createRenewSessionRequest(
					clId, chId, SESSION_ID, true);
			mEventQueue.put(renewSession);
			ActionSeries as = mActionQueue.get();
			assertEquals(1, as.getActions().size());
			assertTrue(ResponseCheck.checkErrorResponse(ErrorCode.InvalidSessionID.toString(),
					as.getActions().get(0),chId));
	}

	@Test
	public void testInvalidSessionID_publish() throws InterruptedException {
			RequestChannelEvent publish = TestEventCreator.createPublishRequest(
					clId, chId, SESSION_ID, PUBLISH_CONTENT, true);
			mEventQueue.put(publish);
			ActionSeries as = mActionQueue.get();
			assertEquals(1, as.getActions().size());
			assertTrue(ResponseCheck.checkErrorResponse(ErrorCode.InvalidSessionID.toString(),
					as.getActions().get(0),chId));
	}

	@Test
	public void testInvalidSessionID_search() throws InterruptedException {
			RequestChannelEvent search = TestEventCreator.createSearchRequest(
					clId, chId, SESSION_ID, "filtertest", null, null, "20000",
					"afilter", IDENTIFIER, true);
			mEventQueue.put(search);
			ActionSeries as = mActionQueue.get();
			assertEquals(1, as.getActions().size());
			assertTrue(ResponseCheck.checkErrorResponse(ErrorCode.InvalidSessionID.toString(),
					as.getActions().get(0),chId));
	}

	@Test
	public void testInvalidSessionID_subscribe() throws InterruptedException {
			String subContent = TestEventCreator.createSubscrbeUpdateElement("testsub",
					"filtertest", null, null, "20000", "afilter", IDENTIFIER);
			RequestChannelEvent subscribe = TestEventCreator.createSubscribeRequest(
					clId, chId, SESSION_ID, subContent, true);
			mEventQueue.put(subscribe);
			ActionSeries as = mActionQueue.get();
			assertEquals(1, as.getActions().size());
			assertTrue(ResponseCheck.checkErrorResponse(ErrorCode.InvalidSessionID.toString(),
					as.getActions().get(0),chId));
	}

	@Test
	public void testInvalidSessionID_poll() throws InterruptedException {
			RequestChannelEvent poll = TestEventCreator.createPollRequest(
					clId, chId, SESSION_ID, true);
			mEventQueue.put(poll);
			ActionSeries as = mActionQueue.get();
			assertEquals(1, as.getActions().size());
			assertTrue(ResponseCheck.checkErrorResponse(ErrorCode.InvalidSessionID.toString(),
					as.getActions().get(0),chId));
	}

	@Test
	public void testInvalidSessionID_purgePublisher() throws InterruptedException {
			RequestChannelEvent purgePublisher = TestEventCreator.createPurgePublisherRequest(
					clId, chId, SESSION_ID, "JUSTAPUBLISHER", true);
			mEventQueue.put(purgePublisher);
			ActionSeries as = mActionQueue.get();
			assertEquals(1, as.getActions().size());
			assertTrue(ResponseCheck.checkErrorResponse(
					ErrorCode.InvalidSessionID.toString(), as.getActions().get(0),chId));
	}


}
