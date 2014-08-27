package de.fhhannover.inform.iron.mapserver.provider;

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
 * This file is part of irond, version 0.4.2, implemented by the Trust@FHH
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

import java.io.IOException;

import de.fhhannover.inform.iron.mapserver.communication.ClientIdentifier;
import de.fhhannover.inform.iron.mapserver.exceptions.ProviderInitializationException;
import de.fhhannover.inform.iron.mapserver.utils.NullCheck;

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
	 * @see de.fhhannover.inform.iron.mapserver.provider.AuthorizationProvider#isWriteAllowed(de.fhhannover.inform.iron.mapserver.communication.ClientIdentifier)
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
