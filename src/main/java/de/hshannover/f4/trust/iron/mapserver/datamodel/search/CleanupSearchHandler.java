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
package de.hshannover.f4.trust.iron.mapserver.datamodel.search;


import org.apache.log4j.Logger;

import de.hshannover.f4.trust.iron.mapserver.datamodel.graph.GraphElement;
import de.hshannover.f4.trust.iron.mapserver.datamodel.graph.Link;
import de.hshannover.f4.trust.iron.mapserver.datamodel.graph.Node;
import de.hshannover.f4.trust.iron.mapserver.datamodel.identifiers.Identifier;
import de.hshannover.f4.trust.iron.mapserver.exceptions.SearchResultsTooBigException;
import de.hshannover.f4.trust.iron.mapserver.provider.LoggingProvider;
import de.hshannover.f4.trust.iron.mapserver.utils.NullCheck;

class CleanupSearchHandler implements SearchHandler {

	private static final Logger sLogger = LoggingProvider.getTheLogger();
	private static final String sName = "CleanupSearchHandler";

	private final Identifier mStartIdent;
	private final Subscription mSubscription;
	private final String mName;

	CleanupSearchHandler(Identifier start, Subscription sub) {
		NullCheck.check(start, "start identifier is null");
		NullCheck.check(sub, "sub is null");
		mStartIdent = start;
		mSubscription = sub;
		mName = sName + "[" + sub.getName() + "]";
	}

	@Override
	public Identifier getStartIdentifier() {
		return mStartIdent;
	}

	@Override
	public void onStart() {
		if (SearchHandler.SEARCH_HANDLER_DEBUG) {
			sLogger.trace(mName +": Start for " + mSubscription + " at "
					+ getStartIdentifier());
		}
	}

	@Override
	public void onNode(Node cur) throws SearchResultsTooBigException {
		cleanGraphElement(cur);
	}

	@Override
	public boolean travelLinksOf(Node cur) {
		return true;
	}

	@Override
	public boolean travelLink(Link l) {
		return l.getRemovedSubscriptionEntry(mSubscription) != null;
	}

	@Override
	public boolean traverseTo(Node nextNode) {
		return nextNode.getRemovedSubscriptionEntry(mSubscription) != null;
	}

	@Override
	public void onLink(Link l) throws SearchResultsTooBigException {
		cleanGraphElement(l);
	}


	@Override
	public void afterNode(Node cur) {
		// nothing
	}

	@Override
	public void onEnd() {
		if (SearchHandler.SEARCH_HANDLER_DEBUG) {
			sLogger.trace(mName + ": Finished " + mSubscription);
		}
	}

	@Override
	public void nextDepth() {
		// nothing
	}

	private void cleanGraphElement(GraphElement ge) {
		SubscriptionEntry entry = ge.getRemovedSubscriptionEntry(mSubscription);

		if (SearchHandler.SEARCH_HANDLER_DEBUG) {
			sLogger.trace(mName + ": Cleaning " + ge);
		}

		if (entry == null) {
			if (SearchHandler.SEARCH_HANDLER_DEBUG) {
				sLogger.trace(mName + ": No entry here anymore");
			}
			return;
		}


		if (SearchHandler.SEARCH_HANDLER_DEBUG) {
			sLogger.trace(mName + ": Removing old sub entry from " + ge + " for "
					+ mSubscription);
		}

		ge.removeRemovedSubscriptionEntry(mSubscription);
	}
}
