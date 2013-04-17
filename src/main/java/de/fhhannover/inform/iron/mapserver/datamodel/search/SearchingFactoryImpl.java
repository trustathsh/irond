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

import java.util.Set;

import de.fhhannover.inform.iron.mapserver.datamodel.Publisher;
import de.fhhannover.inform.iron.mapserver.datamodel.graph.GraphElement;
import de.fhhannover.inform.iron.mapserver.datamodel.graph.GraphElementRepository;
import de.fhhannover.inform.iron.mapserver.datamodel.graph.Node;
import de.fhhannover.inform.iron.mapserver.datamodel.identifiers.Identifier;
import de.fhhannover.inform.iron.mapserver.datamodel.meta.MetadataHolder;
import de.fhhannover.inform.iron.mapserver.messages.SearchRequest;
import de.fhhannover.inform.iron.mapserver.messages.SearchResultType;
import de.fhhannover.inform.iron.mapserver.provider.DataModelServerConfigurationProvider;

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
			boolean ignoreSize) {
		return new BasicSearchHandler(request, result, conf, additionalBytes,
				ignoreSize);
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
			Subscription sub, Set<GraphElement> visitedGraphElement,
			Set<MetadataHolder> newMeta, Set<Node> starters) {
		return new ContinueSearchHandler(start, depth, sub, visitedGraphElement,
				newMeta, starters);
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
