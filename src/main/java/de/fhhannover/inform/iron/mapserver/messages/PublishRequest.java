package de.fhhannover.inform.iron.mapserver.messages;

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

import java.util.List;

import de.fhhannover.inform.iron.mapserver.communication.ifmap.EventProcessor;
import de.fhhannover.inform.iron.mapserver.exceptions.AccessDeniedException;
import de.fhhannover.inform.iron.mapserver.exceptions.RequestCreationException;

/**
 * PublishRequest
 *
 * @author awe, vp
 * @version 0.1
 */

/*
 * created: 19.11.09
 * changes:
 * 	22.11.09 vp - Added methods:
 * 		addSubPublishRequest(), getSubPublishRequestList(), setSessionId().
 * 	05.12.09 aw - Added Constructor to enforce setting of the sessionId
 *  22.12.09 aw - Exception with message added.
 *  05.02.10 aw - use Request super class, a PublishRequest should be
 *		created with a List of SubPublishRequests. So add()
 *		is marked deprecated.
 *  02.12.10 aw - Use RequestWithSessionId as superclass, remove comment
 */
public class PublishRequest extends RequestWithSessionId {


	/**
	 *  List of {@link PublishUpdate}, {@link PublishDelete} and
	 *  {@link PublishNotify} objects.
	 */
	private final List<SubPublishRequest> subPublishRequests;

	/**
	 * Construct a PublishRequest
	 *
	 * @param sessionid
	 * @throws RequestCreationException
	 */
	public PublishRequest(String sessionid, List<SubPublishRequest> subpublishrequests)
												throws RequestCreationException {
		super(sessionid);

		if (subpublishrequests == null || subpublishrequests.size() == 0) {
			throw new RequestCreationException("no subpublishrequests given");
		}
		subPublishRequests = subpublishrequests;
	}

	public List<SubPublishRequest> getSubPublishRequestList() {
		return subPublishRequests;
	}

	@Override
	public void dispatch(EventProcessor ep) throws AccessDeniedException {
		ep.processPublishRequest(this);
	}
}


