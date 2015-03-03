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
package de.hshannover.f4.trust.iron.mapserver.datamodel.graph;


import de.hshannover.f4.trust.iron.mapserver.datamodel.identifiers.Identifier;
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
	 * @see de.hshannover.f4.trust.iron.mapserver.datamodel.graph.GraphElement#dummy()
	 */
	@Override
	public Node dummy();

}
