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
package de.hshannover.f4.trust.iron.mapserver.contentauth;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;

import de.hshannover.f4.trust.iron.mapserver.IfmapConstStrings;
import de.hshannover.f4.trust.iron.mapserver.communication.ClientIdentifier;
import de.hshannover.f4.trust.iron.mapserver.contentauth.IfmapPepHandler.IfmapDecisionRequest;
import de.hshannover.f4.trust.iron.mapserver.contentauth.IfmapPepHandler.IfmapOp;
import de.hshannover.f4.trust.iron.mapserver.contentauth.IfmapPepHandler.PdpType;
import de.hshannover.f4.trust.iron.mapserver.datamodel.Publisher;
import de.hshannover.f4.trust.iron.mapserver.datamodel.graph.GraphElement;
import de.hshannover.f4.trust.iron.mapserver.datamodel.graph.GraphElementRepository;
import de.hshannover.f4.trust.iron.mapserver.datamodel.graph.Link;
import de.hshannover.f4.trust.iron.mapserver.datamodel.graph.Node;
import de.hshannover.f4.trust.iron.mapserver.datamodel.identifiers.Identifier;
import de.hshannover.f4.trust.iron.mapserver.datamodel.meta.Metadata;
import de.hshannover.f4.trust.iron.mapserver.datamodel.meta.MetadataHolder;
import de.hshannover.f4.trust.iron.mapserver.datamodel.search.Filter;
import de.hshannover.f4.trust.iron.mapserver.exceptions.ServerInitialException;
import de.hshannover.f4.trust.iron.mapserver.exceptions.SystemErrorException;
import de.hshannover.f4.trust.iron.mapserver.messages.PublishDelete;
import de.hshannover.f4.trust.iron.mapserver.messages.PublishNotify;
import de.hshannover.f4.trust.iron.mapserver.messages.PublishRequest;
import de.hshannover.f4.trust.iron.mapserver.messages.PublishUpdate;
import de.hshannover.f4.trust.iron.mapserver.messages.SubPublishRequest;
import de.hshannover.f4.trust.iron.mapserver.provider.LoggingProvider;
import de.hshannover.f4.trust.iron.mapserver.provider.RoleMapperProvider;
import de.hshannover.f4.trust.iron.mapserver.provider.ServerConfigurationProvider;
import de.hshannover.f4.trust.iron.mapserver.utils.CollectionHelper;
import de.hshannover.f4.trust.iron.mapserver.utils.NullCheck;

/**
 * Implementation of the {@link IfmapPep} interface.
 *
 * @author aw
 */
class IfmapPepImpl implements IfmapPep {

	private static final Logger sLogger = LoggingProvider.getDecisionRequestLogger();
	private static final String sName = "PEP";

	private final IfmapPepHandler mHandler;
	private final RoleMapperProvider mRoleProvider;
	private final boolean mDryRun;
	private final boolean mRawLog;
	private final PdpType mPdpType;
	private final String mPdpParams;
	private final Map<String, String> mEmptyMap;

	private final Collection<String> mIdentAttrList;
	private final Collection<String> mMetadataAttrList;

	private final ExecutorService mExecutor;


	IfmapPepImpl(ServerConfigurationProvider conf, RoleMapperProvider roleProv) throws ServerInitialException {
		NullCheck.check(conf, "conf is null");
		NullCheck.check(roleProv, "roleProv is null");
		mPdpType = conf.getPdpType();
		mPdpParams = conf.getPdpParameters();
		mRoleProvider = roleProv;
		mDryRun = conf.isPdpDryRun();
		mRawLog = conf.isPdpDecisionRequestRawLog();
		IfmapPepHandler tmp = IfmapPepHandlers.handlerFor(mPdpType, mPdpParams, mRawLog);
		mExecutor = Executors.newFixedThreadPool(conf.getPdpThreads());
		mEmptyMap = Collections.unmodifiableMap(
				CollectionHelper.provideMapFor(String.class, String.class));


		// Used to extract all top-level attributes from metadata and
		// identifier elements.
		mIdentAttrList = conf.getPdpSelectedIdentifierAttributes();
		mMetadataAttrList = conf.getPdpSelectedMetadataAttributes();

		// If we do caching, put a cache handler around it...
		if (conf.isEnablePdpCache()) {
			tmp = IfmapPepHandlers.getCache(tmp, conf.getPdpCacheTtl(),
											conf.getPdpCacheMaxEntries());
		}

		mHandler = tmp;
	}

