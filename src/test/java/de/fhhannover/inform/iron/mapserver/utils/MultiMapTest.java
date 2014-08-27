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

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import junit.framework.TestCase;

import org.junit.Test;

public class MultiMapTest extends TestCase {

	@Test
	public void testIsEmpty() {
		MultiMap<Integer, String> mm = new MultiArrayListMap<Integer, String>();

		assertTrue(mm.isEmpty());
		mm.put("hello".hashCode(), "hello");
		mm.remove("hello".hashCode(), "hello");
		assertTrue(mm.isEmpty());
	}

	@Test
	public void testSize() {
		MultiMap<Integer, String> mm = new MultiArrayListMap<Integer, String>();
		assertEquals(0, mm.size());
		mm.put("hello".hashCode(), "hello");
		assertEquals(1, mm.size());
		mm.remove("hello".hashCode(), "hello");
		assertEquals(0, mm.size());

		mm.put("hello2".hashCode(), "hello2");
		assertEquals(1, mm.size());
		mm.clear();
		assertEquals(0, mm.size());

		mm.put("hello3".hashCode(), "hello3");
		assertEquals(1, mm.size());
		mm.removeAll("hello3".hashCode());
		assertEquals(0, mm.size());
	}

	@Test
	public void testDuplicatesAndNonDuplicates() {
		MultiMap<Integer, String> mm = new MultiArrayListMap<Integer, String>();
		String hello1 = "hello";
		String hello2 = new StringBuilder("hello").toString();
		String hello3 = "hello3";

		assertEquals(0, mm.size());
		assertNotSame(hello1, hello2);

		mm.put("hello".hashCode(), hello1);
		assertEquals(1, mm.size());
		assertSame(hello1, mm.put("hello".hashCode(), hello2));
		assertEquals(1, mm.size());
		assertSame(hello2, mm.put("hello".hashCode(), hello1));
		assertEquals(1, mm.size());
		assertSame(null, mm.put("hello".hashCode(), hello3));
		assertEquals(2, mm.size());
	}

	@Test
	public void testPutGet() {
		MultiMap<Integer, Object> mm = new MultiArrayListMap<Integer, Object>();
		Object obj1 = "abcdefgh";
		Object obj2 = new Integer(10);
		Object obj3 = new Object();

		assertSame(null, mm.get(20, "abcdefgh"));
		assertSame(null, mm.get(20, obj2));
		assertSame(null, mm.get(30, obj3));

		mm.put(20, obj1);
		mm.put(20, obj2);
		mm.put(30, obj3);

		assertEquals(3, mm.size());
		assertSame(obj1, mm.get(20, "abcdefgh"));
		assertSame(obj2, mm.get(20, obj2));
		assertSame(obj3, mm.get(30, obj3));

		assertSame(null, mm.get(20, obj3));
		assertSame(null, mm.get(30, obj1));
		assertSame(null, mm.get(30, obj2));
	}

	@Test
	public void testPutRemove() {
		MultiMap<Integer, Object> mm = new MultiArrayListMap<Integer, Object>();
		Object obj1 = "abcdefgh";
		Object obj2 = new Integer(10);
		Object obj3 = new Object();

		assertFalse(mm.remove(20, obj1));
		assertFalse(mm.remove(20, obj1));
		assertFalse(mm.remove(30, obj3));

		mm.put(20, obj1);
		mm.put(20, obj2);
		mm.put(30, obj3);

		assertEquals(3, mm.size());
		assertSame(obj1, mm.get(20, "abcdefgh"));
		assertSame(obj2, mm.get(20, obj2));

		assertTrue(mm.remove(20, "abcdefgh"));
		assertSame(null, mm.get(20, "abcdefgh"));
		assertFalse(mm.remove(20, "abcdefgh"));
		assertFalse(mm.remove(30, obj2));
		assertTrue(mm.remove(30, obj3));
	}

	@Test
	public void testRemoveAll() {
		MultiMap<Integer, Object> mm = new MultiArrayListMap<Integer, Object>();
		Object obj1 = "abcdefgh";
		Object obj2 = new Integer(10);
		Object obj3 = new Object();

		assertFalse(mm.removeAll(20));

		mm.put(20, obj1);
		mm.put(20, obj2);
		mm.put(30, obj3);

		assertTrue(mm.removeAll(20));
		assertEquals(1, mm.size());
		assertSame(null, mm.get(20, "abcdefgh"));
		assertSame(null, mm.get(20, obj2));
		assertSame(obj3, mm.get(30, obj3));

		assertTrue(mm.removeAll(30));
		assertSame(null, mm.get(30, obj3));
		assertEquals(0, mm.size());
	}

	@Test
	public void testValues() {
		MultiMap<Integer, Object> mm = new MultiArrayListMap<Integer, Object>();
		Object obj1 = "abcdefgh";
		Object obj2 = new Integer(10);
		Object obj3 = new Object();
		List<Object> values = new LinkedList<Object>();
		values.add(obj1);
		values.add(obj2);
		values.add(obj3);

		assertEquals(0, mm.values().size());

		mm.put(20, obj1);
		mm.put(20, obj2);
		mm.put(30, obj3);

		Collection<Object> queriedValues = mm.values();
		assertEquals(3, queriedValues.size());
		assertEquals(values, queriedValues);

		mm.clear();
		assertEquals(0, mm.values().size());
	}

	public void testGetAll() {
		MultiMap<Integer, Object> mm = new MultiArrayListMap<Integer, Object>();
		Object obj1 = "abcdefgh";
		Object obj2 = new Integer(10);
		Object obj3 = new Object();
		List<Object> values = new LinkedList<Object>();
		values.add(obj1);
		values.add(obj2);

		assertEquals(0, mm.getAll(20).size());
		assertEquals(0, mm.getAll(30).size());

		mm.put(20, obj1);
		mm.put(20, obj2);
		mm.put(30, obj3);

		Collection<Object> queried = mm.getAll(20);
		assertEquals(2, queried.size());
		assertEquals(values, queried);

		values.clear();
		values.add(obj3);
		queried = mm.getAll(30);
		assertEquals(1, queried.size());
		assertEquals(values, queried);

		mm.clear();
		assertEquals(0, mm.getAll(20).size());
		assertEquals(0, mm.getAll(30).size());
	}

	public void testKeySet() {
		MultiMap<Integer, Object> mm = new MultiArrayListMap<Integer, Object>();
		Object obj1 = "abcdefgh";
		Object obj2 = new Integer(10);
		Object obj3 = new Object();
		assertFalse(mm.containsKey(20));
		assertFalse(mm.containsKey(30));
		mm.put(20, obj1);
		mm.put(20, obj2);
		mm.put(30, obj3);
		assertTrue(mm.containsKey(20));
		assertTrue(mm.containsKey(30));

		mm.remove(30, obj3);
		assertFalse(mm.containsKey(30));

		mm.remove(20, obj1);
		mm.removeAll(20);
		assertFalse(mm.containsKey(30));
		assertTrue(mm.isEmpty());

		mm.put(10, "abbac");
		assertTrue(mm.containsKey(10));
		mm.clear();
		assertFalse(mm.containsKey(10));
	}

	public void testContainsKey() {

	}

}
