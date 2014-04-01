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
 * This file is part of irond, version 0.4.1, implemented by the Trust@FHH
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

import de.fhhannover.inform.iron.mapserver.exceptions.ProviderInitializationException;
import de.fhhannover.inform.iron.mapserver.utils.CollectionHelper;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import javax.xml.transform.stream.StreamSource;

/**
 * Implements the ServerConfigurationProvider, which reads the server-properties
 * 
 *
 * @author tr
 *
 */
public class ServerConfigurationProviderPropImpl implements ServerConfigurationProvider {
	
	private static final String PROP_PREFIX = "irond.";
	
	private static final String BASIC_AUTH_PORT_KEY = PROP_PREFIX + "comm.basicauth.port";
	private static final String BASIC_AUTH_PORT_DEFAULT = "8443";

	private static final String CERT_AUTH_PORT_KEY = PROP_PREFIX + "comm.certauth.port";
	private static final String CERT_AUTH_PORT_DEFAULT = "8444";

	private static final String BASIC_AUTH_USER_FILE_KEY = PROP_PREFIX + "auth.basic.users.file";
	private static final String BASIC_AUTH_USER_FILE_DEFAULT = "basicauthusers.properties";
	
	private static final String CERT_AUTH_KEYSTORE_FILE_KEY = PROP_PREFIX + "auth.cert.keystore.file";
	private static final String CERT_AUTH_KEYSTORE_FILE_DEFAULT = "";
	private static final String CERT_AUTH_KEYSTORE_PWD_KEY = PROP_PREFIX + "auth.cert.keystore.pass";
	private static final String CERT_AUTH_KEYSTORE_PWD_DEFAULT = "";
	
	private static final String CERT_AUTH_TRUSTSTORE_FILE_KEY = PROP_PREFIX + "auth.cert.truststore.file";
	private static final String CERT_AUTH_TRUSTSTORE_FILE_DEFAULT = "";
	private static final String CERT_AUTH_TRUSTSTORE_PWD_KEY = PROP_PREFIX + "auth.cert.truststore.pass";
	private static final String CERT_AUTH_TRUSTSTORE_PWD_DEFAULT = "";

	private static final String AUTHORIZATION_FILE_KEY = PROP_PREFIX + "ifmap.authorization.file";
	private static final String AUTHORIZATION_FILE_DEFAULT = "authorization.properties";
	
	private static final String IFMAP_SESSION_TIMEOUT_SEC_KEY = PROP_PREFIX + "ifmap.session.timeout";
	private static final String IFMAP_SESSION_TIMEOUT_SEC_DEFAULT = "180";
	
	private static final String IFMAP_PUBLISHER_PROP_KEY = PROP_PREFIX + "ifmap.publishers.file";
	private static final String IFMAP_PUBLISHER_PROP_DEFAULT = "publisher.properties";
	
	private static final String IFMAP_DEF_MAX_POLL_RES_SIZE_KEY = PROP_PREFIX + "ifmap.default.maxpollresultsize";
	private static final String IFMAP_DEF_MAX_POLL_RES_SIZE_DEFAULT = "5000000";

	private static final String IFMAP_DEF_MAX_SEARCH_RES_SIZE_KEY = PROP_PREFIX + "ifmap.default.searchresultsize";
	private static final String IFMAP_DEF_MAX_SEARCH_RES_SIZE_DEFAULT = "100000";

	private static final String IFMAP_ADM_DOMAIN_CASESENSITIVE_KEY = PROP_PREFIX + "ifmap.casesensitive.administrativedomain";
	private static final String IFMAP_ADM_DOMAIN_CASESENSITIVE_DEFAUTLT = "true";	

	private static final String IFMAP_RESTRICT_PUREGE_PUBLISHER_KEY = PROP_PREFIX + "ifmap.restrict.purgepublisher";
	private static final String IFMAP_RESTRICT_PUREGE_PUBLISHER_DEFAULT = "true";

	private static final String IFMAP_IDENTITY_CASESENSITIVE_PREFIX_KEY = PROP_PREFIX + "ifmap.casesensitive.";
	private static final String IFMAP_IDENTITY_CASESENSITIVE_DEFAULT = "true";

	private static final String IFMAP_ENABLE_SANITY_CHECKS = PROP_PREFIX + "ifmap.default.sanitychecks";
	private static final String IFMAP_ENABLE_SANITY_CHECKS_DEFAULT = "true";
	
