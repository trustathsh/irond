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
package de.hshannover.f4.trust.iron.mapserver.datamodel.search;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import de.hshannover.f4.trust.iron.mapserver.contentauth.IfmapPep;
import de.hshannover.f4.trust.iron.mapserver.datamodel.Publisher;
import de.hshannover.f4.trust.iron.mapserver.datamodel.graph.GraphElement;
import de.hshannover.f4.trust.iron.mapserver.datamodel.graph.Link;
import de.hshannover.f4.trust.iron.mapserver.datamodel.graph.Node;
import de.hshannover.f4.trust.iron.mapserver.datamodel.identifiers.Identifier;
import de.hshannover.f4.trust.iron.mapserver.datamodel.meta.MetadataHolder;
import de.hshannover.f4.trust.iron.mapserver.exceptions.SearchException;
import de.hshannover.f4.trust.iron.mapserver.exceptions.SearchResultsTooBigException;
import de.hshannover.f4.trust.iron.mapserver.exceptions.SystemErrorException;
import de.hshannover.f4.trust.iron.mapserver.provider.LoggingProvider;
import de.hshannover.f4.trust.iron.mapserver.utils.CollectionHelper;
import de.hshannover.f4.trust.iron.mapserver.utils.NullCheck;

class ContinueSearchHandler extends AbstractSearchHandler {

	private static final Logger sLogger = LoggingProvider.getTheLogger();
	private static final String sName = "ContinueSearchHandler";

	private final Subscription mSubscription;
	private final Set<MetadataHolder> mNewMetadata;
	private final Set<Node> mStarters;
	private final String mName;

	ContinueSearchHandler(Identifier start, int depth, Subscription sub,
			Map<GraphElement, List<MetadataHolder>> visitedElements,
			Set<MetadataHolder> newMeta, Set<Node> starters, Publisher pub,
			IfmapPep pep) {
		super(sub.getSearchRequest(), start, visitedElements, depth, pub, pep);

		NullCheck.check(newMeta, "newMeta is null");
		NullCheck.check(starters, "nextContPoints is null");
		mSubscription = sub;
		mNewMetadata = newMeta;
		mStarters = starters;
		mName = sName + "[" + sub.getName() + "]";
	}

	@Override
	public void onStart() {
		super.onStart();
		if (SearchHandler.SEARCH_HANDLER_DEBUG) {
			sLogger.trace(mName + ": Starting for " + mSubscription + " at "
					+ getStartIdentifier());
		}
	}

	@Override
	public void onNode(Node cur) throws SearchResultsTooBigException {
		visitGraphElementGeneric(cur);
	}

	@Override
	public boolean travelLinksOf(Node cur) throws SearchException {

		if (SearchHandler.SEARCH_HANDLER_DEBUG) {
			if (getCurrentDepth() >= getMaxDepth()) {
				sLogger.trace(mName + ": max-depth reached at " + cur);
			}

			if (!TerminalIdentifierChecker.isTerminalIdentifier(cur,
					mTermIdentTypes)) {
				sLogger.trace(mName + ": terminal identifier at " + cur);
			}
		}

		return mCurDepth < mMaxDepth
				&& !TerminalIdentifierChecker.isTerminalIdentifier(cur,
						mTermIdentTypes);
	}

	@Override
	public boolean travelLink(Link l) {
		SubscriptionEntry entry = l.getSubscriptionEntry(mSubscription);
		SubscriptionEntry remEntry = l
				.getRemovedSubscriptionEntry(mSubscription);
		List<MetadataHolder> newMatching;
		List<MetadataHolder> nextMatching;

		if (entry != null && remEntry != null && entry != remEntry) {
			throw new SystemErrorException("removed entries should be reused");
		}

		if (remEntry != null) {
			if (SearchHandler.SEARCH_HANDLER_DEBUG) {
				sLogger.trace(mName + ": Found removed sub entry on " + l);
				sLogger.trace(mName + ": Removed entry mh size: on "
						+ remEntry.getMetadataHolder().size());
			}
		}

		// might be a circle search?
		if (entry != null && entry.getDepth() <= getCurrentDepth()) {
			return false;
		}

		// There might be some metadata from before here.
		if (entry != null && entry.getMetadataHolder().size() > 0) {
			return true;
		}

		// We can try to use the metedata of the old entry if one exists:
		if (remEntry != null && remEntry.getMetadataHolder().size() > 0) {
			return true;
		}

		// Just check whether there is some new Metadata that matches,
		// assuming this is always a subset of the Next metadata
		newMatching = l.getMetadataHolderNew(getMatchLinksFilter());

		// Only for what the client is authorized
		newMatching = authorized(newMatching);

		if (newMatching.size() > 0) {
			return true;
		}

		// Only now check whether there is metadata that would match in
		// the next graph state.
		nextMatching = l.getMetadataHolderNext(getMatchLinksFilter());
		nextMatching = authorized(nextMatching);

		return nextMatching.size() > 0;
	}

