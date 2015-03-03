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
package de.hshannover.f4.trust.iron.mapserver.datamodel;


import de.hshannover.f4.trust.iron.mapserver.exceptions.SystemErrorException;

public abstract class SimpleSearchAbleImpl implements SearchAble {

	/**
	 * indicates whether the byte count was set for this object.
	 */
	private boolean mByteCountSet;

	/**
	 * the byte count for this object.
	 */
	private int mByteCount;

	/**
	 * construct uninitialized {@link SimpleSearchAbleImpl} object.
	 */
	public SimpleSearchAbleImpl() {
		mByteCountSet = false;
		mByteCount = -1;
	}

	/**
	 * construct initialized {@link SimpleSearchAbleImpl} object.
	 *
	 * @param bytes
	 */
	public SimpleSearchAbleImpl(int bytes) {
		this();
		setByteCount(bytes);
	}

	/**
	 * Set the byte count for this {@link SearchAble} object.
	 * @throws SystemErrorException if bytes <= 0 or this method was called
	 * 			before.
	 *
	 * @param bytes
	 */
	protected final void setByteCount(int bytes) {

		if (mByteCountSet) {
			throw new SystemErrorException("reinitialization of byte count");
		}

		if (bytes <= 0) {
			throw new SystemErrorException("byte count " + bytes + " invalid");
		}

		mByteCountSet = true;
		mByteCount = bytes;
	}

	@Override
	public final int getByteCount() {

		if (!mByteCountSet) {
			throw new SystemErrorException("access to uninitialized byte count");
		}

		return mByteCount;
	}
}