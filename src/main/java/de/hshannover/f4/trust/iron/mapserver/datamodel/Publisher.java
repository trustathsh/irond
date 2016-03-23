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
package de.hshannover.f4.trust.iron.mapserver.datamodel;


import java.util.Collections;
import java.util.List;

import de.hshannover.f4.trust.iron.mapserver.communication.ClientIdentifier;
import de.hshannover.f4.trust.iron.mapserver.datamodel.meta.MetadataHolder;
import de.hshannover.f4.trust.iron.mapserver.datamodel.search.SubscriptionState;
import de.hshannover.f4.trust.iron.mapserver.utils.CollectionHelper;
import de.hshannover.f4.trust.iron.mapserver.utils.LengthCheck;
import de.hshannover.f4.trust.iron.mapserver.utils.NullCheck;

/**
 * This class represents a Publisher which in general represents a MAPC.
 *
 * During a call to newSession() a new Publisher is generated.
 *
 * A publisher has references to its published metadata. Splitted up into
 * lifetime and session metadata to be easily removed when endSession() or
 * purgePublisher() is called.
 *
 *
 * @author aw
 * @version 0.1
 *
 */
public class Publisher {

	/**
	 * The Publishers ifmap-publisher-id
	 */
	private final String mPublisherId;

	/**
	 * The session-id, this can be null if the Publisher currently has no
	 * session open but still has metadata in the graph
	 */
	private String mSessionId;

	private final List<MetadataHolder> mSessionMetadata;

	private final List<MetadataHolder> mForeverMetadata;

	private SubscriptionState mSubscriptionState;

	// Data about authentication used
	private ClientIdentifier mClientIdent;

	public Publisher(String pId, String sId, Integer mprs, ClientIdentifier clId) {

		NullCheck.check(pId, "publisher-id is null");
		NullCheck.check(sId, "session-id is null");
		LengthCheck.checkMin(pId, 1, "publisher-id length bad");
		LengthCheck.checkMin(sId, 1, "session-id length bad");

		mSubscriptionState = new SubscriptionState();
		mSessionMetadata = CollectionHelper.provideListFor(MetadataHolder.class);
		mForeverMetadata = CollectionHelper.provideListFor(MetadataHolder.class);
		mPublisherId = pId;
		mSessionId = sId;
		mClientIdent = clId;

		mSubscriptionState.setMaxPollResultSize(mprs);
	}


	/**
	 * Set the session id of this publisher. Only works if the sessionId
	 * is not null
	 *
	 * @param sid
	 */
	public void setSessionId(String sid) {
			mSessionId = sid;
	}

	/**
	 * The only way to set the sessionId attribute to null.
	 */
	public void deleteSessionId() {
		setSessionId(null);
	}

	/**
	 * Add a reference to the given metadata. The implementation decides
	 * whether it is added into the session metadata list or lifetime
	 * metadata list.
	 *
	 * @param m
	 */
	public void addMetadataHolder(MetadataHolder m) {
		NullCheck.check(m, "metadataHolder is null");
		switch (m.getLifetime()) {
		case session:
			addSessionMetadata(m);
			break;

		case forever:
			addLifeTimeMetadata(m);
			break;
		}
	}

	private void addSessionMetadata(MetadataHolder m) {
		mSessionMetadata.add(m);
	}

	private void addLifeTimeMetadata(MetadataHolder m) {
		mForeverMetadata.add(m);
	}

	/**
	 * @return a read-only list of the session metadata.
	 */
	public List<MetadataHolder> getSessionMetadata() {
		return Collections.unmodifiableList(mSessionMetadata);
	}

	/**
	 * @return a read-only list of the lifetime metadata.
	 */
	public List<MetadataHolder> getForeverMetadata() {
		return Collections.unmodifiableList(mForeverMetadata);
	}

	public String getPublisherId() {
			return mPublisherId;
	}

	public ClientIdentifier getClientIdentifier() {
			return mClientIdent;
	}

	public String getSessionId() {
		return mSessionId;
	}

	/**
	 * Remove metadata from a publisher.
	 *
	 * Check which lifetime the Metadata has and call
	 * the corresponding remove methods.
	 *
	 * @param m
	 */
	public boolean removeMetadataHolder(MetadataHolder m) {
		NullCheck.check(m, "metadataHolder is null");
		boolean res = false;
		switch (m.getLifetime()) {
		case session:
			res = removeSessionMetadata(m);
			break;

		case forever:
			res = removeLifeTimeMetadata(m);
			break;
		}
		return res;
	}


	private boolean removeLifeTimeMetadata(MetadataHolder m) {
		return mForeverMetadata.remove(m);
	}

	private boolean removeSessionMetadata(MetadataHolder m) {
		return mSessionMetadata.remove(m);
	}

	public SubscriptionState getSubscriptionState() {
		return mSubscriptionState;
	}

	@Override
	public String toString() {
		return "publisher{" + mPublisherId + "}";
	}
}