	@Override
	public boolean isAuthorized(Publisher pub, PublishRequest req,
			GraphElementRepository graph) {

		List<Future<DecisionResult>> futures = new ArrayList<Future<DecisionResult>>();

		startPublishDecisionRequest(futures, req, pub, graph);

		return quickWait(futures);
	}

	private void startPublishDecisionRequest(List<Future<DecisionResult>> results,
			PublishRequest req, Publisher pub, GraphElementRepository graph) {

		for (SubPublishRequest sreq : req.getSubPublishRequestList()) {
			List<Identifier> idents = CollectionHelper.provideListFor(Identifier.class);

			if (sreq.getIdent1() != null) {
				idents.add(sreq.getIdent1());
			}

			if (sreq.getIdent2() != null) {
				idents.add(sreq.getIdent2());
			}

			GraphElement ge = graph.getGraphElement(sreq.getIdent1(), sreq.getIdent2());
			boolean clob = false;
			ClientIdentifier clId = pub.getClientIdentifier();

			switch (sreq.getType()) {

			case UPDATE:
				for (Metadata md : ((PublishUpdate)sreq).getMetadataList()) {
					clob = replaceOthersMetadata(pub, ge, md);
					results.add(mExecutor.submit(
							DT(clId, IfmapOp.update, idents, md, clob)));
				}
				break;

			case DELETE:
				Filter f = ((PublishDelete)sreq).getFilter();

				for (MetadataHolder mh : ge.getMetadataHolderInGraph(f, false)) {
					Metadata md = mh.getMetadata();
					clob = !samePublisher(pub, mh);
					results.add(mExecutor.submit(
							DT(clId, IfmapOp.delete, idents, md, clob)));
				}
				break;

			case NOTIFY:
				for (Metadata md : ((PublishNotify)sreq).getMetadataList()) {
					// notify never clobbers
					clob = false;
					results.add(mExecutor.submit(
							DT(clId, IfmapOp.notify, idents, md)));
				}

				break;

			default:
				throw new SystemErrorException("unknown publish operation");
			}
		}
	}


	private boolean makeDecisionRequestFor(ClientIdentifier clId, IfmapOp op,
			Identifier i, Metadata m, boolean isLink, boolean clob,
			boolean dryRun, List<String> roles) {

		NullCheck.check(clId, "clId is null");
		NullCheck.check(i, "i is null");
		NullCheck.check(m, "i is null");

		boolean isSelfIdent = false; // Figure it out using clId and i
		boolean isClIdent = false; // Figure it out using i

		Map<String, String> identAttrs =
				CollectionHelper.provideMapFor(String.class, String.class);
		Map<String, String> metaAttrs =
				CollectionHelper.provideMapFor(String.class, String.class);


		Element identXml = (Element)i.getXmlDocument().getFirstChild();
		Element mdXml = (Element)m.toW3cDocument().getFirstChild();

		findAttributes(identAttrs, mIdentAttrList, identXml);
		findAttributes(metaAttrs, mMetadataAttrList, mdXml);

		boolean res = false;
		IfmapDecisionRequest dreq = null;

		dreq = new IfmapDecisionRequest(
				op,
				i.getTypeString(),
				identAttrs,
				m.getType().getTypeString(),
				metaAttrs,
				isLink,
				clob,
				dryRun,
				isSelfIdent,
				isClIdent,
				roles);


		res = mHandler.isAuthorized(dreq);

		sLogger.debug(sName + ": " + (dryRun ? "[dry-run] " : " ")
				+ (res ? "permit" : "denial") + " for " + dreq.toString());

		return res;
	}

