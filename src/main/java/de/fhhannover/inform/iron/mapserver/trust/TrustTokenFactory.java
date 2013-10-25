package de.fhhannover.inform.iron.mapserver.trust;

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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import de.fhhannover.inform.iron.mapserver.datamodel.meta.MetaCardinalityType;
import de.fhhannover.inform.iron.mapserver.datamodel.meta.Metadata;
import de.fhhannover.inform.iron.mapserver.datamodel.meta.MetadataFactory;
import de.fhhannover.inform.iron.mapserver.exceptions.InvalidMetadataException;
import de.fhhannover.inform.iron.mapserver.trust.domain.SecurityProperty;
import de.fhhannover.inform.iron.mapserver.trust.domain.SecurityPropertyRecord;
import de.fhhannover.inform.iron.mapserver.trust.domain.TrustToken;
import de.fhhannover.inform.iron.mapserver.trust.utils.TrustConstStrings;

public class TrustTokenFactory {

	private DocumentBuilder mDocumentBuilder;
	
	private MetadataFactory mMetadataFactory;
	
	public TrustTokenFactory(MetadataFactory metadatafac) {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		mMetadataFactory = metadatafac;
		try {
			mDocumentBuilder = dbf.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			throw new RuntimeException(e);
		}
	}

	private Document createTtSingleElementDocument(String name, MetaCardinalityType card) {
		return createSingleElementDocument(
				TrustConstStrings.TT_METADATA_PREFIX + ":" + name,
				TrustConstStrings.TT_NS_URI,
				card);
	}
	
	private Document createSingleElementDocument(String qualifiedName,
			String uri, MetaCardinalityType cardinality) {
		Document doc = mDocumentBuilder.newDocument();
		Element e = doc.createElementNS(uri, qualifiedName);
		e.setAttributeNS(null, "ifmap-cardinality", cardinality.toString());
		doc.appendChild(e);
		return doc;
	}
	
	/**
	 * Helper to create a new element with name elName and append it to the
	 * {@link Element} given by parent. The new {@link Element} will have
	 * {@link Text} node containing value.
	 * 
	 * @param doc {@link Document} where parent is located in
	 * @param parent where to append the new element
	 * @param elName the name of the new element.
	 * @param value the value of the {@link Text} node appended to the new element,
	 *        using toString() on the object.
	 * @throws NullPointerException if value is null, or any other parameter
	 *         is null
	 * @return the new {@link Element}
	 */
	private Element createAndAppendTextElementCheckNull(Document doc, Element parent,
			String elName, Object value) {
		
		if (doc == null || parent == null || elName == null)
				throw new NullPointerException("bad parameters given");
		
		if (value == null)
			throw new NullPointerException("null is not allowed for " + elName 
					+ " in " + doc.getFirstChild().getLocalName());
		
		String valueStr = value.toString();
		if (valueStr == null)
			throw new NullPointerException("null-string not allowed for " + elName 
					+ " in " + doc.getFirstChild().getLocalName());
			
		Element child = createAndAppendElement(doc, parent, elName);
		Text txtCElement = doc.createTextNode(valueStr);
		child.appendChild(txtCElement);
		return child;
	}

	/**
	 * Helper to create an {@link Element} without a namespace in 
	 * {@link Document} doc and append it to the {@link Element} given by
	 * parent.
	 * 
	 * @param doc the target {@link Document}
	 * @param parent the parent {@link Element}
	 * @param elName the name of the new {@link Element} 
	 * @return the new {@link Element}
	 */
	private Element createAndAppendElement(Document doc, Element parent, String elName) {
		Element el = doc.createElementNS(null, elName);
		parent.appendChild(el);
		return el;
	}
	
	public Metadata createTtmMetadata(TrustToken p2tt, String ttId, String timestamp) {
		Document tt = createTtm(p2tt);
		try {
			Metadata m = mMetadataFactory.newMetadata(tt);
			setOperationalAttributes(m, ttId, timestamp);
			return m;
		} catch (InvalidMetadataException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public Metadata createTtiMetadata(TrustToken p2tt, String timestamp) {
		Document tt = createTti(p2tt);
		try {
			Metadata m = mMetadataFactory.newMetadata(tt);
			setOperationalAttributes(m, "<empty>", timestamp);
			return m;
		} catch (InvalidMetadataException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
		
	private void setOperationalAttributes(Metadata m, String ttid, String timestamp) {
		m.setTrustTokenId(ttid);
		m.setTimestamp(timestamp);
		m.setPublisherId("MAPS");
	}
	
	
	private Document createTtm(TrustToken ttm) {
		Document doc = createTtSingleElementDocument(TrustConstStrings.TT_TTM_NAME,
				MetaCardinalityType.multiValue);
		Element root = (Element) doc.getFirstChild();

		createAndAppendTextElementCheckNull(doc, root, "trust-level", ttm.getValue());
		createAndAppendTextElementCheckNull(doc, root, "mapc-id", ttm.getMapcId());
		
		createAndAppendSprElement(doc, root, "spr-process-sender", ttm.getProcessSender());
		createAndAppendSprElement(doc, root, "spr-transmit-sender-receiver", ttm.getTransmitSenderProvider());
		createAndAppendSprElement(doc, root, "spr-process-provider", ttm.getProcessProvider());
		createAndAppendSprElement(doc, root, "spr-transmit-provider-receiver", ttm.getTransmitProviderReceiver());
		
		return doc;
	}
	
	private Document createTti(TrustToken tti) {
		Document doc = createTtSingleElementDocument(TrustConstStrings.TT_TTI_NAME,
				MetaCardinalityType.singleValue);
		Element root = (Element) doc.getFirstChild();
		
		createAndAppendTextElementCheckNull(doc, root, "trust-level", tti.getValue());
		createAndAppendTextElementCheckNull(doc, root, "mapc-id", tti.getMapcId());
		
		createAndAppendSprElement(doc, root, "spr-process-sender", tti.getProcessSender());
		createAndAppendSprElement(doc, root, "spr-transmit-sender-receiver", tti.getTransmitSenderProvider());
		createAndAppendSprElement(doc, root, "spr-process-provider", tti.getProcessProvider());
		createAndAppendSprElement(doc, root, "spr-transmit-provider-receiver", tti.getTransmitProviderReceiver());
		
		return doc;
	}
	
	private void createAndAppendSprElement(Document doc, Element parent, String sprName, SecurityPropertyRecord spr) {
		Element sprElem = createAndAppendElement(doc, parent, sprName);
		createAndAppendTextElementCheckNull(doc, sprElem, "security-level", spr.getSl() + ";" );
		if (spr.getListOfSp().size() > 0) {
			for (SecurityProperty sp : spr.getListOfSp()) {
				createAndAppendTextElementCheckNull(doc, sprElem, "security-property", sp.getPropertyName() + ":" + sp.getRating() + ";");
			}
		}
	}
}
