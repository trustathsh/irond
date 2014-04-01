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

import de.fhhannover.inform.iron.mapserver.exceptions.SystemErrorException;
import de.fhhannover.inform.iron.mapserver.utils.NullCheck;

class DummyLinkImpl extends DummyGraphElement implements Link {
	
	private final Node mNode1;
	private final Node mNode2;
	
	DummyLinkImpl(Node n1, Node n2) {
		NullCheck.check(n1, "node1 is null");
		NullCheck.check(2, "node2 is null");
		if (!(n1 instanceof DummyNodeImpl) || !(n2 instanceof DummyNodeImpl))
			throw new SystemErrorException("DummyLink with non-DummyNodeImpl :(");
		
		mNode1 = n1;
		mNode2 = n2;
	}

	/* (non-Javadoc)
	 * @see de.fhhannover.inform.iron.mapserver.datamodel.graph.Link#getNode1()
	 */
	@Override
	public Node getNode1() {
		return mNode1;
	}

	/* (non-Javadoc)
	 * @see de.fhhannover.inform.iron.mapserver.datamodel.graph.Link#getNode2()
	 */
	@Override
	public Node getNode2() {
		return mNode2;
	}

	/* (non-Javadoc)
	 * @see de.fhhannover.inform.iron.mapserver.datamodel.graph.Link#getNeighborNode(de.fhhannover.inform.iron.mapserver.datamodel.graph.Node)
	 */
	@Override
	public Node getNeighborNode(Node cur) {
		if (mNode1 == cur)
			return mNode2;
		else if (mNode2 == cur)
			return mNode1;
		else
			throw new SystemErrorException("getNeighborNode with bad Node");
	}

	/* (non-Javadoc)
	 * @see de.fhhannover.inform.iron.mapserver.datamodel.graph.GraphElement#equalsIdentifiers(de.fhhannover.inform.iron.mapserver.datamodel.graph.GraphElement)
	 */
	@Override
	public boolean equalsIdentifiers(GraphElement o) {
		Link l;
		
		if (this == o)
			return true;
		
		if (!(o instanceof Link))
			return false;
		
		l = (Link)o;
		return (l.getNode1().equalsIdentifiers(getNode1()) &&
				l.getNode2().equalsIdentifiers(getNode2()))
				||
				(l.getNode1().equalsIdentifiers(getNode2()) &&
				l.getNode2().equalsIdentifiers(getNode1()));
		
	}

	/* (non-Javadoc)
	 * @see de.fhhannover.inform.iron.mapserver.datamodel.graph.GraphElement#dummy()
	 */
	@Override
	public Link dummy() {
		return this;
	}

	/* (non-Javadoc)
	 * @see de.fhhannover.inform.iron.mapserver.datamodel.SearchAble#getByteCount()
	 */
	@Override
	public int getByteCount() {
		return getNode1().getByteCount() + getNode2().getByteCount();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "dlink{" + getNode1() + ", " + getNode2() + "}";
	}
}
