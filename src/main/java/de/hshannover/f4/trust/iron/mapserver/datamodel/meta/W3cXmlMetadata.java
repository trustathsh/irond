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
 * This file is part of irond, version 0.5.7, implemented by the Trust@HsH
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
package de.hshannover.f4.trust.iron.mapserver.datamodel.meta;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;

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

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import de.hshannover.f4.trust.iron.mapserver.exceptions.InvalidMetadataException;
import de.hshannover.f4.trust.iron.mapserver.utils.Iso8601DateTime;
import de.hshannover.f4.trust.iron.mapserver.utils.NullCheck;
import de.hshannover.f4.trust.iron.mapserver.utils.TimestampFraction;

public class W3cXmlMetadata extends Metadata {

	private final Document mXmlDocument;
	private final Element mXmlElement;
	private String mPrefixElementName;
	private String mMetadataAsString;
	private Date mTimeStamp;
	private String mPublisherId;

	private static TransformerFactory transformerFactory;

	static {
		try {
			transformerFactory = TransformerFactory.newInstance();
		} catch (TransformerFactoryConfigurationError e) {
			sLogger.error("Could not get a TransformerFactory instance: "
					+ e.getMessage());
			throw new RuntimeException(e.getMessage());
		}
	}

	public W3cXmlMetadata(Document doc, MetadataType type, boolean validated)
			throws InvalidMetadataException {
		// As dangerous as before :-(
		super(type, validated);
		NullCheck.check(doc, "node is null");

		mXmlDocument = doc;

		if (mXmlDocument.getChildNodes().getLength() != 1) {
			throw new InvalidMetadataException("wrong number of elements");
		}

		if (mXmlDocument.getFirstChild().getNodeType() != Node.ELEMENT_NODE) {
			throw new InvalidMetadataException("node is not element node");
		}

		mXmlElement = (Element) mXmlDocument.getFirstChild();
		createStrings();
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
	public void setTimeStampInternal(Date ts) {
		mTimeStamp = ts;
		mXmlElement.setAttribute(TIMESTAMP,
				Iso8601DateTime.formatDate(mTimeStamp));
		mXmlElement.setAttribute(TIMESTAMP_FRACTION,
				TimestampFraction.getSecondFraction(mTimeStamp) + "");
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
			sLogger.error("Could not create Transformer instance: "
					+ e.getMessage());
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
			sLogger.error("Could not create Transformer instance: "
					+ e.getMessage());
			throw new RuntimeException(e.getMessage());
		}
		mMetadataAsString = new String(baos.toByteArray());
		mPrefixElementName = mXmlElement.getNodeName();

		// only set the byte count if we are fully initialzied
		if (mTimeStamp != null && mPublisherId != null) {
			setByteCount(mMetadataAsString.length());
		}
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
}
