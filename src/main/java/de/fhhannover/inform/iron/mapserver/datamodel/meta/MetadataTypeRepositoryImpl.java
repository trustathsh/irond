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

import java.util.HashMap;

import org.apache.log4j.Logger;

import de.fhhannover.inform.iron.mapserver.exceptions.InvalidMetadataException;
import de.fhhannover.inform.iron.mapserver.provider.LoggingProvider;
import de.fhhannover.inform.iron.mapserver.utils.NullCheck;

/**
 *
 * @since 0.3.0
 * @author aw
 */
public class MetadataTypeRepositoryImpl implements MetadataTypeRepository {
	
	private static final String sName = "MetadataTypeRepository";

	private static final Logger sLogger = LoggingProvider.getTheLogger();
	private final HashMap<String, MetadataType> mTypes;
	
	private MetadataTypeRepositoryImpl() {
		mTypes = new HashMap<String, MetadataType>();
		initializeStandardMetadataCardinalities();
	}

	public static MetadataTypeRepository newInstance() {
		return new MetadataTypeRepositoryImpl();
	}

	/* (non-Javadoc)
	 * @see de.fhhannover.inform.iron.mapserver.datamodel.meta.MetadataTypeRepository#getTypeFor(java.lang.String, java.lang.String, de.fhhannover.inform.iron.mapserver.datamodel.meta.MetadataLifeTime)
	 */
	@Override
	public MetadataType getTypeFor(String ns, String name, MetaCardinalityType card)
			throws InvalidMetadataException {
		NullCheck.check(name, "name is null");
		NullCheck.check(ns, "ns is null");
		NullCheck.check(card, "lt is null");
	
		String key = key(name, ns);
		MetadataType type = mTypes.get(key);
	
		// new entry
		if (type == null) {
			type = new MetadataTypeImpl(key, card);
			mTypes.put(key, type);
			sLogger.trace(sName +": new MetadataType " + key + " - " + card);
		} else if (type.getCardinality() != card) {
			sLogger.warn(sName + ": Inconsisten cardinality for " + key
					+ ", known=" + type.getCardinality() + ", new=" + card);
			throw new InvalidMetadataException("Inconsisten cardinality for "
					+ key + ", known=" + type.getCardinality() + ", new=" + card);
		}
		return type;
	}


	/* (non-Javadoc)
	 * @see de.fhhannover.inform.iron.mapserver.datamodel.meta.MetadataTypeRepository#contains(java.lang.String, java.lang.String, de.fhhannover.inform.iron.mapserver.datamodel.meta.MetadataLifeTime)
	 */
	@Override
	public boolean contains(String ns, String name, MetaCardinalityType card) {
		NullCheck.check(name, "name is null");
		NullCheck.check(ns, "ns is null");
		NullCheck.check(card, "lt is null");
		MetadataType type = mTypes.get(key(name, ns));
		return type != null && type.getCardinality() == card;
	}

	/* (non-Javadoc)
	 * @see de.fhhannover.inform.iron.mapserver.datamodel.meta.MetadataTypeRepository#clear()
	 */
	@Override
	public void clear() {
		mTypes.clear();
	}
	
	/**
	 * For the set of standard metadata, the cardinalities are known.
	 * Create types for these when the class is first created.
	 * This way standard metadata cannot be publish with a inconsisten
	 * ifmap-cardinality value.
	 */
	private void initializeStandardMetadataCardinalities() {
		for (String[] desc : StandardMetadata.STANDARD_METADATA) {
			try {
				getTypeFor(desc[0], desc[1], MetaCardinalityType.valueOf(desc[2]));
			} catch (InvalidMetadataException e) {
				sLogger.error(sName + " UNEXPECTED: Could not create MetaType for"
						+ " standard metadata: " + e.getMessage());
			}
		}
	}
	
	private String key(String name, String ns) {
		return ns + ":" + name;
	}
}
