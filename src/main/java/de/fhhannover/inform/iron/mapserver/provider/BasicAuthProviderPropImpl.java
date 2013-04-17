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
 * This file is part of irond, version 0.4.0, implemented by the Trust@FHH 
 * research group at the Fachhochschule Hannover.
 * 
 * irond is an an *experimental* IF-MAP 2.0 compliant MAP server written in
 * JAVA. irond supports both basic authentication and certificate-based 
 * authentication (using X.509 certificates) of MAP clients. irond is
 * maintained by the Trust@FHH group at the Fachhochschule Hannover, initial
 * developement was carried out during the ESUKOM research project.
 * %%
 * Copyright (C) 2010 - 2013 Trust@FHH
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

import de.fhhannover.inform.iron.mapserver.exceptions.ProviderInitializationException;
import de.fhhannover.inform.iron.mapserver.utils.NullCheck;

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
