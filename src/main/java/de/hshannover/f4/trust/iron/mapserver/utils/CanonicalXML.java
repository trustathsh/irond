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
package de.hshannover.f4.trust.iron.mapserver.utils;



import org.xml.sax.*;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXSource;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Modifed version of James Clark's XMLTest
 * This tests an XML parser that has a SAX driver,
 * by generating the canonical XML for one or more XML files.
 * This is not written with performance in mind.
 * <p/>
 * Modified by MHK to be callable from code and deliver the canonical XML as a string.
 * Added an option to remove whitespace-only text nodes
 * Added code to output all namespace nodes
 *
 * @version Date: 1999/12/10
 *
 * Taken from:
 * http://www.cs.duke.edu/courses/fall08/cps116/docs/saxon/drivers/java/test/CanonicalXML.java
 * and it seems to come from:
 * http://www.jclark.com/xml/XMLTest.java
 */
public class CanonicalXML extends DefaultHandler implements LexicalHandler {

	private StringWriter out;
	private StringBuffer buf = new StringBuffer();
	private boolean strip = false;
	private boolean isWhite = true;
	private AttributesImpl namespaces = new AttributesImpl();

	public String toCanonicalXML(XMLReader parser, InputSource inputSource, boolean stripSpace) {

		strip = stripSpace;
		out = new StringWriter();

		parser.setContentHandler(this);
		try {
			parser.setProperty("http://xml.org/sax/properties/lexical-handler", this);
		} catch (SAXNotSupportedException err) {	// this just means we won't see the comments
		} catch (SAXNotRecognizedException err) {
		}
		parser.setErrorHandler(this);
		try {
			parser.parse(inputSource);
			return out.toString();
		} catch (SAXParseException e) {
			System.err.println("XML parsing error on line " + e.getLineNumber() + " while creating Canonical XML");
			System.err.println(e.getMessage());
			//e.printStackTrace();
			System.err.println("Parser: " + parser.getClass());
			try {
				System.err.println("Supports XML 1.1: " + parser.getFeature("http://xml.org/sax/features/xml-1.1"));
			} catch (Exception e2) {}
		} catch (SAXException e) {
			System.err.println("XML parsing error while creating Canonical XML");
			System.err.println(e.getMessage());
		} catch (IOException e) {
			System.err.println("XML parsing error while creating Canonical XML");
			System.err.println(e.toString());
		}
		return null;
	}


	/**
	 * Create canonical XML silently, throwing exceptions rather than displaying messages
	 * @param parser
	 * @param inputSource
	 * @param stripSpace
	 * @return
	 * @throws Exception
	 */

	public String toCanonicalXML2(XMLReader parser, InputSource inputSource, boolean stripSpace) throws Exception {
		strip = stripSpace;
		out = new StringWriter();
		parser.setContentHandler(this);
		parser.setErrorHandler(this);
		parser.parse(inputSource);
		return out.toString();
	}

	/**
	 * Create canonical XML silently, throwing exceptions rather than displaying messages. This version
	 * of the method uses the Saxon identityTransformer rather than parsing directly, because for some reason
	 * we seem to be able to get XML 1.1 to work this way.
	 */

	public String toCanonicalXML3(TransformerFactory factory, XMLReader resultParser, String inxml, boolean stripSpace) throws Exception {
		strip = stripSpace;
		out = new StringWriter();
		Transformer t = factory.newTransformer();
		SAXSource ss = new SAXSource(resultParser, new InputSource(new StringReader(inxml)));
		ss.setSystemId("http://localhost/string-input");
		t.setOutputProperty(OutputKeys.METHOD, "xml");
		t.setOutputProperty(OutputKeys.INDENT, "no");
		t.transform(ss, new SAXResult(this));
		return out.toString();
	}

