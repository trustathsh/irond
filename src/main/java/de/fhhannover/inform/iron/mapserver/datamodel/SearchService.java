package de.fhhannover.inform.iron.mapserver.datamodel;

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
 * This file is part of irond, version 0.4.1, implemented by the Trust@FHH
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

import org.apache.log4j.Logger;

import de.fhhannover.inform.iron.mapserver.IfmapConstStrings;
import de.fhhannover.inform.iron.mapserver.datamodel.graph.GraphElementRepository;
import de.fhhannover.inform.iron.mapserver.datamodel.search.ModifiableSearchResult;
import de.fhhannover.inform.iron.mapserver.datamodel.search.SearchHandler;
import de.fhhannover.inform.iron.mapserver.datamodel.search.SearchResult;
import de.fhhannover.inform.iron.mapserver.datamodel.search.Searcher;
import de.fhhannover.inform.iron.mapserver.datamodel.search.SearchingFactory;
import de.fhhannover.inform.iron.mapserver.exceptions.SearchResultsTooBigException;
import de.fhhannover.inform.iron.mapserver.messages.SearchRequest;
import de.fhhannover.inform.iron.mapserver.provider.DataModelServerConfigurationProvider;
import de.fhhannover.inform.iron.mapserver.provider.LoggingProvider;

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

	
	SearchService(GraphElementRepository graph, PublisherRep pRep,
			SearchingFactory sresFac, DataModelServerConfigurationProvider conf) {
		mGraph = graph;
		mPublisherRep = pRep;
		mSearchingFactory = sresFac;
		mAdd = IfmapConstStrings.SRES_MIN_CNT;
		mConf = conf;
	}
	
	/**
	 * Here the search is processed with the help
	 * of a searcher and a builder object who really do the hard work together.
	 * 
	 * @param req
	 * @return
	 * @throws SearchResultsTooBigException 
	 */
	SearchResult search(SearchRequest req) throws SearchResultsTooBigException {
	
		Publisher p = mPublisherRep.getPublisherBySessionId(req.getSessionId());
		
		logger.trace("SearchService: search for " + p.getPublisherId());
		
		ModifiableSearchResult res = mSearchingFactory.newCopySearchResult();
		SearchHandler handler = mSearchingFactory.newBasicSearchHandler(mConf,
				req, res, mAdd, false);
		Searcher searcher = mSearchingFactory.newSearcher(mGraph, handler);
		searcher.runSearch();
		return res;
	}
}
 
