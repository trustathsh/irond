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
 * This file is part of irond, version 0.5.3, implemented by the Trust@HsH
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
import de.hshannover.f4.trust.iron.mapserver.exceptions.AlreadyObservedException;
import de.hshannover.f4.trust.iron.mapserver.exceptions.NoPollResultAvailableException;
import de.hshannover.f4.trust.iron.mapserver.exceptions.PollResultsTooBigException;

/**
 * Interface for the {@link SubscriptionNotifier} and {@link SubscriptionObserver}
 * mechanism. This interface is implemented by the {@link DataModelService}
 *
 * @version 0.1
 * @author aw
 *
 */
public interface SubscriptionNotifier {

	/**
	 * Register a {@link SubscriptionObserver} with this {@link SubscriptionNotifier}.
	 * Only one observer is allowed. If this method is called twice an exception
	 * is thrown. A {@link SubscriptionObserver} will be notified whenever a new
	 * @link PollResult} gets available for a given session. See the documentation
	 * of {@link SubscriptionObserver}.
	 *
	 * @param subObs
	 * @throws AlreadyObservedException
	 */
	public void registerSubscriptionObserver(SubscriptionObserver subObs)
		throws AlreadyObservedException;



	/**
	 * This method returns a {@link PollResult} for the given session to the
	 * caller. If no {@link PollResult} is available a {@link NoPollResultAvailableException}
	 * is thrown. If the {@link PollResult} exceeded the given max-poll-result-size
	 * an {@link PollResultsTooBigException} is thrown.See the documentation of
	 * {@link SubscriptionObserver} when a call to this method makes sense.
	 *
	 * @param sessionId
	 * @return
	 * @throws NoPollResultAvailableException
	 * @throws PollResultsTooBigException
	 */
	public PollResult getPollResultFor(String sessionId)
		throws NoPollResultAvailableException, PollResultsTooBigException;

}
