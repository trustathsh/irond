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
 * This file is part of irond, version 0.5.0, implemented by the Trust@HsH
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
package de.hshannover.f4.trust.iron.mapserver.datamodel.graph;


import java.util.Collection;
import java.util.Map;

import de.hshannover.f4.trust.iron.mapserver.datamodel.identifiers.Identifier;
import de.hshannover.f4.trust.iron.mapserver.exceptions.SystemErrorException;
import de.hshannover.f4.trust.iron.mapserver.utils.CollectionHelper;
import de.hshannover.f4.trust.iron.mapserver.utils.NullCheck;

class NodeImpl extends GraphElementImpl implements Node {

	private final Identifier mIdentifier;
	private final Map<Integer, Link> mLinks;
	private final Node mDummy;

	NodeImpl(Identifier i) {
		super();
		NullCheck.check(i, "Identifier is null");
		mIdentifier = i;
		mLinks = CollectionHelper.provideMapFor(Integer.class, Link.class);
		mDummy = new DummyNodeImpl(i);
	}

	@Override
	public Identifier getIdentifier() {
		return mIdentifier;
	}

	@Override
	public Collection<Link> getLinks() {
		return CollectionHelper.copy(mLinks.values());
	}

	@Override
	public void addLink(Link l) {
		NullCheck.check(l, "link is null");
		if (mLinks.containsKey(l.hashCode())) {
			throw new SystemErrorException("Link " + l + " already on " + this);
		}

		mLinks.put(l.hashCode(), l);
	}

	@Override
	public void removeLink(Link l) {
		NullCheck.check(l, "link is null");
		if (!mLinks.containsKey(l.hashCode())) {
			throw new SystemErrorException("Link " + l + " not on "  + this);
		}

		mLinks.remove(l.hashCode());
	}

	@Override
	public void removeAllLinks() {
		mLinks.clear();
	}

	@Override
	public String provideToStringStart() {
		return "node{" + mIdentifier;
	}

	@Override
	public boolean equalsIdentifiers(GraphElement o) {
		if (o == this) {
			return true;
		}

		if (!(o instanceof Node)) {
			return false;
		}

		return ((Node)o).getIdentifier().equals(getIdentifier());
	}

	@Override
	public Node dummy() {
		return mDummy;
	}

	@Override
	public int getByteCount() {
		return getIdentifier().getByteCount();
	}
}
