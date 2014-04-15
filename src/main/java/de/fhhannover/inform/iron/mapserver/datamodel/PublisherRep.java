package de.fhhannover.inform.iron.mapserver.datamodel;

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

import java.util.HashMap;

import org.apache.log4j.Logger;

import de.fhhannover.inform.iron.mapserver.exceptions.NoSuchPublisherException;
import de.fhhannover.inform.iron.mapserver.exceptions.SystemErrorException;
import de.fhhannover.inform.iron.mapserver.provider.LoggingProvider;

/**
 * Repository which stores Publishers
 * 
 * @since 0.1.0
 * @author aw, vp
 */
class PublisherRep {
	
	private static Logger logger;
	
	static {
		logger = LoggingProvider.getTheLogger();
	}
	
	PublisherRep() {
		publishers = new HashMap<String, Publisher>();
		sessions = new HashMap<String, Publisher>();
	}
 
	private HashMap<String, Publisher> publishers;
	private HashMap<String, Publisher> sessions;
	 
	/**
	 * Get a publisher with the given publisherId
	 * 
	 * @param id
	 * @return
	 * @throws NoSuchPublisherException if no publisher is available with
	 * the given sessionId, but someone did something wrong in this case...
	 */
	Publisher getPublisherByPublisherId(String id) {
		Publisher ret = publishers.get(id);
		if (ret == null) {
			throw new NoSuchPublisherException("No Publisher with publisher-id=" + id);
		}
		return publishers.get(id);
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
		return publishers.get(id);
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
		Publisher ret = sessions.get(id);
		if (ret == null)
			throw new NoSuchPublisherException("Publisher with session-id=" + id +
					" does not exist");
		return ret;
	}
	 
	/**
	 * Add a Publisher to the repository.
	 * If there already is a publisher with the given
	 * publisherId in the hashmap reuse this publisher
	 * object. If the publisher still has an open
	 * session throw an RunningSessionException
	 * 
	 * @param publisherId
	 * @param sessionId
	 * @return
	 * @throws RunningSessionException
	 * @throws PublisherConstructionException
	 */
	void addPublisher(String publisherId, String sessionId, Integer maxPollResSize) {
		
		logger.trace("Adding new Publisher: sessionid=" + sessionId + 
				" publisherid=" + publisherId);
		
		Publisher p = publishers.get(publisherId);	
		
		if (p != null) {
			if (p.getSessionId() != null && sessions.containsKey(p.getSessionId()))
				throw new SystemErrorException("Session for " + p.getPublisherId() 
						+ " was not closed!");
			
			logger.trace("Reusing existing publisher object...");
			p.getSubscriptionState().setMaxPollResultSize(maxPollResSize);
			
		} else {
			logger.trace("Creating new Publisher...");
			p = new Publisher(publisherId, sessionId, maxPollResSize);
			publishers.put(publisherId, p);
		}
		
		p.setSessionId(sessionId);		
		sessions.put(sessionId, p);
	}
	 
	/**
	 * Remove a publisher by its publisher id.
	 * 
	 * @param id
	 */
	void removePublisherByPubliherId(String id) {
		if (id != null) {
			Publisher pub = publishers.get(id);
			if (pub != null) {
				String sessionid = pub.getSessionId();
				if(sessionid != null && sessionid.length() > 0) {
					Publisher pub2 = sessions.get(sessionid);
					if  (pub2 != null) {
						sessions.remove(sessionid);
					}
				}
				publishers.remove(id);
			}
		}
	 
	}

	void removePublisherSession(String sessionId) {
		sessions.remove(sessionId);
	}
}
 
