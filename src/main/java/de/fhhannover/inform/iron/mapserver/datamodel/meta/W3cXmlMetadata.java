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


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

import javax.xml.namespace.NamespaceContext;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import de.fhhannover.inform.iron.mapserver.datamodel.search.Filter;
import de.fhhannover.inform.iron.mapserver.exceptions.InvalidMetadataException;
import de.fhhannover.inform.iron.mapserver.utils.FilterAdaption;
import de.fhhannover.inform.iron.mapserver.utils.NullCheck;
import de.fhhannover.inform.iron.mapserver.utils.SimpleNamespaceContext;

class W3cXmlMetadata extends Metadata {
	
	private final Document mXmlDocument;
	private final Element mXmlElement;
	private String mPrefixElementName;
	private String mMetadataAsString;
	private String mTimeStamp;
	private String mPublisherId;
	
	private String mTrustTokenId;
	
	private static XPathFactory xpathFactory;
	private static TransformerFactory transformerFactory;

	static { 
		try {
			xpathFactory = XPathFactory.newInstance();
			transformerFactory = TransformerFactory.newInstance();
		} catch (TransformerFactoryConfigurationError e) {
			sLogger.error("Could not get a TransformerFactory instance: "
					+ e.getMessage());
			throw new RuntimeException(e.getMessage());
		}
	}

	W3cXmlMetadata(Document doc, MetadataType type, boolean validated)
			throws InvalidMetadataException {
		// As dangerous as before :-(
		super(type, validated);
		NullCheck.check(doc, "node is null");
		
		mXmlDocument = doc;
		
		if (mXmlDocument.getChildNodes().getLength() != 1)
			throw new InvalidMetadataException("wrong number of elements");
		
		if (mXmlDocument.getFirstChild().getNodeType() != Node.ELEMENT_NODE)
			throw new InvalidMetadataException("node is not element node");

		mXmlElement = (Element) mXmlDocument.getFirstChild();
		createStrings();
	}
	
	@Override
	public boolean matchesFilter(Filter f) {
		NullCheck.check(f, "filter is null");
		/*
		logger.trace("matching with filter " + f.toString());
		*/
		
		// shortcut
		if (f.isMatchEverything()) {
			return true;
		}
	
		// shortcut
		if (f.isMatchNothing()) {
			return false;
		}
		
		String fs = f.getFilterString();
		XPath xpath = xpathFactory.newXPath();
		
		Map<String, String> nsMap = f.getNamespaceMap();
		
		/*
		if (logger.isTraceEnabled()) {
			int cnt = 1;
			logger.trace("Namespace map used for matching:");
			for (Entry<String, String> e : nsMap.entrySet()) {
				logger.trace(cnt++ + ":\t" +  e.getKey() + " -- " + e.getValue());
			}
		}
		*/
		
		NamespaceContext nsCtx = new SimpleNamespaceContext(nsMap);
		xpath.setNamespaceContext(nsCtx);
	
		/*
		logger.trace("Filter before adaption: " + fs);
		*/

		// add * to lonely brackets
		fs = FilterAdaption.adaptFilterString(fs);
	
		/*
		logger.trace("Filter after adaption: " + fs);
		*/
		
		XPathExpression expr = null;

		// this should never happen, as we checked it before
		try {
			expr = xpath.compile(fs);
		} catch (XPathExpressionException e1) {
			sLogger.error("UNEXPECTED: Could not compile filterstring" + fs);
			return false;
		}
		
		Object ret = null;
		try {
			ret = expr.evaluate(mXmlDocument, XPathConstants.BOOLEAN);
			/*
			logger.trace("matching result is " + ((Boolean)ret).booleanValue());
			*/
		} catch (XPathExpressionException e) {
			e.printStackTrace();
			sLogger.error("evaluate failed badly: " + e.getMessage());
			return false;
		}
		return ((Boolean)ret).booleanValue();
	}

	@Override
	public void setPublisherIdInternal(String pubId) {
		mPublisherId = pubId;
		mXmlElement.setAttribute(PUBLISHERID, mPublisherId);
		createStrings();
	}

	@Override
	public Document toW3cDocument() {
		return (Document) mXmlDocument.cloneNode(true);
	}
	
	@Override
	public void setTimeStampInternal(String ts) {
		mTimeStamp = ts;
		mXmlElement.setAttribute(TIMESTAMP, mTimeStamp);
		createStrings();
	}

	/**
	 * No pretty print, no nothing, but remove the leading <xml .... > stuff
	 */
	private void createStrings() {
		
		
		Transformer trans = null;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		Result result = new StreamResult(baos);
		Source source = new DOMSource(mXmlElement);
		try {
			trans = transformerFactory.newTransformer();
			trans.setOutputProperty(OutputKeys.INDENT, "no");
			trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			trans.setOutputProperty(OutputKeys.METHOD, "xml");
		} catch (TransformerConfigurationException e) {
			sLogger.error("Could not create Transformer instance: " + e.getMessage());
			throw new RuntimeException(e.getMessage());
		}
		
		try {
			trans.transform(source, result);
		} catch (TransformerException e) {
			sLogger.error("Could not do transformation: " + e.getMessage());
			throw new RuntimeException(e.getMessage());
		}

		try {
			baos.flush();
		} catch (IOException e) {
			sLogger.error("Could not create Transformer instance: " + e.getMessage());
			throw new RuntimeException(e.getMessage());
		}
		mMetadataAsString = new String(baos.toByteArray());
		mPrefixElementName = mXmlElement.getNodeName();

		// only set the byte count if we are fully initialzied
		if (mTimeStamp != null && mPublisherId != null && mTrustTokenId != null)
			setByteCount(mMetadataAsString.length());
	}

	@Override
	public String getMetadataAsString() {
		return mMetadataAsString;
	}

	@Override
	public String toString() {
		return mMetadataAsString;
	}

	@Override
	public String getPrefixAndElement() {
		return mPrefixElementName;
	}

	@Override
	public void setTrustTokenIdInternal(String ttid) {
		mTrustTokenId = ttid;
		mXmlElement.setAttribute(TRUST_TOKEN_ID, mTrustTokenId);
		createStrings();
	}

	@Override
	protected String getTrustTokenInternal() {
		return mTrustTokenId;
	}

}

