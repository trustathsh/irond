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
 * This file is part of irond, version 0.5.0, implemented by the Trust@HsH
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
package de.hshannover.f4.trust.iron.mapserver.datamodel.identifiers;


import de.hshannover.f4.trust.iron.mapserver.IfmapConstStrings;
import de.hshannover.f4.trust.iron.mapserver.exceptions.InvalidIdentifierException;
import de.hshannover.f4.trust.iron.mapserver.utils.IpAddressValidator;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Implementation of the ip-address identifier.
 *
 * @since 0.1.0
 * @author aw
 */
public class IpAddress extends IdentifierWithAdministrativeDomainImpl
		implements IdentifierWithAdministrativeDomain {

	private final String mValue;
	private final IpAddressTypeEnum mType;
	private InetAddress mIpAddress;

	/**
	 * Construct a IpAddress from value, administrative-domain and type.
	 *
	 * @param value
	 * @param ad
	 * @param ipat
	 * @throws InvalidIdentifierException
	 */
	public IpAddress(final String value, final String ad, IpAddressTypeEnum ipat)
			throws InvalidIdentifierException {
		super(IfmapConstStrings.IP, ad);

		// default to IPv4
		if (ipat == null) {
			ipat = IpAddressTypeEnum.IPv4;
		}

		if (value == null || value.length() == 0) {
			throw new InvalidIdentifierException("IpAddress: Empty or null "
					+ "(" + value + ")");
		}

		mType = ipat;
		mValue = value;

		if (ipat == IpAddressTypeEnum.IPv6 && !IpAddressValidator.validateIPv6(value)) {
			throw new InvalidIdentifierException("IpAddress: Invalid " + ipat +
					mType.toString() + " address (" + value + ")");
		}

		if (ipat == IpAddressTypeEnum.IPv4 && !IpAddressValidator.validateIPv4(value)) {
			throw new InvalidIdentifierException("IpAddress: Invalid " + ipat +
					mType.toString() + " address (" + value + ")");
		}

		try {
			if (ipat == IpAddressTypeEnum.IPv6) {
				mIpAddress = InetAddress.getByName(value);
			} else {
				mIpAddress = InetAddress.getByName(value);
			}
		} catch (UnknownHostException e) {
			throw new InvalidIdentifierException("IpAddress: Invalid " +
					mType.toString() + " format (" + value + ")");
		}

		// specs say MAPS must reject IP not canonicalized
		if (!mIpAddress.getHostAddress().equals(mValue)) {
			throw new InvalidIdentifierException("IpAddress: not in canonical " +
					mType.toString() + " format (" + value + " != " +
					mIpAddress.getHostAddress() + ")");
		}

		setByteCount(IfmapConstStrings.IP_CNT +  mType.toString().length()
				+ mValue.length()  + getByteCountForAdministrativeDomain());
	}

	public IpAddress(final String value, final IpAddressTypeEnum ipat)
			throws InvalidIdentifierException {
		this(value, "", ipat);
	}

	public String getValue() {
		return mValue;
	}

	public IpAddressTypeEnum getIpAddressType() {
		return mType;
	}

	protected InetAddress getIpAddress() {
		return mIpAddress;
	}

	@Override
	public boolean equals(Object o) {
		IpAddress oIp;

		if (this == o) {
			return true;
		}

		if (!super.equals(o)) {
			return false;
		}

		if (!(o instanceof IpAddress)) {
			return false;
		}

		oIp = (IpAddress) o;
		return mType == oIp.getIpAddressType() &&
				mIpAddress.equals(oIp.getIpAddress());
	}

	/**
	 * Generate a hash code based on the IP address and its type
	 * @return hash value
	 */
	@Override
	protected final int getHashCode() {
		int hash = 23 * 7 + mIpAddress.hashCode();
		hash = 23 * hash + (mType != null ? mType.hashCode() : 0);
		return 23 * hash + super.getHashCode();
	}

	@Override
	protected final String getPrintableString() {
		String ad = getAdministrativeDomain();
		StringBuilder sb = new StringBuilder();
		sb.append("ip{");
		sb.append(getValue());
		if (!(ad == null | ad.length() == 0)) {
			sb.append(", ");
			sb.append(ad);
		}
		sb.append(", ");
		sb.append(mType.toString());
		sb.append("}");
		return sb.toString();
	}
}

