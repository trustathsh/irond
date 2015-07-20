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
package de.hshannover.f4.trust.iron.mapserver.communication;


import de.hshannover.f4.trust.iron.mapserver.utils.NullCheck;

/**
 * Identifies a channel based on ip:port and a counter value,
 *
 * TODO: IPv4, IPv6 issue? In the end it doesn't matter, we use this class
 *       only to identify a channel. We don't care what kind of protocol is
 *       used.
 *
 * TODO: Do we really need the counter? Or maybe we can only use the counter?
 *
 * @author aw
 *
 */
public class ChannelIdentifier {

	/**
	 * Host IP address
	 */
	private final String mIpAddress;

	/**
	 * Represents the port used by the MAPC
	 */
	private final int mPort;

	/**
	 * Represents a simple counter value
	 */
	private final int mCounter;


	/**
	 * Cached hash code value
	 */
	private final int mHashCode;


	/**
	 * Construct a {@link ChannelIdentifier} object representing either a SSRC
	 * or ARC.
	 *
	 * @param ip
	 * @param port
	 * @param counter
	 * @throws Exception
	 */
	public ChannelIdentifier(String ip, int port, int counter) {

		NullCheck.check(ip, "ip is null");

		if (ip.length() == 0) {
			throw new RuntimeException("length of ChannelIdentifier mIpAddress too short");
		}

		mIpAddress = ip;
		mPort = port;
		mCounter = counter;
		mHashCode = (mIpAddress + ":" + mPort + ":"+ mCounter).hashCode();
	}

	@Override
	public int hashCode() {
		return mHashCode;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}

		if (o instanceof ChannelIdentifier) {
			ChannelIdentifier ci = (ChannelIdentifier) o;
			return this.mIpAddress.equals(ci.mIpAddress)
				&& this.mPort == ci.mPort
				&& this.mCounter == ci.mCounter;
		}

		return false;
	}

	@Override
	public String toString() {
		return mIpAddress + ":" + mPort + ":" + mCounter;
	}
}
