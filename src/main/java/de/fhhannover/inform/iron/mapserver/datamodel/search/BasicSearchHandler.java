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
 * This file is part of irond, version 0.4.2, implemented by the Trust@HsH
 * research group at the Hochschule Hannover.
 * %%
 * Copyright (C) 2010 - 2014 Trust@HsH
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
package de.fhhannover.inform.iron.mapserver.datamodel.search;


import java.util.List;
import de.fhhannover.inform.iron.mapserver.contentauth.IfmapPep;
import de.fhhannover.inform.iron.mapserver.datamodel.Publisher;
import de.fhhannover.inform.iron.mapserver.datamodel.graph.GraphElement;
import de.fhhannover.inform.iron.mapserver.datamodel.graph.Link;
import de.fhhannover.inform.iron.mapserver.datamodel.graph.Node;
import de.fhhannover.inform.iron.mapserver.datamodel.meta.Metadata;
import de.fhhannover.inform.iron.mapserver.datamodel.meta.MetadataHolder;
import de.fhhannover.inform.iron.mapserver.exceptions.SearchException;
import de.fhhannover.inform.iron.mapserver.exceptions.SearchResultsTooBigException;
import de.fhhannover.inform.iron.mapserver.exceptions.SystemErrorException;
import de.fhhannover.inform.iron.mapserver.messages.SearchRequest;
import de.fhhannover.inform.iron.mapserver.provider.DataModelServerConfigurationProvider;
import de.fhhannover.inform.iron.mapserver.utils.CollectionHelper;
import de.fhhannover.inform.iron.mapserver.utils.NullCheck;

/**
 * A basic implementation of the {@link SearchHandler} interface for the
 * standard search operation.
 *
 * @since 0.3.0
 * @author aw
 */
class BasicSearchHandler extends AbstractSearchHandler {
	private static final String sName = "BasicSearchHandler";

	private final int mMaxResultSize;
	private final boolean mIgnoreSize;
	private final int mAddBytes;
	private final DataModelServerConfigurationProvider mConf;
	private final ModifiableSearchResult mResult;

	BasicSearchHandler(
			SearchRequest sreq,
			ModifiableSearchResult sres,
			DataModelServerConfigurationProvider conf,
			int add, boolean ignoreSize,
			Publisher pub,
			IfmapPep pep) {
		super(sreq, pub, pep);
		NullCheck.check(sres, "sres is null");
		NullCheck.check(conf, "conf is null");
		mAddBytes = add;
		mIgnoreSize = ignoreSize;
		mConf = conf;
		mResult = sres;

		if (!sreq.maxSizeGiven()) {
			mMaxResultSize = mConf.getDefaultMaxSearchResultSize();
		} else {
			mMaxResultSize = sreq.getMaxResultSize();
		}
	}

	@Override
	public void onStart() {
		super.onStart();
		sLogger.debug(sName + ": starting search with parameters:");
		sLogger.debug("\tstartIdent=" + getStartIdentifier());
		sLogger.debug("\tmaxDepth=" + getMaxDepth());
		sLogger.debug("\tmaxSize=" + sizeString());
		sLogger.debug("\tmatch-links-filter=" + getMatchLinksFilter());
		sLogger.debug("\tresult-filter=" + getResultFilter());
		sLogger.debug("\tterminal-identifier-types=" + getTerminalIdentifiers());
	}

	@Override
	public void onNode(Node cur) throws SearchResultsTooBigException {
		appendToResult(cur);
		throwIfTooBig();
	}

	@Override
	public boolean travelLinksOf(Node cur) throws SearchException {
		return mCurDepth < mMaxDepth && !TerminalIdentifierChecker.isTerminalIdentifier(cur, mTermIdentTypes);
	}

	@Override
	public boolean travelLink(Link l) {
		List<MetadataHolder> matching;

		// Fast path out.
		if (wasVisited(l)) {
			return false;
		}

		// Matching and authorization is costly, cache the result.
		matching = authorized(l.getMetadataHolderInGraph(getMatchLinksFilter()));

		getVisitedElements().put(l, matching);

		return matching.size() > 0;
	}

	@Override
	public void onLink(Link l) throws SearchResultsTooBigException {
		List<MetadataHolder> matchingMetadata = getVisitedElements().get(l);
		if (matchingMetadata == null || matchingMetadata.size() == 0) {
			throw new SystemErrorException("on link which never asked for?");
		}

		appendToResult(l, matchingMetadata);
		throwIfTooBig();
	}

	@Override
	public boolean traverseTo(Node nextNode) {
		if (!wasVisited(nextNode)) {
			appendToBeVisited(nextNode);
			return true;
		}
		return false;
	}

	@Override
	public void afterNode(Node cur) {
		// nothing
	}

	@Override
	public void onEnd() {
		super.onEnd();
		sLogger.debug(sName + ": search finished:");
		sLogger.debug("\ttime=" + usedTime());
		sLogger.debug("\tused=" + usedBytesString());
	}

	private String usedTime() {
		return getEndTime() - getStartTime() + "";
	}

	/**
	 * Put everything of this {@link Node} into a {@link SearchResult}.
	 *
	 * @param n
	 */
	private void appendToResult(Node n) {
		List<MetadataHolder> mhlist =  n.getMetadataHolderInGraph(getResultFilter());
		List<Metadata> toAdd = CollectionHelper.provideListFor(Metadata.class);

		for (MetadataHolder mh : authorized(mhlist)) {
			toAdd.add(mh.getMetadata());
		}

		appendToResult(n, toAdd);
	}

	/**
	 * In the case of a {@link Link}, we only need to take the {@link Metadata}
	 * objects we got by using the {@link #mMatchLinksFilter} and match these
	 * with the {@link #mResultFilter}.
	 *
	 * NOTE: Authorization already took place here!
	 *
	 * @param link
	 * @param matchLinksMd
	 */
	private void appendToResult(Link link, List<MetadataHolder> matchLinksMd) {
		List<Metadata> toAdd = CollectionHelper.provideListFor(Metadata.class);

		for (MetadataHolder mh : matchLinksMd) {
			if (mh.getMetadata().matchesFilter(getResultFilter())) {
				toAdd.add(mh.getMetadata());
			}
		}

		appendToResult(link, toAdd);
	}

	private void appendToResult(GraphElement ge, List<Metadata> toAdd) {
		mResult.addMetadata(ge, toAdd);
	}
	private void appendToBeVisited(GraphElement ge) {
		getVisitedElements().put(ge, null);
	}

	private boolean wasVisited(GraphElement ge) {
		return getVisitedElements().containsKey(ge);
	}

	private void throwIfTooBig() throws SearchResultsTooBigException {
		if (resultIsTooBig()) {
			throw new SearchResultsTooBigException("SearchResult grew too big",
					mMaxResultSize, curByteCount());
		}
	}

	private boolean resultIsTooBig() {
		return !mIgnoreSize && curByteCount() > mMaxResultSize;
	}

	private int curByteCount() {
			return mResult.getByteCount() + mAddBytes;
	}

	private String usedBytesString() {
		return String.format("%d of %s bytes", curByteCount(), sizeString());
	}

	private String sizeString() {
		return mIgnoreSize ? "unlimited" : mMaxResultSize + "";
	}

}
