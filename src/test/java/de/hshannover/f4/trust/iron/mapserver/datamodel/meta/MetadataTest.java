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
 * This file is part of irond, version 0.5.4, implemented by the Trust@HsH
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
package de.hshannover.f4.trust.iron.mapserver.datamodel.meta;

import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import junit.framework.TestCase;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.hshannover.f4.trust.iron.mapserver.datamodel.search.Filter;
import de.hshannover.f4.trust.iron.mapserver.datamodel.search.FilterType;
import de.hshannover.f4.trust.iron.mapserver.exceptions.InvalidMetadataException;
import de.hshannover.f4.trust.iron.mapserver.provider.StubProvider;

public class MetadataTest extends TestCase {

	private static String CARDINALITY = "ifmap-cardinality";
	private static String META_PREFIX = "meta";
	private static String META_URI = "http://www.trustedcomputinggroup.org/2010/IFMAP-METADATA/2";
	private Document mMetaRoleDoc;
	private Document mMetaCapDoc;
	private MetadataFactory mFac;

	private static Map<String, String> nsMap = new HashMap<String, String>();

	static {
		nsMap.put(META_PREFIX, META_URI);
	}

	public MetadataTest() {
		DocumentBuilderFactory fac = DocumentBuilderFactory.newInstance();
		fac.setNamespaceAware(true);
		DocumentBuilder builder = null;
		try {

			builder = fac.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		Document doc = builder.newDocument();
		Element role = doc.createElementNS(META_URI, "meta:role");
		role.setAttributeNS(null, CARDINALITY,
				MetaCardinalityType.multiValue.toString());
		role.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:meta",
				META_URI);
		Element name = doc.createElement("name");
		name.setTextContent("administrator");
		role.appendChild(name);

		doc.appendChild(role);
		mMetaRoleDoc = doc;

		doc = builder.newDocument();
		Element cap = doc.createElementNS(META_URI, "meta:capability");
		cap.setAttributeNS(null, CARDINALITY,
				MetaCardinalityType.multiValue.toString());
		cap.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:meta",
				META_URI);
		Element name2 = doc.createElement("name");
		name2.setTextContent("antivir installed");
		cap.appendChild(name2);
		doc.appendChild(cap);

		mMetaCapDoc = doc;

		MetadataTypeRepository mdtf = MetadataTypeRepositoryImpl.newInstance();
		mFac = MetadataFactoryImpl.newInstance(mdtf,
				StubProvider.getServerConfStub(1));
	}

	@Test
	public void testSimpleConstruction_Role() {

		Metadata meta = null;
		try {
			meta = mFac.newMetadata(mMetaRoleDoc);
		} catch (InvalidMetadataException e) {
			e.printStackTrace();
			fail();
		}
		assertNotNull(meta);
	}

	@Test
	public void testSimpleConstruction_Capability() {

		Metadata meta = null;
		try {
			meta = mFac.newMetadata(mMetaCapDoc);
		} catch (InvalidMetadataException e) {
			e.printStackTrace();
			fail();
		}
		assertNotNull(meta);
	}

	@Test
	public void testMatching_Role() {
		try {
			Filter f1 = new Filter("meta:role", nsMap,
					FilterType.MATCH_LINKS_FILTER);
			Filter f2 = new Filter("meta:capability", nsMap,
					FilterType.MATCH_LINKS_FILTER);
			Filter f3 = new Filter("meta:role/name=\"administrator\"", nsMap,
					FilterType.MATCH_LINKS_FILTER);
			Filter f4 = new Filter("meta:role/name=\"chief\"", nsMap,
					FilterType.MATCH_LINKS_FILTER);
			Filter f5 = new Filter("meta:role/name=\"administrator\"", nsMap,
					FilterType.MATCH_LINKS_FILTER);
			Filter f6 = new Filter("role/name=\"administrator\"", nsMap,
					FilterType.MATCH_LINKS_FILTER);

			Metadata meta = mFac.newMetadata(mMetaRoleDoc);
			assertTrue(meta.matchesFilter(f1));
			assertFalse(meta.matchesFilter(f2));
			assertTrue(meta.matchesFilter(f3));
			assertFalse(meta.matchesFilter(f4));
			assertTrue(meta.matchesFilter(f5));
			assertFalse(meta.matchesFilter(f6));

		} catch (InvalidMetadataException e) {
			e.printStackTrace();
			fail();
		}
	}
}
