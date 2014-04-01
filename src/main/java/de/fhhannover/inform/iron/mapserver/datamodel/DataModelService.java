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

import java.util.Collection;

import de.fhhannover.inform.iron.mapserver.datamodel.graph.GraphElementRepository;
import de.fhhannover.inform.iron.mapserver.datamodel.graph.GraphElementRepositoryImpl;
import de.fhhannover.inform.iron.mapserver.datamodel.graph.Node;
import de.fhhannover.inform.iron.mapserver.datamodel.identifiers.Identifier;
import de.fhhannover.inform.iron.mapserver.datamodel.meta.MetadataHolderFactory;
import de.fhhannover.inform.iron.mapserver.datamodel.meta.MetadataHolderFactoryImpl;
import de.fhhannover.inform.iron.mapserver.datamodel.search.PollResult;
import de.fhhannover.inform.iron.mapserver.datamodel.search.SearchResult;
import de.fhhannover.inform.iron.mapserver.datamodel.search.SearchingFactory;
import de.fhhannover.inform.iron.mapserver.datamodel.search.SearchingFactoryImpl;
import de.fhhannover.inform.iron.mapserver.exceptions.AccessDeniedException;
import de.fhhannover.inform.iron.mapserver.exceptions.AlreadyObservedException;
import de.fhhannover.inform.iron.mapserver.exceptions.InvalidMetadataException;
import de.fhhannover.inform.iron.mapserver.exceptions.NoPollResultAvailableException;
import de.fhhannover.inform.iron.mapserver.exceptions.NoSuchSubscriptionException;
import de.fhhannover.inform.iron.mapserver.exceptions.ParameterException;
import de.fhhannover.inform.iron.mapserver.exceptions.PollResultsTooBigException;
import de.fhhannover.inform.iron.mapserver.exceptions.PurgePublisherNoAllowedException;
import de.fhhannover.inform.iron.mapserver.exceptions.ResponseCreationException;
import de.fhhannover.inform.iron.mapserver.exceptions.SearchResultsTooBigException;
import de.fhhannover.inform.iron.mapserver.exceptions.SystemErrorException;
import de.fhhannover.inform.iron.mapserver.messages.DumpResult;
import de.fhhannover.inform.iron.mapserver.messages.PublishRequest;
import de.fhhannover.inform.iron.mapserver.messages.SearchRequest;
import de.fhhannover.inform.iron.mapserver.messages.SubscribeRequest;
import de.fhhannover.inform.iron.mapserver.provider.DataModelServerConfigurationProvider;
import de.fhhannover.inform.iron.mapserver.trust.TrustService;
import de.fhhannover.inform.iron.mapserver.utils.CollectionHelper;
import de.fhhannover.inform.iron.mapserver.utils.NullCheck;

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
	 * this static fields represents the current serverConfiguration to
	 * be used.
	 * Necessary for the case sensitive settings and the purge publisher
	 * operation.
	 * 
	 * FIXME: This is currently overridden by each newInstance() call.
	 * Not really what we want, but I don't see a way around this right now.
	 */
	private static DataModelServerConfigurationProvider sServerConfiguration;
	
	private final PublishService publishService;
	private final ClientService clientService;
	private final SearchService searchService;
	private final SubscriptionService	subscriptionService;
	private final PublisherRep publisherRep;
	private final GraphElementRepository mGraph;
	private final MetadataHolderFactory mMetaHolderFac;
	private final SearchingFactory mSearchingFac;
	
	private DataModelService(DataModelServerConfigurationProvider serverConf, TrustService trustService) {
	
		publisherRep = new PublisherRep();
		
		mGraph = GraphElementRepositoryImpl.newInstance();
		mMetaHolderFac = MetadataHolderFactoryImpl.newInstance();
		mSearchingFac = SearchingFactoryImpl.newInstance();

		searchService = new SearchService(mGraph, publisherRep, mSearchingFac,
				sServerConfiguration, trustService);
		
		subscriptionService = new SubscriptionService(mGraph, publisherRep,
				mSearchingFac, trustService);
		
		publishService = new PublishService(publisherRep, mGraph,
				mMetaHolderFac, subscriptionService, serverConf, trustService);
		
		clientService = new ClientService(publisherRep, subscriptionService, trustService);
	}
 
	
	/**
	 * Returns a <b>new </b> {@link DataModelService} instance.
	 * 
	 * Note:
	 * Calling this twice gives two completely independent DataModels. Might
	 * be confusing but it's nice at the same time.
	 * 
	 * @return a <b>new</b> instance of a {@link DataModelService} instance
	 */
	public static DataModelService newInstance(DataModelServerConfigurationProvider serverConf, TrustService metadatafactory) {
		NullCheck.check(serverConf, "serverConf is null");
		sServerConfiguration = serverConf;
		
		return new DataModelService(serverConf, metadatafactory);
	}
	
	public static DataModelServerConfigurationProvider getServerConfiguration() {
		if (sServerConfiguration == null)
			throw new SystemErrorException("DataModelService: ServerConfiguration"
					+ " not initialized");
		
		return sServerConfiguration;
	}

	public static void setServerConfiguration(DataModelServerConfigurationProvider prov) {
		sServerConfiguration = prov;
	}
	
	/**
	 * A operation of publish is for create, modify or delete the metadata by
	 * one or more identifiers or links.
	 *
	 * @param PublishRequest
	 * @return publishReceived
	 * @throws ParameterException 
	 * @throws NotSupportedException 
	 * @throws InvalidMetadataException 
	 * @throws AccessDeniedException 
	 * @throws ResponseCreationException 
	 */
	synchronized public void publish(PublishRequest request) throws InvalidMetadataException, AccessDeniedException {
		checkNull(request);
		publishService.publish(request);
	}


	synchronized public SearchResult search(SearchRequest request) throws SearchResultsTooBigException {
		checkNull(request);
		return searchService.search(request);
	}
	 
	synchronized public void purgePublisher(String sessionId, String publisherId)
											throws PurgePublisherNoAllowedException {
		checkNull(sessionId);
		checkNull(publisherId);
		clientService.purgePublisher(sessionId, publisherId);
	}
	
	synchronized public void newSession(String sessionId, String publisherId, Integer mprs) {
		checkNull(sessionId);
		checkNull(publisherId);
		clientService.newSession(sessionId, publisherId, mprs);
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
		
		for (Node n : mGraph.getAllNodes())
			idents.add(n.getIdentifier());
		
		result.setIdentifier(idents);
		result.setLastUpdateTime(subscriptionService.getLogicalTimeStamp());
		return result;
	}
	
	synchronized public void subscribe(SubscribeRequest request) throws NoSuchSubscriptionException {
		checkNull(request);
		subscriptionService.subscribe(request);
	}

	@Override
	synchronized public PollResult getPollResultFor(String sessionId)
			throws NoPollResultAvailableException, PollResultsTooBigException {
		checkNull(sessionId);
		return subscriptionService.getPollResultFor(sessionId);
	}

	@Override
	synchronized public void registerSubscriptionObserver(SubscriptionObserver subObs)
			throws AlreadyObservedException {
		checkNull(subObs);
		subscriptionService.setSubscriptionObserver(subObs);
	}
	
	private void checkNull(Object obj) throws NullPointerException {
		if (obj == null) {
			throw new NullPointerException("null was given");
		}
	}
}
