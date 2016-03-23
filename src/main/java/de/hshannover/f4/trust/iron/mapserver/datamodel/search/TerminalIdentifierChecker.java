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
package de.hshannover.f4.trust.iron.mapserver.datamodel.search;



import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import de.hshannover.f4.trust.iron.mapserver.IfmapConstStrings;
import de.hshannover.f4.trust.iron.mapserver.datamodel.graph.Node;
import de.hshannover.f4.trust.iron.mapserver.datamodel.identifiers.Identity;
import de.hshannover.f4.trust.iron.mapserver.datamodel.identifiers.IdentityTypeEnum;
import de.hshannover.f4.trust.iron.mapserver.exceptions.SearchException;

/**
 * Utility class for IF-MAP 2.2 compliant checking of terminal identifiers for search and subscriptions.
 *
 * @author rennersl
 *
 */
public class TerminalIdentifierChecker {

	/**
	 * Returns whether a given node (identifier) type is included in the terminal identifiers (patterns)
	 *
	 * @param node The node to check
	 * @param ti The terminal identifiers to check against
	 * @return whether the node (identifier) type is included in the terminal identifiers
	 * @throws SearchException If the search request is corrupted
	 */
	public static boolean isTerminalIdentifier(Node node, TerminalIdentifiers ti) throws SearchException {
		String type = node.getIdentifier().getTypeString();
		// If standard identifier type matches
		if(ti.contains(type)) {
			return true;
		}

		// Further investigations for identity subtypes (see IF-MAP 2.2 rev 9 spec: 3.9.3.2.6)
		if (type.equals("identity")) {
			Identity id = (Identity) node.getIdentifier();
			//  if nonextended is set
			if(ti.contains("identity:nonextended")) {
				// standard types like username
				if(!id.getIdentityType().equals(IdentityTypeEnum.other)) {
					return true;
				}
				// other types except extended
				if(!id.getOtherTypeDefinition().equals(IfmapConstStrings.ID_OTHER_EXT)) {
					return true;
				}
			}

			// extended identifiers
			if(id.getIdentityType().equals(IdentityTypeEnum.other)) {
				String othertype = id.getOtherTypeDefinition();
				if(othertype.equals(IfmapConstStrings.ID_OTHER_EXT)) {
					// if all extended identifiers are matched
					if(ti.contains(IfmapConstStrings.ID_EXT)) {
						return true;
					}
					// extract and compare namespace pattern from extended identifiers (NAMESPACE#TYPE)
					try {
						return ti.contains(getNamespaceAndType(id));
					} catch (SAXException e) {
						throw new SearchException(e.getMessage());
					}
				}
				// if other typed, but not extended set type for later comparison
				type = type + ":other:" + othertype;
			}
			else {
				//if regular subtype of identity set type for later comparison
				type = type + ":" + id.getIdentityType();
			}
		}
		//if the identity was not extended and the "nonextended" terminalIdentifier was not set check for the single types
		return ti.contains(type);
	}

	private static String getNamespaceAndType(Identity identity) throws SAXException {
		DocumentBuilderFactory fac = DocumentBuilderFactory.newInstance();
		fac.setNamespaceAware(true);
		DocumentBuilder builder;
		try {
			builder = fac.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			throw new RuntimeException("could not create document builder", e);
		}

		String escapedXml = identity.getName();
		String xml = unescapeXML(escapedXml);
		StringReader reader = new StringReader(xml);
		InputSource source = new InputSource(reader);
		Document d;
			try {
				d = builder.parse(source);
			} catch (IOException e) {
				// should never happen, since we read from a string instead of a file
				throw new RuntimeException(e);
			}
		String typename = d.getDocumentElement().getLocalName();
		String namespace = d.getDocumentElement().getNamespaceURI();
		return namespace + "#" + typename;
	}


	//--------------------------------------------------------------------------
	/*
	 * Taken from http://stackoverflow.com/questions/2833956/how-to-unescape-xml-in-java,
	 * thanks to texclayton (http://stackoverflow.com/users/909872/texclayton).
	 */
	public static String unescapeXML( final String xml )
	{
	    Pattern xmlEntityRegex = Pattern.compile( "&(#?)([^;]+);" );
	    //Unfortunately, Matcher requires a StringBuffer instead of a StringBuilder
	    StringBuffer unescapedOutput = new StringBuffer( xml.length() );

	    Matcher m = xmlEntityRegex.matcher( xml );
	    Map<String,String> builtinEntities = null;
	    String entity;
	    String hashmark;
	    String ent;
	    int code;
	    while ( m.find() ) {
	        ent = m.group(2);
	        hashmark = m.group(1);
	        if ( hashmark != null && hashmark.length() > 0 ) {
	            code = Integer.parseInt( ent );
	            entity = Character.toString( (char) code );
	        } else {
	            //must be a non-numerical entity
	            if ( builtinEntities == null ) {
	                builtinEntities = buildBuiltinXMLEntityMap();
	            }
	            entity = builtinEntities.get( ent );
	            if ( entity == null ) {
	                //not a known entity - ignore it
	                entity = "&" + ent + ';';
	            }
	        }
	        m.appendReplacement( unescapedOutput, entity );
	    }
	    m.appendTail( unescapedOutput );

	    return unescapedOutput.toString();
	}
	private static Map<String,String> buildBuiltinXMLEntityMap()
	{
	    Map<String,String> entities = new HashMap<String,String>(10);
	    entities.put( "lt", "<" );
	    entities.put( "gt", ">" );
	    entities.put( "amp", "&" );
	    entities.put( "apos", "'" );
	    entities.put( "quot", "\"" );
	    return entities;
	}
	//--------------------------------------------------------------------------

}
