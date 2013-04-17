package de.fhhannover.inform.iron.mapserver.datamodel.search;

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
 * This file is part of irond, version 0.4.0, implemented by the Trust@FHH 
 * research group at the Fachhochschule Hannover.
 * 
 * irond is an an *experimental* IF-MAP 2.0 compliant MAP server written in
 * JAVA. irond supports both basic authentication and certificate-based 
 * authentication (using X.509 certificates) of MAP clients. irond is
 * maintained by the Trust@FHH group at the Fachhochschule Hannover, initial
 * developement was carried out during the ESUKOM research project.
 * %%
 * Copyright (C) 2010 - 2013 Trust@FHH
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

import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import de.fhhannover.inform.iron.mapserver.datamodel.graph.GraphElement;
import de.fhhannover.inform.iron.mapserver.datamodel.graph.Link;
import de.fhhannover.inform.iron.mapserver.datamodel.graph.Node;
import de.fhhannover.inform.iron.mapserver.datamodel.identifiers.Identifier;
import de.fhhannover.inform.iron.mapserver.datamodel.meta.MetadataHolder;
import de.fhhannover.inform.iron.mapserver.exceptions.SearchResultsTooBigException;
import de.fhhannover.inform.iron.mapserver.exceptions.SystemErrorException;
import de.fhhannover.inform.iron.mapserver.messages.SearchRequest;
import de.fhhannover.inform.iron.mapserver.provider.LoggingProvider;
import de.fhhannover.inform.iron.mapserver.utils.CollectionHelper;
import de.fhhannover.inform.iron.mapserver.utils.NullCheck;

class ContinueSearchHandler implements SearchHandler {

	private static final Logger sLogger = LoggingProvider.getTheLogger();
	private static final String sName = "ContinueSearchHandler";

	private final Identifier mStartIdent;
	private final Subscription mSubscription;
	private final Filter mMatchLinksFilter;
	private final Filter mResultFilter;
	private int mMaxDepth;
	private int mCurDepth;
	private final TerminalIdentifiers mTermIdents;
	private final Set<GraphElement> mVisitedGraphElements;
	private final Set<MetadataHolder> mNewMetadata;
	private final Set<Node> mStarters;
	private final String mName;
	
	ContinueSearchHandler(Identifier start, int depth, Subscription sub,
			Set<GraphElement> visitedGraphElement, Set<MetadataHolder> newMeta,
			Set<Node> starters) {
		NullCheck.check(start, "start identifier is null");
		NullCheck.check(visitedGraphElement, "visisted graph elements is null");
		NullCheck.check(newMeta, "newMeta is null");
		NullCheck.check(starters, "nextContPoints is null");
		SearchRequest sreq = sub.getSearchRequest();
		mStartIdent = start;
		mSubscription = sub;
		mCurDepth = depth;
		mMaxDepth = sreq.getMaxDepth();
		mMatchLinksFilter = sreq.getMatchLinksFilter();
		mResultFilter = sreq.getResultFilter();
		mTermIdents = sreq.getTerminalIdentifiers();
		mVisitedGraphElements = visitedGraphElement;
		mNewMetadata = newMeta;
		mStarters = starters;
		mName = sName + "[" + sub.getName() + "]";
	}

	@Override
	public Identifier getStartIdentifier() {
		return mStartIdent;
	}

	@Override
	public void onStart() {
		if (SearchHandler.SEARCH_HANDLER_DEBUG)
			sLogger.trace(mName + ": Starting for " + mSubscription + " at "
					+ mStartIdent);
	}

	@Override
	public void onNode(Node cur) throws SearchResultsTooBigException {
		visitGraphElementGeneric(cur);
	}

	@Override
	public boolean travelLinksOf(Node cur) {
		
		
		if (SearchHandler.SEARCH_HANDLER_DEBUG) {
			if (mCurDepth >= mMaxDepth)
				sLogger.trace(mName + ": max-depth reached at " + cur);
		
			if (mTermIdents.contains(cur.getIdentifier()))
				sLogger.trace(mName + ": terminal identifier at " + cur);
		}
		
		return mCurDepth < mMaxDepth && !mTermIdents.contains(cur.getIdentifier());
	}

