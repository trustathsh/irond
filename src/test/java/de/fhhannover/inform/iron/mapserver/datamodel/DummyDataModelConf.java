package de.fhhannover.inform.iron.mapserver.datamodel;

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

import javax.xml.transform.stream.StreamSource;

import de.fhhannover.inform.iron.mapserver.provider.DataModelServerConfigurationProvider;

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
