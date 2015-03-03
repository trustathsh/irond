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
package de.hshannover.f4.trust.iron.mapserver.utils;


import java.util.Iterator;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;

public class SimpleNamespaceContext implements NamespaceContext {

	private final Map<String, String> mNamespaceMap;

	public SimpleNamespaceContext(Map<String, String> nsMap) {
		NullCheck.check(nsMap, "nsMap is null");
		mNamespaceMap = nsMap;
	}

	@Override
	public String getNamespaceURI(String prefix) {
		NullCheck.check(prefix, "prefix is null");
		if (mNamespaceMap.containsKey(prefix)) {
			return mNamespaceMap.get(prefix);
		}
		return XMLConstants.NULL_NS_URI;
	}

	@Override
	public String getPrefix(String namespaceURI) {
		throw new RuntimeException("Not implemented!");
		/*NullCheck.check(namespaceURI, "namespaceURI is null");
		if (mNamespaceMap.containsValue(namespaceURI)) {
			Set<Entry<String, String>> entries = mNamespaceMap.entrySet();
			for (Entry<String, String> entry : entries) {
				if (entry.getValue().equals(namespaceURI)) {
					return entry.getKey();
				}
			}
		}
		*/
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Iterator getPrefixes(String namespaceURI) {
		throw new RuntimeException("Not implemented!");
	}

}