	private static final String PROC_ACTION_FORWARDERS_KEY = PROP_PREFIX + "proc.action.forwarders";
	private static final String PROC_ACTION_FORWARDERS_DEFAULT = "1";
	
	private static final String PROC_ACTION_WORKERS_KEY = PROP_PREFIX + "proc.action.workers";
	private static final String PROC_ACTION_WORKERS_DEFAULT = "1";
	
	private static final String PROC_EVENT_FORWARDERS_KEY = PROP_PREFIX + "proc.event.forwarders";
	private static final String PROC_EVENT_FORWARDERS_DEFAULT = "2";
	
	private static final String PROC_EVENT_WORKERS_KEY = PROP_PREFIX + "proc.event.workers";
	private static final String PROC_EVENT_WORKERS_DEFAULT = "4";
	
	private static final String RAWREQUEST_LOGGIN_KEY = PROP_PREFIX + "comm.rawlog";
	private static final String RAWREQUEST_LOGGING_DEFAULT = "false";
	
	private static final String XML_VALIDATION_SEVERITY_KEY = PROP_PREFIX + "xml.validate";
	private static final String XML_VALIDATION_SEVERITY_DEFAULT = "true";

	private static final String XML_SCHEMA_KEY = PROP_PREFIX + "xml.schema.";
	private static final String XML_SCHEMA_DEFAULT_VALUE = "schema/soap12.xsd";

	private static final String XML_EXTENDED_IDENTIFIER_URI_KEY = PROP_PREFIX + "ifmap.strict.identity.extended.schema.uri.";
	private static final String XML_EXTENDED_IDENTIFIER_FILE_KEY = PROP_PREFIX + "ifmap.strict.identity.extended.schema.file.";
	
	private static final String XML_METADATA_SCHEMA_URI_KEY = PROP_PREFIX + "ifmap.strict.metadata.schema.uri.";
	private static final String XML_METADATA_SCHEMA_FILE_KEY = PROP_PREFIX + "ifmap.strict.metadata.schema.file.";
	
	private static final String IFMAP_IDENTITY_DISTINGUISHED_NAMES_STRICT_KEY = PROP_PREFIX + "ifmap.strict.distinguished-name.rewrite";
	private static final String IFMAP_IDENTITY_DISTINGUISHED_NAMES_STRICT_DEFAULT = "true";

	private static final String IFMAP_IDENTITY_EXTENDED_STRICT_KEY = PROP_PREFIX + "ifmap.strict.identity.extended.rewrite";
	private static final String IFMAP_IDENTITY_EXTENDED_STRICT_DEFAULT = "false";

	private static final String IFMAP_IDENTITY_EXTENDED_VALIDATE_KEY = PROP_PREFIX + "ifmap.strict.identity.extended.schema.validate";
	private static final String IFMAP_IDENTITY_EXTENDED_VALIDATE_DEFAULT = "false";

	private static final String COMM_SOCKET_KEEPALIVE_KEY = PROP_PREFIX + "comm.socket.keep-alive";
	private static final String COMM_SOCKET_KEEPALIVE_DEFAULT = "false";
	
	private static final String COMM_SOCKET_TIMEOUT_KEY = PROP_PREFIX + "comm.socket.timeout";
	private static final String COMM_SOCKET_TIMEOUT_DEFAULT = "0";
	
	private static final String XML_VALIDATION_METADATA_KEY = PROP_PREFIX + "ifmap.strict.metadata.schema.validate";
	private static final String XML_VALIDATION_METADATA_DEFAULT = "false";

	private static final String XML_VALIDATION_METADATA_LOCKDOWN_KEY = PROP_PREFIX + "ifmap.strict.metadata.lockdown";
	private static final String XML_VALIDATION_METADATA_LOCKDOWN_DEFAULT = "false";

	private static final String XML_VALIDATION_IDENTITY_EXTENDED_LOCKDOWN_KEY = PROP_PREFIX + "ifmap.strict.identity.extended.lockdown";
	private static final String XML_VALIDATION_IDENTITY_EXTENDED_LOCKDOWN_DEFAULT = "false";

	private PropertiesReaderWriter mProperties;
	
