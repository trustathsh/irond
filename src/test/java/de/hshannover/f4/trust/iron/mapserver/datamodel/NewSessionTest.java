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
 * This file is part of irond, version 0.4.2, implemented by the Trust@HsH
 * research group at the Hochschule Hannover.
 * %%
 * Copyright (C) 2010 - 2014 Trust@HsH
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


import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import de.hshannover.f4.trust.iron.mapserver.communication.ClientIdentifier;
import de.hshannover.f4.trust.iron.mapserver.contentauth.IfmapPep;
import de.hshannover.f4.trust.iron.mapserver.messages.RequestFactory;
import de.hshannover.f4.trust.iron.mapserver.stubs.IfmapPepStub;

public class NewSessionTest extends TestCase {
	RequestFactory rf = RequestFactory.getInstance();
	DataModelService dms;

	String sessionId = "1111";
	String publisherId = "2222";

	@Override
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
