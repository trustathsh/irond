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
package de.hshannover.f4.trust.iron.mapserver.utils;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import de.hshannover.f4.trust.iron.mapserver.exceptions.InvalidIdentifierException;
import de.hshannover.f4.trust.iron.mapserver.exceptions.InvalidMetadataException;
import de.hshannover.f4.trust.iron.mapserver.exceptions.SystemErrorException;
import de.hshannover.f4.trust.iron.mapserver.exceptions.ValidationFailedException;

/**
 * Some helpers to handle javax.xml and org.w3c.* stuff...
 *
 * @author aw
 * @author jk
 */
public class DomHelpers {

	private static String unwanted[] =  { "&",     "<",    ">",   "\"",    "'" };
	private static String replaceBy[] = { "&amp;", "&lt;", "&gt;", "&quot;", "&apos;" };

	private static final DocumentBuilderFactory sDocumentBuilderFactory;
	private static final DocumentBuilderThreadLocal sDocBuilder;
	private static final TransformerFactory sTransformerFactory;
	private static final TransformerThreadLocal sTransformer;

	static {
		sDocumentBuilderFactory = DocumentBuilderFactory.newInstance();
		sDocumentBuilderFactory.setNamespaceAware(true);
		sTransformerFactory = TransformerFactory.newInstance();
		sDocBuilder = new DocumentBuilderThreadLocal();
		sTransformer = new TransformerThreadLocal();
	}

	private static final DocumentBuilder getDocumentBuilder() {
			sDocBuilder.get().reset();
			return sDocBuilder.get();
	}

	private static final Transformer getTransformer() {
			sTransformer.get().reset();
			return sTransformer.get();
	}

	/**
	 * Marshal a {@link Document} to {@link InputStream}
	 *
	 * @param is the InputStream containing XML data
	 * @return {@link Document} containg the XML data
	 * @throws SAXException
	 * @throws IOException
	 */
	public static final Document toDocument(InputStream is)
			throws SAXException, IOException {
		DocumentBuilder db = getDocumentBuilder();

		return db.parse(is);
	}

	/**
	 * Marshal a {@link Document} from {@link String} with XML data
	 *
	 * @param s string containing XML data
	 * @param c charset used for encoding the string
	 * @return {@link Document} containg the XML data
	 * @throws IOException
	 * @throws SAXException
	 */
	public static final Document toDocument(String s, Charset c)
			throws SAXException, IOException {

		byte[] bytes = c == null ? s.getBytes() : s.getBytes(c);
		ByteArrayInputStream bais = new ByteArrayInputStream(bytes);

		return toDocument(bais);
	}

	public static final String fromDocument(Document doc) throws TransformerException {
		Transformer tf = getTransformer();
		StringWriter buffer = new StringWriter();
		tf.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			tf.transform(new DOMSource(doc.getFirstChild()),
					new StreamResult(buffer));
		return buffer.toString();
	}
	/**
	 * Compare two DOM documents
	 *
	 * @param d1 First DOM document
	 * @param d2 Second DOM document
	 * @return true if both are equal
	 */
	public static final boolean compare(Document d1, Document d2) {
		d1.normalize();
		d2.normalize();
		Boolean result = d1.isEqualNode(d2);
		return result;
	}

	/**
	 * Prepare an extended identifier by setting the default namespace,
	 * checking for multiple namespaces, putting the document into normal
	 * form and escaping all XML characters.
	 *
	 * @param str
	 * @return
	 * @throws InvalidIdentifierException
	 */
	public static final String toExtendedIdentifierValue(Document doc)
			throws InvalidIdentifierException {

		String res = null;

		Transformer tf = getTransformer();

		fixupNamespace(doc);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DOMSource domSource = new DOMSource(doc.getFirstChild());
		Result result = new StreamResult(baos);

		tf.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		tf.setOutputProperty(OutputKeys.INDENT, "no");
		tf.setOutputProperty(OutputKeys.METHOD, "xml");

		try {
			tf.transform(domSource, result);
		} catch (TransformerException e) {
			throw new SystemErrorException(e.getMessage());
		}

		ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
		InputSource inputSource = new InputSource(bais);

		CanonicalXML cxml = new CanonicalXML();
		XMLReader reader = null;
		try {
			reader = XMLReaderFactory.createXMLReader();
		} catch (SAXException e) {
			throw new SystemErrorException(e.getMessage());
		}

		try {
			res = cxml.toCanonicalXML2(reader, inputSource, true);
		} catch (Exception e) {
			// hmm... toCanonicalXML throws Exception...
			throw new SystemErrorException(e.getMessage());
		}

		return escapeXml(res);
	}


