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

import de.fhhannover.inform.iron.mapserver.datamodel.identifiers.AccessRequest;
import de.fhhannover.inform.iron.mapserver.datamodel.identifiers.Device;
import de.fhhannover.inform.iron.mapserver.datamodel.identifiers.DeviceTypeEnum;
import de.fhhannover.inform.iron.mapserver.datamodel.identifiers.Identifier;
import de.fhhannover.inform.iron.mapserver.datamodel.identifiers.Identity;
import de.fhhannover.inform.iron.mapserver.datamodel.identifiers.IdentityTypeEnum;
import de.fhhannover.inform.iron.mapserver.datamodel.identifiers.IpAddress;
import de.fhhannover.inform.iron.mapserver.datamodel.identifiers.IpAddressTypeEnum;
import de.fhhannover.inform.iron.mapserver.datamodel.identifiers.MacAddress;
import de.fhhannover.inform.iron.mapserver.datamodel.search.TerminalIdentifiers;
import de.fhhannover.inform.iron.mapserver.exceptions.InvalidIdentifierException;

public class TerminalIdentifierTest extends TestCase {
	Identifier ar, ip, id, dev, mac;
	
	@Before
	public void setUp() {
		DataModelService.setServerConfiguration(DummyDataModelConf.getDummyConf());
			try {
				ip = new IpAddress("192.168.0.1", IpAddressTypeEnum.IPv4);
				mac = new MacAddress("aa:bb:cc:dd:ee:ff");
				dev = new Device("abc", DeviceTypeEnum.name);
				id = new Identity("test", IdentityTypeEnum.userName);
				ar = new AccessRequest("AR100");
			} catch (InvalidIdentifierException e) {
				fail();
				e.printStackTrace();
			}
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
			assertFalse(ti.contains(id));

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
		} catch (InvalidIdentifierException e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void testUnknownTerminalIdentifier() {
		try {
			new TerminalIdentifiers("ipx-address");
			fail();
		} catch (InvalidIdentifierException e) {
			// all good
		}
	}
	
	@Test
	public void testTooManyTerminalIdentifiers() {
		try {
			new TerminalIdentifiers("ip-address" +
									",mac-address" +
									",access-request" +
									",device" +
									",identity" +
									",identity");
			fail();
		} catch (InvalidIdentifierException e) {
			// all good
		}
	}
	
	@Test
	public void testTerminalIdentifierTwice() {
		try {
			new TerminalIdentifiers("ip-address" +
									",mac-address" +
									",identity" +
									",identity");
			fail();
		} catch (InvalidIdentifierException e) {
			// all good
		}
	}
	
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
