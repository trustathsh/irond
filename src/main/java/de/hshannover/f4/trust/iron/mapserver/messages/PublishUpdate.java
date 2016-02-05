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
 * This file is part of irond, version 0.5.7, implemented by the Trust@HsH
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


import java.util.List;

import de.hshannover.f4.trust.iron.mapserver.datamodel.identifiers.Identifier;
import de.hshannover.f4.trust.iron.mapserver.datamodel.meta.MetadataLifeTime;
import de.hshannover.f4.trust.iron.mapserver.datamodel.meta.Metadata;
import de.hshannover.f4.trust.iron.mapserver.exceptions.RequestCreationException;

/**
 * This class needs to be encapsulated into a {@link PublishUpdate}.
 *
 * @author awe, vp
 * @version 0.1
 */


/* created: 19.11.09
 * changes:
 *	22.11.09 vp - Added methods:
 * 		getter and setter for lifetime, setMetadataList()
 *	28.11.09 aw - Created lots of constructors, removed Lifetime Type in favor
 *		for MetaLifeTimeType
 *	05.12.09 aw - Made setter for lifetime deprecated as it should be given in
 *		a constructor
 *  04.02.10 aw - Added timestamp
 *  10.02.10 aw - Cleaned up...
 *
 *  thoughs:
 *   - We do not copy the metadataList?
 */
public class PublishUpdate extends SubPublishRequest {

	private MetadataLifeTime lifetime;
	private List<Metadata> metadataList;

	protected PublishUpdate(Identifier ident1, Identifier ident2, List<Metadata> ml,
			MetadataLifeTime lt, PublishRequestType type) throws RequestCreationException {

		super(ident1, ident2, type);

		if (ml == null) {
			throw new RequestCreationException("Metadata List is null");
		}

		if (ml.size() == 0) {
			throw new RequestCreationException("Metadata List is empty");
		}
		this.metadataList = ml;
		this.lifetime = lt;
	}

	/**
	 * Create a PublishUpdate object (PublishUpdateRequest) with every
	 * possible parameter.
	 *
	 * @param ident1
	 * @param ident2
	 * @param ml
	 * @param lt
	 * @throws RequestCreationException
	 */
	public PublishUpdate(Identifier ident1, Identifier ident2, List<Metadata> ml,
			MetadataLifeTime lt) throws RequestCreationException {
		this(ident1, ident2, ml, lt, PublishRequestType.UPDATE);
	}

	/**
	 * Construct a PublishUpdate with default session lifetime.
	 *
	 * @param ident1
	 * @param ident2
	 * @param ml
	 * @throws RequestCreationException
	 */
	public PublishUpdate(Identifier ident1, Identifier ident2, List<Metadata> ml)
			throws RequestCreationException {
		this(ident1, ident2, ml, MetadataLifeTime.session);
	}

	/**
	 * Construct a PublishUpdate with only one identifier.
	 *
	 * @param ident1
	 * @param ml
	 * @param lt
	 * @throws RequestCreationException
	 */
	public PublishUpdate(Identifier ident1, List<Metadata> ml, MetadataLifeTime lt)
			throws RequestCreationException {
		this(ident1, null, ml, lt);
	}

	/**
	 * Construct a PublishUpdate with only one identifier and session
	 * lifetime.
	 *
	 * @param ident1
	 * @param ml
	 * @throws RequestCreationException
	 */
	public PublishUpdate(Identifier ident1, List<Metadata> ml) throws RequestCreationException {
		this(ident1, ml, MetadataLifeTime.session);
	}

	public List<Metadata> getMetadataList() {
		return metadataList;
	}

	public MetadataLifeTime getLifeTime() {
		return lifetime;
	}
}


