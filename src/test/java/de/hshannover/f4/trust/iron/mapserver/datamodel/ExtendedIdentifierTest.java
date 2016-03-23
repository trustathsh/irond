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

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import de.hshannover.f4.trust.iron.mapserver.contentauth.IfmapPep;
import de.hshannover.f4.trust.iron.mapserver.datamodel.identifiers.Identity;
import de.hshannover.f4.trust.iron.mapserver.datamodel.identifiers.IdentityTypeEnum;
import de.hshannover.f4.trust.iron.mapserver.exceptions.InvalidIdentifierException;
import de.hshannover.f4.trust.iron.mapserver.stubs.IfmapPepStub;


/**
 * Extended Identifier tests.
 *
 * These tests likely need to be adapted when there's a real test-suite
 * from the TCG for extended identifiers.
 *
 * @author aw
 */
public class ExtendedIdentifierTest {

	private static String valid = "&lt;network xmlns=&quot;http://ns&quot;" +
										 " administrative-domain=&quot;mydomain&quot;" +
										 " ip=&quot;192.168.0.1&quot;" +
										 "&gt;&lt;/network&gt;";

	private static String validMultiElement = "&lt;parent xmlns=&quot;http://ns&quot;" +
										 " administrative-domain=&quot;mydomain&quot;" +
										 "&gt;" +
										 "&lt;child value=&quot;child&quot;&gt;&lt;/child&gt;" +
										 "&lt;/parent&gt;";

	private static String inValidNoAdmDom = "&lt;network xmlns=&quot;http://ns&quot;" +
										 " ip=&quot;192.168.0.1&quot;" +
										 "&gt;&lt;/network&gt;";

	private static String validEmptyAdmDom = "&lt;network xmlns=&quot;http://ns&quot;" +
										 " administrative-domain=&quot;&quot;" +
										 " ip=&quot;192.168.0.1&quot;" +
										 "&gt;&lt;/network&gt;";

	private static String validAttrWrong = "&lt;network xmlns=&quot;http://ns&quot;" +
										 " ip=&quot;192.168.0.1&quot;" +
										 " administrative-domain=&quot;mydomain&quot;" +
										 "&gt;&lt;/network&gt;";

	private static String validNoEscape = "<network xmlns=\"http://ns\"" +
									 " administrative-domain=\"mydomain\"" +
									 " ip=\"192.168.0.2\"></network>";

	private static String validBadNorm = "&lt;network xmlns=&quot;http://ns&quot;" +
										 " ip=&quot;192.168.0.1&quot;" +
										 " administrative-domain=&quot;mydomain&quot;" +
										 "/&gt;";
	private static String validBadNormSpace = "&lt;network xmlns=&quot;http://ns&quot;" +
										 "     ip=&quot;192.168.0.1&quot;" +
										 "     administrative-domain=&quot;mydomain&quot;" +
										 "/&gt;";

	private static String validWithNewline = "&lt;network xmlns=&quot;http://ns&quot;" +
										 " administrative-domain=&quot;mydomain&quot;" +
										 " ip=&quot;192.168.0.1&quot;" +
										 "&gt;\n&lt;/network&gt;";

	private static String noNS = "&lt;network" +
								 " administrative-domain=&quot;mydomain&quot;" +
								 " ip=&quot;192.168.0.1&quot;" +
								 "&gt;&lt;/network&gt;";

	private static String unusedNS = "&lt;network xmlns=&quot;http://ns&quot;" +
									 " xmlns:unused=&quot;http://unused&quot;" +
									 " administrative-domain=&quot;mydomain&quot;" +
									 " ip=&quot;192.168.0.1&quot;" +
									 "&gt;&lt;/network&gt;";

	private static String multiNS = "&lt;parent xmlns=&quot;http://ns&quot;" +
									 " administrative-domain=&quot;mydomain&quot;" +
									 "&gt;" +
									 "&lt;child xmlns=&quot;http://ns2&quot;" +
									 " value=&quot;child&quot;&gt;&lt;/child&gt;" +
									 "&lt;/parent&gt;";


