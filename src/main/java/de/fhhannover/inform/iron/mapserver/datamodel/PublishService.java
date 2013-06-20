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

import java.util.List;

import org.apache.log4j.Logger;

import de.fhhannover.inform.iron.mapserver.datamodel.graph.GraphElement;
import de.fhhannover.inform.iron.mapserver.datamodel.graph.GraphElementRepository;
import de.fhhannover.inform.iron.mapserver.datamodel.identifiers.Identifier;
import de.fhhannover.inform.iron.mapserver.datamodel.meta.Metadata;
import de.fhhannover.inform.iron.mapserver.datamodel.meta.MetadataHolder;
import de.fhhannover.inform.iron.mapserver.datamodel.meta.MetadataHolderFactory;
import de.fhhannover.inform.iron.mapserver.datamodel.meta.MetadataLifeTime;
import de.fhhannover.inform.iron.mapserver.datamodel.meta.MetadataState;
import de.fhhannover.inform.iron.mapserver.datamodel.search.Filter;
import de.fhhannover.inform.iron.mapserver.exceptions.InvalidMetadataException;
import de.fhhannover.inform.iron.mapserver.exceptions.SystemErrorException;
import de.fhhannover.inform.iron.mapserver.messages.PublishDelete;
import de.fhhannover.inform.iron.mapserver.messages.PublishNotify;
import de.fhhannover.inform.iron.mapserver.messages.PublishRequest;
import de.fhhannover.inform.iron.mapserver.messages.PublishUpdate;
import de.fhhannover.inform.iron.mapserver.messages.SubPublishRequest;
import de.fhhannover.inform.iron.mapserver.provider.DataModelServerConfigurationProvider;
import de.fhhannover.inform.iron.mapserver.provider.LoggingProvider;
import de.fhhannover.inform.iron.mapserver.utils.CollectionHelper;
import de.fhhannover.inform.iron.mapserver.utils.Iso8601DateTime;

/**
 * Publish or delete metadata.
 *
 * @author awe, vp
 * @since 0.1.0
 */
class PublishService {
	
	/**
	 * a static logger instance
	 */
	private final static Logger sLogger = LoggingProvider.getTheLogger();
	private final static String sName = "PublishService";
	
	private final PublisherRep mPublisherRep;
	private final GraphElementRepository mGraph;
	private final SubscriptionService mSubService;
	private final MetadataHolderFactory mMetaHolderFac;
	private final DataModelServerConfigurationProvider mConf;
	
	public PublishService(DataModelParams params, SubscriptionService subService) {
		mPublisherRep = params.pubRep;
		mGraph = params.graph;
		mMetaHolderFac = params.metaHolderFac;
		mSubService	= subService;
		mConf = params.conf;
	}

	/**
	 * Go through the whole PublishRequest and dispatch the elements to
	 * the corresponding process* methods
	 *
	 * @param req
	 * @throws InvalidMetadataException 
	 */
	void publish(PublishRequest req) throws InvalidMetadataException {
		List<SubPublishRequest> list = req.getSubPublishRequestList();
		String sId = req.getSessionId();
		Publisher pub = mPublisherRep.getPublisherBySessionId(sId);
		String pId = pub.getPublisherId();
		List<MetadataHolder> changes = 
					CollectionHelper.provideListFor(MetadataHolder.class);
		
		sLogger.trace(sName + ": process publishRequest for session " + sId);
		
		for (SubPublishRequest sreq : list) {
			if (sreq instanceof PublishNotify)
				processPublishNotify(pub, (PublishNotify) sreq, changes);
			else if (sreq instanceof PublishUpdate)
				processPublishUpdate(pub, (PublishUpdate)sreq, changes);
			else if (sreq instanceof PublishDelete)
				processPublishDelete(pub, (PublishDelete)sreq, changes);
			else
				throw new SystemErrorException("Unknown SubPublishRequest implementation");
		}
		
		// Set timestamp of metadata and publisher-id _now_ and set
		// references to GraphElements and Publishers...
		String timeNow = Iso8601DateTime.getTimeNow();
		for (MetadataHolder mh : changes) {
			Metadata m = mh.getMetadata();
			
			if (mh.isNew() || mh.isNotify())
				addOperationalAttributes(m, timeNow, pId);
		}
	
		mSubService.commitChanges(changes);
	}
			
	/**
	 * Publish persistent {@link Metadata}.
	 */
	private void processPublishUpdate(Publisher pub, PublishUpdate req,
			List<MetadataHolder> changes) {
		Identifier i1 = req.getIdent1();
		Identifier i2 = req.getIdent2();
		GraphElement ge = mGraph.getGraphElement(i1, i2);
		sLogger.trace(sName + ": publish update for " + ge + " and "
				+ req.getMetadataList().size() + " metadata objects");
		processPublish(pub, req, MetadataState.NEW, changes);
	}

	/**
	 * Publish notify {@link Metadata}.
	 */
	private void processPublishNotify(Publisher pub, PublishNotify req,
			List<MetadataHolder> changes) {
		processPublish(pub, req, MetadataState.NOTIFY, changes);
	}
	