	/**
	 * Receive notification of the start of a Namespace mapping.
	 * <p/>
	 * <p>By default, do nothing.  Application writers may override this
	 * method in a subclass to take specific actions at the start of
	 * each Namespace prefix scope (such as storing the prefix mapping).</p>
	 *
	 * @param prefix The Namespace prefix being declared.
	 * @param uri	The Namespace URI mapped to the prefix.
	 * @throws org.xml.sax.SAXException Any SAX exception, possibly
	 *								  wrapping another exception.
	 * @see org.xml.sax.ContentHandler#startPrefixMapping
	 */
	@Override
	public void startPrefixMapping(String prefix, String uri) throws SAXException {
		namespaces.addAttribute("", prefix, prefix.equals("") ? "xmlns": "xmlns:" + prefix, "CDATA", uri);
		if (!"".equals(uri)) {
			try {
				URI u = new URI(uri);
				if (!u.isAbsolute()) {
					System.err.println("*** (Canonical XML:) namespace URI " + u + " is not absolute");
				}
			} catch (URISyntaxException err) {
				System.err.println("*** (Canonical XML:) namespace " + uri + " is not a valid URI");
			}
		}
	}

	/**
	 * Receive notification of the start of an element.
	 * <p/>
	 * <p>By default, do nothing.  Application writers may override this
	 * method in a subclass to take specific actions at the start of
	 * each element (such as allocating a new tree node or writing
	 * output to a file).</p>
	 *
	 * @param uri		The Namespace URI, or the empty string if the
	 *				   element has no Namespace URI or if Namespace
	 *				   processing is not being performed.
	 * @param localName  The local name (without prefix), or the
	 *				   empty string if Namespace processing is not being
	 *				   performed.
	 * @param qName	  The qualified name (with prefix), or the
	 *				   empty string if qualified names are not available.
	 * @param atts	   The attributes attached to the element.  If
	 *				   there are no attributes, it shall be an empty
	 *				   Attributes object.
	 * @throws org.xml.sax.SAXException Any SAX exception, possibly
	 *								  wrapping another exception.
	 * @see org.xml.sax.ContentHandler#startElement
	 */
	@Override
	public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
		flushChars();
		write("<");
		write(qName);
		// output the namespaces
		outputAttributes(namespaces);
		// output the attributes
		outputAttributes(atts);
		write(">");
		namespaces.clear();
	}

	private void outputAttributes(Attributes atts) {
		int len = atts.getLength();
		if (len > 0) {
			int[] v = new int[len];
			for (int i = 0; i < len; i++) {
				v[i] = i;
			}
			/* Do an insertion sort. */
			for (int i = 1; i < len; i++) {
				int n = v[i];
				String s = atts.getQName(n);
				int j;
				for (j = i - 1; j >= 0; j--) {
					if (s.compareTo(atts.getQName(v[j])) >= 0) {
						break;
					}
					v[j + 1] = v[j];
				}
				v[j + 1] = n;
			}
			for (int i = 0; i < len; i++) {
				write(" ");
				int n = v[i];
				write(atts.getQName(n));
				write("=\"");
				String value = atts.getValue(n);
				int valueLen = value.length();
				for (int j = 0; j < valueLen; j++) {
					appendChar(value.charAt(j));
				}
				flushChars();
				write("\"");
			}
		}
	}

	@Override
	public void ignorableWhitespace(char[] cbuf, int start, int len) {
		characters(cbuf, start, len);
	}

	@Override
	public void characters(char[] cbuf, int start, int len) {
		while (len-- > 0) {
			appendChar(cbuf[start++]);
		}
	}

	private void appendChar(char c) {
		if (strip && isWhite) {
			if (!Character.isWhitespace(c)) {
				isWhite = false;
			}
		}

		switch (c) {
			case 1: case 2: case 3: case 4: case 5: case 6: case 7: case 8: case 11: case 12:
			case 14: case 15:
				buf.append(charRef(c));
				break;
			case '&':
				buf.append("&amp;");
				break;
			case '<':
				buf.append("&lt;");
				break;
			case '>':
				buf.append("&gt;");
				break;
			case '"':
				buf.append("&quot;");
				break;
			case '\t':
				buf.append("&#9;");
				break;
			case '\n':
				buf.append("&#10;");
				break;
			case '\r':
				buf.append("&#13;");
				break;
			case 128: case 129: case 130: case 131: case 132: case 133: case 134: case 135:
			case 136: case 137: case 138: case 139: case 140: case 141: case 142: case 143:
			case 144: case 145: case 146: case 147: case 148: case 149: case 150: case 151:
			case 152: case 153: case 154: case 155: case 156: case 157: case 158: case 159:
			case 160:
				buf.append(charRef(c));
				break;
			default:
				buf.append(c);
				break;
		}
	}

	private String charRef(char c) {
		return "&#" + (int)c + ";";
	}

	/**
	 * Receive notification of the end of an element.
	 * <p/>
	 * <p>By default, do nothing.  Application writers may override this
	 * method in a subclass to take specific actions at the end of
	 * each element (such as finalising a tree node or writing
	 * output to a file).</p>
	 *
	 * @param uri	   The Namespace URI, or the empty string if the
	 *				  element has no Namespace URI or if Namespace
	 *				  processing is not being performed.
	 * @param localName The local name (without prefix), or the
	 *				  empty string if Namespace processing is not being
	 *				  performed.
	 * @param qName	 The qualified name (with prefix), or the
	 *				  empty string if qualified names are not available.
	 * @throws org.xml.sax.SAXException Any SAX exception, possibly
	 *								  wrapping another exception.
	 * @see org.xml.sax.ContentHandler#endElement
	 */
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		flushChars();
		write("</");
		write(qName);
		write(">");
	}

	@Override
	public void processingInstruction(String target, String data) throws SAXException {
		flushChars();
		write("<?");
		write(target);
		write(" ");
		write(data);
		write("?>");
	}



	@Override
	public void startDocument() {
	}

	@Override
	public void endDocument() throws SAXException {
		try {
			flushChars();
			out.close();
		} catch (IOException e) {
			throw new SAXException(e);
		}
	}

	@Override
	public void startDTD(String name, String publicId, String systemId) throws SAXException {
		//
	}

	@Override
	public void endDTD() throws SAXException {
		//
	}

	@Override
	public void startEntity(String name) throws SAXException {
		//
	}

	@Override
	public void endEntity(String name) throws SAXException {
		//
	}

	@Override
	public void startCDATA() throws SAXException {
		//
	}

	@Override
	public void endCDATA() throws SAXException {
		//
	}

	@Override
	public void comment(char ch[], int start, int length) throws SAXException {
		flushChars();
		write("<!--");
		write(new String(ch, start, length));
		write("-->");
	}

	private void flushChars() {
		if (buf.length() > 0 && !(strip && isWhite)) {
			write(buf.toString());
		}
		buf.setLength(0);
		isWhite = true;
	}

	private void write(String s) {
		int len = s.length();
		for (int i = 0; i < len; i++) {
			char c = s.charAt(i);
			if (c < 0x80) {
				out.write(c);
			} else {
				switch (c & 0xF800) {
					case 0:
						out.write(c >> 6 & 0x1F | 0xC0);
						out.write(c & 0x3F | 0x80);
						break;
					case 0xD800:
						char c2;
						if (i + 1 < len
								&& (c & 0xFC00) == 0xD800
								&& ((c2 = s.charAt(i + 1)) & 0xFC00) == 0xDC00) {
							++i;
							int n = (c & 0x3FF) << 10 | c2 & 0x3FF;
							n += 0x10000;
							out.write(n >> 18 & 0x7 | 0xF0);
							out.write(n >> 12 & 0x3F | 0x80);
							out.write(n >> 6 & 0x3F | 0x80);
							out.write(n & 0x3F | 0x80);
							break;
						}
						/* this is an error situation really */
						/* fall through */
					default:
						out.write(c >> 12 & 0xF | 0xE0);
						out.write(c >> 6 & 0x3F | 0x80);
						out.write(c & 0x3F | 0x80);
						break;
				}
			}
		}
	}
}
