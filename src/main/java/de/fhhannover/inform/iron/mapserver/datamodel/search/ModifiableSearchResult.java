package de.fhhannover.inform.iron.mapserver.datamodel.search;

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
 * This file is part of irond, version 0.4.1, implemented by the Trust@FHH
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

import java.util.List;

import de.fhhannover.inform.iron.mapserver.datamodel.graph.GraphElement;
import de.fhhannover.inform.iron.mapserver.datamodel.meta.Metadata;

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