	@Override
	public boolean travelLink(Link l) {
		SubscriptionEntry entry = l.getSubscriptionEntry(mSubscription);
		SubscriptionEntry remEntry = l.getRemovedSubscriptionEntry(mSubscription);
		int countMatching;
	
		if (entry != null && remEntry != null && entry != remEntry)
			throw new SystemErrorException("IF A REMOVED ENTRY EXISTS IT SHOULD BE REUSED");
		
		if (remEntry != null) {
			if (SearchHandler.SEARCH_HANDLER_DEBUG) {
				sLogger.trace(mName + ": Found removed sub entry on " + l);
				sLogger.trace(mName + ": Removed entry mh size: on " 
						+ remEntry.getMetadataHolder().size());
			}
		}
		
		// might be a circle search?
		if (entry != null && entry.getDepth() <= mCurDepth)
			return false;

		// There might be some metadata from before here.
		if (entry != null && entry.getMetadataHolder().size() > 0)
			return true;
		
		// We can try to use the old entry if one exists:
		if (remEntry != null && remEntry.getMetadataHolder().size() > 0)
			return true;

		// Just check whether there is some new Metadata that matches
		if (l.getMetadataHolderNew(mMatchLinksFilter).size() > 0)
			return true;
	
		// Decide based on whether this link contains any interesting metadata
		// for us. This is more heavy then the previous things used
		countMatching = l.getMetadataHolderNext(mMatchLinksFilter).size();
		
		if (SearchHandler.SEARCH_HANDLER_DEBUG)
			sLogger.trace(mName + ": Travelling " + l + "=" + (countMatching > 0));
		
		return countMatching > 0;
	}

	@Override
	public void onLink(Link l) throws SearchResultsTooBigException {
		visitGraphElementGeneric(l);
	}

	@Override
	public boolean traverseTo(Node nextNode) {
		
		SubscriptionEntry entry =  nextNode.getSubscriptionEntry(mSubscription);
		
		// If this subscription doesn't have an entry on this node or has a
		// higher depth than we do, yes, please bring us there...
		// bring us there... 
		//
		if (entry == null) {
			if (SearchHandler.SEARCH_HANDLER_DEBUG) {
				sLogger.trace(mName + ": Travelling to " + nextNode + 
						" as no entry is there for " + mSubscription);
			}
			
			return true;
		}
		if (entry.getDepth() > mCurDepth + 1) {
			if (SearchHandler.SEARCH_HANDLER_DEBUG) {
				sLogger.trace(mName + ": Travelling to " + nextNode 
						+ " as current depth is " + mCurDepth
						+ " and there it is  " + entry.getDepth());
			}
			
			return true;
		}
	
		// We have restart the search at nodes where it'll result in lower 
		if (entry.getDepth() < mCurDepth - 1) {
			
			if (SearchHandler.SEARCH_HANDLER_DEBUG)
				sLogger.trace(mName + ": Found new continue starter at " + nextNode);
			
			mStarters.add(nextNode);
		}
	
		if (SearchHandler.SEARCH_HANDLER_DEBUG) {
			sLogger.trace(mName + ": Will not visit " + nextNode + " with depth " 
					+ entry.getDepth());
		}
		
		return false;
	}


	@Override
	public void afterNode(Node cur) {
		// NOTHING
	}

	@Override
	public void onEnd() {
		if (SearchHandler.SEARCH_HANDLER_DEBUG)
			sLogger.trace(mName + ": Finished " + mSubscription);
	}

	@Override
	public void nextDepth() {
		mCurDepth++;
		
		if (SearchHandler.SEARCH_HANDLER_DEBUG)
			sLogger.trace(mName + ": Depth is now " + mCurDepth);
	}
	
	@Override
	public void depthOver() {
		// NOTHING
	}
	
	private void visitGraphElementGeneric(GraphElement ge) {
		SubscriptionEntry entry = ge.getSubscriptionEntry(mSubscription);
		SubscriptionEntry remEntry = ge.getRemovedSubscriptionEntry(mSubscription);
		List<MetadataHolder> toAdd = null;

		if (SearchHandler.SEARCH_HANDLER_DEBUG)
			sLogger.trace(mName + ": Visiting " + ge + " at depth " + mCurDepth);
		
		if (entry == null) {
			if (remEntry != null) {
			
				if (SearchHandler.SEARCH_HANDLER_DEBUG)
					sLogger.trace(mName + ": Reusing old entry on " + ge);
				
				entry = remEntry;
				toAdd = remEntry.getMetadataHolder();
				
			} else {
				
				if (SearchHandler.SEARCH_HANDLER_DEBUG) {
					sLogger.trace(mName + ": Creating new entry on " + ge
							+ " with depth " + mCurDepth);
				}
				
				entry = new SubscriptionEntry(mSubscription);
				
				if (ge instanceof Node) {
					toAdd = ge.getMetadataHolder(mResultFilter);
				} else if (ge instanceof Link) {
					List<MetadataHolder> tmp = CollectionHelper.provideListFor(MetadataHolder.class);
					toAdd = ge.getMetadataHolder(mMatchLinksFilter);
					
					for (MetadataHolder mh : toAdd)
						if (mh.getMetadata().matchesFilter(mResultFilter))
							tmp.add(mh);
					
					toAdd = tmp;
				}
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
			sLogger.trace(mName + ": Setting depth to " + mCurDepth
					+ " for entry on " + ge);
		}
	
		// go null if we don't have any elements
		toAdd = (toAdd == null || toAdd.size() == 0) ? null : toAdd;
		
		if (toAdd != null)
			mNewMetadata.addAll(toAdd);
		
		entry.setDepth(mCurDepth);
		mVisitedGraphElements.add(ge);
	}

}
