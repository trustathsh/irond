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
package de.hshannover.f4.trust.iron.mapserver.datamodel;



import junit.framework.TestCase;

import org.junit.Test;

import de.hshannover.f4.trust.iron.mapserver.datamodel.identifiers.MacAddress;
import de.hshannover.f4.trust.iron.mapserver.exceptions.InvalidIdentifierException;

/**
 * Some testcases for MAC Addresses
 *
 * @author aw
 * @version 0.1
 *
 * created: 27.11.09
 * changes:
 *  27.11.09 aw - Created first testcases.
 *
 */
public class MacAddressTest extends TestCase {

	MacAddress mac1, mac3, mac4, mac5,
				mac6, mac7, mac8, mac9, mac10,
				mac11, mac12, mac13, mac14, mac15,
				mac17, mac18,mac19, mac20,
				mac21;

	public MacAddressTest() throws InvalidIdentifierException {
		DataModelService.setServerConfiguration(DummyDataModelConf.getDummyConf());
		mac1 = new MacAddress("01:23:45:67:89:ab", "");
		mac1 = new MacAddress("aa:bb:cc:dd:ee:ff", "");
		mac3 = new MacAddress("aa:bb:cc:dd:ee:fe", "");
		mac4 = new MacAddress("11:22:33:44:55:66", "");

		mac14 = new MacAddress("01:23:45:67:89:ab", "");
		//mac14 = new MacAddress("aa:bb:cc:dd:ee:ff");
		mac15 = new MacAddress("aa:bb:cc:dd:ee:ff", "myDomain");
		//mac16 = new MacAddress("AA:BB:CC:DD:EE:FF", "myDomain");
		//mac17 = new MacAddress("AA:BB:CC:DD:EE:FF", "mydomain");
		mac18 = new MacAddress("aa:bb:cc:dd:ee:ff", null);
		mac19 = new MacAddress("aa:bb:cc:dd:ee:ff", "");

	}

	@Test
	public void testDifferent() {
		assertFalse(mac1.equals(mac3));
		assertFalse(mac3.equals(mac4));
	}

	@Test
	public void testDomain() {
		//assertTrue(mac1.equals(mac14));
		assertTrue(mac1.equals(mac18));
		assertTrue(mac18.equals(mac19));
		assertTrue(mac1.equals(mac19));
		assertFalse(mac14.equals(mac17));		/* this might fail if domainName is case insensitive */
	}


	@Test
	public void testGoodMacAddresses() {
		boolean exception;

		exception = false;
		try {
			mac6 = new MacAddress("aa:bb:cc:11:ff:22");	// ok!
		} catch (InvalidIdentifierException e) {
			exception = true;
		}
		assertFalse(exception);
		assertNotNull(mac6);

		exception = false;
		try {
			mac21 = new MacAddress("00:00:00:00:00:00");	// ok!
		} catch (InvalidIdentifierException e) {
			exception = true;
		}
		assertFalse(exception);
		assertNotNull(mac21);
	}


	@Test
	public void testBadMacAddresses() {
		boolean exception = false;

		exception = false;
		try {
			mac13 = new MacAddress("11:22:00:aa:CC:fe");	// ok!
		} catch (InvalidIdentifierException e) {
			exception = true;
		}
		assertTrue(exception);
		assertNull(mac13);

		try {
			mac5 = new MacAddress("xx:xx:xx:xx:xx:xx");		//exception
		} catch (InvalidIdentifierException e) {
			exception = true;
		}
		assertTrue(exception);
		assertNull(mac5);

		exception = false;
		try {
			mac8 = new MacAddress("aa:bb:11:ee:ff:gg");		// exception!
		} catch (InvalidIdentifierException e) {
			exception = true;
		}
		assertTrue(exception);
		assertNull(mac8);

		exception = false;
		try {
			mac9 = new MacAddress("127.0.0.1");				// exception!
		} catch (InvalidIdentifierException e) {
			exception = true;
		}
		assertTrue(exception);
		assertNull(mac9);

		exception = false;
		try {
			mac9 = new MacAddress("aa:bb:cc:dd:ee");		// exception!
		} catch (InvalidIdentifierException e) {
			exception = true;
		}
		assertTrue(exception);
		assertNull(mac9);

		/* the following might be allowed??? */
		exception = false;
		try {
			mac10 = new MacAddress("a:b:c:d:e:f");			// exception!
		} catch (InvalidIdentifierException e) {
			exception = true;
		}
		assertTrue(exception);
		assertNull(mac10);


		/* the following might be allowed??? */
		exception = false;
		try {
			mac11 = new MacAddress("1:2:3:4:5:6");			// exception!
		} catch (InvalidIdentifierException e) {
			exception = true;
		}
		assertTrue(exception);
		assertNull(mac11);


		exception = false;
		try {
			mac12 = new MacAddress("11:22:00:aa:CC:eg");	// exception!
		} catch (InvalidIdentifierException e) {
			exception = true;
		}
		assertTrue(exception);
		assertNull(mac12);

		exception = false;
		try {
			mac20 = new MacAddress("FF:FF:FF:FF:FF:ff");	// ok!
		} catch (InvalidIdentifierException e) {
			exception = true;
		}
		assertTrue(exception);
		assertNull(mac20);
	}
}
