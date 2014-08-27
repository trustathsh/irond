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

import org.junit.Test;

import de.fhhannover.inform.iron.mapserver.datamodel.identifiers.Identity;
import de.fhhannover.inform.iron.mapserver.datamodel.identifiers.IdentityTypeEnum;
import de.fhhannover.inform.iron.mapserver.exceptions.InvalidIdentifierException;

/**
 * Some tests of type identity.
 *
 * Only a few types are tested as they are basically all
 * handled the same way.
 * More care is taken with the type other.
 *
 *
 * @author aw
 * @version 0.1
 * created: 27.11.09
 * changes: aw - First version of this class
 *
 */
public class IdentityTest extends TestCase {

	Identity id0, id1, id2, id3, id4;	/* aiknames */
	Identity id5, id6, id7, id8;		/* distinguished names */
	Identity id9, id10, id11, id12;		/* dns-names */
	Identity id13, id14, id15, id16;	/* email-addresses */
	Identity id17, id18, id19, id20;	/* kerberos-principal */
	Identity id21, id22, id23, id24;	/* username */
	Identity id25, id26, id27, id28;	/* sip-uri */
	Identity id29, id30, id31, id32;	/* tel-uri */
	Identity id33, id34, id35, id36;	/* hip-hit */
	Identity id37, id38, id39, id40;	/* other */
	Identity id41, id42;				/* other */


	public IdentityTest() throws Exception {

		DataModelService.setServerConfiguration(DummyDataModelConf.getDummyConf());

		/* create aik names */
		try {
			id0 = new Identity("xxlkjds", "myDomain", null, IdentityTypeEnum.aikName);
			id1 = new Identity("xxlkjds", "mydomain", null, IdentityTypeEnum.aikName);
			id2 = new Identity("aikname1", null, null, IdentityTypeEnum.aikName);
			id3 = new Identity("aikname1", "", null, IdentityTypeEnum.aikName);
			id4 = new Identity("xxlkjdt", "mydomain", null, IdentityTypeEnum.aikName);
		} catch (InvalidIdentifierException e) {
			e.printStackTrace();
		}

		/* create distinguished names */
		try {
			id5 = new Identity("cn=Ben Gray,ou=editing,o=New York Times,c=US", "mydomain", null, IdentityTypeEnum.distinguishedName);
			id6 = new Identity("cn=Tom Brown,ou=reporting,o=New York Times,c=US", IdentityTypeEnum.distinguishedName);
			id7 = new Identity("cn=Tom Brown,ou=reporting,o=New York Times,c=US", "", null, IdentityTypeEnum.distinguishedName);
			id8 = new Identity("cn=Lucille White,ou=editing,o=New York Times,c=US", "mydomain", null, IdentityTypeEnum.distinguishedName);
		} catch (InvalidIdentifierException e) {
			e.printStackTrace();
		}

		/* create dnsnames names */
		try {
			id9 = new Identity("xxlkjds", "mydomain", null, IdentityTypeEnum.dnsName);
			id10 = new Identity("dns1", IdentityTypeEnum.dnsName);
			id11 = new Identity("dns1", "", null, IdentityTypeEnum.dnsName);
			id12 = new Identity("xxlkjdt", "mydomain", null, IdentityTypeEnum.dnsName);
		} catch (InvalidIdentifierException e) {
			e.printStackTrace();
		}

		/* create email-addresses */
		try {
			id13 = new Identity("a@b.c", "mydomain", null, IdentityTypeEnum.emailAddress);
			id14 = new Identity("x@y.c", IdentityTypeEnum.emailAddress);
			id15 = new Identity("test1@bbb.cde", "", null, IdentityTypeEnum.emailAddress);
			id16 = new Identity("xxx@yyy.zzz", "mydomain",null, IdentityTypeEnum.emailAddress);
		} catch (InvalidIdentifierException e) {
			e.printStackTrace();
		}

		/* create kerberos-addresses */
		try {
			id17 = new Identity("mueller/admin@EXAMPLE.COM", "mydomain", null, IdentityTypeEnum.kerberosPrincipal);
			id18 = new Identity("maier/user@EXAMPLE.COM", IdentityTypeEnum.kerberosPrincipal);
			id19 = new Identity("mueller/admin@EXAMPLE.COM", "", null, IdentityTypeEnum.kerberosPrincipal);
			id20 = new Identity("mueller/user@EXAMPLE.COM", "mydomain", null, IdentityTypeEnum.kerberosPrincipal);
		} catch (InvalidIdentifierException e) {
			e.printStackTrace();
		}

		/* create usernames */
		try {
			id21 = new Identity("user1", "mydomain", null, IdentityTypeEnum.kerberosPrincipal);
			id22 = new Identity("user1", IdentityTypeEnum.kerberosPrincipal);
			id23 = new Identity("user1", "", null, IdentityTypeEnum.kerberosPrincipal);
			id24 = new Identity("user2", "mydomain", null, IdentityTypeEnum.kerberosPrincipal);
		} catch (InvalidIdentifierException e) {
			e.printStackTrace();
		}

		/* other types */
		try {
			id37 = new Identity("value22", "mydomain", "inform.fh-hannover.de:mytype", IdentityTypeEnum.other);
			id38 = new Identity("value22", "mydomain", "inform.fh-hannover.de:mytype", IdentityTypeEnum.other);
			id39 = new Identity("value22", "mydomain", "inform.fh-hannover.de:mytype2", IdentityTypeEnum.other);
			id40 = new Identity("value33", "mydomain", "inform.fh-hannover.de:mytype2", IdentityTypeEnum.other);
			id41 = new Identity("value33", "myDomain", "inform.fh-hannover.de:mytype2", IdentityTypeEnum.other);
		} catch (InvalidIdentifierException e) {
			e.printStackTrace();
		}
	}



