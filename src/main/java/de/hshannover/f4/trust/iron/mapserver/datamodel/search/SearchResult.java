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
 * This file is part of irond, version 0.5.5, implemented by the Trust@HsH
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
package de.hshannover.f4.trust.iron.mapserver.datamodel.search;


import de.hshannover.f4.trust.iron.mapserver.datamodel.SearchAble;
import de.hshannover.f4.trust.iron.mapserver.messages.SearchResultType;
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
