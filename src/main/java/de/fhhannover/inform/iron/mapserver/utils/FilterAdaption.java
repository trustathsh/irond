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
 * This file is part of irond, version 0.4.2, implemented by the Trust@HsH
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
package de.fhhannover.inform.iron.mapserver.utils;


/**
 * Implements adaptFilterString().
 *
 * This method transforms a given filter string such that in can be used with
 * standard XPath implementations. Basically, this is for now
 * transforming a "[" to "*[" and " [" to " *[".
 *
 * @author aw
 *
 */
public class FilterAdaption {

	/**
	 * Replace all occurrences of [ without a preceding element or at the
	 * beginning of the string with *[. This is because the * is not used
	 * as wild card in IF-MAP filterstrings.
	 *
	 * @param fs
	 * @return adapted filterstring
	 */
	public static String adaptFilterString(String fs) {
		NullCheck.check(fs, "filter string is null");
		String first = fs.replaceAll("^\\[", "*[");
		return first.replace(" [", " *[");
	}
}
