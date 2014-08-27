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
package de.hshannover.f4.trust.iron.mapserver.communication;


import de.hshannover.f4.trust.iron.mapserver.exceptions.SystemErrorException;

/**
 * Identifies a MAPC based on the credentials used during authentication
 * with the MAPS. Stores all information needed to identify a client.
 *
 * @author aw
 * @author jk
 *
 */
public class ClientIdentifier {

	private final String mUsername;
	private final String mIssuer;
	private final String mSubject;

	/**
	 * Depending on whether the client authenticated using cert or basic
	 * auth, this field holds the username or the fingerprint of the
	 * client's certificate
	 */
	private final String mUsernameOrFp;
	private final int mHashCode;

	/**
	 * Constructs a {@link ClientIdentifier} object based on certificate
	 * subject, top-level issuer, and certificate finger print.
	 *
	 * @param username
	 * @param subject
	 * @param issuer
	 */
	public ClientIdentifier(String readableIdent, String subject, String issuer,
			String fp) {

		if (readableIdent == null || readableIdent.length() == 0) {
			throw new SystemErrorException("simpleUser not given");
		}

		if (fp == null || fp.length() == 0) {
			throw new SystemErrorException("fp not given");
		}

		mUsername = readableIdent;
		mSubject = subject != null ? subject : "";
		mIssuer = issuer != null ? issuer : "";
		mUsernameOrFp = fp;
		mHashCode = calculateHashCode();
	}

	/**
	 * Constructs a {@link ClientIdentifier} object based on username
	 *
	 * @param userName client username
	 */
	public ClientIdentifier(String userName) {
		this(userName, null, null, userName);
	}

	/**
	 * Calculate hash code for the client
	 * @return hashcode value
	 */
	private int calculateHashCode() {
		return 3 + 11 * mUsername.hashCode() + 11 *
				mSubject.hashCode() + 11 * mIssuer.hashCode()
				+ 11 * mUsernameOrFp.hashCode();
	}

	/**
	 * Client username
	 * @return client username
	 */
	public String getUsername() {
		return mUsername;
	}

	/**
	 * Certificate subject DN
	 * @return subject
	 */
	public String getSubject() {
		return mSubject;
	}

	/**
	 * Root certificate issuer DN
	 * @return issuer
	 */
	public String getIssuer() {
		return mIssuer;
	}

	/**
	 * Client IP address
	 * @return ip
	 *
	public String getIpAddress() {
		return mIpAddress;
	}
	*/

	public String getReadablePseudoIdentifier() {
		return mUsername;
	}

	public String getUserameOrCertFingerPrint() {
		return mUsernameOrFp;
	}

	public String getUserNameOrCertFingerprint() {
		return mUsernameOrFp;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return mHashCode;
	}

	/**
	 * Compares the client identifier against any other client identifier
	 * @param o second client identifier
	 * @return true if both are equal
	 */
	@Override
	public boolean equals(Object o) {
		ClientIdentifier oClId = (ClientIdentifier) o;

		if (this == o) {
			return true;
		}

		if (o == null) {
			return false;
		}

		if (mHashCode != o.hashCode()) {
			return false;
		}

		if (!(o instanceof ClientIdentifier)) {
			return false;
		}
		oClId = (ClientIdentifier) o;

		return mUsername.equals(oClId.getUsername()) &&
				mSubject.equals(oClId.getSubject()) &&
				mIssuer.equals(oClId.getIssuer()) &&
				mUsernameOrFp.equals(oClId.mUsernameOrFp);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "client{" + getReadablePseudoIdentifier() + "}";
	}
}
