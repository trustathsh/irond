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
 * This file is part of irond, version 0.5.5, implemented by the Trust@HsH
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


import de.hshannover.f4.trust.iron.mapserver.communication.bus.Queue;
import de.hshannover.f4.trust.iron.mapserver.communication.bus.messages.Event;
import de.hshannover.f4.trust.iron.mapserver.provider.ServerConfigurationProvider;
import de.hshannover.f4.trust.iron.mapserver.utils.NullCheck;

/**
 * Very simple class used to construct {@link SessionTimer} instances.
 *
 *
 * @author aw
 *
 */
public class SessionTimerFactory {

	/**
	 * Timers have to know about the event queue, so the factory has to, too.
	 */
	private Queue<Event> mEventQueue;

	/**
	 * Used to read the timeout value from the configuration.
	 */
	private ServerConfigurationProvider mServerConfig;


	public SessionTimerFactory(Queue<Event> eventQueue, ServerConfigurationProvider serverConf) {
		NullCheck.check(eventQueue, "eventQueue is null");
		NullCheck.check(serverConf, "serverConf is null");
		mEventQueue = eventQueue;
		mServerConfig = serverConf;
	}

	/**
	 * Create a new instance of a {@link SessionTimer}.
	 *
	 * This timer is not started yet.
	 *
	 * @param sessionId
	 * @return
	 */
	public SessionTimer newTimer(String sessionId) {
		NullCheck.check(sessionId, "sessionId is null");
		long timeout = mServerConfig.getSessionTimeOutMilliSeconds();
		return new SessionTimer(sessionId, mEventQueue, timeout);
	}
}
