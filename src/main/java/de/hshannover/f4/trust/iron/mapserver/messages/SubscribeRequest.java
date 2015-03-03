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
package de.hshannover.f4.trust.iron.mapserver.messages;


import java.util.List;

import de.hshannover.f4.trust.iron.mapserver.communication.ifmap.EventProcessor;
import de.hshannover.f4.trust.iron.mapserver.exceptions.AccessDeniedException;
import de.hshannover.f4.trust.iron.mapserver.exceptions.RequestCreationException;

/**
 * A Subscription Request can contain a number of
 * {@link SubscriptionDelete} and {@link SubscriptionUpdate} elements
 * which are stored in a list of {@link SubSubscriptionRequest}s
 *
 * @version 0.1
 * @author aw
 */

/*
 * created: 30.04.10
 *
 * changes:
 *  30.04.10 aw - ...
 *  02.12.10 aw - Use RequestWithSessionId as superclass
 */
public class SubscribeRequest extends RequestWithSessionId {

	private List<SubSubscribeRequest> ssrList;

	SubscribeRequest(String sessionId, List<SubSubscribeRequest> ssrlist)
												throws RequestCreationException {
		super(sessionId);

		if (ssrlist == null || ssrlist.size() == 0) {
			throw new RequestCreationException("No SubSubscriptionRequests given");
		}

		ssrList = ssrlist;
	}

	public List<SubSubscribeRequest> getSubSubscribeRequests() {
		return ssrList;
	}

	@Override
	public void dispatch(EventProcessor ep) throws AccessDeniedException {
		ep.processSubscribeRequest(this);

	}
}