	public ServerConfigurationProviderPropImpl(String filename) throws ProviderInitializationException{
		try {
			mProperties = new PropertiesReaderWriter(filename, true);
			String exMsg = "";
			
			String val = getOrSetDefaultAndGet(CERT_AUTH_KEYSTORE_FILE_KEY,
					CERT_AUTH_KEYSTORE_FILE_DEFAULT);
			if (val.length() == 0)
				exMsg += "\n" + CERT_AUTH_KEYSTORE_FILE_KEY;
				
			val = getOrSetDefaultAndGet(CERT_AUTH_KEYSTORE_PWD_KEY,
					CERT_AUTH_KEYSTORE_PWD_DEFAULT);
			if (val.length() == 0)
				exMsg += "\n" + CERT_AUTH_KEYSTORE_PWD_KEY;
			
			val = getOrSetDefaultAndGet(CERT_AUTH_TRUSTSTORE_FILE_KEY,
					CERT_AUTH_TRUSTSTORE_FILE_DEFAULT);
			if (val.length() == 0)
				exMsg += "\n" + CERT_AUTH_TRUSTSTORE_FILE_KEY;
			
			val = getOrSetDefaultAndGet(CERT_AUTH_TRUSTSTORE_PWD_KEY,
					CERT_AUTH_TRUSTSTORE_PWD_DEFAULT);
			if (val.length() == 0)
				exMsg += "\n" + CERT_AUTH_TRUSTSTORE_PWD_KEY;
			
			if (exMsg.length() != 0)
				throw new ProviderInitializationException(
						"Please set the following entries in " + filename + ":" +
								exMsg);
			
		} catch (IOException e) {
			throw new ProviderInitializationException(e.getMessage());
		}
	}


	
	private String getOrSetDefaultAndGet(String key, String defaultVal) {
		String val = mProperties.getProperty(key);
		if (val == null) {
			try {
				val = defaultVal;
				mProperties.storeProperty(key, val);
			} catch (IOException e) {
				// it's an error :-( but here we can simply return the default
				// value.
			}
		}
		return val;
	}

	@Override
	public String getBasicAuthenticationPropFileName() {
		return getOrSetDefaultAndGet(BASIC_AUTH_USER_FILE_KEY, BASIC_AUTH_USER_FILE_DEFAULT);
	}

	@Override
	public int getBasicAuthPort() {
		String val = getOrSetDefaultAndGet(BASIC_AUTH_PORT_KEY, BASIC_AUTH_PORT_DEFAULT);
		// this throws an exception if it goes wrong, but i'm fine with that
		return Integer.parseInt(val);
	}

	@Override
	public int getCertAuthPort() {
		String val = getOrSetDefaultAndGet(CERT_AUTH_PORT_KEY, CERT_AUTH_PORT_DEFAULT);
		// this throws an exception if it goes wrong, but i'm fine with that
		return Integer.parseInt(val);
	}

	@Override
	public int getDefaultMaxPollResultSize() {
		String val = getOrSetDefaultAndGet(IFMAP_DEF_MAX_POLL_RES_SIZE_KEY,
				IFMAP_DEF_MAX_POLL_RES_SIZE_DEFAULT);
		// this throws an exception if it goes wrong, but i'm fine with that
		return Integer.parseInt(val);
	}

	@Override
	public int getDefaultMaxSearchResultSize() {
		String val = getOrSetDefaultAndGet(IFMAP_DEF_MAX_SEARCH_RES_SIZE_KEY, 
				IFMAP_DEF_MAX_SEARCH_RES_SIZE_DEFAULT);
		// this throws an exception if it goes wrong, but i'm fine with that
		return Integer.parseInt(val);
	}

	@Override
	public int getEventProcessorForwardersCount() {
		String val = getOrSetDefaultAndGet(PROC_EVENT_FORWARDERS_KEY,
				PROC_EVENT_FORWARDERS_DEFAULT);
		// this throws an exception if it goes wrong, but i'm fine with that
		return Integer.parseInt(val);
	}

	@Override
	public int getEventProcessorWorkersCount() {
		String val = getOrSetDefaultAndGet(PROC_EVENT_WORKERS_KEY,
				PROC_EVENT_WORKERS_DEFAULT);
		// this throws an exception if it goes wrong, but i'm fine with that
		return Integer.parseInt(val);
	}

	@Override
	public int getActionProcessorForwardersCount() {
		String val = getOrSetDefaultAndGet(PROC_ACTION_FORWARDERS_KEY,
				PROC_ACTION_FORWARDERS_DEFAULT);
		// this throws an exception if it goes wrong, but i'm fine with that
		return Integer.parseInt(val);
	}

