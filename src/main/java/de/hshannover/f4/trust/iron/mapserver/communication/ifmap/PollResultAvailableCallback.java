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
 * This file is part of irond, version 0.5.7, implemented by the Trust@HsH
 * research group at the Hochschule Hannover.
 * %%
 * Copyright (C) 2010 - 2016 Trust@HsH
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


import org.apache.log4j.Logger;

import de.hshannover.f4.trust.iron.mapserver.communication.bus.Queue;
import de.hshannover.f4.trust.iron.mapserver.communication.bus.messages.Event;
import de.hshannover.f4.trust.iron.mapserver.communication.bus.messages.PollResultAvailableEvent;
import de.hshannover.f4.trust.iron.mapserver.datamodel.SubscriptionObserver;
import de.hshannover.f4.trust.iron.mapserver.provider.LoggingProvider;
import de.hshannover.f4.trust.iron.mapserver.utils.NullCheck;

/**
 * An implementation of the {@link SubscriptionObserver} interface which
 * transforms the callback into an {@link PollResultAvailableEvent} and puts it
 * into the {@link Queue} used during construction.
 *
 * @author aw
 * @version 0.2
 *
 */
public class PollResultAvailableCallback implements SubscriptionObserver {

	static Logger sLogger = LoggingProvider.getTheLogger();

	/**
	 * The queue where events are placed on a callback.
	 */
	private Queue<Event> mEventQueue;

	public PollResultAvailableCallback(Queue<Event> queue) {
		NullCheck.check(queue, "queue is null");
		mEventQueue = queue;
	}


	/* (non-Javadoc)
	 * @see de.hshannover.f4.trust.iron.mapserver.datamodel.SubscriptionObserver#pollResultAvailable(java.lang.String)
	 *
	 * This method is called if the Datamodel wants to inform us about a available
	 * PollResult. We simply put this into the queue as an event.
	 */
	@Override
	public void pollResultAvailable(String sessionId) {

		NullCheck.check(sessionId, "PROGRAMMING ERROR: sessionId is null");
		putEventIntoEventQueue(new PollResultAvailableEvent(sessionId));
	}


	/**
	 * Helper to put an event into the event queue which is actually used
	 * by the {@link EventProcessor} itself.

	 * TODO: If we want to shutdown cleanly, we should probably honor
	 * a {@link InterruptedException} and the {@link Thread#isInterrupted()}
	 * further use the method?
	 *
	 * @param event
	 */
	private void putEventIntoEventQueue(Event event) {
		NullCheck.check(event, "event is null");
		boolean added = false;
		do {
			try {
				mEventQueue.put(event);
				added = true;
			} catch (InterruptedException e) { }
		} while (!added);
	}
}
