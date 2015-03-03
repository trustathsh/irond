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
 * This file is part of irond, version 0.5.3, implemented by the Trust@HsH
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


import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.xml.bind.JAXBException;

import oasis.names.tc.xacml._2_0.context.schema.os.ActionType;
import oasis.names.tc.xacml._2_0.context.schema.os.AttributeType;
import oasis.names.tc.xacml._2_0.context.schema.os.AttributeValueType;
import oasis.names.tc.xacml._2_0.context.schema.os.EnvironmentType;
import oasis.names.tc.xacml._2_0.context.schema.os.RequestType;
import oasis.names.tc.xacml._2_0.context.schema.os.ResourceType;
import oasis.names.tc.xacml._2_0.context.schema.os.SubjectType;

import com.sun.xacml.ctx.Result;

import de.hshannover.f4.trust.iron.mapserver.exceptions.SystemErrorException;
import de.hshannover.f4.trust.iron.mapserver.utils.LengthCheck;
import de.hshannover.f4.trust.iron.mapserver.utils.NullCheck;


/**
 * Implementation of a PEP Sun's Implementation and a local
 * policy file.
 *
 * @author aw
 *
 */
abstract class AbstractSunXacml implements IfmapPepHandler {

	static URI tcgUri;
	static URI xacmlUri;
	static URI stringUri;
	static URI booleanUri;
	static URI subjectIdUri;
	static URI roleUri;
	static URI actionUri;
	static URI requestTypeUri;
	static URI publishReqSubTypeUri;
	static URI clobberUri;
	static URI identTypeUri;
	static URI identAttrUri;
	static URI metadataTypeUri;
	static URI metadataAttrUri;
	static URI resourceIdUri;
	static URI adminDomaindUri;
	static URI dryRunUri;
	static URI onLinkUri;
	static URI selfIdentUri;
	static URI clientIdentUri;

	static final String tcXacmlNsPrefix_3_0 = "urn:oasis:names:tc:xacml:3.0:if-map:content:";
	static final String tcXacmlNsPrefix_1_0 = "urn:oasis:names:tc:xacml:1.0:";

	static final String xacmlNs = "fix:this:xacml:ns:";
	static final String xacmlNs10 = "urn:oasis:names:tc:xacml:1.0:";

	static final String tcgNsResource = "urn:oasis:names:tc:xacml:3.0:resource:if-map:content:";
	static final String w3cXmlSchema ="http://www.w3.org/2001/XMLSchema#";

	static {
		try {
			stringUri = new URI(w3cXmlSchema + "string");
			booleanUri = new URI(w3cXmlSchema + "boolean");

			subjectIdUri = new URI(tcXacmlNsPrefix_1_0 + "subject:subject-id");

			roleUri =   new URI(tcXacmlNsPrefix_3_0 + "subject:role");
			dryRunUri = new URI(tcXacmlNsPrefix_3_0 + "environment:dry-run");

			metadataTypeUri = new URI(tcXacmlNsPrefix_3_0 + "resource:metadata-type");
			identTypeUri =    new URI(tcXacmlNsPrefix_3_0 + "resource:identifier-type");
			clientIdentUri =  new URI(tcXacmlNsPrefix_3_0 + "resource:is-map-client-identifier");
			selfIdentUri =    new URI(tcXacmlNsPrefix_3_0 + "resource:is-self-identifier");
			onLinkUri =       new URI(tcXacmlNsPrefix_3_0 + "resource:on-link");
			metadataAttrUri = new URI(tcXacmlNsPrefix_3_0 + "resource:metadata-attribute:");
			identAttrUri =    new URI(tcXacmlNsPrefix_3_0 + "resource:identifier-attribute:");

			actionUri =      new URI(tcXacmlNsPrefix_1_0 + "action:action-id");
			requestTypeUri = new URI(tcXacmlNsPrefix_3_0 + "action:request-type");
			clobberUri =     new URI(tcXacmlNsPrefix_3_0 + "action:delete-metadata-by-other-client");
			publishReqSubTypeUri = new URI(tcXacmlNsPrefix_3_0 + "action:publish-request-subtype");


			resourceIdUri = new URI(xacmlNs10 + "resource:resource-id");
			adminDomaindUri = new URI(tcgNsResource + "administrative-domain");
		} catch (URISyntaxException e) {
			// This should never happend. If it does, make sure we die...
			e.printStackTrace();
			System.exit(1);
		}
	}

