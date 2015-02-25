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
 * This file is part of irond, version 0.5.2, implemented by the Trust@HsH
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


import java.util.Collection;

import de.hshannover.f4.trust.iron.mapserver.datamodel.identifiers.Identifier;
import de.hshannover.f4.trust.iron.mapserver.exceptions.SystemErrorException;
import de.hshannover.f4.trust.iron.mapserver.utils.NullCheck;

class DummyNodeImpl extends DummyGraphElement implements Node {

	private final Identifier mIdentifier;

	DummyNodeImpl(Identifier ident) {
		NullCheck.check(ident, "ident is null");
		mIdentifier = ident;
	}

	/* (non-Javadoc)
	 * @see de.hshannover.f4.trust.iron.mapserver.datamodel.graph.Node#getIdentifier()
	 */
	@Override
	public Identifier getIdentifier() {
		return mIdentifier;
	}

	/* (non-Javadoc)
	 * @see de.hshannover.f4.trust.iron.mapserver.datamodel.graph.Node#getLinks()
	 */
	@Override
	public Collection<Link> getLinks() {
		throw new SystemErrorException(sErrorString);
	}

	/* (non-Javadoc)
	 * @see de.hshannover.f4.trust.iron.mapserver.datamodel.graph.Node#addLink(de.hshannover.f4.trust.iron.mapserver.datamodel.graph.Link)
	 */
	@Override
	public void addLink(Link l) {
		throw new SystemErrorException(sErrorString);
	}

	/* (non-Javadoc)
	 * @see de.hshannover.f4.trust.iron.mapserver.datamodel.graph.Node#removeLink(de.hshannover.f4.trust.iron.mapserver.datamodel.graph.Link)
	 */
	@Override
	public void removeLink(Link l) {
		throw new SystemErrorException(sErrorString);
	}

	/* (non-Javadoc)
	 * @see de.hshannover.f4.trust.iron.mapserver.datamodel.graph.Node#removeAllLinks()
	 */
	@Override
	public void removeAllLinks() {
		throw new SystemErrorException(sErrorString);
	}

	/* (non-Javadoc)
	 * @see de.hshannover.f4.trust.iron.mapserver.datamodel.graph.GraphElement#equalsIdentifiers(de.hshannover.f4.trust.iron.mapserver.datamodel.graph.GraphElement)
	 */
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

	/* (non-Javadoc)
	 * @see de.hshannover.f4.trust.iron.mapserver.datamodel.graph.GraphElement#dummy()
	 */
	@Override
	public Node dummy() {
		return this;
	}

	/* (non-Javadoc)
	 * @see de.hshannover.f4.trust.iron.mapserver.datamodel.SearchAble#getByteCount()
	 */
	@Override
	public int getByteCount() {
		return getIdentifier().getByteCount();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "dnode{" + getIdentifier() + "}";
	}
}
