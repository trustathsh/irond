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

import java.util.Collection;
import java.util.Map;

import de.fhhannover.inform.iron.mapserver.datamodel.identifiers.Identifier;
import de.fhhannover.inform.iron.mapserver.exceptions.SystemErrorException;
import de.fhhannover.inform.iron.mapserver.trust.domain.TrustToken;
import de.fhhannover.inform.iron.mapserver.utils.CollectionHelper;
import de.fhhannover.inform.iron.mapserver.utils.NullCheck;

class NodeImpl extends GraphElementImpl implements Node {
	
	private final Identifier mIdentifier;
	private final Map<Integer, Link> mLinks;
	private final Node mDummy;
	
	private TrustToken mTrustToken;

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
		if (mLinks.containsKey(l.hashCode()))
			throw new SystemErrorException("Link " + l + " already on " + this);
		
		mLinks.put(l.hashCode(), l);
	}

	@Override
	public void removeLink(Link l) {
		NullCheck.check(l, "link is null");
		if (!mLinks.containsKey(l.hashCode()))
			throw new SystemErrorException("Link " + l + " not on "  + this);
		
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
		if (o == this)
			return true;
		
		if (!(o instanceof Node))
			return false;
		
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

	@Override
	public TrustToken getTrustToken() {
		return mTrustToken;
	}

	@Override
	public void setTrustToken(TrustToken tt) {
		mTrustToken = tt;	
	}

	@Override
	public boolean isConnected() {
		boolean b1 = hasUnchangedMetadataHolder();
		boolean b2 = false;
		
		for (Link l : mLinks.values()) {
			if (l.hasUnchangedMetadataHolder())
				b2 = true;
		}
		return b1 || b2;
	}
}
