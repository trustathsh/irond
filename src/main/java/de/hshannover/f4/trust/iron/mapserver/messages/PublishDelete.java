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
import de.hshannover.f4.trust.iron.mapserver.datamodel.search.Filter;
import de.hshannover.f4.trust.iron.mapserver.exceptions.RequestCreationException;

/**
 * This Message represents a Request from a client to delete some Metadata
 * from a identifier. It needs to be encapsulated into a {@link PublishRequest}.
 *
 * @author awe
 * @version 0.1
 *
 */

/*
 *
 * created: 28.11.09
 * changes:
 * 	28.11.09 aw - Just a added some constructor
 *
 * thoughts:
 * 	28.11.09 aw - Somebody needs to check if the MAPC is allowed to delete
 * 		the metadata
 *
 */
public class PublishDelete extends SubPublishRequest {

	private Filter filter;

	/**
	 * Construct a PublishDelete (PublishDeleteRequest) with everything
	 * that's possible. Can be used to delete something from a link.
	 *
	 * @param ident1
	 * @param ident2
	 * @param f
	 * @throws RequestCreationException
	 */
	PublishDelete(Identifier ident1, Identifier ident2, Filter f)
		throws RequestCreationException {
		super(ident1, ident2, PublishRequestType.DELETE);
		filter = f;
	}

	/**
	 * Create a PublishDelete from only one identifier and a filter,
	 * (e.g. a PublishDeleteRequest for a identifier).
	 *
	 * @param ident
	 * @param f
	 * @throws RequestCreationException
	 */
	PublishDelete(Identifier ident, Filter f)
		throws RequestCreationException {
		super(ident, PublishRequestType.DELETE);
		filter = f;
	}

	public Filter getFilter() {
		return filter;
	}
}

