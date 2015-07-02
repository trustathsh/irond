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
 * This file is part of irond, version 0.5.5, implemented by the Trust@HsH
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

import de.hshannover.f4.trust.iron.mapserver.datamodel.identifiers.IpAddress;
import de.hshannover.f4.trust.iron.mapserver.datamodel.identifiers.IpAddressTypeEnum;
import de.hshannover.f4.trust.iron.mapserver.exceptions.InvalidIdentifierException;


/**
 * @author aw
 * @version 0.1
 *
 * created: 27.11.09
 * changes:
 *  27.11.09 aw - Created first version.
 *
 */
public class IpAddressTest extends TestCase {

	IpAddress ip1, ip2, ip3, ip4, ip5, ip6, ip7, ip8, ip9, ip10;
	IpAddress ip11, ip12, ip13, ip14, ip15, ip16, ip17, ip18, ip19, ip20;
	IpAddress ip30, ip31, ip32, ip33, ip34, ip35;
	IpAddress ip36, ip37, ip38, ip39, ip40, ip41;

	public IpAddressTest() throws Exception {

		DataModelService.setServerConfiguration(DummyDataModelConf.getDummyConf());

		try {
			ip30 = new IpAddress("127.0.0.1",IpAddressTypeEnum.IPv4);
			ip31 = new IpAddress("127.0.0.1", "", IpAddressTypeEnum.IPv4);
			ip32 = new IpAddress("127.0.0.1", "myDomain", IpAddressTypeEnum.IPv4);
			ip34 = new IpAddress("127.0.0.1", "myDomain", IpAddressTypeEnum.IPv4);
			ip33 = new IpAddress("11.0.11.1", "myDomain", IpAddressTypeEnum.IPv4);
			ip35 = new IpAddress("11.0.11.1", "mydomain", IpAddressTypeEnum.IPv4);


			ip36 = new IpAddress("aaaa:bbbb:cccc:dddd:eeee:ffff:1:8", IpAddressTypeEnum.IPv6);
			ip37 = new IpAddress("aaaa:bbbb:cccc:dddd:eeee:ffff:1:8", "", IpAddressTypeEnum.IPv6);
			ip38 = new IpAddress("aaaa:bbbb:cccc:dddd:eeee:ffff:1:8", "myDomain", IpAddressTypeEnum.IPv6);
			ip39 = new IpAddress("aaaa:bbbb:cccc:dddd:eeee:ffff:1:8", "myDomain", IpAddressTypeEnum.IPv6);
			ip40 = new IpAddress("aaaa:bbbb:cccc:dddd:eeee:ffff:1:8", "mydomain", IpAddressTypeEnum.IPv6);
			ip41 = new IpAddress("1111:22:0:0:a:a:2:a", "myDomain", IpAddressTypeEnum.IPv6);

		} catch (InvalidIdentifierException e) {
			e.printStackTrace();
		}

	}

	@Test
	public void testCompareIPv4() {

		assertTrue(ip30.equals(ip31));
		assertFalse(ip30.equals(ip32));
		assertTrue(ip32.equals(ip34));
		assertFalse(ip33.equals(ip35));	/* fails if ad is case insensitive */
		assertFalse(ip33.equals(ip34));
	}

	@Test
	public void testCompareIPv6() {

		assertTrue(ip36.equals(ip37));
		assertFalse(ip36.equals(ip38));
		assertTrue(ip38.equals(ip39));
		assertFalse(ip39.equals(ip40));	/* fails if ad is case insensitive */
		assertFalse(ip39.equals(ip41));
	}

	public void testCompairIPv4IPv6() {
		assertFalse(ip32.equals(ip38));
		assertFalse(ip35.equals(ip41));
	}


	/**
	 * -- check wrong ipv4 format with:			ip1, ip2, ip3, ip4
	 * -- check correct ipv6 with type ipv4:	ip5, ip6
	 */
	@Test
	public void testBadIPv4Addresses() {
		boolean exception = false;

		try {
			ip1 = new IpAddress("256.0.0.1", IpAddressTypeEnum.IPv4);
		} catch (InvalidIdentifierException e) {
			exception = true;
		}
		assertTrue(exception);
		assertNull(ip1);

		exception = false;
		try {
			ip2 = new IpAddress("127.0.1", IpAddressTypeEnum.IPv4);
		} catch (InvalidIdentifierException e) {
			exception = true;
		}
		assertTrue(exception);
		assertNull(ip2);

		exception = false;
		try {
			ip3 = new IpAddress("example.com", IpAddressTypeEnum.IPv4);
		} catch (InvalidIdentifierException e) {
			exception = true;
		}
		assertTrue(exception);
		assertNull(ip3);

		exception = false;
		try {
			ip4 = new IpAddress("127.0.a.b", IpAddressTypeEnum.IPv4);
		} catch (InvalidIdentifierException e) {
			exception = true;
		}
		assertTrue(exception);
		assertNull(ip4);

		exception = false;
		try {
			ip5 = new IpAddress("aa:bb:1111:0000:111:2222:3333:22", IpAddressTypeEnum.IPv4);
		} catch (InvalidIdentifierException e) {
			exception = true;
		}
		assertTrue(exception);
		assertNull(ip5);

		exception = false;
		try {
			ip6 = new IpAddress("11:222:aaa:bbb:ccc:111:27:cc", IpAddressTypeEnum.IPv4);
		} catch (InvalidIdentifierException e) {
			exception = true;
		}
		assertTrue(exception);
		assertNull(ip6);
	}

