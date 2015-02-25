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
package de.hshannover.f4.trust.iron.mapserver.datamodel.search;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hshannover.f4.trust.iron.mapserver.IfmapConstStrings;
import de.hshannover.f4.trust.iron.mapserver.datamodel.Publisher;
import de.hshannover.f4.trust.iron.mapserver.exceptions.SystemErrorException;
import de.hshannover.f4.trust.iron.mapserver.utils.NullCheck;

/**
 * Class to encapsulate all state concerning subscriptions for a single
 * {@link Publisher}
 *
 * @author aw
 */
public class SubscriptionState {

	private boolean mNotified;
	private Integer  maxPollResultSize;
	private boolean mPollResGrewTooBig;

	private final Map<String, Subscription> mSubscriptions;
	private ModifiablePollResult mPollResult;

	public SubscriptionState() {
		mSubscriptions = new HashMap<String, Subscription>();
		mPollResult = new PollResultImpl();
		mPollResGrewTooBig = false;
	}

	/**
	 * @return read-only copy of the {@link Subscription} instances
	 */
	public List<Subscription> getSubscriptions() {
		return Collections.unmodifiableList(
				new ArrayList<Subscription>(mSubscriptions.values()));
	}

	/**
	 * Add a {@link Subscription} to this {@link SubscriptionState}.
	 * Goes crazy if the {@link Subscription} existed.
	 *
	 * @param sub
	 */
	public void addSubscription(Subscription sub) {
		NullCheck.check(sub, "sub is null");
		if (mSubscriptions.containsKey(sub.getName())) {
			throw new SystemErrorException("subscription " + sub.getName()
					+ " exists!");
		}

		mSubscriptions.put(sub.getName(), sub);
	}

	/**
	 * Removes the given {@link Subscription} from this {@link SubscriptionState}
	 * and cleans the {@link PollResult}.
	 *
	 * @param sub
	 */
	public void removeSubscription(Subscription sub) {
		NullCheck.check(sub, "sub is null");
		NullCheck.check(sub.getName(), "sub.getName() returns null");

		if (!mSubscriptions.containsKey(sub.getName())) {
			throw new SystemErrorException("Cannot remove nonexistent subscription"
					+ sub.getName());
		}

		mSubscriptions.remove(sub.getName());
		mPollResult.removeResultsOf(sub.getName());
	}

	/**
	 * @param name
	 * @return the subscription with name name or null
	 */
	public Subscription getSubscription(String name) {
		NullCheck.check(name, "name is null");
		return mSubscriptions.get(name);
	}

	/**
	 * Remove all {@link Subscription} instances and clean the {@link PollResult}.
	 */
	public void clearSubscriptions() {
		for (Subscription sub : getSubscriptions()) {
			removeSubscription(sub);
		}

		// Sanity
		if (mSubscriptions.size() != 0) {
			throw new SystemErrorException("All subscriptions should be gone");
		}

		if (mPollResult.getResults().size() != 0) {
			throw new SystemErrorException("PollResult should be empty");
		}

		if (mPollResult.getByteCount() != IfmapConstStrings.PRES_MIN_CNT) {
			throw new SystemErrorException("PollResult size should be zero");
		}
	}

	public Integer getMaxPollSize() {
		return maxPollResultSize;
	}

	public void setMaxPollResultSize(Integer mprs) {
		maxPollResultSize = mprs;
	}

	public void setNotified() {
		mNotified = true;
	}

	public void unsetNotified() {
		mNotified = false;
	}

	public boolean isNotified() {
		return mNotified;
	}

	public void setPollResultsTooBig() {
		mPollResGrewTooBig = true;
	}

	void unsetPollResultsTooBig() {
		mPollResGrewTooBig = false;
	}

	public boolean isPollResultsTooBig() {
		if (mPollResult.getByteCount() > getMaxPollSize()) {
			mPollResGrewTooBig = true;
		}

		return mPollResult.getByteCount() > getMaxPollSize()
				|| mPollResGrewTooBig;
	}

	public void resetPollResult() {
		unsetPollResultsTooBig();
		mPollResult = new PollResultImpl();
	}

	public ModifiablePollResult getPollResult() {
		return mPollResult;
	}

}
