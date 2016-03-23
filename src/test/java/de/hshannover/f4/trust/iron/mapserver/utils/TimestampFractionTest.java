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
 * This file is part of irond, version 0.5.8, implemented by the Trust@HsH
 * research group at the Hochschule Hannover.
 * %%
 * Copyright (C) 2010 - 2016 Trust@HsH
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


import static org.junit.Assert.assertEquals;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Test;

public class TimestampFractionTest {

	private static final double TOLERANCE = 0.000001;

	private static SimpleDateFormat formatter =
			new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
	private static SimpleDateFormat formatterNoMilliseconds =
			new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

	/**
	 * Parse the given string according to the date
	 * format "yyyy-MM-dd'T'HH:mm:ss.SSSZ".
	 *
	 * @param dateString the string to parse
	 * @return the date object
	 */
	private static Date parseDate(String dateString) {
		try {
			return formatter.parse(dateString);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

	@Test
	public void testFormatMilliseconds() {
		Date d = parseDate("2014-07-15T22:22:22.123+0200");
		assertEquals(0.123, TimestampFraction.getSecondFraction(d), TOLERANCE);
	}

	@Test
	public void testFormatMillisecondsWithZero() {
		Date d = parseDate("2014-07-15T22:22:22.0+0200");
		assertEquals(0.0, TimestampFraction.getSecondFraction(d), TOLERANCE);
	}

	@Test
	public void testFormatMillisecondsNoMilliseconds() {
		try {
			Date d = formatterNoMilliseconds.parse("2014-07-15T22:22:22+0200");
			assertEquals(0.0, TimestampFraction.getSecondFraction(d), TOLERANCE);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

}
