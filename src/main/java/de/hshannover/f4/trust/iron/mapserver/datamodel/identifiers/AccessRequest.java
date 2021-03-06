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
package de.hshannover.f4.trust.iron.mapserver.datamodel.identifiers;


import de.hshannover.f4.trust.iron.mapserver.IfmapConstStrings;
import de.hshannover.f4.trust.iron.mapserver.exceptions.InvalidIdentifierException;

/**
 * Implementation of the access-request Identifier
 *
 * @since 0.1.0
 * @author aw
 */
public class AccessRequest extends IdentifierWithAdministrativeDomainImpl
		implements IdentifierWithAdministrativeDomain {

	private final String mName;

	/**
	 * Create a AccessRequest Object based on a administrative-domain and a name.
	 *
	 * @param ad
	 * @param name
	 * @throws InvalidIdentifierException
	 */
	@Deprecated
	public AccessRequest(final String name, final String ad) throws InvalidIdentifierException {
		super(IfmapConstStrings.AR, ad);

		if (name == null || name.length() == 0) {
			throw new InvalidIdentifierException("AccessRequest: " +
					"name is invalid (" + name + ")");
		}
		this.mName = name;
		setByteCount(IfmapConstStrings.AR_CNT + name.length() +
				getByteCountForAdministrativeDomain());
	}

	/**
	 * Create a AccessRequest only based on a given name.
	 * The administrative-domain will be set to ""
	 *
	 * @param name
	 * @throws InvalidIdentifierException
	 */
	public AccessRequest(final String name) throws InvalidIdentifierException {
		this(name, null);
	}

	public String getName() {
		return mName;
	}

	/* (non-Javadoc)
	 * @see de.hshannover.f4.trust.iron.mapserver.datamodel.identifiers.IdentifierWithAdministrativeDomain#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}

		if (!super.equals(o)) {
			return false;
		}

		if (!(o instanceof AccessRequest)) {
			return false;
		}

		return mName.equals(((AccessRequest)o).getName());
	}

	@Override
	protected final int getHashCode() {
		int hash = 59 * 3 + mName.hashCode();
		return 59 * hash + super.getHashCode();
	}

	/* (non-Javadoc)
	 * @see de.hshannover.f4.trust.iron.mapserver.datamodel.identifiers.IdentifierImpl#buildToStringString()
	 */
	@Override
	protected final String getPrintableString() {
		final String ad = getAdministrativeDomain();
		final StringBuffer sb = new StringBuffer();
		sb.append("ar{");
		sb.append(mName);
		if (!(ad == null || ad.length() == 0)) {
			sb.append(", ");
			sb.append(ad);
		}
		sb.append("}");
		return sb.toString();
	}
}
