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
package de.hshannover.f4.trust.iron.mapserver.binding;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.Binder;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.validation.Schema;

import org.trustedcomputinggroup._2010.ifmap._2.DeleteSearchRequestType;
import org.trustedcomputinggroup._2010.ifmap._2.DeleteType;
import org.trustedcomputinggroup._2010.ifmap._2.DumpRequestType;
import org.trustedcomputinggroup._2010.ifmap._2.EndSessionType;
import org.trustedcomputinggroup._2010.ifmap._2.MetadataListType;
import org.trustedcomputinggroup._2010.ifmap._2.NewSessionRequestType;
import org.trustedcomputinggroup._2010.ifmap._2.NotifyType;
import org.trustedcomputinggroup._2010.ifmap._2.PollRequestType;
import org.trustedcomputinggroup._2010.ifmap._2.PublishRequestType;
import org.trustedcomputinggroup._2010.ifmap._2.PurgePublisherRequestType;
import org.trustedcomputinggroup._2010.ifmap._2.RenewSessionType;
import org.trustedcomputinggroup._2010.ifmap._2.SearchRequestType;
import org.trustedcomputinggroup._2010.ifmap._2.SearchType;
import org.trustedcomputinggroup._2010.ifmap._2.SubscribeRequestType;
import org.trustedcomputinggroup._2010.ifmap._2.UpdateType;
import org.w3._2003._05.soap_envelope.Body;
import org.w3._2003._05.soap_envelope.Envelope;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import de.hshannover.f4.trust.iron.mapserver.datamodel.identifiers.Identifier;
import de.hshannover.f4.trust.iron.mapserver.datamodel.meta.Metadata;
import de.hshannover.f4.trust.iron.mapserver.datamodel.meta.MetadataFactory;
import de.hshannover.f4.trust.iron.mapserver.datamodel.meta.MetadataLifeTime;
import de.hshannover.f4.trust.iron.mapserver.datamodel.search.Filter;
import de.hshannover.f4.trust.iron.mapserver.datamodel.search.FilterType;
import de.hshannover.f4.trust.iron.mapserver.datamodel.search.TerminalIdentifiers;
import de.hshannover.f4.trust.iron.mapserver.exceptions.InvalidFilterException;
import de.hshannover.f4.trust.iron.mapserver.exceptions.InvalidIdentifierException;
import de.hshannover.f4.trust.iron.mapserver.exceptions.InvalidMetadataException;
import de.hshannover.f4.trust.iron.mapserver.exceptions.RequestCreationException;
import de.hshannover.f4.trust.iron.mapserver.exceptions.SystemErrorException;
import de.hshannover.f4.trust.iron.mapserver.exceptions.UnmarshalException;
import de.hshannover.f4.trust.iron.mapserver.messages.DumpRequest;
import de.hshannover.f4.trust.iron.mapserver.messages.EndSessionRequest;
import de.hshannover.f4.trust.iron.mapserver.messages.NewSessionRequest;
import de.hshannover.f4.trust.iron.mapserver.messages.PollRequest;
import de.hshannover.f4.trust.iron.mapserver.messages.PublishRequest;
import de.hshannover.f4.trust.iron.mapserver.messages.PurgePublisherRequest;
import de.hshannover.f4.trust.iron.mapserver.messages.RenewSessionRequest;
import de.hshannover.f4.trust.iron.mapserver.messages.Request;
import de.hshannover.f4.trust.iron.mapserver.messages.RequestFactory;
import de.hshannover.f4.trust.iron.mapserver.messages.SearchRequest;
import de.hshannover.f4.trust.iron.mapserver.messages.SubPublishRequest;
import de.hshannover.f4.trust.iron.mapserver.messages.SubSubscribeRequest;
import de.hshannover.f4.trust.iron.mapserver.messages.SubscribeDelete;
import de.hshannover.f4.trust.iron.mapserver.messages.SubscribeRequest;
import de.hshannover.f4.trust.iron.mapserver.provider.SchemaProvider;
import de.hshannover.f4.trust.iron.mapserver.utils.CollectionHelper;
import de.hshannover.f4.trust.iron.mapserver.utils.DomHelpers;
import de.hshannover.f4.trust.iron.mapserver.utils.NullCheck;

