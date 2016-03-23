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
package de.hshannover.f4.trust.iron.mapserver.messages;


import de.hshannover.f4.trust.iron.mapserver.datamodel.identifiers.Identifier;
import de.hshannover.f4.trust.iron.mapserver.exceptions.RequestCreationException;
import de.hshannover.f4.trust.iron.mapserver.utils.NullCheck;

/**
 * SubPublishRequest
 *
 * Superclass which may contain two identifiers and needs at least one
 * identifier.
 * If both identifiers are set a link is described.
 * If only one identifier is set a single identifier is described.
 *
 * @author aw, vp
 * @version 0.1
 */
public abstract class SubPublishRequest {

	public enum PublishRequestType {
		UPDATE,
		DELETE,
		NOTIFY
	}

	private Identifier mIdent1;
	private Identifier mIdent2;
	private PublishRequestType mType;

	/**
	 * Constructor a {@link SubPublishRequest} from two {@link Identifier}s.
	 *
	 * Implementation note: If one {@link Identifier} is null, mIdent1 will
	 *                      always be non-null.
	 *
	 * @param ident1
	 * @param ident2
	 * @throws RequestCreationException
	 */
	public SubPublishRequest(Identifier ident1, Identifier ident2, PublishRequestType type)
			throws RequestCreationException {

		NullCheck.check(type, "type is null");

		if (ident1 == null && ident2 == null) {
			throw new RequestCreationException("Both identifiers null");
		} else if (ident1 == null || ident2 == null) {
			if (ident1 == null) {
				ident1 = ident2;
				ident2 = null;
			}
		}

		mIdent1 = ident1;
		mIdent2 = ident2;
		mType = type;
	}

	/**
	 * Construct a {@link SubPublishRequest} from a single {@link Identifier}.
	 *
	 * @param ident
	 * @throws RequestCreationException
	 */
	public SubPublishRequest(Identifier ident, PublishRequestType type) throws RequestCreationException {
		this(ident, null, type);
	}

	public Identifier getIdent1() {
		return mIdent1;
	}

	public Identifier getIdent2() {
		return mIdent2;
	}

	public PublishRequestType getType() {
		return mType;
	}
}


