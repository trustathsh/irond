package de.fhhannover.inform.iron.mapserver;

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

import java.util.List;

import javax.xml.bind.Unmarshaller;

import org.apache.log4j.Logger;

import de.fhhannover.inform.iron.mapserver.binding.RequestUnmarshaller;
import de.fhhannover.inform.iron.mapserver.binding.RequestUnmarshallerFactory;
import de.fhhannover.inform.iron.mapserver.binding.ResultMarshaller;
import de.fhhannover.inform.iron.mapserver.binding.ResultMarshallerFactory;
import de.fhhannover.inform.iron.mapserver.communication.ClientIdentifier;
import de.fhhannover.inform.iron.mapserver.communication.bus.Queue;
import de.fhhannover.inform.iron.mapserver.communication.bus.messages.ActionSeries;
import de.fhhannover.inform.iron.mapserver.communication.bus.messages.Event;
import de.fhhannover.inform.iron.mapserver.communication.http.ActionProcessor;
import de.fhhannover.inform.iron.mapserver.communication.http.ChannelAcceptor;
import de.fhhannover.inform.iron.mapserver.communication.http.ChannelRep;
import de.fhhannover.inform.iron.mapserver.communication.ifmap.EventProcessor;
import de.fhhannover.inform.iron.mapserver.communication.ifmap.PollResultAvailableCallback;
import de.fhhannover.inform.iron.mapserver.communication.ifmap.SessionTimerFactory;
import de.fhhannover.inform.iron.mapserver.contentauth.IfmapPep;
import de.fhhannover.inform.iron.mapserver.contentauth.IfmapPepFactory;
import de.fhhannover.inform.iron.mapserver.datamodel.DataModelService;
import de.fhhannover.inform.iron.mapserver.datamodel.SubscriptionObserver;
import de.fhhannover.inform.iron.mapserver.datamodel.meta.Metadata;
import de.fhhannover.inform.iron.mapserver.datamodel.meta.MetadataFactory;
import de.fhhannover.inform.iron.mapserver.datamodel.meta.MetadataFactoryImpl;
import de.fhhannover.inform.iron.mapserver.datamodel.meta.MetadataTypeRepository;
import de.fhhannover.inform.iron.mapserver.datamodel.meta.MetadataTypeRepositoryImpl;
import de.fhhannover.inform.iron.mapserver.exceptions.AlreadyObservedException;
import de.fhhannover.inform.iron.mapserver.exceptions.ProviderInitializationException;
import de.fhhannover.inform.iron.mapserver.exceptions.ServerInitialException;
import de.fhhannover.inform.iron.mapserver.provider.AuthorizationProvider;
import de.fhhannover.inform.iron.mapserver.provider.AuthorizationProviderImpl;
import de.fhhannover.inform.iron.mapserver.provider.BasicAuthProvider;
import de.fhhannover.inform.iron.mapserver.provider.BasicAuthProviderPropImpl;
import de.fhhannover.inform.iron.mapserver.provider.LoggingProvider;
import de.fhhannover.inform.iron.mapserver.provider.PublisherIdGenerator;
import de.fhhannover.inform.iron.mapserver.provider.PublisherIdGeneratorImpl;
import de.fhhannover.inform.iron.mapserver.provider.PublisherIdProvider;
import de.fhhannover.inform.iron.mapserver.provider.PublisherIdProviderPropImpl;
import de.fhhannover.inform.iron.mapserver.provider.RandomSessionIdProvider;
import de.fhhannover.inform.iron.mapserver.provider.RoleMapperProvider;
import de.fhhannover.inform.iron.mapserver.provider.RoleMapperProviderPropImpl;
import de.fhhannover.inform.iron.mapserver.provider.SchemaProvider;
import de.fhhannover.inform.iron.mapserver.provider.SchemaProviderImpl;
import de.fhhannover.inform.iron.mapserver.provider.ServerConfigurationProvider;
import de.fhhannover.inform.iron.mapserver.provider.ServerConfigurationProviderPropImpl;
import de.fhhannover.inform.iron.mapserver.provider.SessionIdProvider;

