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
 * This file is part of irond, version 0.5.7, implemented by the Trust@HsH
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
package de.hshannover.f4.trust.iron.mapserver.datamodel;


import javax.xml.transform.stream.StreamSource;

import de.hshannover.f4.trust.iron.mapserver.provider.DataModelServerConfigurationProvider;

public class DummyDataModelConf implements DataModelServerConfigurationProvider {

	@Override
	public boolean getIdentityTypeIsCaseSensitive(String identityType) {
		return true;
	}

	@Override
	public boolean getAdministrativeDomainIsCaseSensitive() {
		return true;
	}

	@Override
	public boolean getPurgePublisherIsRestricted() {
		return false;
	}

	public static DataModelServerConfigurationProvider getDummyConf() {
		return new DummyDataModelConf();
	}

	@Override
	public int getDefaultMaxPollResultSize() {
		return 1000000;
	}

	@Override
	public int getDefaultMaxSearchResultSize() {
		// TODO Auto-generated method stub
		return 1000000;
	}

	@Override
	public boolean getStrictExtendedIdentity() {
		return false;
	}

	@Override
	public boolean getStrictDistinguishedName() {
		return true;
	}

	@Override
	public boolean getXmlValidatationMetadata() {
		return false;
	}

	@Override
	public boolean getXmlValidationMetadataLockDownMode() {
		return false;
	}

	@Override
	public boolean getXmlValidationExtendedIdentityLockDownMode() {
		return false;
	}

	@Override
	public boolean getXmlValidationExtendedIdentity() {
		return false;
	}

	@Override
	public StreamSource getExtendedIdentitySchema(String uri) {
		return null;
	}

	@Override
	public StreamSource getMetadataSchema(String uri) {
		return null;
	}

	@Override
	public boolean isSanityChecksEnabled() {
		// we are running tests, so this should be fine
		return true;
	}

	@Override
	public boolean isRootLinkEnabled() {
		return false;
	}

}
