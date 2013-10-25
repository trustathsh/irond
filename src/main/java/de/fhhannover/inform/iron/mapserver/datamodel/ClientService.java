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
 * This file is part of irond, version 0.4.0, implemented by the Trust@FHH 
 * research group at the Fachhochschule Hannover.
 * 
 * irond is an an *experimental* IF-MAP 2.0 compliant MAP server written in
 * JAVA. irond supports both basic authentication and certificate-based 
 * authentication (using X.509 certificates) of MAP clients. irond is
 * maintained by the Trust@FHH group at the Fachhochschule Hannover, initial
 * developement was carried out during the ESUKOM research project.
 * %%
 * Copyright (C) 2010 - 2013 Trust@FHH
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

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import de.fhhannover.inform.iron.mapserver.datamodel.meta.MetadataHolder;
import de.fhhannover.inform.iron.mapserver.datamodel.meta.MetadataState;
import de.fhhannover.inform.iron.mapserver.exceptions.PurgePublisherNoAllowedException;
import de.fhhannover.inform.iron.mapserver.exceptions.ResponseCreationException;
import de.fhhannover.inform.iron.mapserver.provider.LoggingProvider;
import de.fhhannover.inform.iron.mapserver.trust.TrustService;
import de.fhhannover.inform.iron.mapserver.trust.TrustServiceImpl;
import de.fhhannover.inform.iron.mapserver.trust.domain.TrustToken;

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

	private PublisherRep publisherRep;
	private SubscriptionService subService;

	private TrustService mTrustService;

	ClientService(PublisherRep pr, SubscriptionService subServ,
			TrustService trustService) {
		publisherRep = pr;
		subService = subServ;
		mTrustService = trustService;
	}
	
	/**
	 * Method which handles newSession calls.
	 * Simply a new publisher is added to the publisherRepository.
	 * In case of errors exceptions are thrown.
	 * 
	 * @throws RunningSessionException newSession is called, the publisher exists
	 * 			but had never received an end session call
	 */
	void newSession(String sessionId, String publisherId, Integer mprs) {
		if (mprs == null)
			mprs = new Integer(DataModelService.getServerConfiguration().getDefaultMaxPollResultSize());
		sLogger.trace(sName + ": newSession for " + publisherId 
				+ " and maxPollResultSize=" + mprs + " bytes");
	
		publisherRep.addPublisher(publisherId, sessionId, mprs);

		/*
		 * TrustService
		 * 
		 * Mappt die Session-ID des MAP-Clients auf den passenden Client-Identifier.
		 */
		((TrustServiceImpl)mTrustService).mapSessionIdToClientIdentifier(sessionId);
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

		// TrustService
		mTrustService.removeAllSprOfMapc(sessionId);
	}

	/**
	 * Remove all metadata from publisher
	 * 
	 * @param request
	 * @return
	 * @throws PurgePublisherNoAllowedException 
	 * @throws ResponseCreationException 
	 */
	void purgePublisher(String sessionId, String publisherId) throws PurgePublisherNoAllowedException {
		List<MetadataHolder> changes = new ArrayList<MetadataHolder>();
		Publisher requestor = publisherRep.getPublisherBySessionId(sessionId);
		Publisher toPurge = publisherRep.getPublisherByPublisherIdUnsafe(publisherId);
	
		// Check if allowed to do so... TODO: Move this one layer up?
		if (DataModelService.getServerConfiguration().getPurgePublisherIsRestricted()) {
			if (!requestor.getPublisherId().equals(publisherId)) {
				sLogger.warn(sName + ": purgePublisher not allowed");
				throw new PurgePublisherNoAllowedException("Not allowed to" +
						" purge metadata! of a different publisher");
			}
		}
		
		// If we don't know about the publisher, log a warning, but don't throw
		// an exception.
		if (toPurge == null) {
			sLogger.warn(sName + ": " + requestor + " tried purging non-existing"
					+ " publisher-id=" + publisherId);
			return;
		}

		/* 
		 * TrustService
		 * 
		 * 
		 * 
		 */
		TrustToken tt = mTrustService.getP1TT(sessionId, publisherId);
		for (MetadataHolder mh : toPurge.getForeverMetadata()) {
			mh.setTrustToken(tt);
		}
		
		changes.addAll(toPurge.getSessionMetadata());
		changes.addAll(toPurge.getForeverMetadata());
		sLogger.debug(sName + ": " + requestor + " is purging " + changes.size()
				+ " metadata objects of " + toPurge);
		
		setStateDeleted(changes);
		subService.commitChanges(changes);
	}
	
	private void setStateDeleted(List<MetadataHolder> mhlist) {
		for (MetadataHolder mh : mhlist)
			mh.setState(MetadataState.DELETED);
	}
}
