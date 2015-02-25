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
 * This file is part of irond, version 0.5.2, implemented by the Trust@HsH
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
package de.hshannover.f4.trust.iron.mapserver.datamodel;


import java.util.Collection;

import de.hshannover.f4.trust.iron.mapserver.communication.ClientIdentifier;
import de.hshannover.f4.trust.iron.mapserver.contentauth.IfmapPep;
import de.hshannover.f4.trust.iron.mapserver.datamodel.graph.GraphElementRepository;
import de.hshannover.f4.trust.iron.mapserver.datamodel.graph.GraphElementRepositoryImpl;
import de.hshannover.f4.trust.iron.mapserver.datamodel.graph.Node;
import de.hshannover.f4.trust.iron.mapserver.datamodel.identifiers.Identifier;
import de.hshannover.f4.trust.iron.mapserver.datamodel.meta.MetadataHolderFactory;
import de.hshannover.f4.trust.iron.mapserver.datamodel.meta.MetadataHolderFactoryImpl;
import de.hshannover.f4.trust.iron.mapserver.datamodel.search.PollResult;
import de.hshannover.f4.trust.iron.mapserver.datamodel.search.SearchResult;
import de.hshannover.f4.trust.iron.mapserver.datamodel.search.SearchingFactory;
import de.hshannover.f4.trust.iron.mapserver.datamodel.search.SearchingFactoryImpl;
import de.hshannover.f4.trust.iron.mapserver.exceptions.AccessDeniedException;
import de.hshannover.f4.trust.iron.mapserver.exceptions.AlreadyObservedException;
import de.hshannover.f4.trust.iron.mapserver.exceptions.InvalidMetadataException;
import de.hshannover.f4.trust.iron.mapserver.exceptions.NoPollResultAvailableException;
import de.hshannover.f4.trust.iron.mapserver.exceptions.NoSuchSubscriptionException;
import de.hshannover.f4.trust.iron.mapserver.exceptions.PollResultsTooBigException;
import de.hshannover.f4.trust.iron.mapserver.exceptions.PurgePublisherNoAllowedException;
import de.hshannover.f4.trust.iron.mapserver.exceptions.SearchException;
import de.hshannover.f4.trust.iron.mapserver.exceptions.SearchResultsTooBigException;
import de.hshannover.f4.trust.iron.mapserver.exceptions.SystemErrorException;
import de.hshannover.f4.trust.iron.mapserver.messages.DumpResult;
import de.hshannover.f4.trust.iron.mapserver.messages.PublishRequest;
import de.hshannover.f4.trust.iron.mapserver.messages.SearchRequest;
import de.hshannover.f4.trust.iron.mapserver.messages.SubscribeRequest;
import de.hshannover.f4.trust.iron.mapserver.provider.DataModelServerConfigurationProvider;
import de.hshannover.f4.trust.iron.mapserver.utils.CollectionHelper;
import de.hshannover.f4.trust.iron.mapserver.utils.NullCheck;

/**
 * Entry Points for all Operations.
 *
 * This class delegates all calls to the corresponding service objects.
 *
 * @since 0.1.0
 * @author awe, vp
 *
 */
public class DataModelService implements SubscriptionNotifier {

	/**
	 * This static field represents the current serverConfiguration to
	 * be used.
	 * Necessary for the case sensitive settings and the purge publisher
	 * operation.
	 *
	 * FIXME: This is currently overridden by each newInstance() call.
	 * Not really what we want, but I don't see a way around this right now.
	 */
	private static DataModelServerConfigurationProvider sServerConf;

	private final PublishService publishService;
	private final ClientService clientService;
	private final SearchService searchService;
	private final SubscriptionService	subService;
	private final PublisherRep mPublisherRep;
	private final GraphElementRepository mGraph;
	private final MetadataHolderFactory mMetaHolderFac;
	private final SearchingFactory mSearchingFac;
	private final IfmapPep mPep;

	private DataModelService(IfmapPep pep) {

		mPublisherRep = new PublisherRep();
		mGraph = GraphElementRepositoryImpl.newInstance();
		mMetaHolderFac = MetadataHolderFactoryImpl.newInstance();
		mSearchingFac = SearchingFactoryImpl.newInstance();
		mPep = pep;

		DataModelParams params = new DataModelParams(mPep, sServerConf,
				mGraph, mPublisherRep, mMetaHolderFac, mSearchingFac);

		searchService = new SearchService(params);
		subService = new SubscriptionService(params);
		publishService = new PublishService(mPublisherRep, mGraph, mMetaHolderFac, subService, sServerConf);
		clientService = new ClientService(params, subService);
	}


