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

import java.util.Date;

import de.fhhannover.inform.iron.mapserver.datamodel.SimpleSearchAbleImpl;
import de.fhhannover.inform.iron.mapserver.datamodel.search.Filter;
import de.fhhannover.inform.iron.mapserver.exceptions.SystemErrorException;
import de.fhhannover.inform.iron.mapserver.provider.LoggingProvider;
import de.fhhannover.inform.iron.mapserver.utils.LengthCheck;
import de.fhhannover.inform.iron.mapserver.utils.NullCheck;
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
	 * This method should contain the logic to check
	 * whether or not this {@link Metadata} object matches the
	 * given {@link Filter} f.
	 *
	 * @param f
	 * @return
	 */
	abstract public boolean matchesFilter(Filter f);

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

