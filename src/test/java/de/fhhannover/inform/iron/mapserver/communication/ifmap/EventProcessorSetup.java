package de.fhhannover.inform.iron.mapserver.communication.ifmap;

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

import de.fhhannover.inform.iron.mapserver.binding.RequestUnmarshaller;
import de.fhhannover.inform.iron.mapserver.binding.RequestUnmarshallerFactory;
import de.fhhannover.inform.iron.mapserver.binding.ResultMarshaller;
import de.fhhannover.inform.iron.mapserver.binding.ResultMarshallerFactory;
import de.fhhannover.inform.iron.mapserver.communication.bus.Queue;
import de.fhhannover.inform.iron.mapserver.communication.bus.messages.ActionSeries;
import de.fhhannover.inform.iron.mapserver.communication.bus.messages.Event;
import de.fhhannover.inform.iron.mapserver.datamodel.DataModelService;
import de.fhhannover.inform.iron.mapserver.datamodel.meta.MetadataFactory;
import de.fhhannover.inform.iron.mapserver.datamodel.meta.MetadataFactoryImpl;
import de.fhhannover.inform.iron.mapserver.datamodel.meta.MetadataTypeRepository;
import de.fhhannover.inform.iron.mapserver.datamodel.meta.MetadataTypeRepositoryImpl;
import de.fhhannover.inform.iron.mapserver.exceptions.AlreadyObservedException;
import de.fhhannover.inform.iron.mapserver.exceptions.ProviderInitializationException;
import de.fhhannover.inform.iron.mapserver.provider.SchemaProvider;
import de.fhhannover.inform.iron.mapserver.provider.SchemaProviderImpl;
import de.fhhannover.inform.iron.mapserver.provider.ServerConfigurationProvider;
import de.fhhannover.inform.iron.mapserver.provider.StubProvider;

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
		mDms = DataModelService.newInstance(serverConf);
		
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