/**
 * Implementation of the {@link RequestTransformer} interface using JAXB for the
 * XML binding.
 *
 * Functionality originates from the old MessageTransformer class. It was a mess
 * back then and it's still a mess, but we now hide it behind an interface.
 *
 * @author aw
 */
class JaxbRequestUnmarshaller implements RequestUnmarshaller {

	private static DocumentBuilderFactory sDocumentBuilderFactory;

	private ThreadLocal<Binder<Node>> mBinder;
	private ThreadLocal<DocumentBuilder> mDocumentBuilder;

	private final JAXBContext mJaxbCtx;
	private final JaxbIdentifierHelper identifierHelper;
	private final RequestFactory requestFactory;

	private final MetadataFactory mMetaFac;
	private final ValidationEventHandlerFactory mValidationEventHandlerFactory;

	private final Schema mSchema;

	// Static initialization
	static {
		sDocumentBuilderFactory = DocumentBuilderFactory.newInstance();
		sDocumentBuilderFactory.setNamespaceAware(true);
		// done by JAXB...
		sDocumentBuilderFactory.setValidating(false);
	}

	/**
	 * Constructer
	 * 
	 * @param schemaProvider
	 * @param metaFac2
	 */
	JaxbRequestUnmarshaller(MetadataFactory metaFac,
			ValidationEventHandlerFactory mvefac, SchemaProvider schemaProvider) {

		try {
			NullCheck.check(metaFac, "metaFac is null");
			NullCheck.check(mvefac, "fac is null");
			NullCheck.check(schemaProvider, "schemaProvider is null");

			// Schema is allowed to be null, in which case we won't do
			// validation at all
			mSchema = schemaProvider.provideSchema();

			mMetaFac = metaFac;
			mValidationEventHandlerFactory = mvefac;

			// FIXME: Move this one out to the initialization?
			identifierHelper = new JaxbIdentifierHelper();
			requestFactory = RequestFactory.getInstance();
			mJaxbCtx = JAXBContext.newInstance(Envelope.class);

			mBinder = new BinderThreadLocal();
			mDocumentBuilder = new DocumentBuilderThreadLocal();

		} catch (JAXBException e) {
			// go crazy if we can't initialize the JAXB context or something
			// JAXB related
			e.printStackTrace();
			throw new SystemErrorException(e.getMessage());
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Request unmarshal(InputStream requestContent)
			throws UnmarshalException, InvalidIdentifierException,
			InvalidMetadataException, InvalidFilterException {

		SimpleValidationEventHandler validationHandler = null;
		JAXBElement<Envelope> reqEnvJaxb = null;
		Envelope reqEnv = null;
		Body soapBody = null;
		Document requestDocument = null;

		mBinder.set(mJaxbCtx.createBinder());

		// Do we want to validate against the given schema?
		if (mSchema != null) {
			mBinder.get().setSchema(mSchema);
			validationHandler = mValidationEventHandlerFactory
					.newValidationEventHandler();
			try {
				mBinder.get().setEventHandler(validationHandler);
			} catch (JAXBException e) {
				throw new UnmarshalException(e.getMessage());
			}
		}

		try {
			requestDocument = mDocumentBuilder.get().parse(requestContent);
			reqEnvJaxb = (JAXBElement<Envelope>) mBinder.get().unmarshal(
					requestDocument);
		} catch (SAXException e) {
			e.printStackTrace();
			throw new UnmarshalException(e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			throw new UnmarshalException(e.getMessage());
		} catch (JAXBException e) {
			if (validationHandler.hasErrorOccured()) {
				throw new UnmarshalException(
						validationHandler.getErrorMessage());
			}

			e.printStackTrace();
			throw new UnmarshalException(e.getMessage());

		} finally {
			try {
				requestContent.close();
			} catch (IOException e) {
				/* ignored */
				e.printStackTrace();
			}
		}

		if (reqEnvJaxb.getDeclaredType() != Envelope.class) {
			throw new UnmarshalException("No SOAP Envelope found");
		}

		reqEnv = reqEnvJaxb.getValue();

		if (reqEnv == null) {
			throw new UnmarshalException("No SOAP Envelope found");
		}

		soapBody = reqEnv.getBody();

		if (soapBody == null) {
			throw new UnmarshalException("No SOAP Body found");
		}

		try {
			if (soapBody.getDump() != null) {

				return transformDumpRequest(soapBody.getDump());

			} else if (soapBody.getEndSession() != null) {

				return transformEndSessionRequest(soapBody.getEndSession());

			} else if (soapBody.getNewSession() != null) {

				return transformNewSessionRequest(soapBody.getNewSession());

			} else if (soapBody.getPoll() != null) {

				return transformPollRequest(soapBody.getPoll());

			} else if (soapBody.getPublish() != null) {

				return transformPublishRequest(soapBody.getPublish());

			} else if (soapBody.getPurgePublisher() != null) {

				return transformPurgePublisherRequest(soapBody
						.getPurgePublisher());

			} else if (soapBody.getRenewSession() != null) {

				return transformRenewSessionRequest(soapBody.getRenewSession());

			} else if (soapBody.getSearch() != null) {

				return transformSearchRequest(soapBody.getSearch());

			} else if (soapBody.getSubscribe() != null) {

				return transformSubscribeRequest(soapBody.getSubscribe());

			} else {

				throw new UnmarshalException(
						"no IF-MAP operation found in request");
			}
		} catch (RequestCreationException e) {
			throw new UnmarshalException(e.getMessage());
		}
	}

	private Map<String, String> extractNsMap(Node node) {
		Map<String, String> ret = new HashMap<String, String>();
		extractNsMap(node, ret);
		return ret;
	}

	/**
	 * FIXME: I'm afraid this will not work with default namespaces.
	 *
	 * @param w3cNode
	 * @param nsMap
	 */
	private void extractNsMap(Node w3cNode, Map<String, String> nsMap) {

		if (w3cNode == null) {
			return;
		}

		if (w3cNode.getNodeType() == Node.DOCUMENT_NODE) {
			return;
		}

		NamedNodeMap nnm = w3cNode.getAttributes();

		for (int i = 0; i < nnm.getLength(); i++) {
			Node node = nnm.item(i);
			if (node.getNodeType() == Node.ATTRIBUTE_NODE) {
				String nodeName = node.getNodeName();
				String nodeValue = node.getNodeValue();
				if (nodeName.startsWith("xmlns:")) {
					String prefix = nodeName.substring(6); // magic number
															// xmlns:6
					// don't overwrite existing mappings, the inner ones are
					// those
					// that we want to have!
					if (!nsMap.containsKey(prefix)) {
						nsMap.put(nodeName.substring(6), nodeValue);
					}
				}
			}
		}

		extractNsMap(w3cNode.getParentNode(), nsMap);
	}

	private DumpRequest transformDumpRequest(DumpRequestType dump)
			throws RequestCreationException {
		String sessionId = dump.getSessionId();
		String identifier = dump.getIdentifier();
		return requestFactory.createDumpRequest(sessionId, identifier);
	}

	private EndSessionRequest transformEndSessionRequest(
			EndSessionType endSession) throws RequestCreationException {
		String sessionId = endSession.getSessionId();
		return requestFactory.createEndSessionRequest(sessionId);
	}

	private NewSessionRequest transformNewSessionRequest(
			NewSessionRequestType newSession) throws RequestCreationException {
		BigInteger mprsRrecv = newSession.getMaxPollResultSize();
		Integer mprs = mprsRrecv == null ? null : new Integer(
				mprsRrecv.intValue());
		return requestFactory.createNewSessionRequest(mprs);
	}

	private PollRequest transformPollRequest(PollRequestType poll)
			throws RequestCreationException {
		String sessionId = poll.getSessionId();
		return requestFactory.createPollRequest(sessionId);
	}

	private PurgePublisherRequest transformPurgePublisherRequest(
			PurgePublisherRequestType purgePublisher)
			throws RequestCreationException {
		String sessionId = purgePublisher.getSessionId();
		String publisherId = purgePublisher.getIfmapPublisherId();
		return requestFactory.createPurgePublisherRequest(sessionId,
				publisherId);
	}

	private RenewSessionRequest transformRenewSessionRequest(
			RenewSessionType renewSession) throws RequestCreationException {
		String sessionId = renewSession.getSessionId();
		return requestFactory.createRenewSessionRequest(sessionId);
	}

	/**
	 *
	 *
	 * @param search
	 * @return
	 * @throws RequestCreationException
	 * @throws InvalidIdentifierException
	 * @throws InvalidFilterException
	 * @throws UnmarshalException
	 */
	private SearchRequest transformSearchRequest(SearchType search,
			String sessionID) throws RequestCreationException,
			InvalidIdentifierException, InvalidFilterException,
			UnmarshalException {

		Node w3cSearch = null;
		SearchRequest searchRequest = null;
		if (search != null) {
			w3cSearch = mBinder.get().getXMLNode(search);
			Map<String, String> nsMap = extractNsMap(w3cSearch);
			Identifier ident = identifierHelper.extractFromSearch(search,
					mBinder.get());
			Long maxDepth = search.getMaxDepth();
			Long maxSize = search.getMaxSize();
			// if no depth is given, process it with zero depth
			int maxDepthi = maxDepth == null ? 0 : maxDepth.intValue();
			Integer maxSizeI = maxSize == null ? null : new Integer(
					maxSize.intValue());

			Filter matchLinks = FilterFactory.newFilter(search.getMatchLinks(),
					nsMap, FilterType.MATCH_LINKS_FILTER);
			Filter resultFilter = FilterFactory.newFilter(
					search.getResultFilter(), nsMap, FilterType.RESULT_FILTER);
			TerminalIdentifiers terminalIdents = new TerminalIdentifiers(
					search.getTerminalIdentifierType());

			if (search instanceof SearchRequestType) {
				sessionID = ((SearchRequestType) search).getSessionId();
			}

			searchRequest = requestFactory.createSearchRequest(sessionID,
					maxDepthi, maxSizeI, terminalIdents, ident, matchLinks,
					resultFilter);
		}
		return searchRequest;
	}

	private SearchRequest transformSearchRequest(SearchType search)
			throws RequestCreationException, InvalidIdentifierException,
			InvalidFilterException, UnmarshalException {
		return transformSearchRequest(search, null);
	}

	private PublishRequest transformPublishRequest(PublishRequestType publish)
			throws RequestCreationException, InvalidIdentifierException,
			InvalidMetadataException, InvalidFilterException,
			UnmarshalException {
		PublishRequest publishRequest = null;
		List<Object> list = null;
		List<SubPublishRequest> reqlist = null;

		if (publish != null) {
			String sid = publish.getSessionId();

			list = publish.getUpdateOrNotifyOrDelete();
			reqlist = transformToSubPublishRequestList(list);
			publishRequest = requestFactory.createPublishRequest(sid, reqlist);
		}
		return publishRequest;
	}

	private SubscribeRequest transformSubscribeRequest(SubscribeRequestType srt)
			throws RequestCreationException, InvalidIdentifierException,
			InvalidFilterException, UnmarshalException {

		SubscribeRequest sr = null;
		List<Object> list = null;
		List<SubSubscribeRequest> sublist = null;

		if (srt != null) {
			String sid = srt.getSessionId();
			list = srt.getUpdateOrDelete();
			sublist = transformToSubSubscribeList(list, srt.getSessionId());
			sr = requestFactory.createSubscribeRequest(sid, sublist);
		}

		return sr;
	}

	private List<SubSubscribeRequest> transformToSubSubscribeList(
			List<Object> list, String sessionID)
			throws RequestCreationException, InvalidIdentifierException,
			InvalidFilterException, UnmarshalException {

		ArrayList<SubSubscribeRequest> ssr = new ArrayList<SubSubscribeRequest>();

		if (list == null) {
			return ssr;
		}

		for (int i = 0; i < list.size(); i++) {
			if (list.get(i) instanceof DeleteSearchRequestType) {
				ssr.add(new SubscribeDelete(((DeleteSearchRequestType) list
						.get(i)).getName()));
			} else if (list.get(i) instanceof SubscribeRequestType.Update) {
				SearchRequest sr = transformSearchRequest(
						(SubscribeRequestType.Update) list.get(i), sessionID);

				ssr.add(requestFactory.createSubscribeUpdate(
						((SubscribeRequestType.Update) list.get(i)).getName(),
						sr));
			} else {
				throw new UnmarshalException(
						"unknown subscribe operation found");
			}
		}

		return ssr;
	}

	/**
	 * Wrapper to transform a list of objects which can be of type UpdateType,
	 * DeleteType and NotifyType into
	 *
	 *
	 * @param list
	 * @return
	 * @throws InvalidIdentifierException
	 * @throws RequestCreationException
	 * @throws InvalidMetadataExceptionn
	 * @throws InvalidFilterException
	 * @throws UnmarshalException
	 */
	private List<SubPublishRequest> transformToSubPublishRequestList(
			List<Object> list) throws InvalidIdentifierException,
			RequestCreationException, InvalidMetadataException,
			InvalidFilterException, UnmarshalException {
		List<SubPublishRequest> retList = new ArrayList<SubPublishRequest>();

		if (list != null) {
			for (Object o : list) {
				SubPublishRequest tret = transformToSubPublishRequest(o);
				if (tret != null) {
					retList.add(tret);
				}
			}
		}
		return retList;
	}

	private SubPublishRequest transformToSubPublishRequest(Object o)
			throws RequestCreationException, InvalidIdentifierException,
			InvalidMetadataException, InvalidFilterException,
			UnmarshalException {
		SubPublishRequest ret = null;
		if (o != null) {
			if (o instanceof UpdateType) {
				ret = transformUpdateType((UpdateType) o);
			} else if (o instanceof DeleteType) {
				ret = transformDeleteType((DeleteType) o);
			} else if (o instanceof NotifyType) {
				ret = transformNotifyType((NotifyType) o);
			} else {
				throw new UnmarshalException("unknown element in publish list");
			}
		}
		return ret;
	}

	/**
	 * FIXME: Come back here and check what the lifetime should be...
	 *
	 * @param value
	 * @return
	 * @throws InvalidMetadataExceptionn
	 * @throws InvalidIdentifierException
	 * @throws RequestCreationException
	 * @throws UnmarshalException
	 */
	private SubPublishRequest transformNotifyType(NotifyType value)
			throws InvalidMetadataException, InvalidIdentifierException,
			RequestCreationException, UnmarshalException {
		SubPublishRequest ret = null;
		if (value != null) {
			MetadataLifeTime lt;
			String ltstr = value.getLifetime();
			if (ltstr.equals(MetadataLifeTime.session.toString())) {
				lt = MetadataLifeTime.session;
			} else {
				lt = MetadataLifeTime.forever;
			}

			List<Metadata> mlist = transformMetadata(value.getMetadata(), lt);
			Identifier idents[] = identifierHelper.extractFromNotify(value,
					mBinder.get());
			ret = requestFactory.createPublishNotifyRequest(idents[0],
					idents[1], mlist, lt);
		}

		return ret;
	}

	private SubPublishRequest transformDeleteType(DeleteType value)
			throws InvalidIdentifierException, RequestCreationException,
			InvalidFilterException, UnmarshalException {
		SubPublishRequest ret = null;
		Node w3cNode = null;
		if (value != null) {
			w3cNode = mBinder.get().getXMLNode(value);
			Identifier idents[] = identifierHelper.extractFromDelete(value,
					mBinder.get());
			Map<String, String> nsMap = extractNsMap(w3cNode);
			Filter f = FilterFactory.newFilter(value.getFilter(), nsMap,
					FilterType.DELETE_FILTER);
			ret = requestFactory.createPublishDeleteRequest(idents[0],
					idents[1], f);
		}
		return ret;
	}

	private SubPublishRequest transformUpdateType(UpdateType value)
			throws InvalidIdentifierException, RequestCreationException,
			InvalidMetadataException, UnmarshalException {

		SubPublishRequest ret = null;
		if (value != null) {
			MetadataLifeTime lt;
			String ltstr = value.getLifetime();
			if (ltstr == null) {
				throw new UnmarshalException("metadata lifetime not given");
			}
			if (ltstr.equals(MetadataLifeTime.session.toString())) {
				lt = MetadataLifeTime.session;
			} else {
				lt = MetadataLifeTime.forever;
			}
			if (value.getMetadata() == null) {
				throw new InvalidMetadataException(
						"No Metadata in update element found.");

			}
			List<Metadata> mlist = transformMetadata(value.getMetadata(), lt);
			Identifier idents[] = identifierHelper.extractFromUpdate(value,
					mBinder.get());
			ret = requestFactory.createPublishUpdateRequest(idents[0],
					idents[1], mlist, lt);
		}

		return ret;
	}

	/**
	 * Method to transform the autogenerated MetadataList into a List of
	 * datamodel Metadata.
	 *
	 * This method transforms to the XMLMetadata implementation using DOMBuilder
	 * from JDOM to create a JDOM Document from the Document objects in the
	 * MetadataListType.
	 *
	 *
	 * @param metaDataListType
	 * @return List of Metadata that the datamodel can work with
	 * @throws InvalidMetadataExceptionn
	 */
	private List<Metadata> transformMetadata(MetadataListType metaDataListType,
			MetadataLifeTime lifetime) throws InvalidMetadataException {
		List<Metadata> lmeta = CollectionHelper.provideListFor(Metadata.class);
		List<Object> lo = metaDataListType.getAny();

		if (lo == null || lo.size() == 0) {
			throw new InvalidMetadataException(
					"No metadata in metadata list found");
		}

		for (Object o : lo) {
			Metadata metadata;
			Element el;
			// I have no idea why this works...
			try {
				el = (Element) o;
				Document mdDoc = DomHelpers.deepCopy(el);
				metadata = mMetaFac.newMetadata(mdDoc);
			} catch (Exception e) {
				throw new InvalidMetadataException(e.getMessage());
			}

			lmeta.add(metadata);
		}
		return lmeta;
	}

	/**
	 * Simple thread-local class for the {@link DocumentBuilder}
	 */
	private class DocumentBuilderThreadLocal extends
			ThreadLocal<DocumentBuilder> {

		@Override
		protected DocumentBuilder initialValue() {
			try {
				return sDocumentBuilderFactory.newDocumentBuilder();
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
				throw new SystemErrorException(e.getMessage());
			}
		}
	}

	/**
	 * Simple thread-local class for the {@link Binder}
	 */
	private class BinderThreadLocal extends ThreadLocal<Binder<Node>> {

		@Override
		protected Binder<Node> initialValue() {
			return null;
		}
	}
}
