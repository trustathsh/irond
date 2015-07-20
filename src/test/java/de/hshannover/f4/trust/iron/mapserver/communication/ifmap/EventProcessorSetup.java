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
package de.hshannover.f4.trust.iron.mapserver.communication.ifmap;


import de.hshannover.f4.trust.iron.mapserver.binding.RequestUnmarshaller;
import de.hshannover.f4.trust.iron.mapserver.binding.RequestUnmarshallerFactory;
import de.hshannover.f4.trust.iron.mapserver.binding.ResultMarshaller;
import de.hshannover.f4.trust.iron.mapserver.binding.ResultMarshallerFactory;
import de.hshannover.f4.trust.iron.mapserver.communication.bus.Queue;
import de.hshannover.f4.trust.iron.mapserver.communication.bus.messages.ActionSeries;
import de.hshannover.f4.trust.iron.mapserver.communication.bus.messages.Event;
import de.hshannover.f4.trust.iron.mapserver.contentauth.IfmapPep;
import de.hshannover.f4.trust.iron.mapserver.datamodel.DataModelService;
import de.hshannover.f4.trust.iron.mapserver.datamodel.meta.MetadataFactory;
import de.hshannover.f4.trust.iron.mapserver.datamodel.meta.MetadataFactoryImpl;
import de.hshannover.f4.trust.iron.mapserver.datamodel.meta.MetadataTypeRepository;
import de.hshannover.f4.trust.iron.mapserver.datamodel.meta.MetadataTypeRepositoryImpl;
import de.hshannover.f4.trust.iron.mapserver.exceptions.AlreadyObservedException;
import de.hshannover.f4.trust.iron.mapserver.exceptions.ProviderInitializationException;
import de.hshannover.f4.trust.iron.mapserver.provider.SchemaProvider;
import de.hshannover.f4.trust.iron.mapserver.provider.SchemaProviderImpl;
import de.hshannover.f4.trust.iron.mapserver.provider.ServerConfigurationProvider;
import de.hshannover.f4.trust.iron.mapserver.provider.StubProvider;
import de.hshannover.f4.trust.iron.mapserver.stubs.IfmapPepStub;




public class EventProcessorSetup {

	public static EventProcessor setUpEventProcessor(
			ServerConfigurationProvider serverConf, Queue<Event> eventQueue,
			Queue<ActionSeries> actionQueue) {

		DataModelService mDms;
		EventProcessor eventProc;
		RequestUnmarshaller unmarshaller;
		ResultMarshaller marshaller;
		SessionTimerFactory timerFac;
		SchemaProvider schemaProv = null;
		IfmapPep pep = new IfmapPepStub();

		try {
			schemaProv = new SchemaProviderImpl(serverConf);
		} catch (ProviderInitializationException e1) {
			e1.printStackTrace();
		}
		MetadataTypeRepository mdtr = MetadataTypeRepositoryImpl.newInstance();
		MetadataFactory mfac = MetadataFactoryImpl.newInstance(mdtr, serverConf);
		PollResultAvailableCallback cb = new PollResultAvailableCallback(eventQueue);
		unmarshaller = RequestUnmarshallerFactory.newRequestUnmarshaller(mfac, schemaProv);
		marshaller = ResultMarshallerFactory.newResultMarshaller();

		timerFac = new SessionTimerFactory(eventQueue, serverConf);
		mDms = DataModelService.newInstance(serverConf, pep);

		eventProc = new EventProcessor(eventQueue, 1, 1);
		eventProc.setActionQueue(actionQueue);
		eventProc.setPublisherIdProv(StubProvider.getPublisherIdProvStub());
		eventProc.setSessionIdProv(StubProvider.getSessionIdProvStub());
		eventProc.setRequestUnmarshaller(unmarshaller);
		eventProc.setResultMarshaller(marshaller);
		eventProc.setServerConfiguration(serverConf);
		eventProc.setDataModel(mDms);
		eventProc.setAuthorizationProv(StubProvider.getAuthorizationProvStub());
		try {
			mDms.registerSubscriptionObserver(cb);
		} catch (AlreadyObservedException e) {
			e.printStackTrace();
		}
		eventProc.setSessionTimerFactory(timerFac);
		eventProc.setPublisherIdGenerator(StubProvider.getPublisherIdGenStub());

		return eventProc;
	}
}
