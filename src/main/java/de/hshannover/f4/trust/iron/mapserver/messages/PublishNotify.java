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


import java.util.List;

import de.hshannover.f4.trust.iron.mapserver.datamodel.identifiers.Identifier;
import de.hshannover.f4.trust.iron.mapserver.datamodel.meta.Metadata;
import de.hshannover.f4.trust.iron.mapserver.datamodel.meta.MetadataLifeTime;
import de.hshannover.f4.trust.iron.mapserver.exceptions.RequestCreationException;

/**
 * PublishNotify is send in order to send Notifies. It's exactly like
 * a PublishUpdate
 *
 * @version 0.1
 * @author aw
 */

 /*
 * created: 30.04.10
 * changes:
 *  30.04.10 aw - implement some stuff
 *
 */
public class PublishNotify extends PublishUpdate {

	/**
	 * Construct a PublishNotify exactly how {@link PublishUpdate} is
	 * constructed.
	 *
	 * @param i1 Identifier 1
	 * @param i2 Identifier 2
	 * @param ml List of metadata
	 * @param timeStamp timeStamp of this request
	 * @throws RequestCreationException
	 */
	public PublishNotify(Identifier i1, Identifier i2, List<Metadata> ml)
			throws RequestCreationException {
		super(i1, i2, ml, MetadataLifeTime.session, PublishRequestType.NOTIFY);
	}
	public PublishNotify(Identifier i1, List<Metadata> ml)
			throws RequestCreationException {
		this(i1, null, ml);
	}
}