	/**
	 * - check for correct IPv4 ip7 ip8 ip9 ip10
	 */
	@Test
	public void testGoodIPv4Addresses() {
		boolean exception = false;
		try {
			ip7 = new IpAddress("127.0.0.1", IpAddressTypeEnum.IPv4);
		} catch (InvalidIdentifierException e) {
			exception = true;
		}
		assertFalse(exception);
		assertNotNull(ip7);

		exception = false;

		try {
			ip8 = new IpAddress("255.255.255.255", IpAddressTypeEnum.IPv4);
		} catch (InvalidIdentifierException e) {
			exception = true;
		}
		assertFalse(exception);
		assertNotNull(ip8);

		try {
			ip9 = new IpAddress("10.10.1.1", IpAddressTypeEnum.IPv4);
		} catch (InvalidIdentifierException e) {
			exception = true;
		}
		assertFalse(exception);
		assertNotNull(ip9);

		try {
			ip9 = new IpAddress("255.0.255.0", IpAddressTypeEnum.IPv4);
		} catch (InvalidIdentifierException e) {
			exception = true;
		}
		assertFalse(exception);
		assertNotNull(ip9);

		try {
			ip10 = new IpAddress("78.17.153.231", IpAddressTypeEnum.IPv4);
		} catch (InvalidIdentifierException e) {
			exception = true;
		}
		assertFalse(exception);
		assertNotNull(ip10);
	}

	/**
	 * check for invalid ipv6 addresses: ip11 ip12 ip13 ip14, ip18
	 */
	@Test
	public void testBadIPv6Addresses() {
		boolean exception = false;

		try {
			ip11 = new IpAddress("aaaa:bbbb:cc:xx:ff:ff:aa:11", IpAddressTypeEnum.IPv6);
		} catch (InvalidIdentifierException e) {
			exception = true;
		}
		assertTrue(exception);
		assertNull(ip11);

		exception = false;
		try {
			ip12 = new IpAddress("example.com", IpAddressTypeEnum.IPv6);
		} catch (InvalidIdentifierException e) {
			exception = true;
		}
		assertTrue(exception);
		assertNull(ip12);

		exception = false;
		try {
			ip13= new IpAddress("aa:c:a:1:1:ff:a", IpAddressTypeEnum.IPv6);
		} catch (InvalidIdentifierException e) {
			exception = true;
		}
		assertTrue(exception);
		assertNull(ip13);

		exception = false;
		try {
			ip13= new IpAddress("AA:bb:cc:dd:aa:bb:cc:dd", IpAddressTypeEnum.IPv6);
		} catch (InvalidIdentifierException e) {
			exception = true;
		}
		assertTrue(exception);
		assertNull(ip13);

		exception = false;
		try {
			ip14= new IpAddress("aa:bb:cc:dd:aa:bb:cc:fg", IpAddressTypeEnum.IPv6);
		} catch (InvalidIdentifierException e) {
			exception = true;
		}
		assertTrue(exception);
		assertNull(ip14);

		exception = false;
		try {
			ip18= new IpAddress("aa:bb:cc:dd:aa:bb:cc:fg", null);
		} catch (InvalidIdentifierException e) {
			exception = true;
		}
		assertTrue(exception);
		assertNull(ip18);
	}


	/**
	 * check for correct IPv6 addresses:
	 *  ip15, ip16, ip17
	 */
	@Test
	public void testGoodIPv6Addres() {
		boolean exception = false;

		exception = false;
		try {
			ip15= new IpAddress("2001:db8:0:0:8:800:200c:417a", IpAddressTypeEnum.IPv6);
		} catch (InvalidIdentifierException e) {
			exception = true;
		}
		assertFalse(exception);
		assertNotNull(ip15);

		try {
			ip16= new IpAddress("0:1:2:a:b:c:ffff:ab", IpAddressTypeEnum.IPv6);
		} catch (InvalidIdentifierException e) {
			exception = true;
		}
		assertFalse(exception);
		assertNotNull(ip16);

		try {
			ip17= new IpAddress("aa:bb:cc:dd:aa:bb:cc:99", IpAddressTypeEnum.IPv6);
		} catch (InvalidIdentifierException e) {
			exception = true;
		}
		assertFalse(exception);
		assertNotNull(ip17);

	}

}
