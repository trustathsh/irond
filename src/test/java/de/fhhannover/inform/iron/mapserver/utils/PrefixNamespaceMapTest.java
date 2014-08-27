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
package de.fhhannover.inform.iron.mapserver.utils;


import java.io.ByteArrayInputStream;
import java.util.Map;

import junit.framework.TestCase;
import de.fhhannover.inform.iron.mapserver.binding.RequestUnmarshaller;
import de.fhhannover.inform.iron.mapserver.binding.RequestUnmarshallerFactory;
import de.fhhannover.inform.iron.mapserver.datamodel.DataModelService;
import de.fhhannover.inform.iron.mapserver.datamodel.meta.MetadataFactory;
import de.fhhannover.inform.iron.mapserver.datamodel.meta.MetadataFactoryImpl;
import de.fhhannover.inform.iron.mapserver.datamodel.meta.MetadataTypeRepository;
import de.fhhannover.inform.iron.mapserver.datamodel.meta.MetadataTypeRepositoryImpl;
import de.fhhannover.inform.iron.mapserver.datamodel.search.Filter;
import de.fhhannover.inform.iron.mapserver.exceptions.ProviderInitializationException;
import de.fhhannover.inform.iron.mapserver.messages.PublishDelete;
import de.fhhannover.inform.iron.mapserver.messages.PublishRequest;
import de.fhhannover.inform.iron.mapserver.messages.Request;
import de.fhhannover.inform.iron.mapserver.messages.SearchRequest;
import de.fhhannover.inform.iron.mapserver.messages.SubscribeRequest;
import de.fhhannover.inform.iron.mapserver.messages.SubscribeUpdate;
import de.fhhannover.inform.iron.mapserver.provider.SchemaProvider;
import de.fhhannover.inform.iron.mapserver.provider.SchemaProviderImpl;
import de.fhhannover.inform.iron.mapserver.provider.ServerConfigurationProvider;
import de.fhhannover.inform.iron.mapserver.provider.StubProvider;

public class PrefixNamespaceMapTest extends TestCase {

	private RequestUnmarshaller unmarshaller;

	@Override
	public void setUp() {
		ServerConfigurationProvider conf = StubProvider.getServerConfStub(1);
		MetadataTypeRepository mdtr = MetadataTypeRepositoryImpl.newInstance();
		MetadataFactory mfac = MetadataFactoryImpl.newInstance(mdtr, conf);
		SchemaProvider schemaProv = null;
		try {
			schemaProv = new SchemaProviderImpl(conf);
		} catch (ProviderInitializationException e) {
			e.printStackTrace();
		}

		unmarshaller = RequestUnmarshallerFactory.newRequestUnmarshaller(mfac, schemaProv);
		DataModelService.setServerConfiguration(conf);
	}

	public void testBuildNamespaceMap_SearchRequest() {

		ByteArrayInputStream fis = new ByteArrayInputStream(SEARCH_REQUEST.getBytes());
		Request req = null;
		Map<String, String> nsMap = null;
		String val = null;

		try {
			req = unmarshaller.unmarshal(fis);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}

		assertTrue(req instanceof SearchRequest);
		SearchRequest searchRequest = (SearchRequest) req;
		Filter matchLinksFilter = searchRequest.getMatchLinksFilter();
		Filter resultFiler = searchRequest.getResultFilter();

		assertNotNull(matchLinksFilter.getNamespaceMap());
		assertEquals(matchLinksFilter.getNamespaceMap(), resultFiler.getNamespaceMap());


		nsMap = matchLinksFilter.getNamespaceMap();

		assertEquals(nsMap.size(), 3);

		val = nsMap.get("env");
		assertNotNull(val);
		assertEquals(val, "http://www.w3.org/2003/05/soap-envelope");

		val = nsMap.get("ifmap");
		assertNotNull(val);
		assertEquals(val, "http://www.trustedcomputinggroup.org/2010/IFMAP/2");

		val = nsMap.get("blub");
		assertNotNull(val);
		assertEquals(val, "http://blub.com");
	}

	/**
	 * basically the same as for the search, but this time declare
	 * blub two times, http://blub.de is the right one (the most inner one)
	 */
	public void testBuildNamespaceMap_PublishDelete() {

		ByteArrayInputStream fis = new ByteArrayInputStream(PUBLISH_DELETE_REQUEST.getBytes());
		Request req = null;
		Map<String, String> nsMap = null;
		String val = null;

		try {
			req = unmarshaller.unmarshal(fis);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}

		assertTrue(req instanceof PublishRequest);
		PublishRequest publishRequest = (PublishRequest) req;
		// yeah yeah i'm sure...
		PublishDelete publishDelete = (PublishDelete) publishRequest.getSubPublishRequestList().get(0);
		Filter deleteFilter = publishDelete.getFilter();

		assertNotNull(deleteFilter);


		nsMap = deleteFilter.getNamespaceMap();

		assertEquals(nsMap.size(), 3);

		val = nsMap.get("env");
		assertNotNull(val);
		assertEquals(val, "http://www.w3.org/2003/05/soap-envelope");

		val = nsMap.get("ifmap");
		assertNotNull(val);
		assertEquals(val, "http://www.trustedcomputinggroup.org/2010/IFMAP/2");

		val = nsMap.get("blub");
		assertNotNull(val);
		assertEquals(val, "http://blub.de");
	}