	@Override
	public int getActionProcessorWorkersCount() {
		String val = getOrSetDefaultAndGet(PROC_ACTION_WORKERS_KEY,
				PROC_ACTION_WORKERS_DEFAULT);
		// this throws an exception if it goes wrong, but i'm fine with that
		return Integer.parseInt(val);
	}

	@Override
	public String getKeyStoreFileName() {
		return getOrSetDefaultAndGet(CERT_AUTH_KEYSTORE_FILE_KEY,
				CERT_AUTH_KEYSTORE_FILE_DEFAULT);
	}

	@Override
	public String getKeyStorePasswort() {
		return getOrSetDefaultAndGet(CERT_AUTH_KEYSTORE_PWD_KEY,
				CERT_AUTH_KEYSTORE_PWD_DEFAULT);
	}


	@Override
	public String getTrustStoreFileName() {
		return getOrSetDefaultAndGet(CERT_AUTH_TRUSTSTORE_FILE_KEY,
				CERT_AUTH_TRUSTSTORE_FILE_DEFAULT);
	}

	@Override
	public String getTrustStorePasswort() {
		return getOrSetDefaultAndGet(CERT_AUTH_TRUSTSTORE_PWD_KEY,
				CERT_AUTH_TRUSTSTORE_PWD_DEFAULT);
	}

	@Override
	public long getSessionTimeOutMilliSeconds() {
		String val = getOrSetDefaultAndGet(IFMAP_SESSION_TIMEOUT_SEC_KEY,
				IFMAP_SESSION_TIMEOUT_SEC_DEFAULT);
		// this throws an exception if it goes wrong, but i'm fine with that
		return Integer.parseInt(val) * 1000;
	}

	@Override
	public String getPublisherIdMapFileName() {
		return getOrSetDefaultAndGet(IFMAP_PUBLISHER_PROP_KEY,
				IFMAP_PUBLISHER_PROP_DEFAULT);
	}



	@Override
	public boolean getIdentityTypeIsCaseSensitive(String identityType) {
		return new Boolean(getOrSetDefaultAndGet(
				IFMAP_IDENTITY_CASESENSITIVE_PREFIX_KEY + identityType,
				IFMAP_IDENTITY_CASESENSITIVE_DEFAULT));
	}

	@Override
	public boolean getAdministrativeDomainIsCaseSensitive() {
		return new Boolean(getOrSetDefaultAndGet(IFMAP_ADM_DOMAIN_CASESENSITIVE_KEY,
				IFMAP_ADM_DOMAIN_CASESENSITIVE_DEFAUTLT));
	}



	@Override
	public boolean getPurgePublisherIsRestricted() {
		return new Boolean(getOrSetDefaultAndGet(IFMAP_RESTRICT_PUREGE_PUBLISHER_KEY,
				IFMAP_RESTRICT_PUREGE_PUBLISHER_DEFAULT));
	}

	@Override
	public String getAuthorizationPropFileName() {
		return getOrSetDefaultAndGet(AUTHORIZATION_FILE_KEY,
				AUTHORIZATION_FILE_DEFAULT);
	}

	@Override 
	public boolean isSanityChecksEnabled() {
		return new Boolean(getOrSetDefaultAndGet(IFMAP_ENABLE_SANITY_CHECKS, 
				IFMAP_ENABLE_SANITY_CHECKS_DEFAULT));
	}
	
	@Override
	public boolean isLogRaw() {
		return new Boolean(getOrSetDefaultAndGet(RAWREQUEST_LOGGIN_KEY,
				RAWREQUEST_LOGGING_DEFAULT));
	}
	
	@Override
	public boolean getStrictDistinguishedName() {
		return new Boolean(getOrSetDefaultAndGet(IFMAP_IDENTITY_DISTINGUISHED_NAMES_STRICT_KEY,
				IFMAP_IDENTITY_DISTINGUISHED_NAMES_STRICT_DEFAULT));
	}

	@Override
	public boolean getStrictExtendedIdentity() {
		return new Boolean(getOrSetDefaultAndGet(IFMAP_IDENTITY_EXTENDED_STRICT_KEY,
				IFMAP_IDENTITY_EXTENDED_STRICT_DEFAULT));
	}
	
	@Override
	public boolean getSocketKeepAlive() {
		return new Boolean(getOrSetDefaultAndGet(COMM_SOCKET_KEEPALIVE_KEY,
				COMM_SOCKET_KEEPALIVE_DEFAULT));
	}
	