	/**
	 * Returns a <b>new</b> {@link DataModelService} instance.
	 *
	 * Note:
	 * Calling this twice gives two completely independent DataModels. Might
	 * be confusing but it's nice at the same time.
	 *
	 * @return a <b>new</b> instance of a {@link DataModelService} instance
	 */
	public static DataModelService newInstance(
			DataModelServerConfigurationProvider serverConf,
			IfmapPep pep) {
		NullCheck.check(serverConf, "serverConf is null");
		NullCheck.check(pep, "pep is null");
		sServerConf = serverConf;

		return new DataModelService(pep);
	}

	public static DataModelServerConfigurationProvider getServerConfiguration() {
		if (sServerConf == null) {
			throw new SystemErrorException("DataModelService: ServerConfiguration"
					+ " not initialized");
		}

		return sServerConf;
	}

	public static void setServerConfiguration(DataModelServerConfigurationProvider prov) {
		sServerConf = prov;
	}

	/**
	 * A operation of publish is for create, modify or delete the metadata by
	 * one or more identifiers or links.
	 *
	 * @param PublishRequest
	 * @return publishReceived
	 * @throws InvalidMetadataException
	 * @throws AccessDeniedException
	 */
	synchronized public void publish(PublishRequest request) throws InvalidMetadataException, AccessDeniedException {
		checkNull(request);
		Publisher pub = getPublisher(request);

		if (!mPep.isAuthorized(pub, request, mGraph)) {
			throw new AccessDeniedException("not allowed");
		}


		publishService.publish(request);
	}


	/**
	 * Helper to get the {@link Publisher} object for this
	 *
	 *
	 * @param request
	 * @return
	 */
	private Publisher getPublisher(PublishRequest request) {
		return mPublisherRep.getPublisherBySessionId(request.getSessionId());
	}


	synchronized public SearchResult search(SearchRequest request) throws SearchResultsTooBigException, SearchException {
		checkNull(request);
		return searchService.search(request);
	}

	synchronized public void purgePublisher(String sessionId, String publisherId)
											throws PurgePublisherNoAllowedException,
											AccessDeniedException {
		checkNull(sessionId);
		checkNull(publisherId);
		clientService.purgePublisher(sessionId, publisherId);
	}

	synchronized public void newSession(String sessionId, String publisherId,
									    Integer mprs,
									    ClientIdentifier clientId) {
		checkNull(sessionId);
		checkNull(publisherId);
		checkNull(clientId);
		clientService.newSession(sessionId, publisherId, mprs, clientId);
	}

	synchronized public void endSession(String sessionId) {
		checkNull(sessionId);
		clientService.endSession(sessionId);
	}

	/*
	 * Special Operations for visualization
	 */
	synchronized public DumpResult dump(String sessionId) {
		checkNull(sessionId);

		DumpResult result = new DumpResult();
		Collection<Identifier> idents =
			CollectionHelper.provideCollectionFor(Identifier.class);

		for (Node n : mGraph.getAllNodes()) {
			idents.add(n.getIdentifier());
		}

		result.setIdentifier(idents);
		result.setLastUpdateTime(subService.getLogicalTimeStamp());
		return result;
	}

	synchronized public void subscribe(SubscribeRequest request) throws NoSuchSubscriptionException, SearchException {
		checkNull(request);

		subService.subscribe(request);
	}

	@Override
	synchronized public PollResult getPollResultFor(String sessionId)
			throws NoPollResultAvailableException, PollResultsTooBigException {
		checkNull(sessionId);
		return subService.getPollResultFor(sessionId);
	}

	@Override
	synchronized public void registerSubscriptionObserver(SubscriptionObserver subObs)
			throws AlreadyObservedException {
		checkNull(subObs);
		subService.setSubscriptionObserver(subObs);
	}

	private void checkNull(Object obj) throws NullPointerException {
		NullCheck.check(obj, "null was given");
	}
}

class DataModelParams {

	public DataModelParams(IfmapPep pep,
			DataModelServerConfigurationProvider conf,
			GraphElementRepository graph, PublisherRep pubRep,
			MetadataHolderFactory metaHolderFac, SearchingFactory searchFac) {

		this.pep = pep;
		this.conf = conf;
		this.graph = graph;
		this.pubRep = pubRep;
		this.metaHolderFac = metaHolderFac;
		this.searchFac = searchFac;
	}
	final IfmapPep pep;
	final DataModelServerConfigurationProvider conf;
	final GraphElementRepository graph;
	final PublisherRep pubRep;
	final MetadataHolderFactory metaHolderFac;
	final SearchingFactory searchFac;
}
