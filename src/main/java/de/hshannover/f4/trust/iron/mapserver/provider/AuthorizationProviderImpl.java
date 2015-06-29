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
 * This file is part of irond, version 0.5.4, implemented by the Trust@HsH
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


import java.io.IOException;

import de.hshannover.f4.trust.iron.mapserver.communication.ClientIdentifier;
import de.hshannover.f4.trust.iron.mapserver.exceptions.ProviderInitializationException;
import de.hshannover.f4.trust.iron.mapserver.utils.NullCheck;

/**
 * Very simple implementation of the {@link AuthorizationProvider} interface
 * using yet another properties file.<br/><br/>
 *
 * <b>Note:<br/>If a client is not found in the properties
 * file, it is assumed to be a read-write MAPC.</b>
 *
 * @author aw
 *
 */
public class AuthorizationProviderImpl implements AuthorizationProvider {

	/**
	 * The properties stuff, use {@link PropertiesReaderWriter} in order
	 * to take advantage of the automatic creation of the file.
	 */
	private PropertiesReaderWriter mProperties;

	public AuthorizationProviderImpl(ServerConfigurationProvider conf)
										throws ProviderInitializationException {
		loadPropertiesFile(conf);
	}

	/* (non-Javadoc)
	 * @see de.hshannover.f4.trust.iron.mapserver.provider.AuthorizationProvider#isWriteAllowed(de.hshannover.f4.trust.iron.mapserver.communication.ClientIdentifier)
	 */
	@Override
	public boolean isWriteAllowed(ClientIdentifier clientId) {
		String str = mProperties.getProperty(clientId.getUsername());
		boolean ret;

		// not found means the client is a read-write MAPC
		if (str == null) {
			ret = true;
		} else {
			if (str.equalsIgnoreCase("rw")) {
				// if found and set to rw, allow read write
				ret = true;
			} else if (str.equalsIgnoreCase("ro")) {
				ret = false;
			} else {
				// if it's set to something unknown, do not allow write access
				ret = false;
			}
		}
		return ret;
	}

	private void loadPropertiesFile(ServerConfigurationProvider serverConf)
											throws ProviderInitializationException {
		NullCheck.check(serverConf, "serverConf is null");
		try {
			String fileName = serverConf.getAuthorizationPropFileName();
			if (fileName == null) {
				// that's actually not too bad, in this case we'd allow everyone
				// read-write access. But just expect one to be given.
				throw new ProviderInitializationException(
						"authorization filename not given");
			}
			mProperties = new PropertiesReaderWriter(fileName, true);
		} catch (IOException e) {
			throw new ProviderInitializationException(e.getMessage());
		}
	}

}
