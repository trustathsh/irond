package de.fhhannover.inform.iron.mapserver.datamodel.meta;

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
