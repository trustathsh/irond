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

import de.fhhannover.inform.iron.mapserver.IfmapConstStrings;

/**
 * @since 0.3.0
 * @author aw
 */
abstract class IdentifierWithAdministrativeDomainImpl extends IdentifierImpl 
		implements IdentifierWithAdministrativeDomain {
	
	private final String administrativeDomain;
	
	/**
	 * The bytes used for the administrativeDomain attribute, if at all
	 */
	private final int mAdmByteCount;
	
	IdentifierWithAdministrativeDomainImpl(final String type, String ad) {
		super(type);
	
		ad = (ad == null) ? "" : ad;
		mAdmByteCount = (ad.length() == 0) ? 0 : IfmapConstStrings.ADMDOM_CNT + ad.length();
		administrativeDomain = ad;
	}

	/* (non-Javadoc)
	 * @see de.fhhannover.inform.iron.mapserver.datamodel.identifiers.IdentifierWithAdministrativeDomain#getAdministrativeDomain()
	 */
	@Override
	public String getAdministrativeDomain() {
		return administrativeDomain;
	}
	
	/**
	 * @return the number of bytes needed to represent the administrativeDomain
	 *	 attribute, or zero if none is set.
	 */
	protected final int getByteCountForAdministrativeDomain() {
		return mAdmByteCount;
	}

	/**
	 * Compares two identifiers
	 * @param o second identifer
	 * @return true if both identifiers are equal
	 */
	@Override
	public boolean equals(Object o) {
		IdentifierWithAdministrativeDomainImpl oident;
		String oad;
		
		if(this == o) 
			return true;
		
		if (o instanceof IdentifierWithAdministrativeDomainImpl) {
			oident = (IdentifierWithAdministrativeDomainImpl) o;
			oad = oident.getAdministrativeDomain();
			
			if (mConf.getAdministrativeDomainIsCaseSensitive())
				return administrativeDomain.equals(oad);
			
			return administrativeDomain.equalsIgnoreCase(oad);
		}
		return false;
	}

	@Override
	protected int getHashCode() {
		int hash = 83 * 29 + administrativeDomain.hashCode();
		return 83 * hash + super.getHashCode();
	}
	
	@Override
	protected abstract String getPrintableString();
}
