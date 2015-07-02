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
 * This file is part of irond, version 0.5.5, implemented by the Trust@HsH
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
package de.hshannover.f4.trust.iron.mapserver.datamodel;


import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import de.hshannover.f4.trust.iron.mapserver.communication.ClientIdentifier;
import de.hshannover.f4.trust.iron.mapserver.contentauth.IfmapPep;
import de.hshannover.f4.trust.iron.mapserver.datamodel.meta.MetadataHolder;
import de.hshannover.f4.trust.iron.mapserver.datamodel.meta.MetadataState;
import de.hshannover.f4.trust.iron.mapserver.exceptions.AccessDeniedException;
import de.hshannover.f4.trust.iron.mapserver.exceptions.PurgePublisherNoAllowedException;
import de.hshannover.f4.trust.iron.mapserver.exceptions.ResponseCreationException;
import de.hshannover.f4.trust.iron.mapserver.provider.DataModelServerConfigurationProvider;
import de.hshannover.f4.trust.iron.mapserver.provider.LoggingProvider;

/**
 * This class is responsible to handle newSession, endSession and purgePublisher.
 * DataModelService simply delegates the above calls to newSession(), endSession()
 * and purgePublish() respectively.
 *
 * @author aw
 * @since 0.1.0
 */
class ClientService {

	/**
	 * static logger instance
	 */
	private static final Logger sLogger = LoggingProvider.getTheLogger();
	private static final String sName = "ClientService";

	private final PublisherRep publisherRep;
	private final SubscriptionService subService;
	private final IfmapPep mPep;

	ClientService(DataModelParams params, SubscriptionService subServ) {
		publisherRep = params.pubRep;
		subService = subServ;
		mPep = params.pep;
	}

	/**
	 * Method which handles newSession calls.
	 * Simply a new publisher is added to the publisherRepository.
	 * In case of errors exceptions are thrown.
	 *
	 * @throws RunningSessionException newSession is called, the publisher exists
	 * 			but had never received an end session call
	 */
	void newSession(String sId, String pId, Integer mprs, ClientIdentifier clId) {
		if (mprs == null) {
			DataModelServerConfigurationProvider conf =
					DataModelService.getServerConfiguration();
			mprs = conf.getDefaultMaxPollResultSize();
		}

		sLogger.trace(sName + ": newSession for " + pId  + " mprs=" + mprs);
		publisherRep.addPublisher(pId, sId, mprs, clId);
	}

	/**
	 * Method which handles an endSession call.
	 * First check whether the publisher is available, if not
	 * throw a NoSuchPublisherException.
	 *
	 * Delete all session metadata from the publisher with the help
	 * of MetaController
	 *
	 * Remove the session from the publisher and the corresponding
	 * session in the PublisherRep.
	 *
	 * @param request
	 * @return
	 */
	void endSession(String sessionId) {
		List<MetadataHolder> changes = new ArrayList<MetadataHolder>();

		// throws exception if no publisher found
		Publisher pub = publisherRep.getPublisherBySessionId(sessionId);
		sLogger.debug(sName + ": endSession for " + pub);

		// drop all subscriptions
		subService.removeSubscriptions(pub);

		// delete session metadata of this publisher
		changes.addAll(pub.getSessionMetadata());
		sLogger.trace(sName + ": removing " + changes.size() + " session objects");
		//metaController.deleteMetadata(pub, toremove, changes);

		pub.deleteSessionId();
		publisherRep.removePublisherSession(sessionId);

		setStateDeleted(changes);
		subService.commitChanges(changes);
	}

	/**
	 * Remove all metadata from publisher
	 *
	 * @param request
	 * @return
	 * @throws PurgePublisherNoAllowedException
	 * @throws AccessDeniedException
	 * @throws ResponseCreationException
	 */
	void purgePublisher(String sessionId, String publisherId) throws PurgePublisherNoAllowedException, AccessDeniedException {
		List<MetadataHolder> changes = new ArrayList<MetadataHolder>();
		Publisher purger = publisherRep.getPublisherBySessionId(sessionId);
		Publisher purgee = publisherRep.getPublisherByPublisherIdUnsafe(publisherId);


		// FIXME: With content authorization, this is pretty much obsolete.
		if (DataModelService.getServerConfiguration().getPurgePublisherIsRestricted()) {
			if (!purger.getPublisherId().equals(publisherId)) {
				sLogger.warn(sName + ": purgePublisher not allowed");
				throw new PurgePublisherNoAllowedException("Not allowed to" +
						" purge metadata! of a different publisher");
			}
		}

		if (!mPep.isAuthorized(purger, publisherId)) {
			throw new AccessDeniedException("not allowed");
		}

		// If we don't know about the publisher, log a warning, but don't throw
		// an exception.
		if (purgee == null) {
			sLogger.warn(sName + ": " + purger + " tried purging non-existing"
					+ " publisher-id=" + publisherId);
			return;
		}

		changes.addAll(purgee.getSessionMetadata());
		changes.addAll(purgee.getForeverMetadata());
		sLogger.debug(sName + ": " + purger + " is purging " + changes.size()
				+ " metadata objects of " + purgee);

		setStateDeleted(changes);
		subService.commitChanges(changes);
	}

	private void setStateDeleted(List<MetadataHolder> mhlist) {
		for (MetadataHolder mh : mhlist) {
			mh.setState(MetadataState.DELETED);
		}
	}
}
