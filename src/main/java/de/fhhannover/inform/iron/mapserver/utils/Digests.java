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
		
		for (byte c : hash)
			sb.append(String.format("%02x", c));
		
		return sb.toString();
	}
}