	@Test
	public void testCreateOtherTypes() {
		try {
			id37 = new Identity("value", IdentityTypeEnum.other);	/* should throw exception */
			assertTrue(false);									/* test failed */
		} catch (InvalidIdentifierException e) {
			assertTrue(true);									/* test passed */
		}
		try {
			id37 = new Identity("value22", "mydomain", "inform.fh-hannover.de:mytype", IdentityTypeEnum.other);
			assertTrue(true);									/* test passed */
		} catch (InvalidIdentifierException e) {
			assertTrue(false);									/* test failed */
		}

		try {
			id38 = new Identity("value22", "mydomain", "inform.fh-hannover.de:mytype", IdentityTypeEnum.other);
			assertTrue(true);									/* test passed */
		} catch (InvalidIdentifierException e) {
			assertTrue(false);									/* test failed */
		}

		try {
			id39 = new Identity("value22", "mydomain", "inform.fh-hannover.de:mytype2", IdentityTypeEnum.other);
			assertTrue(true);									/* test passed */
		} catch (InvalidIdentifierException e) {
			assertTrue(false);									/* test failed */
		}

		try {
			id40 = new Identity("value33", "mydomain", "inform.fh-hannover.de:mytype2", IdentityTypeEnum.other);
			assertTrue(true);
		} catch (InvalidIdentifierException e) {
			assertTrue(false);
		}
		try {
			id41 = new Identity("value33", "myDomain", "inform.fh-hannover.de:mytype2", IdentityTypeEnum.other);
			assertTrue(true);
		} catch (InvalidIdentifierException e) {
			assertTrue(false);
		}

	}

	@Test
	public void testCompareOtherTypes() {
		assertTrue(id37.equals(id38));
		assertFalse(id38.equals(id39));
		assertFalse(id39.equals(id40));
		assertTrue(id40.equals(id40));
//		assertFalse(id40.equals(id41));
	}


	@Test
	public void testCreateAik() {
		assertNotNull(id1);
		assertNotNull(id2);
		assertNotNull(id3);
		assertNotNull(id4);
	}

	@Test
	public void testCompareAikNames() {
//		assertFalse(id0.equals(id1));
		assertTrue(id2.equals(id2));
		assertTrue(id2.equals(id3));
		assertFalse(id1.equals(id2));
		assertFalse(id1.equals(id4));
	}

	@Test
	public void testCompareDifferentTypes() {
		assertFalse(id1.equals(id5));
		assertFalse(id5.equals(id9));
		assertFalse(id9.equals(id13));
		assertFalse(id13.equals(id17));
		assertFalse(id17.equals(id21));
	}

	@Test
	public void testCreateDistinguished() {
		assertNotNull(id5);
		assertNotNull(id6);
		assertNotNull(id7);
		assertNotNull(id8);
	}

	@Test
	public void testCompareDistinguishedNames() {
		assertTrue(id6.equals(id6));
		assertTrue(id6.equals(id7));
		assertFalse(id5.equals(id6));
		assertFalse(id5.equals(id8));
	}

	@Test
	public void testCompareUserNames() {
		assertTrue(id21.equals(id21));
		assertFalse(id21.equals(id24));
		assertTrue(id22.equals(id23));
		assertFalse(id24.equals(id23));
	}

	@Test
	public void testCreateKerberos() {
		assertNotNull(id13);
		assertNotNull(id14);
		assertNotNull(id15);
		assertNotNull(id16);
	}

	@Test
	public void testCreateDNS() {
		assertNotNull(id9);
		assertNotNull(id10);
		assertNotNull(id11);
		assertNotNull(id12);
	}

	@Test
	public void testCreateEmail() {
		assertNotNull(id13);
		assertNotNull(id14);
		assertNotNull(id15);
		assertNotNull(id16);
	}

	public void testCreateUserNames() {
		assertNotNull(id21);
		assertNotNull(id22);
		assertNotNull(id23);
		assertNotNull(id24);
	}

}
