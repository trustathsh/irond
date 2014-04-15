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
 * This file is part of irond, version 0.4.2, implemented by the Trust@FHH
 * research group at the Fachhochschule Hannover.
 * 
 * irond is an an *experimental* IF-MAP 2.0 compliant MAP server written in
 * JAVA. irond supports both basic authentication and certificate-based 
 * authentication (using X.509 certificates) of MAP clients. irond is
 * maintained by the Trust@FHH group at the Fachhochschule Hannover, initial
 * developement was carried out during the ESUKOM research project.
 * %%
 * Copyright (C) 2010 - 2014 Trust@FHH
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
import de.fhhannover.inform.iron.mapserver.trust.TrustService;

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
			boolean ignoreSize, TrustService trustService);

	public SearchHandler newContinueSearchHandler(Identifier start, int depth,
			Subscription sub, Set<GraphElement> visitedGraphElement,
			Set<MetadataHolder> newMeta, Set<Node> starters);

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