	@Override
	public int getSocketTimeout() {
		return new Integer(getOrSetDefaultAndGet(COMM_SOCKET_TIMEOUT_KEY,
				COMM_SOCKET_TIMEOUT_DEFAULT));
	}
	
	@Override
	public boolean getXmlValidation() {
		return new Boolean(getOrSetDefaultAndGet(XML_VALIDATION_SEVERITY_KEY,
				XML_VALIDATION_SEVERITY_DEFAULT));
	}

	@Override
	public boolean getXmlValidatationMetadata() {
		return new Boolean(getOrSetDefaultAndGet(XML_VALIDATION_METADATA_KEY,
				XML_VALIDATION_METADATA_DEFAULT));
	}
	
	@Override
	public boolean getXmlValidationExtendedIdentity() {
		return new Boolean(getOrSetDefaultAndGet(IFMAP_IDENTITY_EXTENDED_VALIDATE_KEY,
				IFMAP_IDENTITY_EXTENDED_VALIDATE_DEFAULT));
	}
	@Override
	public boolean getXmlValidationMetadataLockDownMode() {
		return new Boolean(getOrSetDefaultAndGet(XML_VALIDATION_METADATA_LOCKDOWN_KEY,
				XML_VALIDATION_METADATA_LOCKDOWN_DEFAULT));
	}

	@Override
	public boolean getXmlValidationExtendedIdentityLockDownMode() {
		return new Boolean(getOrSetDefaultAndGet(XML_VALIDATION_IDENTITY_EXTENDED_LOCKDOWN_KEY,
				XML_VALIDATION_IDENTITY_EXTENDED_LOCKDOWN_DEFAULT));
	}

	private Map<String, String> getMetadataSchemaFileNames() {
		Map<String, String> schema = CollectionHelper.provideMapFor(
				String.class, String.class);
		for (String key : mProperties.getAllKeys())
			if (key.startsWith(XML_METADATA_SCHEMA_URI_KEY)) {
				int nr = Integer.parseInt(key.substring(
						key.lastIndexOf(".") + 1));
				schema.put(mProperties.getProperty(
						XML_METADATA_SCHEMA_URI_KEY +
						nr), mProperties.getProperty(
						XML_METADATA_SCHEMA_FILE_KEY +
						nr));
			}
		return schema;
	}

	private Map<String, String> getExtendedIdentifierSchemaFileNames() {
		Map<String, String> schema = CollectionHelper.provideMapFor(
				 String.class, String.class);
		for (String key : mProperties.getAllKeys()) {
			if (key.startsWith(XML_EXTENDED_IDENTIFIER_URI_KEY)) {
				int nr = Integer.parseInt(key.substring(
						key.lastIndexOf(".") + 1));
				schema.put(mProperties.getProperty(
						XML_EXTENDED_IDENTIFIER_URI_KEY +
						nr), mProperties.getProperty(
						XML_EXTENDED_IDENTIFIER_FILE_KEY +
						nr));
			}
		}
		return schema;
	}
	
	@Override
	public StreamSource getExtendedIdentitySchema(String uri) {
		if (getExtendedIdentifierSchemaFileNames().containsKey(uri))
			return new StreamSource(new File(
					getExtendedIdentifierSchemaFileNames().get(uri)));
		else
			return null;
	}
	

	@Override
	public StreamSource getMetadataSchema(String uri) {
		if (getMetadataSchemaFileNames().containsKey(uri))
			return new StreamSource(new File(
					getMetadataSchemaFileNames().get(uri)));
		else
			return null;
	}
	
	@Override
	public String[] getSchemaFileNames() {
		List<String> fileNames = CollectionHelper.provideListFor(String.class);
		
		for (String key : mProperties.getAllKeys())
			if (key.startsWith(XML_SCHEMA_KEY))
				fileNames.add(mProperties.getProperty(key));
		
		if (fileNames.size() == 0) {
			try {
				mProperties.storeProperty(XML_SCHEMA_KEY + "0", XML_SCHEMA_DEFAULT_VALUE);
			} catch (IOException e) {
				// If this happens, we have other problems to worry about
				// anyway I'm afraid, so we simply continue with only the
				// default base schema. If this doesn't work we are screwed
				// anyway :)
			}
			
			fileNames.add(XML_SCHEMA_DEFAULT_VALUE);
		}
		
		return fileNames.toArray(new String[fileNames.size()]);
	}
}
