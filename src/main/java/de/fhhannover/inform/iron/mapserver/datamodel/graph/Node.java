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

import de.fhhannover.inform.iron.mapserver.datamodel.identifiers.Identifier;
import de.fhhannover.inform.iron.mapserver.datamodel.meta.MetadataState;
import de.fhhannover.inform.iron.mapserver.trust.domain.TrustToken;

import java.util.Collection;

/**
 * Interface to access {@link Node} objects in the MAP graph.
 * A {@link Node} has an {@link Identifier} object attached.
 * 
 * {@link Node} objects may have {@link Link} objects, which link them with
 * other {@link Node} objects.
 * 
 * @since 0.3.0
 * @author aw
 *
 */
public interface Node extends GraphElement {
	
	/**
	 * @return a reference to the {@link Identifier} object attached to this
	 * 			{@link GraphElement} object.
	 */
	public Identifier getIdentifier();
	
	/**
	 * @return a list of all {@link Link} attached to this {@link Node} object.
	 */
	public Collection<Link> getLinks();
	
	/**
	 * Add a {@link Link} object to this {@link Node} object.
	 * 
	 * @param l
	 */
	public void addLink(Link l);
	
	/**
	 * Remove a {@link Link} object from this {@link Node} object.
	 * 
	 * @param l
	 */
	public void removeLink(Link l);
	
	/**
	 * Remova ll {@link Link} objects attached to this {@link Node} object.
	 */
	public void removeAllLinks();

	/* (non-Javadoc)
	 * @see de.fhhannover.inform.iron.mapserver.datamodel.graph.GraphElement#dummy()
	 */
	@Override
	public Node dummy();

	/**
	 * TrustService
	 * 
	 * TTI - Diese Methode gibt zurück, ob der {@link Node} mit Metadaten, die
	 * den {@link MetadataState#UNCHANGED} haben, versehen ist oder nicht.
	 * 
	 * @return
	 */
	public boolean isConnected();

	/**
	 * TrustService
	 * 
	 * TTI - Diese Methode gibt den {@link TrustToken} zurück.
	 * 
	 * @return
	 */
	public TrustToken getTrustToken();

	/**
	 * TrustService
	 * 
	 * TTI - Diese Methode setzt den {@link TrustToken}.
	 * 
	 * @param tt
	 */
	public void setTrustToken(TrustToken tt);
}
