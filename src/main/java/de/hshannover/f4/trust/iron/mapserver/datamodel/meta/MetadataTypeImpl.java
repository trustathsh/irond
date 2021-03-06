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
 * This file is part of irond, version 0.5.8, implemented by the Trust@HsH
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
package de.hshannover.f4.trust.iron.mapserver.datamodel.meta;


import de.hshannover.f4.trust.iron.mapserver.utils.NullCheck;

/**
 * A simple class that represents different {@link MetadataType}s.
 * Use it when comparing if {@link Metadata} is of the same type.
 *
 * {@link Metadata} should have a typestring of urn:type
 * where urn might be http://example.com and type the Metadatatype
 *
 * @author aw
 * @version 0.1
 */
public class MetadataTypeImpl implements MetadataType {

	private final String mTypestring;
	private final MetaCardinalityType mCardinality;

	public MetadataTypeImpl(String typestring, MetaCardinalityType card) {
		NullCheck.check(typestring, "typestring is null");
		NullCheck.check(card, "card is null");
		mTypestring = typestring;
		mCardinality = card;
	}

	@Override
	public String getTypeString() {
		return mTypestring;
	}

	@Override
	public MetaCardinalityType getCardinality() {
		return mCardinality;
	}

	@Override
	public boolean equals(Object o) {
		MetadataType ot;
		if (o == null) {
			return false;
		}

		if (this == o) {
			return true;
		}

		if (!(o instanceof MetadataType)) {
			return false;
		}

		ot = (MetadataType) o;
		return ot.getTypeString().equals(getTypeString());
	}
}