	private void findAttributes(Map<String, String> attrMap,
			Collection<String> attrList, Element xmlEl) {

		for (String attrName : attrList) {

			Attr attr = xmlEl.getAttributeNode(attrName);

			if (attr != null) {
				String val = attr.getValue();
				attrMap.put(attrName, val);

			} else if (attrName.equals(IfmapConstStrings.ADOM_ATTR)) {
				// the attribute selector was administrative-domain, but
				// there was no administrative-domain. Add it with an
				// empty value
				attrMap.put(attrName, "");
			}
			// All other cases are irrelevant.
		}
	}

	/**
	 * Wrap the {@link IfmapPepHandler} calls.
	 *
	 * @param clId
	 * @param op
	 * @param idents
	 * @param md
	 * @param clob
	 * @return
	 */
	private boolean isAuthorized(ClientIdentifier clId, IfmapOp op,
			List<Identifier> idents, Metadata md, boolean clob) {

		List<String> roles = mRoleProvider.getRolesOf(clId);
		boolean res = true;
		boolean resDry = false;
		boolean link = false;

		if (idents.size() == 0 || idents.size() > 2) {
			throw new SystemErrorException("Bad ident count=" + idents.size());
		}

		link = idents.size() == 2;

		for (Identifier i : idents) {
			res = makeDecisionRequestFor(clId, op, i, md, link, clob, false, roles);
			resDry = res;

			// if dryRun is enabled, we need to send a second request...
			if (isDryRun()) {
				resDry = makeDecisionRequestFor(clId, op, i, md, link, clob, true, roles);
			}

			if (resDry != res) {
				sLogger.debug(sName + ": Note, dry-run policy different result");
			}

			// quick way out...
			if (!res) {
				return false;
			}
		}

		return true;
	}

	private boolean replaceOthersMetadata(Publisher pub, GraphElement ge, Metadata md) {

		String ourPubId = pub.getPublisherId();
		String otherPubId = null;
		List<MetadataHolder> mhs = null;

		// update multiValue never clobbers
		if (md.isMultiValue()) {
			return false;
		}

		mhs = ge.getMetadataHolder(md.getType());

		for (MetadataHolder mh : mhs) {
			otherPubId = mh.getPublisher().getPublisherId();

			if (!ourPubId.equals(otherPubId)) {
				return true;
			}
		}

		return false;
	}

	private boolean samePublisher(Publisher pub, MetadataHolder mh) {
		return pub.getPublisherId().equals(mh.getPublisher().getPublisherId());
	}

	private boolean isDryRun() {
		return mDryRun;
	}

	@Override
	public boolean isAuthorized(Publisher purger, String pubId) {
		boolean res = false;
		NullCheck.check(purger, "purger is null");
		NullCheck.check(pubId, "pubId is null");

		IfmapDecisionRequest dreq = null;
		ClientIdentifier clId = purger.getClientIdentifier();
		List<String> roles = mRoleProvider.getRolesOf(clId);
		boolean clobber = !purger.getPublisherId().equals(pubId);

		dreq = new IfmapDecisionRequest(IfmapOp.purgePublisher,
				null,
				mEmptyMap, // ident attrs
				null,
				mEmptyMap, // meta attrs
				false,
				clobber,
				isDryRun(),
				false,		// client identifier
				false, // self identifier
				roles);

		res = mHandler.isAuthorized(dreq);

		sLogger.debug(sName + ": " + (isDryRun() ? "dryrun: " : "")
					+ (res ? "permit" : "denial")
					+ " for " + dreq.toString());

		return res || isDryRun();
	}