/**
 * Entry point to run the IF-MAP Server implementation.
 * Dependencies are resolved here.
 *
 * @author aw
 */
public class Main {

	/**
	 * The name of the default configuration file.
	 */
	private static final String MAIN_CONFIGUARTION_FILE = "ifmap.properties";

	/**
	 * Our static logger instance
	 */
	private static Logger sLogger = LoggingProvider.getTheLogger();

	/**
	 * represents global queue for events.
	 */
	private Queue<Event> mEventQueue;

	/**
	 * represents the global queue for actions.
	 */
	private Queue<ActionSeries> mActionQueue;

	/**
	 * represents the {@link DataModelService} instance to be used.
	 */
	private DataModelService mDataModelService;

	/**
	 * represents the {@link ChannelRep} to be used by {@link ActionProcessor}
	 * and {@link ChannelAcceptor}.
	 */
	private ChannelRep mChannelRep;

	/**
	 * represents the {@link ServerConfigurationProvider} instance to be used.
	 */
	private ServerConfigurationProvider mServerConf;

	/**
	 * represents the {@link PublisherIdProvider} instance to be used.
	 */
	private PublisherIdProvider mPublisherIdProvider;

	/**
	 * represents the {@link SessionIdProvider} instance to be used.
	 */
	private SessionIdProvider mSessionIdProvider;

	/**
	 * represents the {@link ActionProcessor} instance which uses the action queue.
	 */
	private ActionProcessor mActionProcessor;

	/**
	 * represents the {@link EventProcessor} instance which uses the event queue.
	 */
	private EventProcessor mEventProcessor;

	/**
	 * represents the {@link RequestTransformer} to be used by the {@link EventProcessor}.
	 */
	private RequestUnmarshaller mRequestUnmarshaller;

	/**
	 * represents the {@link ResponseCreator} to be used by the {@link EventProcessor}.
	 */
	private ResultMarshaller mResultMarshaller;

	/**
	 * represents the {@link ChannelAcceptor} used to wait for connections.
	 */
	private ChannelAcceptor mChannelAcceptor;

	/**
	 * represents the object to be used for basic authentication. This is,
	 * checking if a username/password combination is correct.
	 */
	private BasicAuthProvider mBasicAuthProvider;

	/**
	 * This factory used to create timers in the {@link EventProcessor}.
	 */
	private SessionTimerFactory mSessionTimerFactory;

	/**
	 * represents the instance to be used to generate publisher-id's
	 */
	private PublisherIdGenerator mPublisherIdGenerator;

	/**
	 * represents the instance to used to query for authorization
	 */
	private AuthorizationProvider mAuthroizationProv;

	/**
	 * The callback object used by the {@link DataModelService}
	 */
	private SubscriptionObserver mCallback;

	/**
	 * The repository for all different metadata-types and their cardinalities.
	 */
	private MetadataTypeRepository mMetadataTypeReop;

	/**
	 * The factory to create actual {@link Metadata} instances.
	 */
	private MetadataFactory mMetadataFactory;

	/**
	 * The {@link SchemaProvider} used by the {@link Unmarshaller}.
	 */
	private SchemaProvider mSchemaProvider;

	/**
	 * The {@link IfmapPep} used by the {@link DataModelService}.
	 */
	private IfmapPep mPep;

	/**
	 * Used to map a {@link ClientIdentifier} to a {@link List} of roles.
	 */
	private RoleMapperProvider mClientRoleProvider;

	/**
	 * Main entry point.
	 *
	 * @throws ServerInitialException
	 */

	public static final String IROND_VERSION = "${project.version}";

	public Main() throws ServerInitialException {
		init();
	}