	/**
	 * this time it's http://blub.org ;-)
	 */
	public void testBuildNamespaceMap_SubscribeUpdat() {

		ByteArrayInputStream fis = new ByteArrayInputStream(SUBSCRIBE_UPDATE_REQUEST.getBytes());
		Request req = null;
		Map<String, String> nsMap = null;
		String val = null;

		try {
			req = unmarshaller.unmarshal(fis);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}

		assertTrue(req instanceof SubscribeRequest);
		SubscribeRequest subRequest = (SubscribeRequest) req;
		// yeah yeah i'm sure...
		SubscribeUpdate subUpdate = (SubscribeUpdate) subRequest.getSubSubscribeRequests().get(0);
		Filter matchLinksFilter = subUpdate.getSearchRequest().getMatchLinksFilter();
		Filter resultFilter = subUpdate.getSearchRequest().getResultFilter();

		assertNotNull(matchLinksFilter);

		assertNotNull(matchLinksFilter);
		assertNotNull(resultFilter);

		assertEquals(matchLinksFilter.getNamespaceMap(), resultFilter.getNamespaceMap());

		nsMap = matchLinksFilter.getNamespaceMap();

		assertEquals(nsMap.size(), 3);

		val = nsMap.get("env");
		assertNotNull(val);
		assertEquals(val, "http://www.w3.org/2003/05/soap-envelope");

		val = nsMap.get("ifmap");
		assertNotNull(val);
		assertEquals(val, "http://www.trustedcomputinggroup.org/2010/IFMAP/2");

		val = nsMap.get("blub");
		assertNotNull(val);
		assertEquals(val, "http://blub.org");
	}




	private static final String SEARCH_REQUEST =
		"<env:Envelope xmlns:env=\"http://www.w3.org/2003/05/soap-envelope\""  +
		" xmlns:ifmap=\"http://www.trustedcomputinggroup.org/2010/IFMAP/2\">\n"   +
		"<env:Body>\n" +
		"<ifmap:search xmlns:blub=\"http://blub.com\" session-id=\"65125CEB1C00BE6C5F6EC580360BB630\"" +
		" max-depth=\"25\" match-links=\"blub:elx or [@ifmap-publisher-id=dumbnut]\">\n" +
		"<ip-address value=\"192.168.0.1\"/>\n" +
		"</ifmap:search>\n" +
			  "</env:Body>\n" +
			"</env:Envelope>";


	private static final String PUBLISH_DELETE_REQUEST =
		"<env:Envelope xmlns:env=\"http://www.w3.org/2003/05/soap-envelope\""  +
		" xmlns:ifmap=\"http://www.trustedcomputinggroup.org/2010/IFMAP/2\">\n"   +
		"<env:Body>\n" +
		"<ifmap:publish xmlns:blub=\"http://blub.com\" session-id=\"65125CEB1C00BE6C5F6EC580360BB630\">\n" +
		"<delete filter=\"blub:glucks\" xmlns:blub=\"http://blub.de\">\n" +
		"<ip-address value=\"192.168.0.1\"/>\n" +
		"</delete>\n" +
		"</ifmap:publish>" +
			  "</env:Body>\n" +
			"</env:Envelope>";


	private static final String SUBSCRIBE_UPDATE_REQUEST =
		"<env:Envelope xmlns:env=\"http://www.w3.org/2003/05/soap-envelope\""  +
		" xmlns:ifmap=\"http://www.trustedcomputinggroup.org/2010/IFMAP/2\">\n"   +
		"<env:Body>\n" +
		"<ifmap:subscribe xmlns:blub=\"http://blub.com\" session-id=\"65125CEB1C00BE6C5F6EC580360BB630\">\n" +
		"<update name=\"mysub\" result-filter=\"ifmap:nothing\" match-links=\"blub:glucks\" xmlns:blub=\"http://blub.org\">\n" +
		"<ip-address value=\"192.168.0.1\"/>\n" +
		"</update>\n" +
		"</ifmap:subscribe>" +
			  "</env:Body>\n" +
			"</env:Envelope>";
}
