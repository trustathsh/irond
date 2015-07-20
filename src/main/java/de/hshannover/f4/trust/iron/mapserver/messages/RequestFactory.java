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
 * This file is part of irond, version 0.5.6, implemented by the Trust@HsH
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
package de.hshannover.f4.trust.iron.mapserver.messages;


import java.util.List;

import de.hshannover.f4.trust.iron.mapserver.datamodel.DataModelService;
import de.hshannover.f4.trust.iron.mapserver.datamodel.identifiers.Identifier;
import de.hshannover.f4.trust.iron.mapserver.datamodel.meta.MetadataLifeTime;
import de.hshannover.f4.trust.iron.mapserver.datamodel.meta.Metadata;
import de.hshannover.f4.trust.iron.mapserver.datamodel.search.Filter;
import de.hshannover.f4.trust.iron.mapserver.datamodel.search.TerminalIdentifiers;
import de.hshannover.f4.trust.iron.mapserver.exceptions.RequestCreationException;

/**
 * A factory to create all possible {@link Request} subtypes to be given
 * to the {@link DataModelService}
 *
 * @author aw
 * @version 0.1
 *
 */
/*
 *
 * created: 05.02.10
 *
 * changes:
 *  05.02.10 - 08.06.10 aw, vp - extended to support subscription functionality
 *  02.12.10 aw - Adapt for changed requests...

 *
 */
public class RequestFactory {

	private static RequestFactory instance;

	private RequestFactory() {

	}


	public static RequestFactory getInstance() {
		if (instance == null) {
			instance = new RequestFactory();
		}
		return instance;
	}


	public PublishRequest createPublishRequest(String sessionId,
			List<SubPublishRequest> slist) throws RequestCreationException {
		PublishRequest pr = new PublishRequest(sessionId, slist);
		return pr;
	}

	public SubPublishRequest createPublishUpdateRequest(Identifier i1,
			Identifier i2, List<Metadata> metadatalist, MetadataLifeTime lt)
			throws RequestCreationException {

		return new PublishUpdate(i1, i2, metadatalist, lt);
	}


	public SearchRequest createSearchRequest(String sessionId, int maxDepth,
		Integer maxResultSize, TerminalIdentifiers terminalIdents, Identifier start,
		Filter matchLinksFilter, Filter resultFilter) throws RequestCreationException {

		return new SearchRequest(sessionId, maxDepth, maxResultSize,
				terminalIdents, start, matchLinksFilter, resultFilter);
	}



	public SubPublishRequest createPublishDeleteRequest(Identifier i1,
					Identifier i2, Filter f) throws RequestCreationException {

		return new PublishDelete(i1, i2, f);
	}

	public PurgePublisherRequest createPurgePublisherRequest(String sessionId,
			String publisherId) throws RequestCreationException {

		return new PurgePublisherRequest(sessionId, publisherId);
	}

	public NewSessionRequest createNewSessionRequest(Integer mprs) throws RequestCreationException {
		return new NewSessionRequest(mprs);
	}

	public EndSessionRequest createEndSessionRequest(String sessionId)
			throws RequestCreationException {
		return new EndSessionRequest(sessionId);
	}

	public SubscribeRequest createSubscribeRequest(String sessionId,
			List<SubSubscribeRequest> ssrlist) throws RequestCreationException {

		return new SubscribeRequest(sessionId, ssrlist);
	}

	public SubSubscribeRequest createSubscribeDelete(String name)
												throws RequestCreationException {
		return new SubscribeDelete(name);
	}

	public SubSubscribeRequest createSubscribeUpdate(String name, SearchRequest searchRequest)
	throws RequestCreationException {
		return new SubscribeUpdate(name, searchRequest);
	}


	public SubPublishRequest createPublishUpdateRequest(Identifier i,
			List<Metadata> metadatalist, MetadataLifeTime lt) throws RequestCreationException {
		return createPublishUpdateRequest(i, null, metadatalist, lt);
	}


	public SubPublishRequest createPublishNotifyRequest(Identifier i1, Identifier i2,
			List<Metadata> mlist, MetadataLifeTime lt) throws RequestCreationException {
		return new PublishNotify(i1, i2, mlist);
	}

	public SubPublishRequest createPublishNotifyRequest(Identifier i1,
			List<Metadata> mlist, MetadataLifeTime lt) throws RequestCreationException {
		return createPublishNotifyRequest(i1, null, mlist, lt);
	}

	public DumpRequest createDumpRequest(String sessionId, String identifier) throws RequestCreationException {
		return new DumpRequest(sessionId, identifier);
	}

	public RenewSessionRequest createRenewSessionRequest(String sessionId) throws RequestCreationException {
		return new RenewSessionRequest(sessionId);
	}


	public PollRequest createPollRequest(String sessionId) throws RequestCreationException {
		return new PollRequest(sessionId);
	}
}
