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
package de.hshannover.f4.trust.iron.mapserver.utils;


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MultiArrayListMap<K, V> implements MultiMap<K, V>{

	private Map<K, List<V>> mMapOfLists;

	private int mSize;

	public MultiArrayListMap() {
		mMapOfLists = new HashMap<K, List<V>>();
		mSize = 0;
	}

	@Override
	public int size() {
		return mSize;
	}

	@Override
	public boolean isEmpty() {
		return mSize == 0;
	}

	@Override
	public boolean containsKey(Object key) {
		return mMapOfLists.get(key) != null && mMapOfLists.get(key).size() > 0;
	}

	@Override
	public V put(K key, V value) {

		/* We do not allow duplicates, that is, if the list of the
		 * key contains an old entry where entry.equals(value) is true,
		 * we remove the entry.
		 */
		List<V> list = mMapOfLists.get(key);
		int idx = findIndexOf(value, list);
		V ret = null;

		/* If there never was a list, put a new one in.
		 * If there was an element already, remove it.
		 */
		if (list == null) {
			list = new ArrayList<V>();
			mMapOfLists.put(key, list);
		} else if (idx >= 0) {
			ret = list.remove(idx);
			mSize--;
		}

		list.add(value);
		mSize++;
		return ret;
	}

	@Override
	public void clear() {
		mMapOfLists.clear();
		mSize = 0;
	}

	@Override
	public Set<K> keySet() {
		return new HashSet<K>(mMapOfLists.keySet());
	}

	@Override
	public Collection<V> values() {
		ArrayList<V> ret = new ArrayList<V>();

		for (List<V> list : mMapOfLists.values()) {
			ret.addAll(list);
		}

		return ret;
	}

	@Override
	public V get(K key, V value) {
		List<V> list = mMapOfLists.get(key);
		int idx = findIndexOf(value, list);

		return idx >= 0 ? list.get(idx) : null;
	}

	@Override
	public boolean remove(K key, V value) {
		List<V> list = mMapOfLists.get(key);
		int idx = findIndexOf(value, list);
		if (idx >= 0) {
			list.remove(idx);
			mSize--;

			// list is not needed anymore
			if (list.size() == 0) {
				mMapOfLists.remove(key);
			}
		}

		return idx >= 0;
	}

	@Override
	public Collection<V> getAll(K key) {
		List<V> ret = new ArrayList<V>();
		List<V> list = mMapOfLists.get(key);

		if (list != null) {
			ret.addAll(list);
		}

		return ret;
	}

	@Override
	public boolean removeAll(K key) {
		List<V> tmp = mMapOfLists.remove(key);
		if (tmp != null) {
			mSize -= tmp.size();
		}
		return tmp != null && tmp.size() > 0;
	}

	/**
	 * Helper to easily get the index of an object in a {@link List} instance.
	 *
	 * @param value
	 * @param list list to look for value, might be null
	 * @return -1 if list == null, otherwise list.indexOf(value)
	 */
	private int findIndexOf(V value, List<V> list) {
		return list == null ? -1 : list.indexOf(value);
	}
}
