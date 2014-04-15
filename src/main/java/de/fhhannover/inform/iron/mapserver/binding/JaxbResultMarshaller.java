package de.fhhannover.inform.iron.mapserver.binding;

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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;

import org.trustedcomputinggroup._2010.ifmap._2.DeleteResultType;
import org.trustedcomputinggroup._2010.ifmap._2.DumpResponseType;
import org.trustedcomputinggroup._2010.ifmap._2.ErrorResultType;
import org.trustedcomputinggroup._2010.ifmap._2.MetadataListType;
import org.trustedcomputinggroup._2010.ifmap._2.NewSessionResultType;
import org.trustedcomputinggroup._2010.ifmap._2.NotifyResultType;
import org.trustedcomputinggroup._2010.ifmap._2.ObjectFactory;
import org.trustedcomputinggroup._2010.ifmap._2.PollResultType;
import org.trustedcomputinggroup._2010.ifmap._2.ResponseType;
import org.trustedcomputinggroup._2010.ifmap._2.ResponseType.EndSessionResult;
import org.trustedcomputinggroup._2010.ifmap._2.ResponseType.PublishReceived;
import org.trustedcomputinggroup._2010.ifmap._2.ResponseType.PurgePublisherReceived;
import org.trustedcomputinggroup._2010.ifmap._2.ResponseType.RenewSessionResult;
import org.trustedcomputinggroup._2010.ifmap._2.ResponseType.SubscribeReceived;
import org.trustedcomputinggroup._2010.ifmap._2.ResultItemType;
import org.trustedcomputinggroup._2010.ifmap._2.SearchResultType;
import org.trustedcomputinggroup._2010.ifmap._2.UpdateResultType;
import org.w3._2003._05.soap_envelope.Body;
import org.w3._2003._05.soap_envelope.Envelope;

import de.fhhannover.inform.iron.mapserver.datamodel.graph.GraphElement;
import de.fhhannover.inform.iron.mapserver.datamodel.graph.Link;
import de.fhhannover.inform.iron.mapserver.datamodel.graph.Node;
import de.fhhannover.inform.iron.mapserver.datamodel.identifiers.Identifier;
import de.fhhannover.inform.iron.mapserver.datamodel.meta.Metadata;
import de.fhhannover.inform.iron.mapserver.datamodel.meta.MetadataValidationEnum;
import de.fhhannover.inform.iron.mapserver.datamodel.search.PollResult;
import de.fhhannover.inform.iron.mapserver.datamodel.search.ResultItem;
import de.fhhannover.inform.iron.mapserver.datamodel.search.SearchResult;
import de.fhhannover.inform.iron.mapserver.exceptions.SystemErrorException;
import de.fhhannover.inform.iron.mapserver.messages.AddressedDumpResult;
import de.fhhannover.inform.iron.mapserver.messages.AddressedPollResult;
import de.fhhannover.inform.iron.mapserver.messages.AddressedSearchResult;
import de.fhhannover.inform.iron.mapserver.messages.DumpResult;
import de.fhhannover.inform.iron.mapserver.messages.ErrorCode;
import de.fhhannover.inform.iron.mapserver.messages.ErrorResult;
import de.fhhannover.inform.iron.mapserver.messages.NewSessionResult;
import de.fhhannover.inform.iron.mapserver.messages.PollResultsTooBigResult;
import de.fhhannover.inform.iron.mapserver.messages.PublishReceivedResult;
import de.fhhannover.inform.iron.mapserver.messages.PurgePublishReceivedResult;
import de.fhhannover.inform.iron.mapserver.messages.Result;
import de.fhhannover.inform.iron.mapserver.messages.SubscribeReceivedResult;
import de.fhhannover.inform.iron.mapserver.utils.NullCheck;

/**
 * A implementation of {@link ResponseCreator} using JAXB as XML binding.
 * 
 * Originates from the old MessageTransformer class
 * 
 * @author aw
 */
class JaxbResultMarshaller implements ResultMarshaller {
	
	private JAXBContext mJaxbCtx; 
	private ThreadLocal<Marshaller> mMarshaller;

