package de.fhhannover.inform.iron.mapserver.messages;

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
 * This file is part of irond, version 0.4.1, implemented by the Trust@FHH
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

import de.fhhannover.inform.iron.mapserver.datamodel.identifiers.Identifier;
import de.fhhannover.inform.iron.mapserver.datamodel.search.Filter;
import de.fhhannover.inform.iron.mapserver.exceptions.RequestCreationException;

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
		super(ident1, ident2);
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
		super(ident);
		filter = f;
	}
	
	public Filter getFilter() {
		return filter;
	}
}
 