	private void init() throws ServerInitialException {

		try {
			mServerConf = new ServerConfigurationProviderPropImpl(MAIN_CONFIGUARTION_FILE);
			mPublisherIdProvider = new PublisherIdProviderPropImpl(mServerConf);
			mPublisherIdGenerator = new PublisherIdGeneratorImpl();
			mSessionIdProvider = new RandomSessionIdProvider();
			mBasicAuthProvider = new BasicAuthProviderPropImpl(mServerConf);
			mAuthroizationProv = new AuthorizationProviderImpl(mServerConf);
			mSchemaProvider = new SchemaProviderImpl(mServerConf);

			// This should dispatch between different RoleMappers, actually...
			mClientRoleProvider = new RoleMapperProviderPropImpl(mServerConf);
		} catch (ProviderInitializationException e) {
			throw new ServerInitialException("A Provider could not be initialized: " +
					e.getMessage());
		}

		// processor and queue part
		int eventForwarders = mServerConf.getEventProcessorForwardersCount();
		int eventWorkers = mServerConf.getEventProcessorWorkersCount();
		int actionForwarders = mServerConf.getActionProcessorForwardersCount();
		int actionWorkers = mServerConf.getActionProcessorWorkersCount();
		mEventQueue = new Queue<Event>();
		mActionQueue = new Queue<ActionSeries>();
		mEventProcessor = new EventProcessor(mEventQueue, eventWorkers, eventForwarders);
		mActionProcessor = new ActionProcessor(mActionQueue, actionWorkers, actionForwarders);
		mChannelRep = new ChannelRep();

		mMetadataTypeReop = MetadataTypeRepositoryImpl.newInstance();
		mMetadataFactory = MetadataFactoryImpl.newInstance(mMetadataTypeReop, mServerConf);
		mRequestUnmarshaller = RequestUnmarshallerFactory.newRequestUnmarshaller(
				mMetadataFactory, mSchemaProvider);
		mResultMarshaller = ResultMarshallerFactory.newResultMarshaller();

		mPep = IfmapPepFactory.newInstance(mServerConf, mClientRoleProvider);

		mDataModelService = DataModelService.newInstance(mServerConf, mPep);

		mSessionTimerFactory = new SessionTimerFactory(mEventQueue, mServerConf);

		mCallback = new PollResultAvailableCallback(mEventQueue);

		// initialize the event processor...
		mEventProcessor.setActionQueue(mActionQueue);
		mEventProcessor.setPublisherIdProv(mPublisherIdProvider);
		mEventProcessor.setSessionIdProv(mSessionIdProvider);
		mEventProcessor.setRequestUnmarshaller(mRequestUnmarshaller);
		mEventProcessor.setResultMarshaller(mResultMarshaller);
		mEventProcessor.setServerConfiguration(mServerConf);
		mEventProcessor.setDataModel(mDataModelService);
		mEventProcessor.setSessionTimerFactory(mSessionTimerFactory);
		mEventProcessor.setPublisherIdGenerator(mPublisherIdGenerator);
		mEventProcessor.setAuthorizationProv(mAuthroizationProv);

		try {
			mDataModelService.registerSubscriptionObserver(mCallback);
		} catch (AlreadyObservedException e) {
			throw new ServerInitialException("Could not register EventProcessor"
					+ " as SubscriptionObserver.");
		}

		// initialize the action processor
		mActionProcessor.setChannelRepository(mChannelRep);
		mActionProcessor.setEventQueue(mEventQueue);

		mChannelAcceptor = new ChannelAcceptor(mServerConf,
				mEventQueue, mChannelRep, mBasicAuthProvider);

		mChannelAcceptor.setUp();
	}

	public void goForIt() {
		mEventProcessor.start();
		mActionProcessor.start();
		mChannelAcceptor.start();
	}


	public static void main(String[] args) {
		sLogger.info("Starting irond version " + IROND_VERSION + " ...");
		try {
			Main main = new Main();
			main.goForIt();
			sLogger.info("irond is running :-)");
		} catch (ServerInitialException e) {
			sLogger.error("irond could not be started :-(");
			sLogger.error(e.getMessage());
		}
	}
}
