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

import java.io.StringReader;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import de.fhhannover.inform.iron.mapserver.datamodel.graph.GraphElement;
import de.fhhannover.inform.iron.mapserver.datamodel.graph.GraphElementRepository;
import de.fhhannover.inform.iron.mapserver.datamodel.identifiers.Identifier;
import de.fhhannover.inform.iron.mapserver.datamodel.identifiers.Identity;
import de.fhhannover.inform.iron.mapserver.datamodel.identifiers.IdentityTypeEnum;
import de.fhhannover.inform.iron.mapserver.datamodel.meta.MetaCardinalityType;
import de.fhhannover.inform.iron.mapserver.datamodel.meta.Metadata;
import de.fhhannover.inform.iron.mapserver.datamodel.meta.MetadataHolder;
import de.fhhannover.inform.iron.mapserver.datamodel.meta.MetadataHolderFactory;
import de.fhhannover.inform.iron.mapserver.datamodel.meta.MetadataLifeTime;
import de.fhhannover.inform.iron.mapserver.datamodel.meta.MetadataState;
import de.fhhannover.inform.iron.mapserver.datamodel.meta.MetadataTypeImpl;
import de.fhhannover.inform.iron.mapserver.datamodel.meta.W3cXmlMetadata;
import de.fhhannover.inform.iron.mapserver.datamodel.search.Filter;
import de.fhhannover.inform.iron.mapserver.exceptions.InvalidIdentifierException;
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

	private PublisherRep mPublisherRep;
	private GraphElementRepository mGraph;
	private SubscriptionService mSubService;
	private MetadataHolderFactory mMetaHolderFac;
	private DataModelServerConfigurationProvider mConf;

	private RootIdentifierExtension mRootIdentifierExtension;


	PublishService(PublisherRep pRep, GraphElementRepository graphRep,
			MetadataHolderFactory metaHolderFac, SubscriptionService subServ,
			DataModelServerConfigurationProvider conf) {
		mPublisherRep = pRep;
		mGraph = graphRep;
		mMetaHolderFac = metaHolderFac;
		mSubService	= subServ;
		mConf = conf;
		mRootIdentifierExtension = new RootIdentifierExtension(
				mConf.getRootIdentifierName(),
				mConf.getRootIdentifierTypeDef());
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
		String pId = null;
		sLogger.trace(sName + ": processing publishRequest for sessionid " + sId);
		
		Publisher publisher = mPublisherRep.getPublisherBySessionId(sId);
		
		sLogger.trace(sName + ": " + publisher + " with " + list.size() + " requests");
	
		List<MetadataHolder> changes = CollectionHelper.provideListFor(MetadataHolder.class);
		
		for (SubPublishRequest sreq : list) {
			if (sreq instanceof PublishNotify)
				processPublishNotify(publisher, (PublishNotify) sreq, changes);
			else if (sreq instanceof PublishUpdate)
				processPublishUpdate(publisher, (PublishUpdate)sreq, changes);
			else if (sreq instanceof PublishDelete)
				processPublishDelete(publisher, (PublishDelete)sreq, changes);
			else
				throw new SystemErrorException("Unknown SubPublishRequest implementation");
		}
		
		
		// set timestamp and publisher id _now_ and set references to 
		// GraphElements and Publishers...
		String timeNow = Iso8601DateTime.getTimeNow();
		for (MetadataHolder mh : changes) {
			Metadata m = mh.getMetadata();
			
			if (mh.isNew() || mh.isNotify())
				addOperationalAttributes(m, timeNow, mh.getPublisher().getPublisherId());
		}
	
		// FIXME!!
		//mdentifierRep.setTimestamp(System.currentTimeMillis());
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

		if (mConf.isRootIdentifierEnabled()) {
			mRootIdentifierExtension.addLinkToRoot(i1, changes, state);
			mRootIdentifierExtension.addLinkToRoot(i2, changes, state);
		}

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


	private class RootIdentifierExtension {

		final String METADATA_NS = "http://trust.f4.hs-hannover.de/ifmap/irond/1";

		final MetadataLifeTime LIFETIME = MetadataLifeTime.forever;
		final Publisher PUBLISHER = new Publisher("irond", "invalid-session-id-for-irond-publisher", 0);

		Identity mRootIdentifier;
		DocumentBuilderFactory mDocBuilerFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder;

		String mRootLinkMetadata =
			"<irond:root xmlns:irond=\""+METADATA_NS+"\" ifmap-cardinality=\"singleValue\" ></irond:root>";
		Document mDoc;

		RootIdentifierExtension(String rootIdentifierName, String rootIdentifierTypeDef) {
			try {
				mRootIdentifier =
					new Identity(rootIdentifierName, "", rootIdentifierTypeDef, IdentityTypeEnum.other);
				docBuilder = mDocBuilerFactory.newDocumentBuilder();
				mDoc = docBuilder.parse(new InputSource(new StringReader(mRootLinkMetadata)));
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

		/**
		 * Add a link to the "root identifier" if the given identifier is
		 * not null. The new metadata gets appended to the given changes list.
		 */
		void addLinkToRoot(Identifier identifier, List<MetadataHolder> changes,
				MetadataState state) {
			if (identifier != null) {
				try {
					GraphElement graphElement =
							mGraph.getGraphElement(identifier, mRootIdentifier);
					W3cXmlMetadata rootLinkMetadata = new W3cXmlMetadata(
							mDoc, new MetadataTypeImpl(METADATA_NS +":irond", MetaCardinalityType.singleValue), false);

					MetadataHolder rootLink = mMetaHolderFac.newMetadataHolder(
							rootLinkMetadata, LIFETIME, graphElement, PUBLISHER);
					changes.add(rootLink);
					rootLink.setState(state);
					setReferences(rootLink);
				} catch (InvalidMetadataException e) {
					throw new RuntimeException(e);
				}
			}
		}
	}
}

