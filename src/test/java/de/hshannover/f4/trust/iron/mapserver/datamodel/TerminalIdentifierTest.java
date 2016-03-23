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
 * This file is part of irond, version 0.5.8, implemented by the Trust@HsH
 * research group at the Hochschule Hannover.
 * %%
 * Copyright (C) 2010 - 2016 Trust@HsH
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

import de.hshannover.f4.trust.iron.mapserver.datamodel.search.TerminalIdentifiers;
import de.hshannover.f4.trust.iron.mapserver.exceptions.InvalidIdentifierException;

public class TerminalIdentifierTest extends TestCase {
	String ar, ip, id, dev, mac, id_user, id_dns, id_other, id_extended;

	@Override
	@Before
	public void setUp() {
		DataModelService.setServerConfiguration(DummyDataModelConf.getDummyConf());
		ar = "access-request";
		ip = "ip-address";
		id = "identity";
		dev = "device";
		mac = "mac-address";
		id_user = "identity:username";
		id_other = "identity:other:ifmaproxx";
		id_dns = "identity:dns-name";
		id_extended = "http://www.example.com/extended-identifiers#network2";
	}

	@Test
	public void testGoodTerminalIdentifiers() {
		try {
			TerminalIdentifiers ti;
			ti = new TerminalIdentifiers(null);
			assertFalse(ti.contains(ip));
			assertFalse(ti.contains(mac));
			assertFalse(ti.contains(dev));
			assertFalse(ti.contains(ar));
			assertFalse(ti.contains(id_user));
			assertFalse(ti.contains(id_other));
			assertFalse(ti.contains(id_dns));
			assertFalse(ti.contains(id_extended));

			ti = new TerminalIdentifiers("");
			assertFalse(ti.contains(ip));
			assertFalse(ti.contains(mac));
			assertFalse(ti.contains(dev));
			assertFalse(ti.contains(ar));
			assertFalse(ti.contains(id));

			ti = new TerminalIdentifiers("ip-address");
			assertTrue(ti.contains(ip));
			assertFalse(ti.contains(mac));
			assertFalse(ti.contains(dev));
			assertFalse(ti.contains(ar));
			assertFalse(ti.contains(id));

			ti = new TerminalIdentifiers("mac-address");
			assertFalse(ti.contains(ip));
			assertTrue(ti.contains(mac));
			assertFalse(ti.contains(dev));
			assertFalse(ti.contains(ar));
			assertFalse(ti.contains(id));

			ti = new TerminalIdentifiers("access-request");
			assertFalse(ti.contains(ip));
			assertFalse(ti.contains(mac));
			assertFalse(ti.contains(dev));
			assertTrue(ti.contains(ar));
			assertFalse(ti.contains(id));

			ti = new TerminalIdentifiers("device");
			assertFalse(ti.contains(ip));
			assertFalse(ti.contains(mac));
			assertTrue(ti.contains(dev));
			assertFalse(ti.contains(ar));
			assertFalse(ti.contains(id));

			ti = new TerminalIdentifiers("identity");
			assertFalse(ti.contains(ip));
			assertFalse(ti.contains(mac));
			assertFalse(ti.contains(dev));
			assertFalse(ti.contains(ar));
			assertTrue(ti.contains(id));

			ti = new TerminalIdentifiers("ip-address" +
									",mac-address" +
									",access-request" +
									",device" +
									",identity");
			assertTrue(ti.contains(ip));
			assertTrue(ti.contains(mac));
			assertTrue(ti.contains(dev));
			assertTrue(ti.contains(ar));
			assertTrue(ti.contains(id));


			ti = new TerminalIdentifiers("identity:username");
			assertFalse(ti.contains(ip));
			assertFalse(ti.contains(mac));
			assertFalse(ti.contains(dev));
			assertFalse(ti.contains(ar));
			assertTrue(ti.contains(id_user));

			ti = new TerminalIdentifiers("identity:username");
			assertFalse(ti.contains(ip));
			assertFalse(ti.contains(mac));
			assertFalse(ti.contains(dev));
			assertFalse(ti.contains(ar));
			assertTrue(ti.contains(id_user));

			ti = new TerminalIdentifiers("identity:dns-name");
			assertFalse(ti.contains(ip));
			assertFalse(ti.contains(mac));
			assertFalse(ti.contains(dev));
			assertFalse(ti.contains(ar));
			assertTrue(ti.contains(id_dns));

			ti = new TerminalIdentifiers("identity:other:ifmaproxx");
			assertFalse(ti.contains(ip));
			assertFalse(ti.contains(mac));
			assertFalse(ti.contains(dev));
			assertFalse(ti.contains(ar));
			assertTrue(ti.contains(id_other));

			ti = new TerminalIdentifiers("http://www.example.com/extended-identifiers#network2");
			assertFalse(ti.contains(ip));
			assertFalse(ti.contains(mac));
			assertFalse(ti.contains(dev));
			assertFalse(ti.contains(ar));
			assertTrue(ti.contains(id_extended));

		} catch (InvalidIdentifierException e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testUnknownTerminalIdentifier() {
		try {
			new TerminalIdentifiers("ipx-address");
			new TerminalIdentifiers("htt://www.example.com/extended-identifiers#network2");
			new TerminalIdentifiers("identity:bonkers");
			fail();
		} catch (InvalidIdentifierException e) {
			// all good
		}
	}

//	@Ignore
//	@Test
//	public void testTooManyTerminalIdentifiers() {
//		try {
//			new TerminalIdentifiers("ip-address" +
//									",mac-address" +
//									",access-request" +
//									",device" +
//									",identity" +
//									",identity");
//			fail();
//		} catch (InvalidIdentifierException e) {
//			// all good
//		}
//	}

//	@Ignore
//	@Test
//	public void testTerminalIdentifierTwice() {
//		try {
//			new TerminalIdentifiers("ip-address" +
//									",mac-address" +
//									",identity" +
//									",identity");
//			fail();
//		} catch (InvalidIdentifierException e) {
//			// all good
//		}
//	}

	@Test
	public void testWeirdTerminalIdentifierString1() {
		try {
			new TerminalIdentifiers(",ip-address" +
									",mac-address" +
									",identity");
			fail();
		} catch (InvalidIdentifierException e) {
			// all good
		}
	}

	@Test
	public void testWeirdTerminalIdentifierString2() {
		try {
			new TerminalIdentifiers("ip-address" +
									",mac-address" +
									",identity,");
			fail();
		} catch (InvalidIdentifierException e) {
			// all good
		}
	}

	@Test
	public void testWeirdTerminalIdentifierString3() {
		try {
			new TerminalIdentifiers("ip-address" +
									",,mac-address" +
									",identity");
			fail();
		} catch (InvalidIdentifierException e) {
			// all good
		}
	}
}