	@BeforeClass
	public static void before() {
		IfmapPep pep = new IfmapPepStub();
		DataModelService.newInstance(DummyDataModelConf.getDummyConf(), pep);
	}

	@Test(expected=InvalidIdentifierException.class)
	public void testNonXmlName() throws InvalidIdentifierException {
		Identity i = new Identity("blub", "", "extended", IdentityTypeEnum.other);
		assertNull(i);
	}

	@Test
	public void testValid() throws InvalidIdentifierException {
		Identity i = new Identity(valid, "", "extended", IdentityTypeEnum.other);
		assertNotNull(i);
	}

	@Test
	public void testValidEmptyAdmDomain() throws InvalidIdentifierException {
		Identity i = new Identity(validEmptyAdmDom, null, "extended", IdentityTypeEnum.other);
		assertNotNull(i);
	}

	@Ignore
	@Test(expected=InvalidIdentifierException.class)
	public void testValidInnerEmptyAdmDomainButOuterAdminDomain() throws InvalidIdentifierException {
		// FIXME: an empty administrativ-domain ist NOT no administrative-domain, this needs to fail!
		Identity i = new Identity(validEmptyAdmDom, "", "extended", IdentityTypeEnum.other);
		assertNotNull(i);
	}

	@Test
	public void testValid2() throws InvalidIdentifierException {
		Identity i = new Identity(valid, null, "extended", IdentityTypeEnum.other);
		assertNotNull(i);
	}

	@Test(expected=InvalidIdentifierException.class)
	public void testAttrOrdering() throws InvalidIdentifierException {
		Identity i = new Identity(validAttrWrong, "", "extended", IdentityTypeEnum.other);
		assertNull(i);
	}

	@Test(expected=InvalidIdentifierException.class)
	public void testAdmDomainMissing() throws InvalidIdentifierException {
		Identity i = new Identity(inValidNoAdmDom, "", "extended", IdentityTypeEnum.other);
		assertNull(i);
	}

	@Test(expected=InvalidIdentifierException.class)
	public void testValidButAdministrativeDomain() throws InvalidIdentifierException {
		Identity i = new Identity(valid, "admdom", "extended", IdentityTypeEnum.other);
		assertNull(i);
	}

	@Test(expected=InvalidIdentifierException.class)
	public void testUnescapedValid() throws InvalidIdentifierException {
		Identity i = new Identity(validNoEscape, "", "extended", IdentityTypeEnum.other);
		assertNull(i);
	}

	@Test(expected=InvalidIdentifierException.class)
	public void testBadNormStartEndTags() throws InvalidIdentifierException {
		Identity i = new Identity(validBadNorm, "", "extended", IdentityTypeEnum.other);
		assertNull(i);
	}

	@Test(expected=InvalidIdentifierException.class)
	public void testBadSpaces() throws InvalidIdentifierException {
		Identity i = new Identity(validBadNormSpace, "", "extended", IdentityTypeEnum.other);
		assertNull(i);
	}

	@Test(expected=InvalidIdentifierException.class)
	public void testWithNewline() throws InvalidIdentifierException {
		Identity i = new Identity(validWithNewline, "", "extended", IdentityTypeEnum.other);
		assertNull(i);
	}

	@Test(expected=InvalidIdentifierException.class)
	public void testNoNs() throws InvalidIdentifierException {
		Identity i = new Identity(noNS, "", "extended", IdentityTypeEnum.other);
		assertNull(i);
	}

	@Test(expected=InvalidIdentifierException.class)
	public void testUnusedNS() throws InvalidIdentifierException {
		Identity i = new Identity(unusedNS, "", "extended", IdentityTypeEnum.other);
		assertNull(i);
	}

	@Test
	public void testMultiElement() throws InvalidIdentifierException {
		Identity i = new Identity(validMultiElement, "", "extended", IdentityTypeEnum.other);
		assertNotNull(i);
	}

	@Test
	public void testMultiNS() throws InvalidIdentifierException {
		Identity i = new Identity(multiNS, "", "extended", IdentityTypeEnum.other);
		assertNotNull(i);
	}
}
