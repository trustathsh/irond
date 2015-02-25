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
 * This file is part of irond, version 0.5.2, implemented by the Trust@HsH
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


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import oasis.names.tc.xacml._2_0.context.schema.os.DecisionType;
import oasis.names.tc.xacml._2_0.context.schema.os.ObjectFactory;
import oasis.names.tc.xacml._2_0.context.schema.os.RequestType;
import oasis.names.tc.xacml._2_0.context.schema.os.ResponseType;
import oasis.names.tc.xacml._2_0.context.schema.os.ResultType;
import oasis.names.tc.xacml._2_0.context.schema.os.StatusCodeType;
import oasis.names.tc.xacml._2_0.context.schema.os.StatusDetailType;
import oasis.names.tc.xacml._2_0.context.schema.os.StatusType;

import com.sun.xacml.BindingUtility;
import com.sun.xacml.ctx.Result;
import com.sun.xacml.ctx.Status;
import com.sun.xacml.ctx.StatusDetail;

import de.hshannover.f4.trust.iron.mapserver.exceptions.SystemErrorException;

/**
 * So we are using a sunxacml version which seems to be somewhere in the
 * transition of using JAXB. This is sh*t, because we can only use the JAXB
 * marshaller/unmarshaller for requests, but not for the responses.
 *
 * This class provides some helpers to get around that thing...
 *
 * @author aw
 */
class SunXacmlGlue {

	/* Creating Marshaller and Unmarshaller is expensive, use ThreadLocals */
	private static ThreadLocalUnmarshaller sUnmarshaller = new ThreadLocalUnmarshaller();
	private static ThreadLocalMarshaller sMarshaller = new ThreadLocalMarshaller();

	/**
	 * @param ctx
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	static ResponseType results2ResponseType(Set results) {

		ResponseType respType = new ResponseType();
		List<ResultType> rtResults = respType.getResult();

		for (Object r : results) {
			rtResults.add(result2ResultType((Result)r));
		}

		return respType;
	}

	static Set<Result> responseType2Results(ResponseType respType) {
		Set<Result> ret = new HashSet<Result>();

		List<ResultType> results = respType.getResult();

		for (ResultType rt : results) {
			ret.add(resultType2Result(rt));

		}

		return ret;
	}

	static InputStream request2Is(RequestType reqType) throws JAXBException {

		ObjectFactory of = new ObjectFactory();
		JAXBElement<RequestType> jaxbReqType = of.createRequest(reqType);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		sMarshaller.get().marshal(jaxbReqType, baos);

		return new ByteArrayInputStream(baos.toByteArray());
	}

	@SuppressWarnings("unchecked")
	static RequestType is2Request(InputStream is) throws JAXBException {
		JAXBElement<RequestType> jaxbEl = null;
		jaxbEl = (JAXBElement<RequestType>) sUnmarshaller.get().unmarshal(is);
		return jaxbEl.getValue();
	}

	private static Result resultType2Result(ResultType rt) {

		int decision = decisionType2Decision(rt.getDecision());
		Status status = statusType2Status(rt.getStatus());

		return new Result(decision, status, rt.getResourceId());
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static Status statusType2Status(StatusType status) {
		List codes = new ArrayList();
		StatusCodeType sct = status.getStatusCode();
		codes.add(sct.getValue());

		return new Status(codes, status.getStatusMessage());
	}

	private static ResultType result2ResultType(Result r) {
		ResultType rt = new ResultType();
		rt.setDecision(intDecision2DecisonEnum(r.getDecision()));
		// We are not taking Obligations into account!!!!

		if (r.getObligations().size() > 0) {
			System.err.println("WARNING: result contains "
								+ r.getObligations().size()
								+ " obligations.");
		}

		rt.setObligations(null);
		rt.setResourceId(r.getResource());
		rt.setStatus(status2StatusType(r.getStatus()));


		return rt;
	}

	private static StatusType status2StatusType(Status status) {
		StatusType st = new StatusType();
		st.setStatusCode(statusCode2StatusCodeType(status.getCode()));

		if (status.getDetail() != null) {
			st.setStatusDetail(statusDetail2StatusDetailType(status.getDetail()));
		}

		st.setStatusMessage(status.getMessage());

		return st;
	}

	private static StatusDetailType statusDetail2StatusDetailType(
			StatusDetail detail) {
		StatusDetailType sdt = new StatusDetailType();
		sdt.getAny().add(detail.getDetail());
		return sdt;
	}

	@SuppressWarnings("rawtypes")
	private static StatusCodeType statusCode2StatusCodeType(List code) {
		StatusCodeType sct = new StatusCodeType();

		if (code.size() == 0 || code.size() > 1) {
			System.err.println("WARNING: result contains "
								+ code.size() + " codes.");
		}

		sct.setValue((String) code.get(0));

		return sct;
	}

	private static DecisionType intDecision2DecisonEnum(int decision) {
		switch (decision) {
		case Result.DECISION_DENY:
			return DecisionType.DENY;

		case Result.DECISION_INDETERMINATE:
			return DecisionType.INDETERMINATE;

		case Result.DECISION_NOT_APPLICABLE:
			return DecisionType.NOT_APPLICABLE;

		case Result.DECISION_PERMIT:
			return DecisionType.PERMIT;
		}

		throw new SystemErrorException("pure sunxacml weirdness!");
	}

	private static int decisionType2Decision(DecisionType decisionType) {
		switch (decisionType) {
		case DENY:
			return Result.DECISION_DENY;

		case INDETERMINATE:
			return Result.DECISION_INDETERMINATE;

		case NOT_APPLICABLE:
			return Result.DECISION_NOT_APPLICABLE;

		case PERMIT:
			return Result.DECISION_PERMIT;
		}

		throw new SystemErrorException("pure sunxacml weirdness");
	}

	static InputStream responseType2Is(ResponseType rt) throws JAXBException {
		ObjectFactory of = new ObjectFactory();
		JAXBElement<ResponseType> jaxbReqType = of.createResponse(rt);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		sMarshaller.get().marshal(jaxbReqType, baos);

		return new ByteArrayInputStream(baos.toByteArray());
	}

	@SuppressWarnings("unchecked")
	static ResponseType is2ResponseType(InputStream is) {
		JAXBElement<ResponseType> jaxbEl = null;
		try {
			jaxbEl = (JAXBElement<ResponseType>) sUnmarshaller.get().unmarshal(is);

			return jaxbEl.getValue();
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		throw new SystemErrorException("unmarshalling failed");
	}

	static void log(RequestType reqType) {

		try {
			ByteArrayInputStream bais = (ByteArrayInputStream) SunXacmlGlue.request2Is(reqType);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			int myByte = 0;

			while ((myByte = bais.read()) >= 0) {
				baos.write(myByte);
			}

		} catch (JAXBException e) {
			// well. logging failed... too bad...
			e.printStackTrace();
		}
	}

	private static class ThreadLocalMarshaller extends ThreadLocal<Marshaller> {

		@Override
		protected Marshaller initialValue() {
			return BindingUtility.createMarshaller();
		}
	}

	private static class ThreadLocalUnmarshaller extends ThreadLocal<Unmarshaller> {

		@Override
		protected Unmarshaller initialValue() {
			try {
				return BindingUtility.getUnmarshaller();
			} catch (JAXBException e) {
				e.printStackTrace();
				System.exit(1);
			}

			// Unreached
			return null;
		}
	}

}
