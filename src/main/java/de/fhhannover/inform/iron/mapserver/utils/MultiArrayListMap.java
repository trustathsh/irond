package de.fhhannover.inform.iron.mapserver.utils;

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
		
		for (List<V> list : mMapOfLists.values())
			ret.addAll(list);
		
		return ret;
	}

	@Override
	public V get(K key, V value) {
		List<V> list = mMapOfLists.get(key);
		int idx = findIndexOf(value, list);
		
		return (idx >= 0) ? list.get(idx) : null;
	}

	@Override
	public boolean remove(K key, V value) {
		List<V> list = mMapOfLists.get(key);
		int idx = findIndexOf(value, list);
		if (idx >= 0) {
			list.remove(idx);
			mSize--;
			
			// list is not needed anymore
			if (list.size() == 0)
				mMapOfLists.remove(key);
		}
		
		return (idx >= 0);
	}

	@Override
	public Collection<V> getAll(K key) {
		List<V> ret = new ArrayList<V>();
		List<V> list = mMapOfLists.get(key);
		
		if (list != null)
			ret.addAll(list);
		
		return ret;
	}

	@Override
	public boolean removeAll(K key) {
		List<V> tmp = mMapOfLists.remove(key);
		if (tmp != null)
			mSize -= tmp.size();
		return (tmp != null && tmp.size() > 0);
	}
	
	/**
	 * Helper to easily get the index of an object in a {@link List} instance.
	 * 
	 * @param value
	 * @param list list to look for value, might be null
	 * @return -1 if list == null, otherwise list.indexOf(value)
	 */
	private int findIndexOf(V value, List<V> list) {
		return (list == null) ? -1 : list.indexOf(value);
	}
}
