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
 * This file is part of irond, version 0.4.2, implemented by the Trust@HsH
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
package de.fhhannover.inform.iron.mapserver.provider;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import de.fhhannover.inform.iron.mapserver.communication.ClientIdentifier;
import de.fhhannover.inform.iron.mapserver.exceptions.ProviderInitializationException;
import de.fhhannover.inform.iron.mapserver.exceptions.SystemErrorException;

/**
 * Provide roles for users from a properties file.
 *
 * @author aw
 */
public class RoleMapperProviderPropImpl implements RoleMapperProvider {

	private PropertiesReaderWriter mProperties;

	public RoleMapperProviderPropImpl(ServerConfigurationProvider mServerConf) throws ProviderInitializationException {
		try {
			mProperties = new PropertiesReaderWriter(mServerConf.getRoleMapperParams(), true);
		} catch (IOException e) {
			throw new ProviderInitializationException(e.getMessage());
		}
	}

	@Override
	public List<String> getRolesOf(ClientIdentifier clientId) {
		String clString = clientId.getUserameOrCertFingerPrint();

		if (clString.contains(" ")) {
			throw new SystemErrorException("Space found in clStr:" + clString);
		}


		String groups = mProperties.getProperty(clString);

		if (groups == null) {
			return new ArrayList<String>();
		}

		return Collections.unmodifiableList(Arrays.asList(groups.split(",")));
	}
}
