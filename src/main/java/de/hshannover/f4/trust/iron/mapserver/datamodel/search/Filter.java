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

import java.util.Map;

import de.hshannover.f4.trust.iron.mapserver.datamodel.meta.Metadata;
import de.hshannover.f4.trust.iron.mapserver.exceptions.SystemErrorException;
import de.hshannover.f4.trust.iron.mapserver.utils.NullCheck;

/**
 * A Filter object simple represents a filter string given by some MAPC.
 *
 * A Filter string can also be used to match nothing or match everything.
 *
 * To construct a filter that matches everything no filter string has to be
 * given (e.g. null).
 *
 * If a filter should match nothing, a empty filter string has to be given.
 * (i.e. a string with length zero).
 *
 * The above mentioned cases can be asked with isMatchEverything() and
 * isMatchNothing(). Note that !isMatchEverything() does not imply
 * isMatchNothing() (or the other way round) as both can be false.
 *
 * Besides having a string which represents a filter we need the namespace
 * prefix namespace uri mapping. Having this we can make our Filter namespace
 * aware. This implementation started without such a mapping. Having no
 * namespace awareness.
 *
 * TODO: Check whether or not this filter has the correct form
 *
 * @author aw
 * @version 0.1
 */

public class Filter {

	private final String mFilterString;
	private final Map<String, String> mPrefixUriMap;
	private final FilterType mFilterType;

	/**
	 * toStringString cache;
	 */
	private String toStringString;

	/**
	 * Construct a filter from a given filter string and a map of namespace
	 * prefixes with namespace uris.
	 *
	 * If no map is given a map with only the meta namespace mapping is used
	 *
	 *
	 * @param fs
	 * @param hu
	 */
	public Filter(String fs, Map<String, String> hu, FilterType type) {
		NullCheck.check(hu, "No Namespace Mapping given");
		mPrefixUriMap = hu;
		mFilterString = fs;
		mFilterType = type;
		toStringString = null;
	}

	public String getFilterString() {
		if (isMatchEverything() || isMatchNothing()) {
			throw new SystemErrorException("get filter for match all/nothing");
		}

		return mFilterString;
	}

	public boolean isMatchNothing() {
		return mFilterString != null && mFilterString.length() == 0;
	}

	public boolean isMatchEverything() {
		return mFilterString == null;
	}

	/**
	 * Returns a hashmap with all namespace prefix namespace uri mappgings known
	 * to this filter.
	 *
	 * @return
	 */
	public Map<String, String> getNamespaceMap() {
		return mPrefixUriMap;
	}

	@Override
	public String toString() {
		if (toStringString == null) {
			toStringString = buildToStringString();
		}
		return toStringString;
	}

	private String buildToStringString() {
		StringBuffer sb = new StringBuffer("filter{");

		if (isMatchEverything()) {
			sb.append("match everything");
		} else if (isMatchNothing()) {
			sb.append("match nothing");
		} else {
			sb.append(mFilterString);
		}

		sb.append("}");
		return sb.toString();
	}

	public boolean isResultFilter() {
		return mFilterType.equals(FilterType.RESULT_FILTER);
	}

	public boolean isMatchLinksFilter() {
		return mFilterType.equals(FilterType.MATCH_LINKS_FILTER);
	}

	public FilterType getFilterType() {
		return mFilterType;
	}

	public static boolean matchesResultFilter(Metadata metadata, Filter filter) {
		if (filter.isMatchEverything()) {
			return true;
		} else if (filter.isMatchNothing()) {
			return false;
		} else {
			return !metadata.matchesFilter(filter);
		}
	}
}
