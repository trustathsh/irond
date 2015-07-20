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
package de.hshannover.f4.trust.iron.mapserver.provider;


import java.util.Set;

import de.hshannover.f4.trust.iron.mapserver.communication.ClientIdentifier;
import de.hshannover.f4.trust.iron.mapserver.utils.CollectionHelper;
import de.hshannover.f4.trust.iron.mapserver.utils.NullCheck;

/**
 * Simple implementation of the {@link PublisherIdGenerator} interface.
 * IF-MAP 2.1 compliant implementationen of publisher id generation.
 * Taking a ClientIdentifier object and make a MD5 hash of the relevant
 * parameters
 *
 * @author jk
 */
public class PublisherIdGeneratorImpl implements PublisherIdGenerator {

	@Override
	public String generatePublisherIdFor(ClientIdentifier clId,
			PublisherIdProvider pubIdProv) {
		NullCheck.check(clId, "clientId is null");
		NullCheck.check(pubIdProv, "pubIdProv is null");
		String genPubId = null;

		// allow for faster lookups
		Set<String> set = CollectionHelper.provideSetFor(String.class);
		set.addAll(pubIdProv.getAllPublisherIds());

		do {
			genPubId = generatePublisherIdFor(clId);
		} while (set.contains(genPubId));

		return genPubId;
	}

	public String generatePublisherIdFor(ClientIdentifier clId) {
		StringBuilder sb = new StringBuilder();
		sb.append(clId.getReadablePseudoIdentifier());
		sb.append("-");
		sb.append(java.util.UUID.randomUUID().toString());

		return sb.toString();
	}
}
