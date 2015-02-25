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
 * This file is part of irond, version 0.5.2, implemented by the Trust@HsH
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
package de.hshannover.f4.trust.iron.mapserver.datamodel;


import de.hshannover.f4.trust.iron.mapserver.datamodel.search.PollResult;

/**
 * Interface a class which wants to observer subscriptions needs to implement.
 * It can register itself with a {@link SubscriptionNotifier} through a call
 * to registerSubscriptionNotifier().
 *
 * Extensive documentation is provided for the methods.
 *
 * @author aw
 */

/*
 * created: 01.05.10
 *
 * changes:
 *  01.05.10 aw - ...
 *  15.06.10 aw - removed endSessionPollResult(), this should be managed
 *  	in the communication layer alone.
 *
 */
public interface SubscriptionObserver {

	/**
	 * This method is called by an object of class {@link SubscriptionNotifier}
	 * if an open session has a {@link PollResult} available. For a given sessionId this
	 * call is only made once. E.g. if a {@link PollResult} becomes available
	 * for this sessionId and it is not fetched using getPollResultFor(), the
	 * pollResultAvailable() method won't be called again if the {@link PollResult}
	 * changes later on.
	 * The caller is responsible to remember whether or not this method has been
	 * called.
	 *
	 * A scenario might be the following:
	 * 	- MAPC sends a subscribe request.
	 *  - Datamodel generates the first PollResult and notifies using pollResultAvailable()
	 *  - {@link SubscriptionNotifier#getPollResultFor(String) is not called by
	 *    the Observer as the MAPC has no active poll open
	 *  - Another MAPC changes data in the graph and affects the firsts MAPC subscription
	 *  - pollResultAvailable() _is not_ called
	 *  - The first MAPC then polls
	 *  - The SubscriptionObserver remembers the pollResultAvailable() call before
	 *    and calls SubscriptionNotifier#getPollResultFor(String) to get the
	 *    current PollResult
	 *  - After a call to getPollResultFor() the next time the Subscription is changed
	 *    a pollResultAvailable() call will be made.
	 *
	 * @param sessionId
	 */
	public void pollResultAvailable(String sessionId);
}
