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
package de.hshannover.f4.trust.iron.mapserver.datamodel.meta;


/**
 * This class contains an array of the standard metadata types with their
 * associated cardinality values. Used to initialize the mappings.
 *
 * TODO: Should find a place to put all the fixed things concerning the IF-MAP
 *       specification.
 *
 * @author aw
 *
 */
class StandardMetadata {

	private static final String IFMAP_METADATA_HREF =
		"http://www.trustedcomputinggroup.org/2010/IFMAP-METADATA/2";

	static String[][] STANDARD_METADATA =
	{
		{IFMAP_METADATA_HREF, "access-request-device", "singleValue"},
		{IFMAP_METADATA_HREF, "access-request-ip", "singleValue"},
		{IFMAP_METADATA_HREF, "access-request-mac", "singleValue"},
		{IFMAP_METADATA_HREF, "authenticated-as", "singleValue"},
		{IFMAP_METADATA_HREF, "authenticated-by", "singleValue"},
		{IFMAP_METADATA_HREF, "capability", "multiValue"},
		{IFMAP_METADATA_HREF, "device-attribute", "multiValue"},
		{IFMAP_METADATA_HREF, "device-characteristic", "multiValue"},
		{IFMAP_METADATA_HREF, "device-ip", "singleValue"},
		{IFMAP_METADATA_HREF, "discovered-by", "singleValue"},
		{IFMAP_METADATA_HREF, "enforcement-report", "multiValue"},
		{IFMAP_METADATA_HREF, "event", "multiValue"},
		{IFMAP_METADATA_HREF, "ip-mac", "multiValue"},
		{IFMAP_METADATA_HREF, "layer2-information", "multiValue"},
		{IFMAP_METADATA_HREF, "location", "multiValue"},
		{IFMAP_METADATA_HREF, "request-for-investigation", "multiValue"},
		{IFMAP_METADATA_HREF, "role", "multiValue"},
		{IFMAP_METADATA_HREF, "wlan-information", "singleValue"},
		{IFMAP_METADATA_HREF, "unexpected-behavior", "multiValue"}
	};

}
