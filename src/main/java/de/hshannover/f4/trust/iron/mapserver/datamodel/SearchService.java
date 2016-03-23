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
package de.hshannover.f4.trust.iron.mapserver.datamodel;


import org.apache.log4j.Logger;

import de.hshannover.f4.trust.iron.mapserver.IfmapConstStrings;
import de.hshannover.f4.trust.iron.mapserver.contentauth.IfmapPep;
import de.hshannover.f4.trust.iron.mapserver.datamodel.graph.GraphElementRepository;
import de.hshannover.f4.trust.iron.mapserver.datamodel.search.ModifiableSearchResult;
import de.hshannover.f4.trust.iron.mapserver.datamodel.search.SearchHandler;
import de.hshannover.f4.trust.iron.mapserver.datamodel.search.SearchResult;
import de.hshannover.f4.trust.iron.mapserver.datamodel.search.Searcher;
import de.hshannover.f4.trust.iron.mapserver.datamodel.search.SearchingFactory;
import de.hshannover.f4.trust.iron.mapserver.exceptions.SearchException;
import de.hshannover.f4.trust.iron.mapserver.exceptions.SearchResultsTooBigException;
import de.hshannover.f4.trust.iron.mapserver.messages.SearchRequest;
import de.hshannover.f4.trust.iron.mapserver.provider.DataModelServerConfigurationProvider;
import de.hshannover.f4.trust.iron.mapserver.provider.LoggingProvider;

/**
 * This class is a sub service and handles {@link SearchRequest} messages
 *
 * @author aw, vp
 * @since 0.1
 */
class SearchService {

	private static Logger logger;
	static {
		logger = LoggingProvider.getTheLogger();
	}

	private final GraphElementRepository mGraph;
	private final PublisherRep mPublisherRep;
	private final SearchingFactory mSearchingFactory;
	private final int mAdd;
	private final DataModelServerConfigurationProvider mConf;
	private final IfmapPep mPep;

	SearchService(DataModelParams params) {
		mGraph = params.graph;
		mPublisherRep = params.pubRep;
		mSearchingFactory = params.searchFac;
		mConf = params.conf;
		mPep = params.pep;
		mAdd = IfmapConstStrings.SRES_MIN_CNT;
	}

	/**
	 * Here the search is processed with the help
	 * of a searcher and a builder object who really do the hard work together.
	 *
	 * @param req
	 * @return
	 * @throws SearchResultsTooBigException
	 * @throws SearchException
	 */
	SearchResult search(SearchRequest req) throws SearchResultsTooBigException, SearchException {

		Publisher p = mPublisherRep.getPublisherBySessionId(req.getSessionId());

		logger.trace("SearchService: search for " + p.getPublisherId());

		ModifiableSearchResult res = mSearchingFactory.newCopySearchResult();
		SearchHandler handler = mSearchingFactory.newBasicSearchHandler(mConf,
				req, res, mAdd, false, p, mPep);
		Searcher searcher = mSearchingFactory.newSearcher(mGraph, handler);
		searcher.runSearch();
		return res;
	}
}

