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
package de.hshannover.f4.trust.iron.mapserver.provider;


import java.util.List;
import java.util.Properties;

import de.hshannover.f4.trust.iron.mapserver.communication.ClientIdentifier;
import de.hshannover.f4.trust.iron.mapserver.exceptions.StorePublisherIdException;

/**
 * Provides functionality to map an identified MAPC to a PublisherId.
 *
 * A MAPC can be identified either by it's username used for basic authentication
 * or by it's distinguished name used in the certificate.
 * This interface provides a mapping between these identifiers and the
 * publisher-id.
 *
 *
 * @author aw
 *
 */
public interface PublisherIdProvider {

	/**
	 * Returns a PublisherId for the MAPC identified by the
	 * {@link ClientIdentifier}.
	 *
	 * The easiest implementation should be a {@link Properties} file mapping
	 * username/distinguished name to a PublisherId.
	 *
	 * @see ClientIdentifier
	 * @param clientId identifies the MAPC.
	 * @return a String representing the publisher-id or null if not found.
	 */
	public String getPublisherIdFor(ClientIdentifier clientId);

	/**
	 * Store a mapping of a {@link ClientIdentifier} to a publisher-id.
	 *
	 * @param clientId
	 * @param publisherId
	 * @throws StorePublisherIdException in case it could not be stored.
	 */
	public void storePublisherIdFor(ClientIdentifier clientId, String publisherId)
		throws StorePublisherIdException;

	/**
	 * @return a {@link List} containing all publisher-id's known to
	 * this instance.
	 */
	public List<String> getAllPublisherIds();

}
