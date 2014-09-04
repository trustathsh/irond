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
 * This file is part of irond, version 0.5.1, implemented by the Trust@HsH
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
package de.hshannover.f4.trust.iron.mapserver.provider;


import java.io.IOException;

import de.hshannover.f4.trust.iron.mapserver.exceptions.ProviderInitializationException;
import de.hshannover.f4.trust.iron.mapserver.utils.NullCheck;

/**
 * Implementation of the BasicAuthProvider.
 *
 *
 * @author tr
 *
 * aw: Did some restructuring... Properties file gets read only once now.
 *     Had some bug with multiple authenticate() calls. Further, changed the
 *     interface to what I had in mind, moving the Base64 stuff upwards.
 *     So we are not dependent on apache stuff down here. Throw a
 *     {@link ProviderInitializationException} if something goes wrong.
 */
public class BasicAuthProviderPropImpl implements BasicAuthProvider {

	private PropertiesReaderWriter mProperties;

	public BasicAuthProviderPropImpl(ServerConfigurationProvider serverConf)
										throws ProviderInitializationException{
		loadPropertiesFile(serverConf);
	}

	@Override
	public boolean verify(String username, String password) {
		String passFile = mProperties.getProperty(username);
		return password.equals(passFile);
	}

	private void loadPropertiesFile(ServerConfigurationProvider serverConf)
										throws ProviderInitializationException {
		NullCheck.check(serverConf, "serverConf is null");
		try {
			String fileName = serverConf.getBasicAuthenticationPropFileName();
			if (fileName == null) {
				throw new ProviderInitializationException(
						"basic auth user/pass file not given");
			}
			mProperties = new PropertiesReaderWriter(fileName, true);
		} catch (IOException e) {
			throw new ProviderInitializationException(e.getMessage());
		}
	}
}
