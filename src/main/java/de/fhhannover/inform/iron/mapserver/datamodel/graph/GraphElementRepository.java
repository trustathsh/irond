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
 * This file is part of irond, version 0.4.0, implemented by the Trust@FHH 
 * research group at the Fachhochschule Hannover.
 * 
 * irond is an an *experimental* IF-MAP 2.0 compliant MAP server written in
 * JAVA. irond supports both basic authentication and certificate-based 
 * authentication (using X.509 certificates) of MAP clients. irond is
 * maintained by the Trust@FHH group at the Fachhochschule Hannover, initial
 * developement was carried out during the ESUKOM research project.
 * %%
 * Copyright (C) 2010 - 2013 Trust@FHH
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

/**
 * Provides access to {@link Node} and {@link Link} instances in the MAP graph.
 * 
 * @since 0.3.0
 * @author aw
 */
public interface GraphElementRepository {
	
	/**
	 * Get a global {@link Node} reference for the given {@link Identifier}
	 * instance.
	 * 
	 * @param i the {@link Identifier} instance
	 * @return
	 */
	public Node getNodeFor(Identifier i);
	
	/**
	 * Get a global {@link Link} reference for the given {@link Identifier}
	 * instances.
	 * Called with getLinkFor(i11, i12) leads to the same {@link Link}
	 * instance as getLinkFor(i21, i22), where i11.equals(i21) and
	 * i12.equals(i22).
	 * 
	 * @param i1	
	 * @param i2
	 * @return
	 */
	public Link getLinkFor(Identifier i1, Identifier i2);
	
	/**
	 * Returns a {@link GraphElement} instance. Allows for one null parameter
	 * 
	 * @param i1
	 * @param i2
	 * @return
	 */
	public GraphElement getGraphElement(Identifier i1, Identifier i2);
	
	/**
	 * Same as above, but obviously will search for an {@link Identifier}
	 * instance.
	 * 
	 * @param i1
	 * @return
	 */
	public Node getGraphElement(Identifier i1);
	
	/**
	 * Return a collection of all {@link Node} objects currently stored in
	 * the {@link GraphElementRepository}. Note, those are dummy instances.
	 */
	public Collection<Node> getAllNodes();
	
	/**
	 * Return a collection of all {@link Link} objects currently stored in
	 * the {@link GraphElementRepository}. Note, those are dummy instances.
	 */
	public Collection<Link> getAllLinks();
	
	/**
	 * Return a collection of all {@link GraphElement} objects currently stored
	 * in the {@link GraphElementRepository}. Note, those are dummy instances.
	 */
	public Collection<GraphElement> getAllElements();

	/**
	 * Make some debugging output about the content of the graph.
	 */
	public void dumpContents();
}
