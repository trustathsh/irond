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

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import de.fhhannover.inform.iron.mapserver.communication.ClientIdentifier;
import de.fhhannover.inform.iron.mapserver.contentauth.IfmapPep;
import de.fhhannover.inform.iron.mapserver.messages.RequestFactory;
import de.fhhannover.inform.iron.mapserver.stubs.IfmapPepStub;

public class NewSessionTest extends TestCase {
	RequestFactory rf = RequestFactory.getInstance();
	DataModelService dms;

	String sessionId = "1111";
	String publisherId = "2222";

	@Before
	public void setUp() {
		IfmapPep pep = new IfmapPepStub();
		dms = DataModelService.newInstance(DummyDataModelConf.getDummyConf(), pep);
	;}

	@Test
	public void testNewSession() {
		ClientIdentifier cl = new ClientIdentifier("some user");
		dms.newSession(sessionId, publisherId, null, cl);
		try {
			dms.newSession(sessionId, publisherId, null, cl);
			fail();
		} catch (RuntimeException e) {
			// TODO: That should be a more specific one, like ServerErrorException
		}

		try {
			dms.endSession(sessionId);
		} catch (RuntimeException e) {
			fail();
			// TODO: That should be a more specific one, like ServerErrorException
		}
	}

	@Test
	public void testRemoveWithoutNew() {
		try {
			dms.endSession(sessionId);
			fail();
		} catch (RuntimeException e) {
			// TODO: That should be a more specific one, like ServerErrorException
		}
	}
}
