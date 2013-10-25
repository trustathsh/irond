package de.fhhannover.inform.iron.mapserver.trust.utils;

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

import de.fhhannover.inform.iron.mapserver.communication.ChannelIdentifier;
import de.fhhannover.inform.iron.mapserver.communication.ClientIdentifier;
import de.fhhannover.inform.iron.mapserver.communication.ifmap.Session;
import de.fhhannover.inform.iron.mapserver.communication.ifmap.SessionRepository;
import de.fhhannover.inform.iron.mapserver.communication.ifmap.SessionRepositoryImpl;
import de.fhhannover.inform.iron.mapserver.datamodel.meta.MetadataFactory;
import de.fhhannover.inform.iron.mapserver.datamodel.meta.MetadataFactoryImpl;
import de.fhhannover.inform.iron.mapserver.datamodel.meta.MetadataTypeRepository;
import de.fhhannover.inform.iron.mapserver.datamodel.meta.MetadataTypeRepositoryImpl;
import de.fhhannover.inform.iron.mapserver.trust.TrustService;
import de.fhhannover.inform.iron.mapserver.trust.TrustServiceImpl;
import de.fhhannover.inform.iron.mapserver.trust.domain.TrustToken;

public class TrustTester {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		MetadataTypeRepository mt = MetadataTypeRepositoryImpl.newInstance();
		MetadataFactory mf = MetadataFactoryImpl.newInstance(mt, null);
		SessionRepository sr = new SessionRepositoryImpl();
		TrustService trustService = new TrustServiceImpl(sr, mf);
		
		Session s = new Session(new ClientIdentifier("user"), "user-id");
		ChannelIdentifier ci = new ChannelIdentifier("ip", 12, 1);
		s.setSessionId("123");
		s.setSsrc(ci);
		sr.store(s);
		sr.map(s, "123");
		sr.map(s, ci);
		
		Session s1 = new Session(new ClientIdentifier("user1"), "user-id1");
		ChannelIdentifier ci1 = new ChannelIdentifier("ip1", 24, 2);
		s.setSessionId("456");
		s.setSsrc(ci1);
		sr.store(s1);
		sr.map(s1, "456");
		sr.map(s1, ci1);
		
		trustService.addSpForMapc(s.getSsrc(), "certAuth", OperationType.PROCESS_MAPC);
		trustService.addSpForMapc(s.getSsrc(), "certAuth", OperationType.TRANSMIT_MAPC_MAPS);
		trustService.addSpForMaps("certAuth");
		
		TrustToken tt = trustService.getP1TT("123", "user");
		System.out.println(trustService.getP2TTI("456", tt));
		trustService.removeAllSprOfMapc("123");
		trustService.removeAllSprOfMapc("456");
	}

}
