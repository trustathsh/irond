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


/**
 * Class to validate MAC Addresses
 *
 *
 * @author aw
 * @version 0.1
 *
 *
 * created: 27.11.09
 * changes:
 *  27.11.09 aw - Created first version of this class
 *
 */
public class MacAddressValidator {

	/**
	 * Checks if the given mac (as string) is valid
	 *
	 * a mac must be 6 lowercase hex digits
	 *
	 * @param value
	 * @return
	 */
	public static boolean validateMacAddress(String value) {
		boolean res = true;

		if (value == null) {
			return false;
		}

		try {
			String doubles[] = value.split(":");
			if (doubles.length == 6) {
				for (String s : doubles) {
						int x = Integer.parseInt(s, 16);
						if (x > 0xff || s.length() != 2 ||
								!s.toLowerCase().equals(s)) {
							res = false;
						break;
					}
				}
			} else {
				res = false;
			}
		} catch (NumberFormatException e) {
			res = false;
		}
		return res;
	}
}
