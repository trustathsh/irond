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
 * This file is part of irond, version 0.5.1, implemented by the Trust@HsH
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

public class SearchingFactoryImpl implements SearchingFactory {

	private SearchingFactoryImpl() { }

	@Override
	public Searcher newSearcher(GraphElementRepository graph,
			SearchHandler handler) {
		return new SearcherImpl(graph,  handler);
	}

	@Override
	public SearchHandler newBasicSearchHandler(
			DataModelServerConfigurationProvider conf, SearchRequest request,
			ModifiableSearchResult result, int additionalBytes,
			boolean ignoreSize, Publisher pub, IfmapPep pep) {
		return new BasicSearchHandler(request, result, conf, additionalBytes,
				ignoreSize, pub, pep);
	}

	public static SearchingFactory newInstance() {
		return new SearchingFactoryImpl();
	}

	@Override
	public ModifiableSearchResult newReferenceSearchResult() {
		return new ReferenceSearchResultImpl();
	}

	@Override
	public ModifiableSearchResult newCopySearchResult(String name, SearchResultType type) {
		return new CopySearchResultImpl(name, type);
	}

	@Override
	public ModifiableSearchResult newCopySearchResult(String name) {
		return newCopySearchResult(name, SearchResultType.SEARCH);
	}

	@Override
	public ModifiableSearchResult newCopySearchResult() {
		return newCopySearchResult(null);
	}

	@Override
	public Subscription newSubscription(Publisher pub, String name,
			SearchRequest searchRequest) {
		return new SubscriptionImpl(pub, name, searchRequest);
	}

	@Override
	public SearchHandler newContinueSearchHandler(Identifier start, int depth,
			Subscription sub, Map<GraphElement, List<MetadataHolder>> visitedGraphElement,
			Set<MetadataHolder> newMeta, Set<Node> starters,
			Publisher pub, IfmapPep pep) {
		return new ContinueSearchHandler(start, depth, sub, visitedGraphElement,
				newMeta, starters, pub, pep);
	}

	@Override
	public SearchHandler newDeleteSearchHandler(Identifier start, int depth,
			Subscription sub, Set<MetadataHolder> newMeta, Set<Node> starters) {
		return new DeleteSearchHandler(start, depth, sub, newMeta, starters);
	}

	@Override
	public SearchHandler newCleanupSearchHandler(Identifier start, Subscription sub) {
		return new CleanupSearchHandler(start, sub);
	}
}
