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

import org.apache.log4j.Logger;

import de.fhhannover.inform.iron.mapserver.communication.bus.Queue;
import de.fhhannover.inform.iron.mapserver.communication.bus.messages.Event;
import de.fhhannover.inform.iron.mapserver.communication.bus.messages.PollResultAvailableEvent;
import de.fhhannover.inform.iron.mapserver.datamodel.SubscriptionObserver;
import de.fhhannover.inform.iron.mapserver.provider.LoggingProvider;
import de.fhhannover.inform.iron.mapserver.utils.NullCheck;

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
	 * @see de.fhhannover.inform.iron.mapserver.datamodel.SubscriptionObserver#pollResultAvailable(java.lang.String)
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
