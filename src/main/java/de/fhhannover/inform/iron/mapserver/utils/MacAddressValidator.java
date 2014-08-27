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
