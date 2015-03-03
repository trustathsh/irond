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
 * This file is part of irond, version 0.5.3, implemented by the Trust@HsH
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

/**
 * Implementation of the device Identifier.
 *
 * @since 0.1.0
 * @author aw
 */
public class Device extends IdentifierImpl implements Identifier {

	private final String mValue;
	private final DeviceTypeEnum mType;

	/**
	 * Constructor using type and value value.length has to be greater 0
	 *
	 * @param value
	 * @param type
	 * @throws InvalidIdentifierException
	 */
	public Device(final String value, final DeviceTypeEnum type) throws InvalidIdentifierException {
		super(IfmapConstStrings.DEV);

		if (value == null || type == null || value.length() == 0) {
			throw new InvalidIdentifierException("Device: Invalid value or type"
					+ " (" + value + "|" + type + ")");
		}

		mValue = value;
		mType = type;
		setByteCount(IfmapConstStrings.DEV_CNT + value.length());
	}

	public String getValue() {
		return mValue;
	}

	public DeviceTypeEnum getDeviceType() {
		return mType;
	}

	/* (non-Javadoc)
	 * @see de.hshannover.f4.trust.iron.mapserver.datamodel.identifiers.IdentifierImpl#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {

		if (this == o) {
			return true;
		}

		if (!(o instanceof Device)) {
			return false;
		}

		Device d = (Device) o;

		return mValue.equals(d.getValue()) &&
				mType.equals(d.getDeviceType());
	}

	/**
	 * Generate a hash code based on the device name and type
	 * @return hash value
	 */
	@Override
	protected final int getHashCode() {
		int hash = 79 * 19 + mValue.hashCode();
		hash = 79 * hash + (mType != null ? mType.hashCode() : 0);
		return 79 * hash + super.getHashCode();
	}

	/* (non-Javadoc)
	 * @see de.hshannover.f4.trust.iron.mapserver.datamodel.identifiers.IdentifierImpl#buildToStringString()
	 */
	@Override
	protected final String getPrintableString() {
		return "dev{" + getValue() + "}";
	}
}