	/**
	 * Delete {@link Metadata} objects.
	 * 
	 * @param pub the publisher responsible for this request
	 * @param req the delete request
	 * @param changes 
	 * @return
	 */
	private void processPublishDelete(Publisher pub, PublishDelete req,
			List<MetadataHolder> changes) {
	
		Identifier i1 = req.getIdent1();
		Identifier i2 = req.getIdent2();
		Filter filter = req.getFilter();
		GraphElement ge = mGraph.getGraphElement(i1, i2);
		List<MetadataHolder> toRemove;
		
		toRemove = ge.getMetadataHolder(filter);
		sLogger.trace(sName + ": publish delete for " + ge + " and "
				+ toRemove.size() + " metadata objects");
		
		for (MetadataHolder mh : toRemove) {
			Metadata m = mh.getMetadata();
			sLogger.trace(sName + ": deleting " + m.getPrefixAndElement() + " from "
					+ ge + " (was state " + mh.getState() + ")");
	
			switch (mh.getState()) {
			case NEW:
				removeReferences(mh);
				// silently drop new Metadata objects,
				// sanity check as mh should be in changes
				if (!changes.remove(mh)) {
					sLogger.error(sName + ": deleted NEW metadata not in changes ("
							+ m.getPrefixAndElement() + " on " + ge + ")");
					throw new SystemErrorException("NEW metadata not in changes");
				}
				break;
				
			case UNCHANGED:
				// UNCHANGED metadata becomes DELETED, references are removed
				// in SubscriptionService
				mh.setState(MetadataState.DELETED);
				changes.add(mh);
				break;
			case REPLACED:
				mh.setState(MetadataState.DELETED);
			
				// Sanity Check: REPLACED metadata should always be in changes.
				if (mConf.isSanityChecksEnabled())
					if (!changes.contains(mh))
						throw new SystemErrorException("replaced metadata "
								+ "not in changes");
				
				mh.setState(MetadataState.DELETED);
				break;
			default:
				// Don't consider REPLACED, DELETED or NOTIFY metadata
			}
		}
	}
	
	private void addOperationalAttributes(Metadata m, String timeNow, String pId) {
		m.setTimestamp(timeNow);
		m.setPublisherId(pId);
	}

	private void setReferences(MetadataHolder mh) {
		mh.getGraphElement().addMetadataHolder(mh);
		mh.getPublisher().addMetadataHolder(mh);
	}
	
	private void removeReferences(MetadataHolder mh) {
		mh.getGraphElement().removeMetadataHolder(mh);
		mh.getPublisher().removeMetadataHolder(mh);
		
	}

	/**
	 * Helper used by {@link #processPublishUpdate(Publisher, PublishUpdate, List)}
	 * and {@link #processPublishNotify(Publisher, PublishNotify, List)}.
	 * 
	 * @param pub
	 * @param req
	 * @param state
	 * @param changes
	 */
	private void processPublish(Publisher pub, PublishUpdate req, MetadataState state,
			List<MetadataHolder> changes) {
		List<Metadata> mlist = req.getMetadataList();
		Identifier i1 = req.getIdent1();
		Identifier i2 = req.getIdent2();
		MetadataLifeTime lt = req.getLifeTime();
		GraphElement graphElement = mGraph.getGraphElement(i1, i2);
		MetadataHolder mh = null;
		
		for (Metadata m : mlist) {
		
			/* singleValue metadata replaces existing metadata */
			if (m.isSingleValue())
				removeExistingSingleValueMetadata(m, graphElement, changes);
				
			
			mh = mMetaHolderFac.newMetadataHolder(m, lt, graphElement, pub);
			changes.add(mh);
			mh.setState(state);
			setReferences(mh);
		}
	}

	/**
	 * Remove elements of singleValue metadata on the given
	 * {@link GraphElement} if they exists. Leave NOTIFY metadata
	 * alone.
	 * 
	 * @param m
	 * @param ge
	 * @param changes 
	 */
	private void removeExistingSingleValueMetadata(Metadata m, GraphElement ge, List<MetadataHolder> changes) {
		MetadataHolder removeMe = null;
		List<MetadataHolder> mhs = null, tmpMhs = null;
	
		// somebody called us without checking m :-/
		if (!m.isSingleValue())
			return;
			
		mhs = ge.getMetadataHolder(m.getType());
		
		// no metadata of the same type, shortcut
		if (mhs.size() == 0)
			return;
			
		// filter out all notify metadata
		tmpMhs = CollectionHelper.provideListFor(MetadataHolder.class);
		for (MetadataHolder mh : mhs)
			if (!mh.isNotify())
				tmpMhs.add(mh);
		
		mhs = tmpMhs;
	
		// Sanity check: Never should there be more than a single singleValue
		// metadata object on a graph object, unless it's notify, but we
		// filtered that before.
		if (mhs.size() > 1)
			throw new SystemErrorException("singleValue occures more than once");

		// Nothing really  to remove here
		if (mhs.size() == 0)
			return;
		
		removeMe = mhs.get(0);
		
		// Sanity Check: If metadata is in state NEW or DELETED, it'd better also
		// be in the list of changes
		if (removeMe.isNew() || removeMe.isDeleted()) {
			if (mConf.isSanityChecksEnabled())
				if (!changes.contains(removeMe))
					throw new SystemErrorException("metadata not in changes");
			
			// We don't have to do anything here?
		} else {
			// Sanity Check: Neither NEW nor DELTED, so it should *not* be
			// in the list of changes
			if (mConf.isSanityChecksEnabled())
				if (changes.contains(removeMe))
					throw new SystemErrorException("metadata in changes");
		
			changes.add(removeMe);
		}
	
		// Real new metadata is not worth replacing
		if (removeMe.isNew()) {
			removeReferences(removeMe);
			changes.remove(removeMe);
		} else {
			removeMe.setState(MetadataState.REPLACED);
		}
	}
}

