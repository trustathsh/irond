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
 * This file is part of irond, version 0.5.6, implemented by the Trust@HsH
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


import java.util.Date;

import de.hshannover.f4.trust.iron.mapserver.datamodel.SimpleSearchAbleImpl;
import de.hshannover.f4.trust.iron.mapserver.datamodel.search.Filter;
import de.hshannover.f4.trust.iron.mapserver.exceptions.SystemErrorException;
import de.hshannover.f4.trust.iron.mapserver.provider.LoggingProvider;
import de.hshannover.f4.trust.iron.mapserver.utils.LengthCheck;
import de.hshannover.f4.trust.iron.mapserver.utils.NullCheck;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;

/**
 * Abstract Metadata class.
 *
 * Concrete {@link Metadata} classes have to implement the abstract methods
 * given in this class.
 *
 * @author aw
 * @version 0.1
 */
public abstract class Metadata extends SimpleSearchAbleImpl {

	public static final String PUBLISHERID = "ifmap-publisher-id";
	public static final String TIMESTAMP = "ifmap-timestamp";

	public static final String TIMESTAMP_FRACTION = "ifmap-timestamp-fraction";

	/**
	 * a static logger
	 */
	protected final static Logger sLogger = LoggingProvider.getTheLogger();

	/**
	 * Used for comparisons between Metadata types
	 */
	private MetadataType mType;

	private boolean mTimeStampSet;
	private boolean mPublisherIdSet;
	private final boolean mValidated;

	protected Metadata(MetadataType type) {
		this(type, false);
	}

	protected Metadata(MetadataType type, boolean validated) {
		NullCheck.check(type, "type is null");
		mType = type;
		mValidated = validated;
	}

	/**
	 * Conversation from the internal representation to a
	 * W3C document representation. This is needed when
	 * transforming this {@link Metadata} object into objects
	 * which will later be marshaled and sent to a MAPC.
	 *
	 * @return a W3C document representation of the metadata object
	 */
	abstract public Document toW3cDocument();

	/**
	 * {@link Metadata} from a MAPC comes without the publisherID
	 * this must be added here to occur in search- and
	 * subscription results.
	 * A implementation als has to set the publisherId field.
	 * this.publisherId = pubid;
	 *
	 * @param pubId
	 * @throws MetadataConstructionException
	 */
	public final void setPublisherId(String pubId) {
		if (mPublisherIdSet) {
			throw new SystemErrorException("ifmap-publisher-id was already set");
		}

		NullCheck.check(pubId, "publisher-id is null");
		LengthCheck.checkMin(pubId, 1, "ifmap-publisher-id");
		setPublisherIdInternal(pubId);
		mPublisherIdSet = true;
	}

	abstract protected void setPublisherIdInternal(String pubid);

	/**
	 * Metadata from a MAPC comes without a timestamp. For a timestamp to
	 * occur in search and subscription results. a concrete implementation
	 * needs to set the timestamp in a way that toW3cDocument() has the
	 * time-stamp set.
	 *
	 * If the timestamp can't be set the method should throw a
	 * MetadataConstructionException.
	 *
	 * @param ts
	 */
	public final void setTimestamp(Date ts) {
		if (mTimeStampSet) {
			throw new SystemErrorException("ifmap-timestamp was already set");
		}

		NullCheck.check(ts, "timestamp is null");
		LengthCheck.checkMin(ts.getTime() + "", 1, "ifmap-timestamp");
		setTimeStampInternal(ts);
		mTimeStampSet = true;
	}

	protected abstract void setTimeStampInternal(Date ts);

	/**
	 * Get a reference to the MetaDataType. This can be used to compare
	 * two metadata objects to be from the same metadata type.
	 *
	 * For example two metadata objects as <meta:role> have the same
	 * MetaDataType reference.
	 *
	 * @return
	 */
	public MetadataType getType() {
		if (mType == null) {
			throw new RuntimeException("type was not initialized, FIX THAT!");
		}

		return mType;
	}

	/**
	 * Get the cardinality. Be aware, if the cardinality wasn't initialized,
	 * this will throw a RuntimeException!
	 *
	 * @return
	 */
	public MetaCardinalityType getCardinality() {
		return getType().getCardinality();
	}

	/**
	 * @return true if the {@link Metadata} object has cardinality multiValue
	 */
	public boolean isMultiValue() {
		return getCardinality() == MetaCardinalityType.multiValue;
	}

	/**
	 * @return true if the {@link Metadata} object has cardinality singleValue
	 */
	public boolean isSingleValue() {
		return !isMultiValue();
	}

	/**
	 * Returns the metadata as simple {@link String} object.
	 *
	 * Implementations are not allowed to do pretty printing. Just a very
	 * long string without new lines.
	 *
	 * @return
	 */
	public abstract String getMetadataAsString();

	/**
	 * Yet another method to output {@link Metadata} to easily print it.
	 *
	 * @return
	 */
	public abstract String getPrefixAndElement();

	/**
	 * Has this {@link Metadata} been schema validated?
	 * @return true if schema validated
	 */
	public boolean getValidated() {
		return mValidated;
	}
}

