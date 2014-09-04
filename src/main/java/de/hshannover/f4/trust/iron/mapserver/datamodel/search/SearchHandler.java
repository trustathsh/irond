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
 * This file is part of irond, version 0.5.1, implemented by the Trust@HsH
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
package de.hshannover.f4.trust.iron.mapserver.datamodel.search;


import de.hshannover.f4.trust.iron.mapserver.datamodel.graph.Link;
import de.hshannover.f4.trust.iron.mapserver.datamodel.graph.Node;
import de.hshannover.f4.trust.iron.mapserver.datamodel.identifiers.Identifier;
import de.hshannover.f4.trust.iron.mapserver.exceptions.SearchException;
import de.hshannover.f4.trust.iron.mapserver.exceptions.SearchResultsTooBigException;

/**
 * TODO: Documentation
 *
 * @since 0.3.0
 * @author aw
 *
 */
public interface SearchHandler {

	public static final boolean SEARCH_HANDLER_DEBUG = false;

	/**
	 * @return the {@link Identifier} object to start the search from.
	 */
	public Identifier getStartIdentifier();

	/**
	 * Called before the search is started.
	 */
	public void onStart();

	/**
	 * Called when a {@link Node} is visited.
	 *
	 * @param cur
	 */
	public void onNode(Node cur) throws SearchResultsTooBigException;

	/**
	 * Called after {@link #onNode(Node)} to indicate whether the {@link Link}
	 * objects of this {@link Node} should be be traveled.
	 *
	 * @param cur
	 * @return
	 * @throws SearchException If somehting was wrong with the search request
	 */
	public boolean travelLinksOf(Node cur) throws SearchException;

	/**
	 * Called for each {@link Link} object of a {@link Node} object to indicate
	 * whether {@link #onLink(Link)} should be called for this {@link Link}
	 * object.
	 *
	 * @param l
	 * @return
	 */
	public boolean travelLink(Link l);

	/**
	 * @param neighborNode
	 * @return true if this node should be traversed.
	 */
	public boolean traverseTo(Node nextNode);

	/**
	 * Called directly after {@link #travelLink(Link)} was evaluated to
	 * <code>true</code>. After {@link #onLink(Link)}, the next {@link Node}
	 * of the given {@link Link} object will be traversed to.
	 *
	 * @param l
	 */
	public void onLink(Link l) throws SearchResultsTooBigException;

	/**
	 * Called after all {@link Link} objects of a {@link Node} object were
	 * visited.
	 *
	 * @param cur
	 */
	public void afterNode(Node cur);

	/**
	 * Called after the search is finished.
	 */
	public void onEnd();

	/**
	 * Called before the next depth starts.
	 */
	public void nextDepth();
}
