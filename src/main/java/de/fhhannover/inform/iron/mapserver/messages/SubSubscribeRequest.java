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

import de.fhhannover.inform.iron.mapserver.exceptions.RequestCreationException;

/**
 * Super class for {@link SubscriptionUpdate} and {@link SubscriptionDelete}
 * to be given to {@link SubscriptionRequest}
 *
 * @version 0.1
 * @author aw
 */

/*
 * created: 30.04.10
 *
 * changes:
 *  30.04.10 aw - ...
 *  08.06.10 aw - changed n to name
 *  29.09.10 aw - check subscription name length
 *
 */
abstract public class SubSubscribeRequest {

	/**
	 * name of the subscription
	 */
	private String name;

	/*
	 */
	public SubSubscribeRequest(String name) throws RequestCreationException {
		if (name == null )
			throw new RequestCreationException("Subscription name null or length zero");
		else if (name.length() < 1 || name.length() > 20)
			throw new RequestCreationException("Subscription name has bad length="
					+ name.length() + ". 1 - 20 is allowed.");

		this.name = new String(name);
	}

	public String getName() {
		return new String(name);
	}
}
