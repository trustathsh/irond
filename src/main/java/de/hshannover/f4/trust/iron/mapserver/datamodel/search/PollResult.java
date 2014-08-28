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
 * This file is part of irond, version 0.5.0, implemented by the Trust@HsH
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

import de.hshannover.f4.trust.iron.mapserver.datamodel.SearchAble;
import de.hshannover.f4.trust.iron.mapserver.datamodel.search.SearchResult;
import de.hshannover.f4.trust.iron.mapserver.messages.ErrorResult;

/**
 * A {@link PollResult} represents a message which is sent to a MAPC in response
 * to a poll call. However, the message which is sent on the network might differ,
 * for example the {@link PollResult} could be split up in shorter messages.
 *
 * A {@link PollResult} contains either searchResult, updateResult, deleteResult
 * or notifyResult elements. All these have to be ordered such that the included
 * metadata elements are ordered as of their modification date.
 * (New 2.0 revision)
 *
 * <b>Note:</b><br/>
 * ErrorResults are not ordered for now.
 *
 * @author aw
 */
public interface PollResult extends SearchAble {

	/**
	 * @return all {@link SearchResult} instances contained in this
	 *			{@link SearchResult} instance.
	 */
	public List<SearchResult> getResults();

	/**
	 * TODO: Might want to have real errors?
	 *
	 * @return a {@link List} of names for {@link Subscription} where an
	 * 			{@link ErrorResult} has to be created.
	 */
	public List<String> getErrorResults();

	/**
	 * @return false if there are no {@link SearchResult} instances in this
	 * 			{@link PollResult} instance.
	 *
	 */
	public boolean isEmpty();

	/**
	 * @param name name of the {@link Subscription}.
	 * @return the number of bytes needed to represent all {@link SearchResult}
	 * 			instance in this {@link PollResult} instance with the given name.
	 */
	public int getByteCountOf(String name);

	/**
	 * Checks if the {@link PollResult} contains metadata and only schema
	 *	      validated {@link Metadata}
	 * @return true if all metadata is schema validated
	 */
	public boolean hasMetadataAndOnlyValidatedMetadata();
}
