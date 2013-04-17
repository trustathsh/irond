package de.fhhannover.inform.iron.mapserver.datamodel.identifiers;

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
 * This file is part of irond, version 0.4.0, implemented by the Trust@FHH 
 * research group at the Fachhochschule Hannover.
 * 
 * irond is an an *experimental* IF-MAP 2.0 compliant MAP server written in
 * JAVA. irond supports both basic authentication and certificate-based 
 * authentication (using X.509 certificates) of MAP clients. irond is
 * maintained by the Trust@FHH group at the Fachhochschule Hannover, initial
 * developement was carried out during the ESUKOM research project.
 * %%
 * Copyright (C) 2010 - 2013 Trust@FHH
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

import de.fhhannover.inform.iron.mapserver.IfmapConstStrings;
import de.fhhannover.inform.iron.mapserver.exceptions.InvalidIdentifierException;
import de.fhhannover.inform.iron.mapserver.utils.MacAddressValidator;

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
		
		if (value == null || value.length() == 0)
			throw new InvalidIdentifierException("MacAddress: Empty or no value "
					+ "(" + value + ")");
		
		if (!MacAddressValidator.validateMacAddress(value))
			throw new InvalidIdentifierException("MacAddress: Invalid format " +
					"(" + value + ")");
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
	 * @see de.fhhannover.inform.iron.mapserver.datamodel.identifiers.IdentifierWithAdministrativeDomainImpl#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {
		
		if (this == o)
			return true;
		
		if (!super.equals(o))
			return false;
	
		if (!(o instanceof MacAddress))
			return false;
		
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
	 * @see de.fhhannover.inform.iron.mapserver.datamodel.identifiers.IdentifierImpl#buildToStringString()
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