	@Override
	public void onLink(Link l) throws SearchResultsTooBigException {
		visitGraphElementGeneric(l);
	}

	@Override
	public boolean traverseTo(Node nextNode) {

		SubscriptionEntry entry = nextNode.getSubscriptionEntry(mSubscription);

		// If this subscription doesn't have an entry on this node or has a
		// higher depth than we do, yes, please bring us there...
		// bring us there...
		//
		if (entry == null) {
			if (SearchHandler.SEARCH_HANDLER_DEBUG) {
				sLogger.trace(mName + ": Traversing to " + nextNode
						+ " as no entry is there for " + mSubscription);
			}

			return true;
		}

		// If there's a greater depth than we would reach we want to travel
		// there.
		if (entry.getDepth() > getCurrentDepth() + 1) {
			if (SearchHandler.SEARCH_HANDLER_DEBUG) {
				sLogger.trace(mName + ": Traversing to " + nextNode
						+ " as current depth is " + getCurrentDepth()
						+ " and there it is  " + entry.getDepth());
			}

			return true;
		}

		// We have to restart the search at nodes where it'll result in
		// a lower depth.
		if (entry.getDepth() < getCurrentDepth() - 1) {

			if (SearchHandler.SEARCH_HANDLER_DEBUG) {
				sLogger.trace(mName + ": Found new continue starter at "
						+ nextNode);
			}

			mStarters.add(nextNode);
		}

		if (SearchHandler.SEARCH_HANDLER_DEBUG) {
			sLogger.trace(mName + ": Will not visit " + nextNode
					+ " with depth " + entry.getDepth());
		}

		return false;
	}

	@Override
	public void afterNode(Node cur) {
		// NOTHING
	}

	@Override
	public void onEnd() {
		if (SearchHandler.SEARCH_HANDLER_DEBUG) {
			sLogger.trace(mName + ": Finished " + mSubscription);
		}
	}

	private void visitGraphElementGeneric(GraphElement ge) {
		SubscriptionEntry entry = ge.getSubscriptionEntry(mSubscription);
		SubscriptionEntry remEntry = ge
				.getRemovedSubscriptionEntry(mSubscription);
		List<MetadataHolder> toAdd = null;

		if (SearchHandler.SEARCH_HANDLER_DEBUG) {
			sLogger.trace(mName + ": Visiting " + ge + " at depth "
					+ getCurrentDepth());
		}

		if (entry == null) {
			if (remEntry != null) {

				if (SearchHandler.SEARCH_HANDLER_DEBUG) {
					sLogger.trace(mName + ": Reusing old entry on " + ge);
				}

				entry = remEntry;
				toAdd = remEntry.getMetadataHolder();

			} else {

				if (SearchHandler.SEARCH_HANDLER_DEBUG) {
					sLogger.trace(mName + ": Creating new entry on " + ge
							+ " with depth " + getCurrentDepth());
				}

				entry = new SubscriptionEntry(mSubscription);

				if (ge instanceof Node) {
					toAdd = ge.getMetadataHolder(getResultFilter());
				} else if (ge instanceof Link) {
					List<MetadataHolder> tmp = CollectionHelper
							.provideListFor(MetadataHolder.class);
					toAdd = ge.getMetadataHolder(getMatchLinksFilter());

					for (MetadataHolder mh : toAdd) {
						if (Filter.matchesResultFilter(mh.getMetadata(),
								getResultFilter())) {
							tmp.add(mh);
						}
					}

					toAdd = tmp;
				}

				// Only authorized stuff
				toAdd = authorized(toAdd);

				if (SearchHandler.SEARCH_HANDLER_DEBUG) {
					sLogger.trace(mName + ": Adding " + toAdd.size()
							+ " metadata objects to entry of " + ge);
				}

				entry.addMetadataHolder(toAdd);
			}

			ge.addSubscriptionEntry(entry);
			entry.getSubscription().addGraphElement(ge);
		}

		if (SearchHandler.SEARCH_HANDLER_DEBUG) {
			sLogger.trace(mName + ": Setting depth to " + getCurrentDepth()
					+ " for entry on " + ge);
		}

		// go null if we don't have any elements
		toAdd = toAdd == null || toAdd.size() == 0 ? null : toAdd;

		if (toAdd != null) {
			mNewMetadata.addAll(toAdd);
		}

		entry.setDepth(getCurrentDepth());
		getVisitedElements().put(ge, null);
	}
}
