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
 * This file is part of irond, version 0.5.6, implemented by the Trust@HsH
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


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
/**
 *
 * @author jk
 */
public class Digests {

	private static MessageDigest sMd5;
	private static MessageDigest sSha1;

	static {
		try {
			sMd5 = MessageDigest.getInstance("MD5");
			sSha1 = MessageDigest.getInstance("SHA1");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	private static String hash(byte[] data, MessageDigest digest) {
		byte[] hash = null;
		synchronized (digest) {
			sMd5.reset();
			hash = digest.digest(data);
		}
		return makePrintable(hash);

	}

	public static String md5(byte[] data) {
		return hash(data, sMd5);
	}

	public static String sha1(byte[] data) {
		return hash(data, sSha1);
	}

	private static String makePrintable(byte[] hash) {
		StringBuilder sb = new StringBuilder();

		for (byte c : hash) {
			sb.append(String.format("%02x", c));
		}

		return sb.toString();
	}
}
