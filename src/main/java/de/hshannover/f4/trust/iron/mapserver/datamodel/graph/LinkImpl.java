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
 * This file is part of irond, version 0.5.5, implemented by the Trust@HsH
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
package de.hshannover.f4.trust.iron.mapserver.datamodel.graph;


import de.hshannover.f4.trust.iron.mapserver.exceptions.SystemErrorException;
import de.hshannover.f4.trust.iron.mapserver.utils.NullCheck;

class LinkImpl extends GraphElementImpl implements Link {

	private final Node mNode1, mNode2;
	private final Link mDummy;

	LinkImpl(Node n1, Node n2) {
		NullCheck.check(n1, "n1 is null");
		NullCheck.check(n2, "n2 is null");

		mNode1 = n1;
		mNode2 = n2;
		mDummy = new DummyLinkImpl(mNode1.dummy(), mNode2.dummy());
	}

	@Override
	public Node getNode1() {
		return mNode1;
	}

	@Override
	public Node getNode2() {
		return mNode2;
	}

	@Override
	public String provideToStringStart() {
		return "link{" + mNode1 + ", " + mNode2;
	}

	@Override
	public Node getNeighborNode(Node cur) {
		if (mNode1 == cur) {
			return mNode2;
		} else if (mNode2 == cur) {
			return mNode1;
		} else {
			throw new SystemErrorException("getNeighborNode with bad Node");
		}
	}

	@Override
	public boolean equalsIdentifiers(GraphElement o) {
		Link l;

		if (this == o) {
			return true;
		}

		if (!(o instanceof Link)) {
			return false;
		}

		l = (Link)o;
		return l.getNode1().equalsIdentifiers(getNode1()) &&
				l.getNode2().equalsIdentifiers(getNode2())
				||
				l.getNode1().equalsIdentifiers(getNode2()) &&
				l.getNode2().equalsIdentifiers(getNode1());

	}

	@Override
	public Link dummy() {
		return mDummy;
	}

	@Override
	public int getByteCount() {
		return getNode1().getByteCount() + getNode2().getByteCount();
	}
}