	@Override
	public List<MetadataHolder> isSearchAuthorized(Publisher pub, List<MetadataHolder> mhs) {
		List<Future<DecisionResult>> futures = new ArrayList<Future<DecisionResult>>();
		ClientIdentifier clId = pub.getClientIdentifier();
		List<MetadataHolder> ret = CollectionHelper.provideListFor(MetadataHolder.class);

		// Create tasks
		for (MetadataHolder mh : mhs) {
			List<Identifier> idents = CollectionHelper.provideListFor(Identifier.class);
			GraphElement ge = mh.getGraphElement();
			Metadata meta = mh.getMetadata();

			if (ge instanceof Link) {
				idents.add(((Link)ge).getNode1().getIdentifier());
				idents.add(((Link)ge).getNode2().getIdentifier());
			} else if (ge instanceof Node) {
				idents.add(((Node)ge).getIdentifier());
			} else {
				throw new SystemErrorException("not link nor node?!");
			}

			futures.add(mExecutor.submit(DT(clId, IfmapOp.search, idents, meta, mh)));
		}

		List<DecisionResult> results = wait(futures);

		for (DecisionResult dr : results) {
			if (dr.res) {
				ret.add((MetadataHolder)dr.aux);
			}
		}

		return ret;
	}

	/**
	 * @param futures
	 * @return all DecisionResult objects after they've been waited for.
	 */
	private List<DecisionResult> wait(List<Future<DecisionResult>> futures) {

		DecisionResult result = null;
		List<DecisionResult> ret = CollectionHelper.provideListFor(DecisionResult.class);

		for (Future<DecisionResult> future : futures) {
			boolean completed = false;

			while (!completed) {
				try {
					result = future.get();
					ret.add(result);
					completed = true;
				} catch (InterruptedException e) {
					continue;
				} catch (ExecutionException e) {
					e.printStackTrace();
				}
			}
		}

		return ret;
	}

	/**
	 * If a single result has res set to false, abort and return false,
	 * otherwise return true.
	 *
	 * @param futures
	 * @return
	 */
	private boolean quickWait(List<Future<DecisionResult>> futures) {

		for (Future<DecisionResult> future : futures) {
			DecisionResult result = null;
			while (result == null) {
				try {
					result = future.get();
				} catch (InterruptedException e) {
					continue;
				} catch (ExecutionException e) {
					e.printStackTrace();
				}
			}

			// Any of the results evaluated to false, we don't really need
			// to wait for the others, so just cancel *all* of them...
			if (!result.res) {
				for (Future<DecisionResult> toCancel : futures) {
					toCancel.cancel(false);
				}

				return false;								// JUMP OUT!
			}
		}

		return true;
	}

	private DecisionTask DT(ClientIdentifier clId, IfmapOp op, List<Identifier> idents,
			Metadata md, boolean clob, Object o) {
		return new DecisionTask(clId, op, idents, md, clob, o);
	}

	private DecisionTask DT(ClientIdentifier clId, IfmapOp op, List<Identifier> idents,
			Metadata md, boolean clob) {
		return DT(clId, op, idents, md, clob, null);
	}

	private DecisionTask DT(ClientIdentifier clId, IfmapOp op, List<Identifier> idents,
			Metadata md, Object o) {
		return DT(clId, op, idents, md, false, o);
	}

	private DecisionTask DT(ClientIdentifier clId, IfmapOp op, List<Identifier> idents,
			Metadata md) {
		return DT(clId, op, idents, md, null);
	}


	class DecisionResult {
		Object aux;
		boolean res;
	}

	class DecisionTask implements Callable<DecisionResult> {

		private final ClientIdentifier mClId;
		private final IfmapOp mOp;
		private final List<Identifier> mIdents;
		private final Metadata mMeta;
		private final boolean mClobber;
		private final Object mAuxData;

		DecisionTask(ClientIdentifier id, IfmapOp op, List<Identifier> idents,
				Metadata m, boolean clobber, Object auxData) {

			mClId = id;
			mOp = op;
			mIdents = idents;
			mMeta = m;
			mClobber = clobber;
			mAuxData = auxData;
		}

		@Override
		public DecisionResult call() throws Exception {
			DecisionResult dr = new DecisionResult();
			dr.res = isAuthorized(mClId, mOp, mIdents, mMeta, mClobber);
			dr.aux = mAuxData;
			return dr;
		}
	}
}
