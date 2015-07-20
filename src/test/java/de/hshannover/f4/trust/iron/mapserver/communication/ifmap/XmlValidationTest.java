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
 * Just check whether irond will complain about a nil="true" attribute
 * on the newSession element, because this was encountered during the plugfest.
 *
 * NOTE: You need schema/soap12.xsd in your project folder!
 */
public class XmlValidationTest extends TestCase {

	private ServerConfigurationProvider mServerConf;
	private EventProcessor mEventProc = null;
	private Queue<Event> mEventQueue;
	private Queue<ActionSeries> mActionQueue;

	private ChannelIdentifier chSsrc;
	private ClientIdentifier clId;

	private void setUp(boolean xmlValidate) {
		chSsrc = new ChannelIdentifier("192.168.0.1", 8888, 0);
		clId = new ClientIdentifier("theClient");
		mServerConf = StubProvider.getServerConfStub(xmlValidate);
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
	public void testXmlValidationOn() throws InterruptedException {
		setUp(true);
		Event newsession = TestEventCreator.createRequest(clId, chSsrc,
				true, NEW_SESSION_REQUEST_WITH_NIL.getBytes());

		mEventQueue.put(newsession);
		ActionSeries as = mActionQueue.get();

		assertEquals(1, as.getActions().size());
		Action a = as.getActions().get(0);
		assertTrue(ResponseCheck.checkErrorResponse("Failure", a, chSsrc));
	}

	@Test
	public void testXmlValidationOff() throws InterruptedException {
		setUp(false);
		Event newsession = TestEventCreator.createRequest(clId, chSsrc,
				true, NEW_SESSION_REQUEST_WITH_NIL.getBytes());

		mEventQueue.put(newsession);
		ActionSeries as = mActionQueue.get();

		assertEquals(1, as.getActions().size());
		Action a = as.getActions().get(0);
		assertTrue(ResponseCheck.checkNewSessionResult(a, chSsrc));
	}


	private static final String NEW_SESSION_REQUEST_WITH_NIL =
		"<env:Envelope xmlns:env=\"http://www.w3.org/2003/05/soap-envelope\" xmlns:ifmap=\"http://www.trustedcomputinggroup.org/2010/IFMAP/2\">\n" +
		  "<env:Body>\n" +
		    "<ifmap:newSession nil=\"true\"/>\n" +
		  "</env:Body>\n" +
		"</env:Envelope>";
}
