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
 * This file is part of irond, version 0.5.2, implemented by the Trust@HsH
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
package de.hshannover.f4.trust.iron.mapserver.utils;


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Lets make use of this to allocate the same type of list, collection and
 * so on all the time...
 *
 * @since 0.3.0
 * @author aw
 *
 */
public class CollectionHelper {

	public static <T> List<T> provideListFor(Class<T> c) {
		return new ArrayList<T>();
	}

	public static <T> List<T> copy(Collection<T> collection) {
		@SuppressWarnings("unchecked")
		List<T>	ret = (List<T>) provideListFor(Object.class);
		ret.addAll(collection);
		return ret;
	}

	public static <T> Collection<T> provideCollectionFor(Class<T> c) {
		return provideListFor(c);
	}

	public static <T, V> Map<T, V> provideMapFor(Class<T> c1, Class<V> c2) {
		return new HashMap<T, V>();
	}

	public static <T> Set<T> provideSetFor(Class<T> c) {
		return new HashSet<T>();
	}
}


