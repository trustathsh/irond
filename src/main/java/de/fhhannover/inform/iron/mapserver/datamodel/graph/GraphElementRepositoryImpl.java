package de.fhhannover.inform.iron.mapserver.datamodel.graph;

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
 * This file is part of irond, version 0.4.1, implemented by the Trust@FHH
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

import java.util.Collection;

import de.fhhannover.inform.iron.mapserver.datamodel.identifiers.Identifier;
import de.fhhannover.inform.iron.mapserver.utils.CollectionHelper;
import de.fhhannover.inform.iron.mapserver.utils.MultiArrayListMap;
import de.fhhannover.inform.iron.mapserver.utils.MultiMap;
import de.fhhannover.inform.iron.mapserver.utils.NullCheck;

/**
 * TODO: Need some way of cleaning up... Either remembering the used Nodes/Links
 *       and upon a cleanup() call checking whether any of the can be removed.
 * 
 * @since 0.3.0
 * @author aw
 */
public class GraphElementRepositoryImpl implements GraphElementRepository {
	
	private MultiMap<Integer, Node> mNodes;
	private MultiMap<Integer, Link> mLinks;
	
	/**
	 * @return a new Instance of a {@link GraphElementRepository} instance.
	 */
	public static GraphElementRepository newInstance() {
		return new GraphElementRepositoryImpl();
	}
	
	/**
	 * Private constructor
	 */
	private GraphElementRepositoryImpl() {
		mNodes = new MultiArrayListMap<Integer, Node>();
		mLinks = new MultiArrayListMap<Integer, Link>();
	}

	/* (non-Javadoc)
	 * @see de.fhhannover.inform.iron.mapserver.datamodel.graph.GraphElementRepository#getNodeFor(de.fhhannover.inform.iron.mapserver.datamodel.identifiers.Identifier)
	 */
	@Override
	public Node getNodeFor(Identifier i) {
		/* Find a node in the existing nodes which has the same identifier
		 * attached. We shouldn't get back too many nodes, so search should
		 * be OK.
		 */
		NullCheck.check(i, "Identifier is null");
		Node node = findExistingNodeFor(i);
		
		// there is no such Node, create a new one
		if (node == null) {
			node = new NodeImpl(i);
			mNodes.put(nodeKey(node), node);
		}
		
		return node;
	}

	/* (non-Javadoc)
	 * @see de.fhhannover.inform.iron.mapserver.datamodel.graph.GraphElementRepository#getLinkFor(de.fhhannover.inform.iron.mapserver.datamodel.identifiers.Identifier, de.fhhannover.inform.iron.mapserver.datamodel.identifiers.Identifier)
	 */
	@Override
	public Link getLinkFor(Identifier i1, Identifier i2) {
		Link l = findExistingLinkFor(i1, i2);
	
		// if there is no such Link, create a new one based on Nodes from the
		// repository. These might be created in turn as well.
		if (l == null) {
			Node linkNode1 = getNodeFor(i1);
			Node linkNode2 = getNodeFor(i2);
			l = new LinkImpl(linkNode1, linkNode2);
			linkNode1.addLink(l);
			
			if (linkNode1 != linkNode2)
				linkNode2.addLink(l);
			
			mLinks.put(linkKey(l), l);
		}
		
		return l;
	}

	@Override
	public GraphElement getGraphElement(Identifier i1, Identifier i2) {
		if (i1 == null && i2 == null)
			throw new NullPointerException("Both Identifiers null"); /* bail out */
		else if (i1 != null && i2 == null)
			return getNodeFor(i1);
		else if (i2 != null && i1 == null)
			return getNodeFor(i2);
		else
			return getLinkFor(i1, i2);
	}

	@Override
	public Node getGraphElement(Identifier i1) {
		return (Node)getGraphElement(i1, null);
	}

	/* (non-Javadoc)
	 * @see de.fhhannover.inform.iron.mapserver.datamodel.graph.GraphElementRepository#getAllNodes()
	 */
	@Override
	public Collection<Node> getAllNodes() {
		Collection<Node> ret = CollectionHelper.provideCollectionFor(Node.class);
		
		for (Node n : mNodes.values())
			ret.add(n.dummy());
		
		return ret;
	}

	/* (non-Javadoc)
	 * @see de.fhhannover.inform.iron.mapserver.datamodel.graph.GraphElementRepository#getAllLinks()
	 */
	
	@Override
	public Collection<Link> getAllLinks() {
		Collection<Link> ret = CollectionHelper.provideCollectionFor(Link.class);
		
		for (Link l : mLinks.values())
			ret.add(l.dummy());
		
		return ret;
	}

	/* (non-Javadoc)
	 * @see de.fhhannover.inform.iron.mapserver.datamodel.graph.GraphElementRepository#getAllElements()
	 */
	@Override
	public Collection<GraphElement> getAllElements() {
		Collection<GraphElement> ret = CollectionHelper.provideCollectionFor(GraphElement.class);
		ret.addAll(getAllNodes());
		ret.addAll(getAllLinks());
		return ret;
	}

	/* (non-Javadoc)
	 * @see de.fhhannover.inform.iron.mapserver.datamodel.graph.GraphElementRepository#dumpContents()
	 */
	@Override
	public void dumpContents() {
		System.out.println("DUMP NODES");
		for (Node n : mNodes.values()) {
			System.out.println(n);
		}
		System.out.println("END NODES");
		System.out.println("DUMP LINKS");
		for (Link l : mLinks.values()) {
			System.out.println(l);
		}
		System.out.println("END LINKS");
	}
	
	/**
	 * Helper to find a {@link Node} instance which has the given
	 * {@link Identifier} instance attached.
	 * @param i
	 * @return
	 */
	private Node findExistingNodeFor(Identifier i) {
		for (Node node : mNodes.getAll(nodeKey(i)))
			if (node.getIdentifier().equals(i))
				return node;
		
		return null;
	}

	/**
	 * Helper to find a {@link Link} instance which has the given
	 * {@link Identifier} instances attached.
	 * @param i1
	 * @param i2
	 * @return
	 */
	private Link findExistingLinkFor(Identifier i1, Identifier i2) {
		for (Link link : mLinks.getAll(linkKey(i1, i2)))
			if (linkHasIdentifiers(link, i1, i2))
					return link;
		
		return null;
	}

	/**
	 * Helper to check whether a given {@link Link} instance contains the given
	 * {@link Identifier} instances on the contained {@link Node} instances.
	 * 
	 * @param link
	 * @param i1
	 * @param i2
	 * @return
	 */
	private boolean linkHasIdentifiers(Link link, Identifier i1, Identifier i2) {
		Identifier li1 = link.getNode1().getIdentifier();
		Identifier li2 = link.getNode2().getIdentifier();
		return i1.equals(li1) && i2.equals(li2) || i1.equals(li2) && i2.equals(li1);
	}
	
	private int linkKey(Link l) {
		return linkKey(l.getNode1(), l.getNode2());
	}

	private int linkKey(Node n1, Node n2) {
		return linkKey(n1.getIdentifier(), n2.getIdentifier());
	}
	
	private int linkKey(Identifier i1, Identifier i2) {
		return nodeKey(i1) + nodeKey(i2);
	}
	
	private int nodeKey(Node n) {
		return nodeKey(n.getIdentifier());
	}
	
	private int nodeKey(Identifier i) {
		return i.hashCode();
	}
}
