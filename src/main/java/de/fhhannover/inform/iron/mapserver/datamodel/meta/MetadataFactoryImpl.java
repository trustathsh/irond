package de.fhhannover.inform.iron.mapserver.datamodel.meta;

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
 * This file is part of irond, version 0.4.0, implemented by the Trust@FHH 
 * research group at the Fachhochschule Hannover.
 * 
 * irond is an an *experimental* IF-MAP 2.0 compliant MAP server written in
 * JAVA. irond supports both basic authentication and certificate-based 
 * authentication (using X.509 certificates) of MAP clients. irond is
 * maintained by the Trust@FHH group at the Fachhochschule Hannover, initial
 * developement was carried out during the ESUKOM research project.
 * %%
 * Copyright (C) 2010 - 2013 Trust@FHH
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


import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import de.fhhannover.inform.iron.mapserver.exceptions.InvalidMetadataException;
import de.fhhannover.inform.iron.mapserver.exceptions.ValidationFailedException;
import de.fhhannover.inform.iron.mapserver.provider.DataModelServerConfigurationProvider;
import de.fhhannover.inform.iron.mapserver.utils.DomHelpers;
import de.fhhannover.inform.iron.mapserver.utils.NullCheck;

public class MetadataFactoryImpl implements MetadataFactory {

	private static final String CARDINALITYSTRING = "ifmap-cardinality";
	protected static DataModelServerConfigurationProvider mConf;
	private final MetadataTypeRepository mTypeRep;
	
	private MetadataFactoryImpl(MetadataTypeRepository typeRep,
			DataModelServerConfigurationProvider conf) {
		NullCheck.check(typeRep, "typeRep is null");
		mTypeRep = typeRep;
		mConf = conf;
	}
	
	public static MetadataFactory newInstance(MetadataTypeRepository typeRep,
			DataModelServerConfigurationProvider conf) {
		return new MetadataFactoryImpl(typeRep, conf);
	}

	@Override
	public Metadata newMetadata(Document doc)
			throws InvalidMetadataException {

		String name = null, ns = null;
		String cardString = null;
		MetaCardinalityType card = null;
		boolean validated = false;
		
		if (doc == null)
			throw new InvalidMetadataException("no doc?");
		
		if (doc.getChildNodes().getLength() != 1)
			throw new InvalidMetadataException("bad child count in doc");
	
		Node rootNode = doc.getChildNodes().item(0);
		
		if (rootNode.getNodeType() != Node.ELEMENT_NODE)
			throw new InvalidMetadataException("rootNode not element");
		
		Element root = (Element)rootNode;
		
		// Get the local name, if this returns null, we get the name
		name = root.getLocalName();
		name = (name == null) ? root.getNodeName() : name;
		
		ns = (root.getNamespaceURI() == null) ? "" : root.getNamespaceURI();
		
		if (name == null || name.length() == 0)
			throw new InvalidMetadataException("Bad metadata root elment?");

		cardString = root.getAttribute(CARDINALITYSTRING);
		
		if (cardString == null || cardString.length() == 0)
			throw new InvalidMetadataException("No ifmap-cardinality given?");

		try {
			card = MetaCardinalityType.valueOf(cardString);
		} catch (IllegalArgumentException e) {
			throw new InvalidMetadataException("Bad ifmap-cardinality ("
					+ cardString + ")");
		}
		
		// validate the metadata against a XML schema
		if (mConf.getXmlValidatationMetadata()) {
			if (ns.isEmpty())
				throw new InvalidMetadataException("No namespace for " + name);
			
			StreamSource schema = mConf.getMetadataSchema(ns);
			Boolean lockdown = mConf.getXmlValidationMetadataLockDownMode();
			
			if (schema == null && lockdown)
				throw new InvalidMetadataException("No schema for " + ns);
			
			try {
				if (schema != null)
					DomHelpers.validate(root.getOwnerDocument(), schema);
			} catch (ValidationFailedException e) {
				throw new InvalidMetadataException("validation failed: " +
						e.getMessage());
			}
		}
		
		DomHelpers.removeUnspecifiedIfmapAttributes(root);
		
		return new W3cXmlMetadata(doc, mTypeRep.getTypeFor(ns, name, card),
				validated);
	}
}
