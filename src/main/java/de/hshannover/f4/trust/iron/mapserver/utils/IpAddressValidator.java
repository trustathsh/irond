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
package de.hshannover.f4.trust.iron.mapserver.utils;


/**
 * Class which contains to check methods
 * for validating IP Adresses.
 *
 * @author aw
 * @version 0.1
 *
 * created: 26.11.09
 * changes:
 *  26.11.09 aw - Implemented first version for IPv4 and IPv6
 *  27.11.09 aw - Check for null values
 *
 */
public class IpAddressValidator {

	/**
	 * Check a IPv4 Address with splitting at the dots.
	 *
	 * FIXME: Accepts 127.0.0.1.... (... means following dots!)
	 *
	 * @param value
	 * @return
	 */
	public static boolean validateIPv4(String value) {
		boolean res = true;

		if (value == null) {
			return false;
		}

		int dots = 0;
		for (byte b : value.getBytes()) {
			if (b == '.') {
				dots++;
			}
			if (dots > 3) {
				res = false;
			}
		}

		try {
			String octets[] = value.split("\\.");
			if (octets.length == 4 && res) {
				for (String s : octets) {
					if (s.length() == 0 || s.startsWith("0") && s.length() > 1) {
						res = false;
						break;
					}
					int x = Integer.parseInt(s);
					if (x > 255 || x < 0) {
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

	/**
	 * Validate IPv6 Addresses those must be lowercase
	 * hexdigits and at least one zero is needed. It may not be
	 * shortened by using :: in between
	 * @param value
	 * @return
	 */
	public static boolean validateIPv6(String value) {
		boolean res = true;

		if (value == null) {
			return false;
		}

		try {
			String quads[] = value.split(":");
			if (quads.length == 8) {
				for (String s : quads) {
					if (s.length() == 0 || s.startsWith("0") && s.length() > 1) {
						res = false;
						break;
					}

					int numb = Integer.parseInt(s, 16);
					if (numb > 0xffff || numb < 0 || !s.toLowerCase().equals(s)) {
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
