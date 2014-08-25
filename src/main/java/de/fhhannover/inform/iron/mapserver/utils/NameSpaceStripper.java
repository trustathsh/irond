package de.fhhannover.inform.iron.mapserver.utils;

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

import java.util.LinkedList;
import java.util.ListIterator;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * A hopefully simple helper to strip unused namespaces from {@link Element}
 * objects and their children.
 * 
 * @author aw
 *
 */
public class NameSpaceStripper {
	
	/**
	 * Traverse the {@link Element} and store all namespace declarations in
	 * a list. Check which of the entries are used/unused.
	 * 
	 * @param el
	 */
	public static void stripUnusedNamespaces(Element el) {
		LinkedList<Tuple<String, Integer, NamedNodeMap>> entries = new LinkedList<Tuple<String, Integer, NamedNodeMap>>();
		stripUnusedNamespaces(el, entries);
	}
	
	private static void stripUnusedNamespaces(Element el, 
			LinkedList<Tuple<String, Integer, NamedNodeMap>> entries) {
		int count = 0;
		NamedNodeMap attributes = el.getAttributes();
		
		// add all namespace declarations of this node to the entries list
		for (int i = 0; i < attributes.getLength(); i++) {
			Node attrNode = attributes.item(i);
			String name = attrNode.getNodeName();
			if (name.startsWith("xmlns:")) {
				String prefix = name.split(":")[1];
				Tuple<String, Integer, NamedNodeMap> t = 
					new Tuple<String, Integer, NamedNodeMap>(prefix, 0, attributes);
				entries.add(t);
				count++;
			}
		}
		
		// look in the entries list for a namespace that matches and increment
		// usage.
		String prefix = el.getPrefix();
		if (prefix != null) {
			ListIterator<Tuple<String, Integer, NamedNodeMap>> iterator =
				entries.listIterator(entries.size());
			while (iterator.hasPrevious()) {
				Tuple<String, Integer, NamedNodeMap> t  = iterator.previous();
				if (t.x.equals(prefix)) {
					t.y++;
					break;
				}
			}
		}
	
		// do the same for all children of this element
		NodeList children = el.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
		
			if (child.getNodeType() == Node.ELEMENT_NODE)
				stripUnusedNamespaces((Element)child, entries);
		}
		
		// look through the last count entries in the list, and if usage count
		// is 0, remove them from the element node
		for (int i = 0; i < count; i++) {
			Tuple<String, Integer, NamedNodeMap> t = entries.removeLast();
			if (t.y == 0)
				t.z.removeNamedItem("xmlns:" + t.x);
		}
	}
	
	/**
	 * A simple tuple to store (prefix, count, reference to the attributes map);
	 */
	private static class Tuple<E, F, G> {
		
		private Tuple(E x, F y, G z) {
			this.x = x;
			this.y = y;
			this.z = z;
		}
		
		private E x;
		private F y;
		private G z;
	}
}