	/**
	 * TODO: Documentation
	 *
	 * @param policyFile
	 */
	@Override
	public boolean isAuthorized(IfmapDecisionRequest dreq) {
		NullCheck.check(dreq, "dreq is null");
		boolean outcome = false;

		try {
			RequestType reqType = makeRequestType(dreq);

			// Hooking
			Set<Result> results = doRequestHook(reqType);

			for (Result res : results) {

				switch (res.getDecision()) {
				case Result.DECISION_PERMIT:
					outcome = true;
					break;
				case Result.DECISION_DENY:
					break;
				case Result.DECISION_INDETERMINATE:
					break;
				case Result.DECISION_NOT_APPLICABLE:
					break;
				default:
					throw new SystemErrorException("SUNXACML sucks!");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (JAXBException e) {
			e.printStackTrace();
		}

		return outcome;
	}

	private RequestType makeRequestType(IfmapDecisionRequest dreq) throws URISyntaxException {

		NullCheck.check(dreq, "dreq is null");

		RequestType reqType = new RequestType();
		addSubject(reqType, dreq.getRoles());
		addAction(reqType, dreq.getOp(), dreq.isClobber());
		addResources(reqType, dreq);
		addEnvironment(reqType, dreq.isDryRun());

		return reqType;
	}


	private void addEnvironment(RequestType reqType, boolean dryrun) {
		EnvironmentType et = new EnvironmentType();
		et.getAttribute().add(BA(dryRunUri.toString(), dryrun));
		reqType.setEnvironment(et);
	}

	private void addResources(RequestType reqType, IfmapDecisionRequest dreq) {

		String iType = dreq.getIdentifierType();
		Map<String, String> iAttrs = dreq.getIdentAttrs();

		String mdType = dreq.getMetadataType();
		Map<String, String> mdAttrs = dreq.getMetadataAttrs();

		boolean onLink = dreq.isOnLink();
		boolean clientIdent = dreq.isClientIdent();
		boolean selfIdent = dreq.isSelfIdent();

		NullCheck.check(reqType, "reqType is null");
		NullCheck.check(iAttrs, "identAttrs is null");
		NullCheck.check(mdAttrs, "metadataAttrs is null");

		ResourceType rt = new ResourceType();
		List<AttributeType> attrList = rt.getAttribute();

		if (iType != null) {
			attrList.add(SA(identTypeUri.toString(), iType));


			// Add all attributes
			// FIXME: There are some particular values which should not
			// have type String, but some particular XACML value, like ipAddress
			// Consider everything to be Strings right now, because... it's easier
			// and simply looks much nicer...
			for (Entry<String, String> e : iAttrs.entrySet()) {
				LengthCheck.checkMin(e.getKey(), 1, "attr name length 0?");
				LengthCheck.checkMin(e.getValue(), 1, "attr value length 0?");
				attrList.add(SA(identAttrUri.toString() + e.getKey(), e.getValue()));
			}
		}

		if (mdType != null) {
			attrList.add(SA(metadataTypeUri.toString(), mdType));

			for (Entry<String, String> e : mdAttrs.entrySet()) {
				LengthCheck.checkMin(e.getKey(), 1, "attr name length 0?");
				LengthCheck.checkMin(e.getValue(), 1, "attr value length 0?");
				attrList.add(SA(metadataAttrUri.toString() + e.getKey(), e.getValue()));
			}
		}

		attrList.add(BA(onLinkUri.toString(), onLink));
		attrList.add(BA(selfIdentUri.toString(), selfIdent));
		attrList.add(BA(clientIdentUri.toString(), clientIdent));

		// FIXME: This is needed by SUNXACML
		attrList.add(SA(resourceIdUri.toString(), "STATIC-NO-MEANING"));

		reqType.getResource().add(rt);
	}

	private void addAction(RequestType reqType, IfmapOp op, boolean clobber) {
		ActionType at = new ActionType();
		String opStr = op.toString();

		// This looks so like redundancy...
		if (op == IfmapOp.update || op == IfmapOp.delete || op == IfmapOp.purgePublisher) {
			at.getAttribute().add(BA(clobberUri.toString(), clobber));
		}

		if (op == IfmapOp.search || op == IfmapOp.subscribe) {
			at.getAttribute().add(SA(actionUri.toString(), "read"));
		} else {
			at.getAttribute().add(SA(actionUri.toString(), "write"));
		}

		if (op == IfmapOp.update || op == IfmapOp.delete || op == IfmapOp.notify) {
			at.getAttribute().add(SA(requestTypeUri.toString(), "publish"));
			at.getAttribute().add(SA(publishReqSubTypeUri.toString(), opStr));
		} else {
			at.getAttribute().add(SA(requestTypeUri.toString(), opStr));
		}

		reqType.setAction(at);
	}

	/**
	 * Create subject part.
	 * @param reqType
	 *
	 * @param clientId
	 * @param roles
	 * @return
	 * @throws URISyntaxException
	 */
	private void addSubject(RequestType reqType, List<String> roles) throws URISyntaxException {
		SubjectType subjectType = new SubjectType();
		AttributeType at = null;
		AttributeValueType atv = null;

		// FIXME: This is needed by SUNXACML!
		at = SA(subjectIdUri.toString(), "STATIC");

		subjectType.getAttribute().add(at);

		if (roles.size() > 0) {
			at = SA(roleUri.toString(), roles.get(0));

			for (int i =  1; i < roles.size(); i++) {
				atv = new AttributeValueType();
				atv.getContent().add(roles.get(i));
				at.getAttributeValue().add(atv);
			}

			subjectType.getAttribute().add(at);
		}

		reqType.getSubject().add(subjectType);
	}

	private StringAttribute SA(String id, String val) {
		return new StringAttribute(id, val);
	}

	private BooleanAttribute BA(String id, boolean val) {
		return new BooleanAttribute(id, val);
	}

	class MyAttribute extends AttributeType {
		public MyAttribute(String id, URI type, String val) {
			AttributeValueType atv = new AttributeValueType();
			atv.getContent().add(val);
			this.getAttributeValue().add(atv);
			this.setDataType(type.toString());
			this.setAttributeId(id);
		}
	}

	class StringAttribute extends MyAttribute {
		public StringAttribute(String id, String val) {
			super(id, stringUri, val);
		}
	}

	class BooleanAttribute extends MyAttribute {
		public BooleanAttribute(String id, boolean val) {
			super(id, booleanUri, val ? "true" : "false");
		}
	}

	/**
	 * Hook method for an actual implementation.
	 *
	 * @param reqType
	 * @return
	 * @throws IOException
	 * @throws JAXBException
	 */
	protected abstract Set<Result> doRequestHook(RequestType reqType)
			throws IOException, JAXBException;
}
