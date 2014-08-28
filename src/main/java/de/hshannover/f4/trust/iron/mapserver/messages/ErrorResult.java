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
package de.hshannover.f4.trust.iron.mapserver.messages;


import de.hshannover.f4.trust.iron.mapserver.communication.ChannelIdentifier;
import de.hshannover.f4.trust.iron.mapserver.communication.ClientIdentifier;
import de.hshannover.f4.trust.iron.mapserver.messages.ErrorCode;
import de.hshannover.f4.trust.iron.mapserver.utils.NullCheck;

/**
 * Represents a errorResult.
 *
 * We allow a null errorString, which means no errorString is to be set.
 *
 *
 * @author aw
 *
 */
public class ErrorResult extends Result {

	private final ErrorCode mErrorCode;
	private final String mErrorString;

	public ErrorResult(ChannelIdentifier channelId, ClientIdentifier clientId,
			ErrorCode errCode, String errorString) {
		super(channelId, clientId);
		NullCheck.check(errCode, "error code is null");
		mErrorCode = errCode;
		mErrorString = errorString;
	}

	public ErrorResult(ChannelIdentifier channelId, ClientIdentifier clientId,
			ErrorCode errCode) {
		this(channelId, clientId, errCode, null);
	}

	public ErrorCode getErrorCode() {
		return mErrorCode;
	}

	public String getErrorString() {
		return mErrorString;
	}

}
