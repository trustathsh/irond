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
 * This file is part of irond, version 0.4.2, implemented by the Trust@FHH
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

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Test;

import de.fhhannover.inform.iron.mapserver.communication.ChannelIdentifier;
import de.fhhannover.inform.iron.mapserver.communication.ClientIdentifier;
import de.fhhannover.inform.iron.mapserver.communication.bus.Queue;
import de.fhhannover.inform.iron.mapserver.communication.bus.messages.Action;
import de.fhhannover.inform.iron.mapserver.communication.bus.messages.ActionSeries;
import de.fhhannover.inform.iron.mapserver.communication.bus.messages.Event;
import de.fhhannover.inform.iron.mapserver.provider.ServerConfigurationProvider;
import de.fhhannover.inform.iron.mapserver.provider.StubProvider;

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