	private ObjectFactory mIfmapObjFac = new ObjectFactory();
	private org.w3._2003._05.soap_envelope.ObjectFactory mSoapObjFac =
			new org.w3._2003._05.soap_envelope.ObjectFactory();
	
	private JaxbIdentifierHelper identifierHelper = new JaxbIdentifierHelper();
	
	JaxbResultMarshaller() {
		try {
			mJaxbCtx = JAXBContext.newInstance(Envelope.class);
						// shouldn't this be enough?
						//Header.class,
						//Body.class,
						//NewSessionRequestType.class);
			mMarshaller = new ThreadLocal<Marshaller>() {
				@Override
				protected Marshaller initialValue() {
					Marshaller ret =  null;
					try {
						ret =  mJaxbCtx.createMarshaller();
						//ret.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
					} catch (PropertyException e) {
						e.printStackTrace();
						// if it's not possible it's not possible
					} catch (JAXBException e) {
						throw new RuntimeException(e);
					}
					return ret;
				}
			};
		} catch (JAXBException e) {
			// go crazy if we can't initialize the JAXB context or something
			// JAXB related
			throw new RuntimeException(e);
		}
	}
	
	private InputStream doMarshal(Envelope env) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			setResponseValidationAttribute(env.getBody().getResponse());
			// I have no idea why... Maybe a book on JAXB would be good...
			JAXBElement<Envelope> jaxbEnv = mSoapObjFac.createEnvelope(env);
			mMarshaller.get().marshal(jaxbEnv, baos);
		} catch (JAXBException e) {
			// why would this ever fail? if it does we have a problem anyway?
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}
		return new ByteArrayInputStream(baos.toByteArray());
	}
	
	/**
	 * Add validation header to @link{ResponseType} based on already
	 * set metadata validation
	 * @param resp 
	 */
	private void setResponseValidationAttribute(ResponseType resp) {
		if (resp == null)
			return;
		if (resp.getValidation() != null && resp.getValidation().equals(
				MetadataValidationEnum.METADATAONLY.toString()))
			resp.setValidation(MetadataValidationEnum.ALL.toString());
		else
			resp.setValidation(MetadataValidationEnum.BASEONLY.toString());
	}

	private Envelope prepareEnvelopeWithResponse() {
		Envelope env = mSoapObjFac.createEnvelope();
		Body body = mSoapObjFac.createBody();
		env.setBody(body);
		ResponseType rt = mIfmapObjFac.createResponseType();
		body.setResponse(rt);
		return env;
	}
	
	private ResponseType getResponseElement(Envelope env) {
		return env.getBody().getResponse();
	}

	private InputStream createNewSessionResponse(String sessionId,
			String publisherId, Integer maxPollResultSize) {
		Envelope env = prepareEnvelopeWithResponse();
		ResponseType resp = getResponseElement(env);
		NewSessionResultType nsrt = getNsrt(sessionId, publisherId);
		
		if (maxPollResultSize != null)
			nsrt.setMaxPollResultSize(new BigInteger("" + maxPollResultSize));
		
		resp.setNewSessionResult(nsrt);
		return doMarshal(env);
	}
	
	private NewSessionResultType getNsrt(String sessionId, String publisherId) {
		NewSessionResultType nsrt = mIfmapObjFac.createNewSessionResultType();
		nsrt.setSessionId(sessionId);
		nsrt.setIfmapPublisherId(publisherId);
		return nsrt;
	}
	
	private InputStream createEndSessionResponse() {
		Envelope env = prepareEnvelopeWithResponse();
		ResponseType resp = getResponseElement(env);
		EndSessionResult esr = new EndSessionResult();
		resp.setEndSessionResult(esr);
		return doMarshal(env);
	}

	private InputStream createRenewSessionResponse() {
		Envelope env = prepareEnvelopeWithResponse();
		ResponseType resp = getResponseElement(env);
		RenewSessionResult rsr = new RenewSessionResult();
		resp.setRenewSessionResult(rsr);
		return doMarshal(env);
	}

	private InputStream createPublishResponse() {
		Envelope env = prepareEnvelopeWithResponse();
		ResponseType resp = getResponseElement(env);
		PublishReceived pr = new PublishReceived();
		resp.setPublishReceived(pr);
		return doMarshal(env);
	}

	private InputStream createPurgePublisherResponse() {
		Envelope env = prepareEnvelopeWithResponse();
		ResponseType resp = getResponseElement(env);
		PurgePublisherReceived ppr = new PurgePublisherReceived();
		resp.setPurgePublisherReceived(ppr);
		return doMarshal(env);
	}
	
	private InputStream createSubscribeResponse() {
		Envelope env = prepareEnvelopeWithResponse();
		ResponseType resp = getResponseElement(env);
		SubscribeReceived sr = new SubscribeReceived();
		resp.setSubscribeReceived(sr);
		return doMarshal(env);
	}

	private InputStream createErrorResponse(ErrorCode errCode, String errStr) {
		Envelope env = prepareEnvelopeWithResponse();
		ResponseType resp = getResponseElement(env);
		ErrorResultType ert = mIfmapObjFac.createErrorResultType();
		ert.setErrorCode(errCode.toString());
		ert.setErrorString(errStr);
		resp.setErrorResult(ert);
		return doMarshal(env);
	}

	private InputStream createSearchResponse(SearchResult searchResult) {
		Envelope env = prepareEnvelopeWithResponse();
		ResponseType resp = getResponseElement(env);
		SearchResultType srt = transformSearchResult(searchResult);
		if (searchResult.hasMetadataAndOnlyValidatedMetadata())
			resp.setValidation(MetadataValidationEnum.METADATAONLY.toString());
		resp.setSearchResult(srt);
		return doMarshal(env);
	}
	
	private InputStream createDumpResponse(DumpResult dumpResult) {
		Envelope env = prepareEnvelopeWithResponse();
		ResponseType resp = getResponseElement(env);		
		DumpResponseType dump = transformDumpResult(dumpResult);
		resp.setDumpResult(dump);
		return doMarshal(env);
	}

	private InputStream createPollResponse(PollResult pollResult) {
		Envelope env = prepareEnvelopeWithResponse();
		ResponseType resp = getResponseElement(env);
		PollResultType prt = transformPollResult(pollResult);
		if (pollResult.hasMetadataAndOnlyValidatedMetadata())
			resp.setValidation(MetadataValidationEnum.METADATAONLY.toString());
		resp.setPollResult(prt);
		return doMarshal(env);
	}
	
	private InputStream createPollResponseError(ErrorCode errCode, String errMsg,
			String subName) {
		Envelope env = prepareEnvelopeWithResponse();
		ResponseType resp = getResponseElement(env);
		ErrorResultType ert = mIfmapObjFac.createErrorResultType();
		ert.setErrorCode(errCode.toString());
		ert.setErrorString(errMsg);
		resp.setErrorResult(ert);
		return doMarshal(env);
	}

	private PollResultType transformPollResult (PollResult pr) {
		PollResultType prt = new PollResultType();

		// list with all results
		List<Object> list = prt.getSearchResultOrUpdateResultOrDeleteResult();
		
		for (SearchResult sr : pr.getResults()) {
			switch (sr.getType()) {
				case SEARCH:
					list.add(transformSearchResult(sr));
					break;
				case UPDATE:
					list.add(transformUpdateResult(sr));
					break;
				case DELETE:
					list.add(transformDeleteResult(sr));
					break;
				case NOTIFY:
					list.add(transformNotifyResult(sr));
					break;
					
				default:
					throw new RuntimeException("Unknown SearchResult type");
			}
			
		}
		
		for (String err : pr.getErrorResults())
			list.add(createErrorPollResult(err));
		
		return prt;
	}
	
	private DeleteResultType transformDeleteResult(SearchResult sr) {
		DeleteResultType res = null;
		if (sr != null) {
			res = new DeleteResultType();
			if (sr.getName() != null) {
				res.setName(sr.getName());
			}
			addResultItems(res, sr);
		}
		
		return res;
	}
	
	private UpdateResultType transformUpdateResult(SearchResult sr) {
		UpdateResultType res = null;
		if (sr != null) {
			res = new UpdateResultType();
			if (sr.getName() != null) {
				res.setName(sr.getName());
			}
			addResultItems(res, sr);
		}
		
		return res;
	}
	
	private NotifyResultType transformNotifyResult(SearchResult sr) {
		NotifyResultType res = null;
		if (sr != null) {
			res = new NotifyResultType();
			if (sr.getName() != null) {
				res.setName(sr.getName());
			}
			addResultItems(res, sr);
		}
		
		return res;
	}
	
	private SearchResultType transformSearchResult(SearchResult sr) {
		SearchResultType res = null;
		if (sr != null) {
			res = new SearchResultType();
			if (sr.getName() != null) {
				res.setName(sr.getName());
			}
			addResultItems(res, sr);
		}
		
		return res;
	}

	private ErrorResultType createErrorPollResult(String err) {
		ErrorResultType errType = new ErrorResultType();
		errType.setErrorCode(ErrorCode.SearchResultsTooBig.toString());
		errType.setErrorString("Results were too big");
		errType.setName(err);
		return errType;
	}

	private DumpResponseType transformDumpResult(DumpResult dr) {
		NullCheck.check(dr, "dr is null");
		
		// TODO: This seems really wrong. All this stuff shouldn't be done
		// during marshalling, but rather before.
		String istr = dr.getFilter();				
    	DumpResponseType dt =  mIfmapObjFac.createDumpResponseType();
    	
    	Collection<Identifier> idents = dr.getIdentifier();
    	long last_update = dr.getLastUpdateTime();
    	
    	dt.setLastUpdate(""+last_update);
    	
    	istr = (istr == null) ? "" : istr.trim().toLowerCase();
    	
    	// No filter or * --> all identifiers.
		if(istr.equals("") ||  istr.equals("*")) {
			for(Identifier id : idents)
    			add(dt, id);
		} else {
			StringTokenizer tokenizer = new StringTokenizer(istr,",");
			ArrayList<String> list = new ArrayList<String>();
				
			while (tokenizer.hasMoreElements())
				list.add(tokenizer.nextToken());
				
			if(!istr.equals("-"))
				for(Identifier id : idents)
					if(list.contains(id.getTypeString()))
						this.add(dt, id);    			
		}
		return dt;
	}
	
	private void add(DumpResponseType drt, Identifier ident) {
		NullCheck.check(drt, "drt is null");
		NullCheck.check(ident, "ident is null");
		drt.getAccessRequestOrIdentityOrIpAddress().add(
				identifierHelper.transformIdentifierToJaxbObject(ident));
	}

	private void addResultItems(DeleteResultType res, SearchResult result) {
		if (result != null && res != null) {
			List<ResultItemType> rits = createResultItemList(result);
				res.getResultItem().clear();
				res.getResultItem().addAll(rits);
		}
	}


	private void addResultItems(UpdateResultType res, SearchResult result) {
		if (result != null && res != null) {
			List<ResultItemType> rits = createResultItemList(result);
				res.getResultItem().clear();
				res.getResultItem().addAll(rits);
		}
	}
	
	private void addResultItems(NotifyResultType res, SearchResult result) {
		if (result != null && res != null) {
			List<ResultItemType> rits = createResultItemList(result);
				res.getResultItem().clear();
				res.getResultItem().addAll(rits);
		}
	}
	
	private void addResultItems(SearchResultType res, SearchResult result) {
		if (result != null && res != null) {
			List<ResultItemType> rits = createResultItemList(result);
				res.getResultItem().clear();
				res.getResultItem().addAll(rits);
		}
	}
		
	private List<ResultItemType> createResultItemList(SearchResult result) {
		List<ResultItemType> list = new LinkedList<ResultItemType>();
		
		for (ResultItem ri : result.getResultItems()) {
			List<Metadata> dmlist = ri.getMetdata();
			GraphElement ge = ri.getGraphElement();
			list.add(transformToResultItem(ge, dmlist));
		}
		return list;
	}
	
	private ResultItemType transformToResultItem(GraphElement ge, List<Metadata> dmlist) {
		NullCheck.check(ge, "ge is null");
		NullCheck.check(dmlist, "dmlist is null");
		ResultItemType rit = createResultItemFor(ge);
		MetadataListType mlist = transformMetadata(dmlist);
		rit.setMetadata(mlist);
		return rit;
	}
	
	private ResultItemType createResultItemFor(GraphElement ge) {
		Identifier i1 = null, i2 = null;
		
		if (ge instanceof Node) {
			i1 = ((Node)ge).getIdentifier();
		} else if (ge instanceof Link) {
			i1 = ((Link)ge).getNode1().getIdentifier();
			i2 = ((Link)ge).getNode2().getIdentifier();
		} else {
			throw new SystemErrorException("Unknown GraphElement implementation");
		}
		return createResultItemFor(i1, i2);
	}

	private ResultItemType createResultItemFor(Identifier i1, Identifier i2) {
		ResultItemType ret = mIfmapObjFac.createResultItemType();
	
		if (i1 != null)
			addToResultItem(ret, i1);
		
		if (i2 != null)
			addToResultItem(ret, i2);
	
		if (ret.getAccessRequestOrIdentityOrIpAddress().size() == 0)
			throw new SystemErrorException("No Identifier in ResultItem");
		
		return ret;
	}

	private void addToResultItem(ResultItemType ret, Identifier i) {
		NullCheck.check(ret, "ret is null");
		NullCheck.check(i, " is null");
		List<Object> identList = ret.getAccessRequestOrIdentityOrIpAddress();
		identList.add(identifierHelper.transformIdentifierToJaxbObject(i));
	}
	
	/**
	 * This method converts from a datamodel metadata list to a
	 * MetadataListType.
	 * It simply uses the toW3CDocument() method of Metadata implementations
	 * and puts those into the MetaDataListType
	 * 
	 * Warning, returns null if no metadata is available in the 
	 * given list.
	 * 
	 * @param List of metadata from the datamodel
	 * @return Type which can be set in the autogenerated classes as
	 *	 list
	 */
	MetadataListType transformMetadata(List<Metadata> metadata) {
		MetadataListType ret = null;
		if (metadata != null && metadata.size() > 0) {
			ret = new MetadataListType();
			
			for (Metadata m : metadata) {
				org.w3c.dom.Document w3cDoc =  m.toW3cDocument();
				if (w3cDoc != null) {
					ret.getAny().add(w3cDoc.getDocumentElement());
				}
			}
		}
		return ret;
	}


	@Override
	public InputStream marshal(Result result) {
		NullCheck.check(result, "result is null");
		InputStream is = null;
		
		if (result instanceof ErrorResult) {
			ErrorResult err = (ErrorResult)result;
			ErrorCode errCode = err.getErrorCode();
			String errStr = err.getErrorString();
			is = createErrorResponse(errCode, errStr);
		} else if (result instanceof NewSessionResult) {
			NewSessionResult nsr = (NewSessionResult)result;
			String sessId = nsr.getSessionId();
			String pubId = nsr.getPublisherId();
			Integer mprs = nsr.getMaxPollResultSize();
			is = createNewSessionResponse(sessId, pubId, mprs);
		} else if (result instanceof AddressedPollResult) {
			AddressedPollResult pr = (AddressedPollResult)result;
			is = createPollResponse(pr.getPollResult());
		} else if (result instanceof AddressedSearchResult) {
			AddressedSearchResult sr = (AddressedSearchResult)result;
			is = createSearchResponse(sr.getSearchResult());
		} else if (result instanceof AddressedDumpResult) {
			AddressedDumpResult dr = (AddressedDumpResult)result;
			is = createDumpResponse(dr.getDumpResult());
		} else if (result instanceof 
				de.fhhannover.inform.iron.mapserver.messages.EndSessionResult) {
			is = createEndSessionResponse();
		} else if (result instanceof PollResultsTooBigResult) {
			is = createPollResponseError(
					ErrorCode.PollResultsTooBig, "too big my friend", null);
		} else if (result instanceof PublishReceivedResult) {
			is = createPublishResponse();
		} else if (result instanceof SubscribeReceivedResult) {
			is = createSubscribeResponse();
		} else if (result instanceof PurgePublishReceivedResult) {
			is = createPurgePublisherResponse();
		} else if (result instanceof 
				de.fhhannover.inform.iron.mapserver.messages.RenewSessionResult) {
			is = createRenewSessionResponse();
		} else {
			throw new RuntimeException("UNEXPECTED: Unknown Result!");
		}
		
		if (is == null) throw new RuntimeException("UNEXPECTED: stream is null?");
		
		return is;
	}
}
