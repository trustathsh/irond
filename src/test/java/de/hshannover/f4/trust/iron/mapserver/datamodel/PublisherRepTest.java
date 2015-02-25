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


import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import de.hshannover.f4.trust.iron.mapserver.communication.ClientIdentifier;
import de.hshannover.f4.trust.iron.mapserver.exceptions.NoSuchPublisherException;

public class PublisherRepTest extends TestCase {

	PublisherRep pubrep;
	ClientIdentifier cl = new ClientIdentifier("some user");

	@Override
	@Before
	public void setUp() {
		pubrep = new PublisherRep();
	}


	@Test
	public void testInstanceSet() {
		assertNotNull(pubrep);
	}

	@Test
	public void testGetAddPublisher() {
		pubrep.addPublisher("110:111", "4711", null, cl);
		Publisher x = null;
		try {
			x = pubrep.getPublisherBySessionId("4711");
		} catch (NoSuchPublisherException e) {
			fail();
		}

		try {
			x = pubrep.getPublisherByPublisherId("110:111");
		} catch (NoSuchPublisherException e) {
			fail();
		}
		assertEquals("110:111", x.getPublisherId());

		try {
			x = null;
			x = pubrep.getPublisherByPublisherId("xxx");
			fail();
		} catch (NoSuchPublisherException e) {
		}
		assertNull(x);
		try {
			x = null;
			x = pubrep.getPublisherBySessionId("xxx");
			fail();
		} catch (NoSuchPublisherException e) {
		}
		assertNull(x);
	}

	@Test
	public void testRemovePublisher() {
		pubrep.addPublisher("110:112", "4712", null, cl);
		pubrep.removePublisherByPubliherId("110:112");

		Publisher x = null;
		try {
			x = pubrep.getPublisherByPublisherId("110:112");
			if (x != null) {
				//lllalalla
			}

			fail();
		} catch (NoSuchPublisherException e) {
		}
		try {
			pubrep.getPublisherBySessionId("4712");
			fail();
		} catch (NoSuchPublisherException e) {
		}
	}
}
