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
package de.hshannover.f4.trust.iron.mapserver.datamodel.identifiers;


import de.hshannover.f4.trust.iron.mapserver.IfmapConstStrings;
import de.hshannover.f4.trust.iron.mapserver.exceptions.InvalidIdentifierException;
import de.hshannover.f4.trust.iron.mapserver.utils.MacAddressValidator;

/**
 * Implementation of the mac-address identifier.
 *
 * @since 0.1.0
 * @author aw
 */
public class MacAddress extends IdentifierWithAdministrativeDomainImpl
		implements IdentifierWithAdministrativeDomain {

	private final String mValue;

	/**
	 * Create a MAC Address based on value and administrative-domain
	 *
	 * @param value
	 * @param ad
	 * @throws InvalidIdentifierException
	 */
	public MacAddress(final String value, final String ad) throws InvalidIdentifierException {
		super(IfmapConstStrings.MAC, ad);

		if (value == null || value.length() == 0) {
			throw new InvalidIdentifierException("MacAddress: Empty or no value "
					+ "(" + value + ")");
		}

		if (!MacAddressValidator.validateMacAddress(value)) {
			throw new InvalidIdentifierException("MacAddress: Invalid format " +
					"(" + value + ")");
		}
		mValue = value;

		setByteCount(IfmapConstStrings.MAC_CNT + mValue.length()
				+ getByteCountForAdministrativeDomain());
	}

	/**
	 * Create a MAC address based only on the value.
	 * The administrativeDomain is set to ""
	 *
	 * @param value
	 * @throws InvalidIdentifierException
	 */
	public MacAddress(final String value) throws InvalidIdentifierException {
		this(value, "");
	}

	public String getValue() {
		return mValue;
	}

	/* (non-Javadoc)
	 * @see de.hshannover.f4.trust.iron.mapserver.datamodel.identifiers.IdentifierWithAdministrativeDomainImpl#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {

		if (this == o) {
			return true;
		}

		if (!super.equals(o)) {
			return false;
		}

		if (!(o instanceof MacAddress)) {
			return false;
		}

		return mValue.equals(((MacAddress)o).getValue());
	}

	/**
	 * Generate a hash code based on the MAC address
	 * @return hash value
	 */
	@Override
	protected final int getHashCode() {
		int hash = 47 * 13 + mValue.hashCode();
		return 47 * hash + super.getHashCode();
	}

	/* (non-Javadoc)
	 * @see de.hshannover.f4.trust.iron.mapserver.datamodel.identifiers.IdentifierImpl#buildToStringString()
	 */
	@Override
	protected final String getPrintableString() {
		String ad = getAdministrativeDomain();
		StringBuilder sb = new StringBuilder();
		sb.append("mac{");
		sb.append(getValue());
		if (!(ad == null | ad.length() == 0)) {
			sb.append(", ");
			sb.append(ad);
		}
		sb.append("}");
		return sb.toString();
	}
}
