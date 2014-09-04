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


import java.util.List;

import de.hshannover.f4.trust.iron.mapserver.datamodel.graph.GraphElement;
import de.hshannover.f4.trust.iron.mapserver.datamodel.meta.Metadata;

/**
 * Interface to {@link SearchResult} with the possibility to modify the
 * contained {@link ResultItem} instances. Compression methods of a
 * {@link SearchResult} can be employed.
 *
 * @since 0.3.0
 * @author aw
 *
 */
public interface ModifiableSearchResult extends SearchResult {

	/**
	 * Add a {@link Metadata} instance to a {@link ResultItem} created
	 * from the {@link GraphElement}.
	 *
	 * @param ge
	 * @param m
	 */
	public void addMetadata(GraphElement ge, Metadata m);

	/**
	 * Add a number of {@link Metadata} objects to a {@link ResultItem} created
	 * based on the {@link GraphElement}.
	 * If the list is empty, creates a {@link ResultItem} without metadata.
	 *
	 * @param ge
	 */
	public void addMetadata(GraphElement ge, List<Metadata> mlist);

	/**
	 * Only add a {@link GraphElement} as {@link ResultItem} to the
	 * {@link SearchResult}.
	 *
	 * @param ge
	 */
	public void addGraphElement(GraphElement ge);

	public void addResultItem(ResultItem ri);

	public void addResultItems(List<ResultItem> rilist);

}
