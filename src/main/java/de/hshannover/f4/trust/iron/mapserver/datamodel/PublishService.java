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
 * This file is part of irond, version 0.4.2, implemented by the Trust@HsH
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
package de.hshannover.f4.trust.iron.mapserver.datamodel;


import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import de.hshannover.f4.trust.iron.mapserver.communication.ClientIdentifier;
import de.hshannover.f4.trust.iron.mapserver.datamodel.graph.GraphElement;
import de.hshannover.f4.trust.iron.mapserver.datamodel.graph.GraphElementRepository;
import de.hshannover.f4.trust.iron.mapserver.datamodel.identifiers.Identifier;
import de.hshannover.f4.trust.iron.mapserver.datamodel.identifiers.Identity;
import de.hshannover.f4.trust.iron.mapserver.datamodel.identifiers.IdentityTypeEnum;
import de.hshannover.f4.trust.iron.mapserver.datamodel.meta.MetaCardinalityType;
import de.hshannover.f4.trust.iron.mapserver.datamodel.meta.Metadata;
import de.hshannover.f4.trust.iron.mapserver.datamodel.meta.MetadataHolder;
import de.hshannover.f4.trust.iron.mapserver.datamodel.meta.MetadataHolderFactory;
import de.hshannover.f4.trust.iron.mapserver.datamodel.meta.MetadataLifeTime;
import de.hshannover.f4.trust.iron.mapserver.datamodel.meta.MetadataState;
import de.hshannover.f4.trust.iron.mapserver.datamodel.meta.MetadataTypeImpl;
import de.hshannover.f4.trust.iron.mapserver.datamodel.meta.W3cXmlMetadata;
import de.hshannover.f4.trust.iron.mapserver.datamodel.search.Filter;
import de.hshannover.f4.trust.iron.mapserver.exceptions.InvalidIdentifierException;
import de.hshannover.f4.trust.iron.mapserver.exceptions.InvalidMetadataException;
import de.hshannover.f4.trust.iron.mapserver.exceptions.RequestCreationException;
import de.hshannover.f4.trust.iron.mapserver.exceptions.SearchException;
import de.hshannover.f4.trust.iron.mapserver.exceptions.SystemErrorException;
import de.hshannover.f4.trust.iron.mapserver.messages.PublishDelete;
import de.hshannover.f4.trust.iron.mapserver.messages.PublishNotify;
import de.hshannover.f4.trust.iron.mapserver.messages.PublishRequest;
import de.hshannover.f4.trust.iron.mapserver.messages.PublishUpdate;
import de.hshannover.f4.trust.iron.mapserver.messages.SubPublishRequest;
import de.hshannover.f4.trust.iron.mapserver.provider.DataModelServerConfigurationProvider;
import de.hshannover.f4.trust.iron.mapserver.provider.LoggingProvider;
import de.hshannover.f4.trust.iron.mapserver.utils.CollectionHelper;

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

	/**
	 * Indicates whether all identifier should be linked to the MAP server
	 * identifier or not.
	 */
	private boolean mIsRootLinkEnabled;

	/**
	 * XML namepspace for IF-MAP version 2.2.
	 */
	static final String IFMAP_2_2_NAMESPACE = "http://www.trustedcomputinggroup.org/2013/IFMAP-SERVER/1";

	/**
	 * XML namespace prefix for IF-MAP version 2.2.
	 */
	static final String IFMAP_2_2_NAMSPACE_PREFIX = "maps";

	/**
	 * XML namespace for irond specific metadata.
	 */
	static final String IROND_NAMESPACE = "http://trust.f4.hs-hannover.de/ifmap/XMLSchema/1";

	/**
	 * XML namespace prefix for the irond namespace.
	 */
	static final String IROND_NAMESPACE_PREFIX = "irond";

	/**
	 * The MAP server identifier (see IF-MAP version 2.2 for details).
	 */
	private Identifier mMapServerIdentifier;

	/**
	 * The name of the MAP server identifier.
	 */
	private final String mMapServerIdentifierName = "&lt;ifmap-server "
			+ "xmlns=&quot;" + IFMAP_2_2_NAMESPACE + "&quot; "
			+ "administrative-domain=&quot;&quot;&gt;&lt;/ifmap-server&gt;";

	/**
	 * The opening XML tag for server-capability metadata.
	 */
	private final String mServerCapabilityMetadataHeadXml =
			"<" + IFMAP_2_2_NAMSPACE_PREFIX + ":server-capability "
					+ "xmlns:" + IFMAP_2_2_NAMSPACE_PREFIX + "=\"" + IFMAP_2_2_NAMESPACE + "\" "
					+ "ifmap-cardinality=\"singleValue\">";

	/**
	 * Template string for server capability metadata elements.
	 */
	private final String mServerCapabilityTemplateXml =
			"<capability>%s</capability>";

	/**
	 * The closing XML tag for server capability metadata.
	 */
	private final String mServerCapabilityMetadataTailXml =
			"</" + IFMAP_2_2_NAMSPACE_PREFIX + ":server-capability>";

	/**
	 * XML for irond root link metadata.
	 */
	private final String mRootLinkMetadataXml =
			"<" + IROND_NAMESPACE_PREFIX + ":root-link "
					+ "xmlns:" + IROND_NAMESPACE_PREFIX + "=\"" + IROND_NAMESPACE + "\" "
					+ "ifmap-cardinality=\"singleValue\" />";

	/**
	 * The publisher used by irond.
	 */
	private final Publisher mIrondPublisher =
			new Publisher("irond", "invalid-session-id-for-irond-publisher", 0,
					new ClientIdentifier("irond"));

	/**
	 * Lifetime of irond specific root link metadata.
	 */
	private final MetadataLifeTime mRootLinkMetadataLifetime =
			MetadataLifeTime.forever;

	/**
	 * Lifetime of server-capability metadata.
	 */
	private final MetadataLifeTime mServerCapabilityMetadataLifetime =
			MetadataLifeTime.forever;


	private final DocumentBuilderFactory mDocumentBuilderFactory;

	private final DocumentBuilder mDocumentBuilder;


	PublishService(PublisherRep pRep, GraphElementRepository graphRep,
			MetadataHolderFactory metaHolderFac, SubscriptionService subServ,
			DataModelServerConfigurationProvider conf) {
		mPublisherRep = pRep;
		mGraph = graphRep;
		mMetaHolderFac = metaHolderFac;
		mSubService	= subServ;
		mConf = conf;
		mIsRootLinkEnabled = mConf.isRootLinkEnabled();

		mDocumentBuilderFactory = DocumentBuilderFactory.newInstance();
		mDocumentBuilderFactory.setNamespaceAware(true);

		mPublisherRep.addPublisher(mIrondPublisher.getPublisherId(), mIrondPublisher.getSessionId(), 0, new ClientIdentifier("irond"));
		try {
			mMapServerIdentifier = new Identity(mMapServerIdentifierName, "", "extended", IdentityTypeEnum.other);
		} catch (InvalidIdentifierException e) {
			throw new RuntimeException("could not create MAP server identifier", e);
		}
		try {
			mDocumentBuilder = mDocumentBuilderFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			throw new RuntimeException(e);
		}

		addServerCapabilities(mMapServerIdentifier);
	}

	private void addServerCapabilities(Identifier mapServerIdentifier) {
		try {
			// create server-capability metadata XML string
			String capabilitiesXml = mServerCapabilityMetadataHeadXml;
			capabilitiesXml += String.format(mServerCapabilityTemplateXml, "ifmap-base-version-2.2");
			if (mConf.isRootLinkEnabled()) {
				capabilitiesXml += String.format(mServerCapabilityTemplateXml, "irond-root-link");
			}
			capabilitiesXml += mServerCapabilityMetadataTailXml;

			Document capabilitiesDoc = stringToDocument(capabilitiesXml);
			W3cXmlMetadata capabilitiesMetadata = new W3cXmlMetadata(
					capabilitiesDoc, new MetadataTypeImpl(IFMAP_2_2_NAMESPACE + ":" + IFMAP_2_2_NAMSPACE_PREFIX,
							MetaCardinalityType.singleValue),
					false);

			List<Metadata> metadata = new ArrayList<Metadata>();
			metadata.add(capabilitiesMetadata);
			PublishUpdate update = new PublishUpdate(mapServerIdentifier, metadata, mServerCapabilityMetadataLifetime);
			List<SubPublishRequest> publishRequests = new ArrayList<SubPublishRequest>();
			publishRequests.add(update);

			PublishRequest publishRequest = new PublishRequest(mIrondPublisher.getSessionId(), publishRequests);

			/* Check if the root-link feature is enabled and disable it for the
			 * following operation to prevent the creation of root-links to
			 * the MAP server identifier.
			 * TODO check if this may cause problems/race conditions while initializing irond
			 */
			if (mIsRootLinkEnabled) {
				mIsRootLinkEnabled = false;
				publish(publishRequest);
				mIsRootLinkEnabled = true;
			} else {
				publish(publishRequest);
			}
		} catch (InvalidMetadataException e) {
			throw new RuntimeException(e);
		} catch (RequestCreationException e) {
			throw new RuntimeException("could not create fake request for server-capability metadata", e);
		}
	}

	/**
	 * Parse the given XML string.
	 *
	 * @param xml the XML string to parse
	 * @return the parsed XML document
	 * @throws RuntimeException if the parsing fails
	 */
	private Document stringToDocument(String xml) {
		try {
			return mDocumentBuilder.parse(new InputSource(new StringReader(xml)));
		} catch (Exception e) {
			throw new RuntimeException("could not parse '" + xml + "' as XML", e);
		}
	}

	/**
	 * Go through the whole PublishRequest and dispatch the elements to
	 * the corresponding process* methods
	 *
	 * @param req
	 * @throws InvalidMetadataException
	 * @throws SearchException
	 */
	void publish(PublishRequest req) throws InvalidMetadataException {
		List<SubPublishRequest> list = req.getSubPublishRequestList();
		String sId = req.getSessionId();
		Publisher pub = mPublisherRep.getPublisherBySessionId(sId);
		String pId = pub.getPublisherId();
		List<MetadataHolder> changes =
					CollectionHelper.provideListFor(MetadataHolder.class);

		sLogger.trace(sName + ": process publishRequest for session " + sId);
		Publisher publisher = mPublisherRep.getPublisherBySessionId(sId);

		sLogger.trace(sName + ": " + publisher + " with " + list.size() + " requests");

		for (SubPublishRequest sreq : list) {
			if (sreq instanceof PublishNotify) {
				processPublishNotify(pub, (PublishNotify) sreq, changes);
			} else if (sreq instanceof PublishUpdate) {
				processPublishUpdate(pub, (PublishUpdate)sreq, changes);
			} else if (sreq instanceof PublishDelete) {
				processPublishDelete(pub, (PublishDelete)sreq, changes);
			} else {
				throw new SystemErrorException("Unknown SubPublishRequest implementation");
			}
		}

		// set timestamp and publisher id _now_ and set references to
		// GraphElements and Publishers...
		Date now = new Date();
		for (MetadataHolder mh : changes) {
			Metadata m = mh.getMetadata();

			if (mh.isNew() || mh.isNotify()) {
				addOperationalAttributes(m, now, mh.getPublisher().getPublisherId());
			}
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
				if (mConf.isSanityChecksEnabled()) {
					if (!changes.contains(mh)) {
						throw new SystemErrorException("replaced metadata "
								+ "not in changes");
					}
				}

				mh.setState(MetadataState.DELETED);
				break;
			default:
				// Don't consider REPLACED, DELETED or NOTIFY metadata
			}
		}
	}

	private void addOperationalAttributes(Metadata m, Date timeNow, String pId) {
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

		if (mIsRootLinkEnabled) {
			addLinkToRoot(i1, mMapServerIdentifier, changes, state);
			addLinkToRoot(i2, mMapServerIdentifier, changes, state);
		}

		for (Metadata m : mlist) {

			/* singleValue metadata replaces existing metadata */
			if (m.isSingleValue()) {
				removeExistingSingleValueMetadata(m, graphElement, changes);
			}


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
		if (!m.isSingleValue()) {
			return;
		}

		mhs = ge.getMetadataHolder(m.getType());

		// no metadata of the same type, shortcut
		if (mhs.size() == 0) {
			return;
		}

		// filter out all notify metadata
		tmpMhs = CollectionHelper.provideListFor(MetadataHolder.class);
		for (MetadataHolder mh : mhs) {
			if (!mh.isNotify()) {
				tmpMhs.add(mh);
			}
		}

		mhs = tmpMhs;

		// Sanity check: Never should there be more than a single singleValue
		// metadata object on a graph object, unless it's notify, but we
		// filtered that before.
		if (mhs.size() > 1) {
			throw new SystemErrorException("singleValue occures more than once");
		}

		// Nothing really  to remove here
		if (mhs.size() == 0) {
			return;
		}

		removeMe = mhs.get(0);

		// Sanity Check: If metadata is in state NEW or DELETED, it'd better also
		// be in the list of changes
		if (removeMe.isNew() || removeMe.isDeleted()) {
			if (mConf.isSanityChecksEnabled()) {
				if (!changes.contains(removeMe)) {
					throw new SystemErrorException("metadata not in changes");
				}
			}

			// We don't have to do anything here?
		} else {
			// Sanity Check: Neither NEW nor DELTED, so it should *not* be
			// in the list of changes
			if (mConf.isSanityChecksEnabled()) {
				if (changes.contains(removeMe)) {
					throw new SystemErrorException("metadata in changes");
				}
			}

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


	/**
	 * Add a root link between 'root' and 'identifier' if the given identifier is
	 * not null. The new metadata gets appended to the given changes list.
	 */
	private void addLinkToRoot(Identifier root, Identifier identifier, List<MetadataHolder> changes,
			MetadataState state) {
		if (identifier != null) {
			try {
				GraphElement graphElement =
						mGraph.getGraphElement(identifier, root);
				W3cXmlMetadata rootLinkMetadata = new W3cXmlMetadata(
						stringToDocument(mRootLinkMetadataXml),
						new MetadataTypeImpl(IROND_NAMESPACE + ":" + IROND_NAMESPACE_PREFIX,
								MetaCardinalityType.singleValue),
						false);

				MetadataHolder rootLink = mMetaHolderFac.newMetadataHolder(
						rootLinkMetadata, mRootLinkMetadataLifetime, graphElement, mIrondPublisher);
				changes.add(rootLink);
				rootLink.setState(state);
				setReferences(rootLink);
			} catch (InvalidMetadataException e) {
				throw new RuntimeException(e);
			}
		}
	}
}