	/**
	 * If the top-level element has a prefix associated with it, drop it.
	 * Go recursively down and remove all prefixes. If we come across
	 * a different prefix, throw a {@link InvalidIdentifierException}...
	 *
	 * @param doc
	 */
	private static void fixupNamespace(Document doc) throws InvalidIdentifierException {

		Node n;
		Element el;
		String prefix;
		String nsUri;

		n = doc.getFirstChild();

		if (n.getNodeType() != Node.ELEMENT_NODE) {
			throw new SystemErrorException("fixupNamespace() No element!");
		}

		el = (Element)n;

		prefix = el.getPrefix();
		nsUri = el.getNamespaceURI();

		if (prefix != null && prefix.length() > 0) {
			el.setPrefix(null);
		} else {
			prefix = "";
		}

		if (nsUri == null || nsUri.length() == 0) {
			throw new InvalidIdentifierException("Top-level element of extended " +
												 "identifier with no namespace");
		}

		dropNamespaceDecls(el);

		removePrefixFromChildren(el, prefix);
	}

	/**
	 * If any child of el has prefix as prefix, remove it. drop all namespace
	 * decls on the way. If we find an element with a different prefix, go
	 * crazy.
	 *
	 * @param el
	 * @param prefix
	 */
	private static void removePrefixFromChildren(Element el, String prefix)
			throws InvalidIdentifierException {

		NodeList nl = el.getChildNodes();
		String localPrefix = null;
		for (int i = 0; i < nl.getLength(); i++) {
			Node n = nl.item(i);

			if (n.getNodeType() != Node.ELEMENT_NODE) {
				continue;
			}

			localPrefix = n.getPrefix();

			if (localPrefix != null && localPrefix.length() > 0) {

				if (!localPrefix.equals(prefix)) {
					throw new InvalidIdentifierException("Extended Identifier: " +
								"Multiple namespaces in extended identifer used." +
								"irond thinks this is not a wise idea. Sorry!");
				}

				n.setPrefix(null);
			}

			removePrefixFromChildren((Element)n, prefix);
			dropNamespaceDecls((Element)n);
		}
	}

	/**
	 * Drop all namespace declarations on the given {@link Element}.
	 *
	 * @param el
	 */
	private static void dropNamespaceDecls(Element el) {
		NamedNodeMap nnm = el.getAttributes();
		List<Attr> toDrop = CollectionHelper.provideListFor(Attr.class);

		for (int i = 0; i < nnm.getLength(); i++) {
			Attr attr = (Attr)nnm.item(i);
			if (attr.getName().startsWith("xmlns:")) {
				toDrop.add(attr);
			}
		}

		for (Attr attr : toDrop) {
			nnm.removeNamedItemNS(attr.getNamespaceURI(), attr.getLocalName());
		}
	}

	/**
	 * Escape all XML characters located in a string.
	 *
	 * @param input
	 * @return
	 */
	public static String escapeXml(String input) {

		String ret = input;

		for (int i = 0; i < unwanted.length; i++) {
			ret = ret.replace(unwanted[i], replaceBy[i]);
		}

		return ret;
	}

	/**
	 * Unescape all XML entity references characters from
	 * an escaped XML string
	 *
	 * @param xml Ecaped XML string representation
	 * @return Unescaped XML string representation
	 */
	public static final String unescapeXml(String input) {
		String ret = input;

		for (int i = 0; i < replaceBy.length; i++) {
			ret = ret.replace(replaceBy[i], unwanted[i]);
		}

		return ret;
	}

