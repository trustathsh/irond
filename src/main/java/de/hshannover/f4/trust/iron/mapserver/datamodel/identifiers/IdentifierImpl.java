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
 * This file is part of irond, version 0.5.0, implemented by the Trust@HsH
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
package de.hshannover.f4.trust.iron.mapserver.datamodel.identifiers;


import org.w3c.dom.Document;

import de.hshannover.f4.trust.iron.mapserver.datamodel.DataModelService;
import de.hshannover.f4.trust.iron.mapserver.datamodel.SimpleSearchAbleImpl;
import de.hshannover.f4.trust.iron.mapserver.exceptions.SystemErrorException;
import de.hshannover.f4.trust.iron.mapserver.provider.DataModelServerConfigurationProvider;
import de.hshannover.f4.trust.iron.mapserver.utils.NullCheck;

/**
 * @since 0.3.0
 * @author aw
 */
abstract class IdentifierImpl extends SimpleSearchAbleImpl implements Identifier {

	private final String mIdentifierType;

	private String mPrintableString;
	private int mHashValue;
	private boolean mHashCached;
	private Document mXmlDocument;

	/**
	 * Some {@link Identifier} impelmentations need this for {@link #equals(Object)}
	 */
	protected static DataModelServerConfigurationProvider mConf;
	static {
		mConf = DataModelService.getServerConfiguration();
	}

	IdentifierImpl(String type) {
		NullCheck.check(type, "identifier type is null");
		mIdentifierType = type;
		mXmlDocument = null;
	}

	/* (non-Javadoc)
	 * @see de.hshannover.f4.trust.iron.mapserver.datamodel.identifiers.Identifier#getTypeString()
	 */
	@Override
	public final String getTypeString() {
		return mIdentifierType;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public final String toString() {
		if (mPrintableString == null) {
			mPrintableString = getPrintableString();
			if (mPrintableString == null) {
				throw new SystemErrorException("Could not build string for"
						+ mIdentifierType);
			}
		}
		return mPrintableString;
	}

	/**
	 * Helper function for toString()-Method
	 * @return string representation of identifier
	 */
	protected abstract String getPrintableString();

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public abstract boolean equals(Object o);

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode(java.lang.Object)
	 */
	@Override
	public final int hashCode() {
		if (!mHashCached) {
			mHashValue = getHashCode();
			mHashCached = true;
		}
		return mHashValue;
	}

	/**
	 * Generate hash code based in identifier type
	 * @return hash value
	 */
	protected int getHashCode() {
		return 67 * 41 + mIdentifierType.hashCode();
	}

	@Override
	public Document getXmlDocument() {

		if (mXmlDocument == null) {
			throw new SystemErrorException("called getXmlDocument() " +
						"before setXmlDocument()");
		}

		return mXmlDocument;
	}

	@Override
	public void setXmlDocument(Document doc) {

		NullCheck.check(doc, "doc is null");

		if (mXmlDocument != null) {
			throw new SystemErrorException("called setXmlDocument() twice");
		}

		mXmlDocument = doc;
	}
}
