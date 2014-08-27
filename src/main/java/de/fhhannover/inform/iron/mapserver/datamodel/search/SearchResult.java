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

import de.fhhannover.inform.iron.mapserver.datamodel.SearchAble;
import de.fhhannover.inform.iron.mapserver.messages.SearchResultType;
import java.util.List;

/**
 * Access a {@link SearchResult} in a read-only manner. Meaning this interface
 * is only useful for marshalling.
 *
 * @author aw
 */
public interface SearchResult extends SearchAble {

	/**
	 * @return a {@link List} of {@link ResultItem} instances contained in this
	 * 			{@link SearchResult}.
	 */
	public abstract List<ResultItem> getResultItems();

	/**
	 * Returns the name of the corresponding subscription or null if no name was
	 * given.
	 *
	 * @return the name of the {@link Subscription} or null if this
	 * 			{@link SearchResult} is not	contained in a {@link PollResult}.
	 */
	public String getName();

	/**
	 * @return the type of this {@link SearchResult}.
	 */
	public SearchResultType getType();

	/**
	 * @return whether any {@link ResultItem} objects can be found in this
	 * 			{@link SearchResult} instance.
	 */
	boolean isEmpty();

	/**
	 * @param o
	 * @return
	 */
	boolean sameNameAndType(SearchResult o);

	/**
	 * Check if the result contains metadata only validated metadata
	 * @return true if contains validated metadata only
	 */
	public boolean hasMetadataAndOnlyValidatedMetadata();
}
