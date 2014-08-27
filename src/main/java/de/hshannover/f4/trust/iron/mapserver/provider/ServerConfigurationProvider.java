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
package de.hshannover.f4.trust.iron.mapserver.provider;


import java.util.Collection;

import de.hshannover.f4.trust.iron.mapserver.communication.http.ActionProcessor;
import de.hshannover.f4.trust.iron.mapserver.communication.ifmap.EventProcessor;
import de.hshannover.f4.trust.iron.mapserver.contentauth.IfmapPepHandler.PdpType;
import de.hshannover.f4.trust.iron.mapserver.provider.RoleMapperProvider.RoleMapperType;


/**
 * Interface to get configuration information about the server.
 *
 * @author aw
 */
public interface ServerConfigurationProvider extends DataModelServerConfigurationProvider {

	/**
	 * @return the port the server should listen on for basic authentication.
	 */
	public int getBasicAuthPort();

	/**
	 * @return the port the server should listen on for certificate authentication.
	 */
	public int getCertAuthPort();

	/**
	 * @return path to the truststore
	 */
	public String getTrustStoreFileName();

	/**
	 * @return password of the truststore
	 */
	public String getTrustStorePasswort();


	/**
	 * @return path to the keystore
	 */
	public String getKeyStoreFileName();

	/**
	 * @return password of the keystore
	 */
	public String getKeyStorePasswort();


	/**
	 * @return information to initialize a {@link PublisherIdProvider}
	 */
	public String getPublisherIdMapFileName();

	/**
	 * @return information to initialize a {@link BasicAuthProvider}
	 */
	public String getBasicAuthenticationPropFileName();

	/**
	 * @return the number of forwarder threads to be used in the {@link EventProcessor}
	 */
	public int getEventProcessorForwardersCount();

	/**
	 * @return the number of worker threads to be used in the {@link EventProcessor}
	 */
	public int getEventProcessorWorkersCount();

	/**
	 * @return the number of forwarder threads to be used in the {@link ActionProcessor};
	 */
	public int getActionProcessorForwardersCount();

	/**
	 * @return the number of worker threads to be used in the {@link ActionProcessor};
	 */
	public int getActionProcessorWorkersCount();

	/**
	 * @return the timeout for a session if it has no active SSRC in mili seconds.
	 */
	public long getSessionTimeOutMilliSeconds();

	/**
	 * @return to initialize a {@link AuthorizationProvider}
	 */
	public String getAuthorizationPropFileName();

	/**
	 * @return true if raw requests should be logged
	 */
	public boolean isLogRaw();

	/**
	 * @return  true if SO_KEEPALIVE should be used
	 */
	public boolean getSocketKeepAlive();

	/**
	 * @return socket timeout in milliseconds (0 = infinite, default)
	 */
	public int getSocketTimeout();

	/**
	 * @return which error should be used to abort parsing.
	 */
	public boolean getXmlValidation();

	/**
	 * @return all schema files to be loaded to do XML validation
	 */
	public String[] getSchemaFileNames();

	/**
	 * @return {@link PepType} to be used.
	 */
	public PdpType getPdpType();

	/**
	 * @return if the used {@link PdpType} is {@link PdpType#local}, then
	 * a {@link String} to the policy file. If {@link PdpType#remote} a
	 * {@link String} describing the location of the remote PDP.
	 *
	 */
	public String getPdpParameters();

	/**
	 * @return whether we are doing a policy dry run.
	 */
	public boolean isPdpDryRun();

	/**
	 * @return should we log the raw XACML logs?
	 */
	public boolean isPdpDecisionRequestRawLog();

	/**
	 * @return should we log the raw XACML logs?
	 */
	public boolean isEnablePdpCache();

	/**
	 * @return the time in seconds decision requests results are to be
	 * cached.
	 */
	public int getPdpCacheTtl();

	/**
	 * @return the maximum number of cached entries in the PDP cache.
	 */
	public long getPdpCacheMaxEntries();

	/**
	 * This value corresponds to the number of parallel connections to an
	 * external PDP. However, it also corresponds to the number of parallel
	 * requests to an internal PDP, which might end up being really
	 * computationally intensive.
	 *
	 * @return the number of threads to use for PDP.
	 */
	public int getPdpThreads();

	/**
	 * @return a list of {@link String} instances indicating which top-level
	 *		   metadata attributes to include.
	 */
	public Collection<String> getPdpSelectedMetadataAttributes();

	/**
	 * @return a list of {@link String} instances indicating which top-level
	 *		   metadata attributes to include.
	 */
	public Collection<String> getPdpSelectedIdentifierAttributes();

	/**
	 * @return the type of {@link RoleMapperProvider} to be used
	 */
	public RoleMapperType getRoleMapperType();

	/**
	 * @return
	 */
	public String getRoleMapperParams();
}

