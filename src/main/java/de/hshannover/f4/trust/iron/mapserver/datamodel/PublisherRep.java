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
 * This file is part of irond, version 0.5.7, implemented by the Trust@HsH
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


import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import de.hshannover.f4.trust.iron.mapserver.communication.ClientIdentifier;
import de.hshannover.f4.trust.iron.mapserver.exceptions.NoSuchPublisherException;
import de.hshannover.f4.trust.iron.mapserver.exceptions.SystemErrorException;
import de.hshannover.f4.trust.iron.mapserver.provider.LoggingProvider;
import de.hshannover.f4.trust.iron.mapserver.utils.CollectionHelper;

/**
 * Repository which stores Publishers
 *
 * @since 0.1.0
 * @author aw, vp
 */
class PublisherRep {

	private static Logger sLogger;

	private final Map<String, Publisher> mPublishers;
	private final Map<String, Publisher> mSessions;

	static {
		sLogger = LoggingProvider.getTheLogger();
	}

	PublisherRep() {
		mPublishers = CollectionHelper.provideMapFor(String.class, Publisher.class);
		mSessions = new HashMap<String, Publisher>();
	}

	/**
	 * Get a publisher with the given publisherId
	 *
	 * @param id
	 * @return
	 * @throws NoSuchPublisherException if no publisher is available with
	 * the given sessionId, but someone did something wrong in this case...
	 */
	Publisher getPublisherByPublisherId(String id) {
		Publisher ret = mPublishers.get(id);

		if (ret == null) {
			throw new NoSuchPublisherException("no publisher with id=" + id);
		}

		return ret;
	}

	/**
	 * Same as above, but might return null if the publisher is
	 * not found.
	 *
	 * @param id
	 * @return reference to the requested {@link Publisher} object or null if
	 *	 none was found.
	 */
	Publisher getPublisherByPublisherIdUnsafe(String id) {
		return mPublishers.get(id);
	}

	/**
	 * Get a publisher with a given sessionId.
	 *
	 * @param id
	 * @return
	 * @throws NoSuchPublisherException if no publisher is available with
	 * this a sessionId.
	 */
	Publisher getPublisherBySessionId(String id) {
		Publisher ret = mSessions.get(id);
		if (ret == null) {
			throw new NoSuchPublisherException("Publisher with session-id=" + id +
					" does not exist");
		}
		return ret;
	}

	/**
	 * Add a Publisher to the repository.
	 * If there already is a publisher with the given
	 * publisherId in the hashmap reuse this publisher
	 * object. If the publisher still has an open
	 * session throw an RunningSessionException
	 *
	 * @param pId publisher-id
	 * @param sId session-id
	 * @return
	 * @throws RunningSessionException
	 * @throws PublisherConstructionException
	 */
	void addPublisher(String pId, String sId, Integer mprs, ClientIdentifier clId) {

		sLogger.trace("Adding new Publisher: sessionid=" + sId +
				" publisherid=" + pId);

		Publisher p = mPublishers.get(pId);

		if (p != null) {

			// Sanity Check: Never should addPublisher() be called for clients
			// with existing sessions.
			if (p.getSessionId() != null && mSessions.containsKey(p.getSessionId())) {
				throw new SystemErrorException("Session for " + pId + " not closed");
			}

			sLogger.trace("Reusing existing publisher object...");
			p.getSubscriptionState().setMaxPollResultSize(mprs);

		} else {
			sLogger.trace("Creating new Publisher...");
			p = new Publisher(pId, sId, mprs, clId);
			mPublishers.put(pId, p);
		}

		p.setSessionId(sId);
		mSessions.put(sId, p);
	}

	/**
	 * Remove a publisher by its publisher id.
	 *
	 * @param id
	 */
	void removePublisherByPubliherId(String id) {
		if (id != null) {
			Publisher pub = mPublishers.get(id);
			if (pub != null) {
				String sessionid = pub.getSessionId();
				if(sessionid != null && sessionid.length() > 0) {
					Publisher pub2 = mSessions.get(sessionid);
					if  (pub2 != null) {
						mSessions.remove(sessionid);
					}
				}
				mPublishers.remove(id);
			}
		}

	}

	void removePublisherSession(String sessionId) {
		mSessions.remove(sessionId);
	}
}