	public static int calculateHashCode(Document doc) {
		try {
			doc.normalize();
			Transformer t = TransformerFactory.newInstance().newTransformer();
			t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			t.setOutputProperty(OutputKeys.INDENT, "no");
			StringWriter sw = new StringWriter();
			DOMSource source = new DOMSource(doc);
			t.transform(source, new StreamResult(sw));
			Integer hc = sw.getBuffer().toString().hashCode();
			return hc;
		} catch (TransformerException e) {
			return 0;
		}
	}

	/**
	 * If we cannot validate this {@link Document} with the given {@link Schema},
	 * throws {@link ValidationFailedException}, otherwise just return.
	 *
	 * @param xml
	 * @param schema
	 * @throws InvalidIdentifierException
	 * @throws InvalidMetadataException
	 */
	public static void validate(Document xml, StreamSource schema) throws ValidationFailedException {

		NullCheck.check(schema, "Schema is null");

		SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		try {
			Schema mSchema = sf.newSchema(schema);
			mSchema.newValidator().validate(new DOMSource(xml));
		} catch (SAXException e) {
				throw new ValidationFailedException(e.getMessage());
		} catch (IOException e) {
				throw new ValidationFailedException(e.getMessage());
		}
	}

	/**
	 * Method to remove attributes from Metadata elements, which start with
	 * "ifmap-", but are not allowed to be sent by IFMAP-Client.
	 *
	 * TODO:
	 * - A logger statement would be nice if attributes are removed
	 *
	 * @param meta
	 */
	public static void removeUnspecifiedIfmapAttributes(Node meta) {
		// clients are only allowed to send ifmap-cardinality
		String[] specifiedIfmapAttributes = { "ifmap-cardinality" };

		if (meta == null) {
			return;
		}

		NamedNodeMap nnm = meta.getAttributes();

		for (int i = 0; nnm != null && i < nnm.getLength(); i++) {
			Node attrNode = nnm.item(i);

			if (attrNode.getNodeType() != Node.ATTRIBUTE_NODE) {
				continue;
			}

			String attrNodeName = attrNode.getLocalName();

			boolean notSpecified = true;
			if (attrNodeName.startsWith("ifmap-")) {
				for (String sa : specifiedIfmapAttributes) {
					if (attrNodeName.equals(sa)) {
						notSpecified = false;
						break;
					}
				}
			} else {
				// not "ifmap-" prefixed, we are not interested
				continue;
			}

			// remove
			if (notSpecified) {
				nnm.removeNamedItem(attrNodeName);
			}
		}
	}

	/**
	 * Takes an {@link Element}, creates a deep copy of it and attaches
	 * it to a new {@link Document} instance.
	 *
	 * @param el
	 * @return
	 */
	public static Document deepCopy(Element el) {
		Document ret = getDocumentBuilder().newDocument();
		Node cpyNode = ret.importNode(el, true);

		if (cpyNode.getNodeType() != Node.ELEMENT_NODE) {
			throw new SystemErrorException("deep copy of non-element nodej");
		}

		ret.appendChild(cpyNode);

		NameSpaceStripper.stripUnusedNamespaces((Element)cpyNode);

		return ret;
	}

	/**
	 * Every {@link Thread} should have it's own {@link DocumentBuilder} instance.
	 */
	private static class DocumentBuilderThreadLocal extends ThreadLocal<DocumentBuilder> {

		@Override
		protected DocumentBuilder initialValue() {
			synchronized (sDocumentBuilderFactory) {
				DocumentBuilder ret = null;
				try {
						ret = sDocumentBuilderFactory.newDocumentBuilder();
				} catch (ParserConfigurationException e) {
					e.printStackTrace();
					System.exit(1);
				}

				return ret;
			}
		}
	}

	/**
	 * Every {@link Thread} should have it's own {@link Transformer} instance.
	 */
	private static class TransformerThreadLocal extends ThreadLocal<Transformer> {

		@Override
		protected Transformer initialValue() {
			Transformer ret = null;
			synchronized (sTransformerFactory) {
				try {
					ret = sTransformerFactory.newTransformer();
				} catch (TransformerConfigurationException e) {
					e.printStackTrace();
					System.exit(1);
				}
			}
			return ret;
		}
	}
}
