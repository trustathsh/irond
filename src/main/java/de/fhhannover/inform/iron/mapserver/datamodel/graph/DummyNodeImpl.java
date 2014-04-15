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

import de.fhhannover.inform.iron.mapserver.datamodel.identifiers.Identifier;
import de.fhhannover.inform.iron.mapserver.exceptions.SystemErrorException;
import de.fhhannover.inform.iron.mapserver.trust.domain.TrustToken;
import de.fhhannover.inform.iron.mapserver.utils.NullCheck;

class DummyNodeImpl extends DummyGraphElement implements Node {
	
	private final Identifier mIdentifier;
	
	DummyNodeImpl(Identifier ident) {
		NullCheck.check(ident, "ident is null");
		mIdentifier = ident;
	}

	/* (non-Javadoc)
	 * @see de.fhhannover.inform.iron.mapserver.datamodel.graph.Node#getIdentifier()
	 */
	@Override
	public Identifier getIdentifier() {
		return mIdentifier;
	}

	/* (non-Javadoc)
	 * @see de.fhhannover.inform.iron.mapserver.datamodel.graph.Node#getLinks()
	 */
	@Override
	public Collection<Link> getLinks() {
		throw new SystemErrorException(sErrorString);
	}

	/* (non-Javadoc)
	 * @see de.fhhannover.inform.iron.mapserver.datamodel.graph.Node#addLink(de.fhhannover.inform.iron.mapserver.datamodel.graph.Link)
	 */
	@Override
	public void addLink(Link l) {
		throw new SystemErrorException(sErrorString);
	}

	/* (non-Javadoc)
	 * @see de.fhhannover.inform.iron.mapserver.datamodel.graph.Node#removeLink(de.fhhannover.inform.iron.mapserver.datamodel.graph.Link)
	 */
	@Override
	public void removeLink(Link l) {
		throw new SystemErrorException(sErrorString);
	}

	/* (non-Javadoc)
	 * @see de.fhhannover.inform.iron.mapserver.datamodel.graph.Node#removeAllLinks()
	 */
	@Override
	public void removeAllLinks() {
		throw new SystemErrorException(sErrorString);
	}

	/* (non-Javadoc)
	 * @see de.fhhannover.inform.iron.mapserver.datamodel.graph.GraphElement#equalsIdentifiers(de.fhhannover.inform.iron.mapserver.datamodel.graph.GraphElement)
	 */
	@Override
	public boolean equalsIdentifiers(GraphElement o) {
		if (o == this)
			return true;
		
		if (!(o instanceof Node))
			return false;
		
		return ((Node)o).getIdentifier().equals(getIdentifier());
	}

	/* (non-Javadoc)
	 * @see de.fhhannover.inform.iron.mapserver.datamodel.graph.GraphElement#dummy()
	 */
	@Override
	public Node dummy() {
		return this;
	}

	/* (non-Javadoc)
	 * @see de.fhhannover.inform.iron.mapserver.datamodel.SearchAble#getByteCount()
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

	@Override
	public TrustToken getTrustToken() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setTrustToken(TrustToken tt) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isConnected() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasUnchangedMetadataHolder() {
		// TODO Auto-generated method stub
		return false;
	}
}
