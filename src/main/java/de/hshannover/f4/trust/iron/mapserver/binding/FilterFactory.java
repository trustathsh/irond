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
package de.hshannover.f4.trust.iron.mapserver.binding;

import java.util.Map;

import de.hshannover.f4.trust.iron.mapserver.datamodel.search.Filter;
import de.hshannover.f4.trust.iron.mapserver.datamodel.search.FilterType;
import de.hshannover.f4.trust.iron.mapserver.exceptions.InvalidFilterException;
import de.hshannover.f4.trust.iron.mapserver.utils.FilterValidator;

/**
 * Simple class to create a {@link Filter} object from the filter string and
 * namespace map.
 *
 * @author aw
 *
 *         created 12.02.10
 *
 *         changes: 19.02.10 aw - modified to be used with a map of namespace
 *         prefix to namespace uri. Deprecated transformFilter(String);
 *
 */
public class FilterFactory {

	private FilterFactory() {
	}

	/**
	 * Transformer method to create a filter object from a filterstring and a
	 * given namespace prefix namespace uri map.<br>
	 *
	 * If fs is null a filterobject which matches everything is contstructed.<br>
	 *
	 * If fs is empty, fs.length() == 0, a filterobject which matches nothing is
	 * constructed.<br/>
	 *
	 * Else fs is seen as a Filterstring specified in the IF-MAP specification.<br/>
	 *
	 * If nm is null a default map with one entry<br>
	 * ["meta", "urn:trustedcomputinggroup.org:2010:IFMAP-METADATA:2" ]<br>
	 * is used. One will get a warning constructing such a filter.
	 *
	 * @param fs
	 *            filterstring
	 * @param nm
	 *            namespace mapping
	 * @return
	 * @throws InvalidFilterException
	 */
	public static Filter newFilter(String fs, Map<String, String> nm,
			FilterType type) throws InvalidFilterException {
		Filter f = new Filter(fs, nm, type);
		if (!FilterValidator.validateFilter(f)) {
			throw new InvalidFilterException("Bad Filter");
		}
		return f;
	}
}
