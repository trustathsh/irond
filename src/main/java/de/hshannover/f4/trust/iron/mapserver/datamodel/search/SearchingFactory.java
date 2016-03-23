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
 * This file is part of irond, version 0.5.8, implemented by the Trust@HsH
 * research group at the Hochschule Hannover.
 * %%
 * Copyright (C) 2010 - 2016 Trust@HsH
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

import de.hshannover.f4.trust.iron.mapserver.contentauth.IfmapPep;
import de.hshannover.f4.trust.iron.mapserver.datamodel.Publisher;
import de.hshannover.f4.trust.iron.mapserver.datamodel.graph.GraphElement;
import de.hshannover.f4.trust.iron.mapserver.datamodel.graph.GraphElementRepository;
import de.hshannover.f4.trust.iron.mapserver.datamodel.graph.Node;
import de.hshannover.f4.trust.iron.mapserver.datamodel.identifiers.Identifier;
import de.hshannover.f4.trust.iron.mapserver.datamodel.meta.MetadataHolder;
import de.hshannover.f4.trust.iron.mapserver.messages.SearchRequest;
import de.hshannover.f4.trust.iron.mapserver.messages.SearchResultType;
import de.hshannover.f4.trust.iron.mapserver.provider.DataModelServerConfigurationProvider;

/**
 * Interface to create {@link SearchResult} implementations.
 *
 * @since 0.3.0
 * @author aw
 *
 */
public interface SearchingFactory {

	/**
	 * Create a new {@link Searcher} instance.
	 *
	 * @param graph
	 * @param request
	 * @param res
	 * @param additionalBytes
	 * @return
	 */
	public Searcher newSearcher(GraphElementRepository graph, SearchHandler handler);

	public SearchHandler newBasicSearchHandler(
			DataModelServerConfigurationProvider conf, SearchRequest request,
			ModifiableSearchResult result, int additionalBytes,
			boolean ignoreSize, Publisher pub, IfmapPep pep);

	public SearchHandler newContinueSearchHandler(Identifier start, int depth,
			Subscription sub, Map<GraphElement, List<MetadataHolder>> visitedGraphElement,
			Set<MetadataHolder> newMeta, Set<Node> starters, Publisher pub,
			IfmapPep pep);

	public SearchHandler newDeleteSearchHandler(Identifier start, int depth,
			Subscription sub, Set<MetadataHolder> newMeta,
			Set<Node> starters);

	public SearchHandler newCleanupSearchHandler(Identifier start, Subscription sub);

	/**
	 * @return a {@link SearchResult} implementation, storing the
	 * {@link GraphElement} instances from the graph, so comparison by
	 * reference is easily possible.
	 */
	public ModifiableSearchResult newReferenceSearchResult();

	/**
	 * @return a {@link SearchResult} implementation, using the
	 * {@link GraphElement} instances, returned by {@link GraphElement#dummy()},
	 * thereby using immutable objects in {@link ResultItem} instances.
	 *
	 * @param name
	 * @param type
	 * @return
	 */
	public ModifiableSearchResult newCopySearchResult(String name,
			SearchResultType type);

	/**
	 * @return a {@link SearchResult} implementation, using the
	 * {@link GraphElement} instances, returned by {@link GraphElement#dummy()},
	 * thereby using immutable objects in {@link ResultItem} instances.
	 *
	 * The returned {@link SearchResult} is of {@link SearchResultType}
	 * {@link SearchResultType#SEARCH}.
	 *
	 * @param name
	 * @param type
	 * @return
	 */
	public ModifiableSearchResult newCopySearchResult(String name);

	/**
	 * @return a {@link SearchResult} implementation, using the
	 * {@link GraphElement} instances, returned by {@link GraphElement#dummy()},
	 * thereby using immutable objects in {@link ResultItem} instances.
	 *
	 * The returned {@link SearchResult} is of {@link SearchResultType}
	 * {@link SearchResultType#SEARCH} and has no name set.
	 *
	 * @param name
	 * @param type
	 * @return
	 */
	public ModifiableSearchResult newCopySearchResult();

	/**
	 * Create a new {@link Subscription} instance.
	 *
	 * @param pub
	 * @param name
	 * @param searchRequest
	 * @return
	 */
	public Subscription newSubscription(Publisher pub, String name,
			SearchRequest searchRequest);

}
