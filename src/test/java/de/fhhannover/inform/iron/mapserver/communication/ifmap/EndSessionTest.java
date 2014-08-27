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
import org.junit.Before;
import org.junit.Test;

import de.fhhannover.inform.iron.mapserver.communication.ChannelIdentifier;
import de.fhhannover.inform.iron.mapserver.communication.ClientIdentifier;
import de.fhhannover.inform.iron.mapserver.communication.bus.Queue;
import de.fhhannover.inform.iron.mapserver.communication.bus.messages.Action;
import de.fhhannover.inform.iron.mapserver.communication.bus.messages.ActionSeries;
import de.fhhannover.inform.iron.mapserver.communication.bus.messages.Event;
import de.fhhannover.inform.iron.mapserver.communication.bus.messages.SendResponseAction;
import de.fhhannover.inform.iron.mapserver.provider.ServerConfigurationProvider;
import de.fhhannover.inform.iron.mapserver.provider.StubProvider;

/**
 * This class includes a test to check that we get an endSessionResult on
 * an ARC if we have a poll pending and endSession is sent on the SSRC.
 *
 */
public class EndSessionTest extends TestCase {

	private ServerConfigurationProvider mServerConf;
	private EventProcessor mEventProc = null;
	private Queue<Event> mEventQueue;
	private Queue<ActionSeries> mActionQueue;

	private ChannelIdentifier chSsrc, chArc1;
	private ClientIdentifier clId;

	// this session-id is provided by the stub implementation
	private static final String SESSION_ID = "0";


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

	@After
	public void tearDown() {
		mEventProc.stop();
	}

	/**
	 * create a new session, do a poll run endSession.
	 * We should receive a ActionSeries containing a endSessonResult
	 * for the SSRC as well as for the ARC.
	 * @throws InterruptedException
	 */
	@Test
	public void testEndSession_PendingPoll() throws InterruptedException {
		Event newsession = TestEventCreator.createNewSessionRequest(clId, chSsrc,
				true);
		Event endsession = TestEventCreator.createEndSessionRequest(clId, chSsrc,
				SESSION_ID, false);
		Event poll = TestEventCreator.createPollRequest(clId, chArc1, SESSION_ID,
				true);

		mEventQueue.put(newsession);
		ActionSeries as = mActionQueue.get();
		assertEquals(1, as.getActions().size());
		assertTrue(ResponseCheck.checkNewSessionResult(as.getActions().get(0), chSsrc));

		// start a poll
		mEventQueue.put(poll);

		// end the session
		mEventQueue.put(endsession);

		as = mActionQueue.get();
		assertEquals(2, as.getActions().size());

		for (Action a : as.getActions()) {
			if (a instanceof SendResponseAction) {
				SendResponseAction sra = (SendResponseAction) a;
				if (sra.getChannelIdentifier().equals(chArc1)) {
					assertTrue(ResponseCheck.checkEndSessionResult(sra, chArc1));
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
}
