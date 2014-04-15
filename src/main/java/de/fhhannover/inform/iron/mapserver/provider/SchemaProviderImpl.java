package de.fhhannover.inform.iron.mapserver.provider;

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

import java.io.File;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.SAXException;

import de.fhhannover.inform.iron.mapserver.exceptions.ProviderInitializationException;
import de.fhhannover.inform.iron.mapserver.utils.NullCheck;

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
		
		if (schemas.length == 0)
			throw new ProviderInitializationException("irond.xml.validate=true,"
					+ " but no schemas in config file?");
		
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
