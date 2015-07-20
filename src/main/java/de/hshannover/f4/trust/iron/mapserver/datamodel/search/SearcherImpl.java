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


import java.util.Set;

import de.hshannover.f4.trust.iron.mapserver.datamodel.graph.GraphElementRepository;
import de.hshannover.f4.trust.iron.mapserver.datamodel.graph.Link;
import de.hshannover.f4.trust.iron.mapserver.datamodel.graph.Node;
import de.hshannover.f4.trust.iron.mapserver.datamodel.identifiers.Identifier;
import de.hshannover.f4.trust.iron.mapserver.datamodel.meta.Metadata;
import de.hshannover.f4.trust.iron.mapserver.exceptions.SearchException;
import de.hshannover.f4.trust.iron.mapserver.exceptions.SearchResultsTooBigException;
import de.hshannover.f4.trust.iron.mapserver.utils.CollectionHelper;
import de.hshannover.f4.trust.iron.mapserver.utils.NullCheck;

/**
 * This class does the real searching using a {@link SearchResultBuilder}.
 *
 * @author aw
 * @since 0.1.0
 */
class SearcherImpl implements Searcher {

	private final GraphElementRepository mGraph;
	private final SearchHandler mHandler;

	/**
	 * Instantiate a {@link Searcher} using a {@link SearchHandler}.
	 *
	 * @param graph	reference to the {@link GraphElementRepository} instance
	 * @param handler the {@link SearchHandler} instance to be used.
	 */
	SearcherImpl(GraphElementRepository graph, SearchHandler handler) {
		NullCheck.check(graph, "graph is null");
		NullCheck.check(handler, "handler is null");
		mGraph = graph;
		mHandler = handler;
	}

	/* (non-Javadoc)
	 * @see de.hshannover.f4.trust.iron.mapserver.datamodel.search.Searcher#runSearch(boolean)
	 */
	@Override
	public void runSearch() throws SearchResultsTooBigException, SearchException {
		Set<Node> nodes = CollectionHelper.provideSetFor(Node.class);

		mHandler.onStart();

		nodes = traverse(mGraph.getNodeFor(mHandler.getStartIdentifier()));

		while (nodes.size() > 0) {
			nodes = traverseDepth(nodes);
		}

		mHandler.onEnd();
	}

	/**
	 * This part represents the "real" algorithm using a {@link SearchHandler}
	 * instance for the important paths.
	 *
	 * This is a description which was valid for an older version, but still here
	 * for reference.
	 *
	 * The search algorithm is best looked up in the
	 * Specification. But a short overview is given here:
	 *
	 * - Add the current {@link Identifier} with its {@link Metadata} objects
	 *
	 * - Check the depth
	 *
	 * - Check if the current {@link Identifier} object has a type which
	 *   is in the list of the terminal identifier types
	 *
	 * - Visit each {@link Link} of the current {@link Identifier} match
	 *   all {@link Metadata} objects against the match links filter.
	 *
	 * - If any {@link Metadata} object matched the match links filter add this
	 *   {@link Link} object with every matched metadata.
	 *
	 * - Visit the "other end" of the {@link Link} and begin the
	 *   search algorithm with this {@link Identifier} and a depth
	 *   incremented by one.
	 *
	 * @param cur
	 * @param depth
	 * @param prev
	 * @throws SearchResultsTooBigException
	 * @throws SearchException If something was wrong with search request
	 */
	private Set<Node> traverse(Node cur) throws SearchResultsTooBigException, SearchException {
		NullCheck.check(cur, "search went wrong");
		Set<Node> ret = CollectionHelper.provideSetFor(Node.class);

		mHandler.onNode(cur);

		if (!mHandler.travelLinksOf(cur)) {
			return ret;
		}


		for (Link l : cur.getLinks()) {

			// Should we skip it?
			if (!mHandler.travelLink(l)) {
				continue;
			}

			mHandler.onLink(l);

			// Is the neighbor node subjected to be visited?
			if (mHandler.traverseTo(l.getNeighborNode(cur))) {
				ret.add(l.getNeighborNode(cur));
			}
		}

		mHandler.afterNode(cur);
		return ret;
	}

	/**
	 * @return {@link Set} of {@link Node} instances for the next depth.
	 * @throws SearchResultsTooBigException
	 * @throws SearchException If something was wrong with the search request
	 */
	private Set<Node> traverseDepth(Set<Node> nextNodes) throws SearchResultsTooBigException, SearchException {
		Set<Node> ret = CollectionHelper.provideSetFor(Node.class);
		mHandler.nextDepth();

		for (Node next : nextNodes) {
			ret.addAll(traverse(next));
		}

		return ret;
	}
}
