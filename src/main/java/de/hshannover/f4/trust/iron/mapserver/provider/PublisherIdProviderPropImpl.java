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
package de.hshannover.f4.trust.iron.mapserver.provider;


import de.hshannover.f4.trust.iron.mapserver.communication.ClientIdentifier;
import de.hshannover.f4.trust.iron.mapserver.exceptions.ProviderInitializationException;
import de.hshannover.f4.trust.iron.mapserver.exceptions.StorePublisherIdException;
import de.hshannover.f4.trust.iron.mapserver.utils.NullCheck;
import java.io.IOException;
import java.util.List;

/**
 * Provides IF-MAP 2.1 compliant publisher ids
 *
 * @author aw
 * @author jk
 *
 */
public class PublisherIdProviderPropImpl implements PublisherIdProvider {

	private PropertiesReaderWriter mProperties;

	public PublisherIdProviderPropImpl(ServerConfigurationProvider serverConfig)
		throws ProviderInitializationException {
		NullCheck.check(serverConfig, "serverConfig is null");
		String fileName = serverConfig.getPublisherIdMapFileName();
		if (fileName == null) {
			throw new ProviderInitializationException("publisher-id mapping file null");
		}

		try {
			mProperties = new PropertiesReaderWriter(fileName, true);
		} catch (IOException e) {
			throw new ProviderInitializationException(e.getMessage());
		}
	}

	@Override
	public String getPublisherIdFor(ClientIdentifier clId) {
		NullCheck.check(clId, "clientIdentifier is null");
		return mProperties.getProperty(clId.getReadablePseudoIdentifier());
	}

	@Override
	public void storePublisherIdFor(ClientIdentifier clId,
			String publisherId) throws StorePublisherIdException {
		NullCheck.check(clId, "clientId is null");
		NullCheck.check(publisherId, "publisherId is null");
		try {
			mProperties.storeProperty(clId.getReadablePseudoIdentifier(), publisherId);
		} catch (IOException e) {
			throw new StorePublisherIdException(e.getMessage());
		}
	}

	@Override
	public List<String> getAllPublisherIds() {
		return mProperties.getAllValues();
	}
}
