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
 * This file is part of irond, version 0.5.3, implemented by the Trust@HsH
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
package de.hshannover.f4.trust.iron.mapserver.provider;


import java.io.File;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.SAXException;

import de.hshannover.f4.trust.iron.mapserver.exceptions.ProviderInitializationException;
import de.hshannover.f4.trust.iron.mapserver.utils.NullCheck;

/**
 * A {@link SchemaProvider} implementation returning a {@link Schema} created
 * from all files specified in the server configuration.
 *
 * @since 0.3.0
 * @author aw
 */
public class SchemaProviderImpl implements SchemaProvider {
	/**
	 * Instance of the schema we will return
	 */
	private final Schema mSchema;

	public SchemaProviderImpl(ServerConfigurationProvider serverConf) throws ProviderInitializationException {
		NullCheck.check(serverConf, "serverConf is null");

		if (!serverConf.getXmlValidation()) {
			mSchema = null;
			return;
		}

		String[] schemas = serverConf.getSchemaFileNames();

		if (schemas.length == 0) {
			throw new ProviderInitializationException("irond.xml.validate=true,"
					+ " but no schemas in config file?");
		}

		Source[] fileSources = new Source[schemas.length];

		SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

		for (int i = 0; i < schemas.length; i++) {
			String schemaFile = schemas[i];
			fileSources[i] = new StreamSource(new File(schemaFile));
		}

		try {
			mSchema = sf.newSchema(fileSources);
		} catch (SAXException e) {
			throw new ProviderInitializationException(e.getMessage());
		}
	}

	@Override
	public Schema provideSchema() {
		return mSchema;
	}
}
