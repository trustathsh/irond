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
package de.hshannover.f4.trust.iron.mapserver.datamodel.graph;


import java.util.List;

import de.hshannover.f4.trust.iron.mapserver.datamodel.meta.MetadataHolder;
import de.hshannover.f4.trust.iron.mapserver.datamodel.meta.MetadataType;
import de.hshannover.f4.trust.iron.mapserver.datamodel.search.Filter;
import de.hshannover.f4.trust.iron.mapserver.datamodel.search.SearchResult;
import de.hshannover.f4.trust.iron.mapserver.datamodel.search.Subscription;
import de.hshannover.f4.trust.iron.mapserver.datamodel.search.SubscriptionEntry;
import de.hshannover.f4.trust.iron.mapserver.exceptions.SystemErrorException;

/**
 * Implementation of the {@link GraphElement}, but without any implementation
 * of references to mutable objects. This way, we have a lightweight
 * {@link GraphElement} implementation for {@link SearchResult} and the like.
 *
 * @since 0.3.0
 * @author aw
 *
 */
abstract class DummyGraphElement implements GraphElement {

	protected static final String sErrorString = "DummyGraphElement: "
		+ "Method not implemented!";

	@Override
	public List<MetadataHolder> getMetadataHolder() {
		throw new SystemErrorException(sErrorString);
	}

	@Override
	public List<MetadataHolder> getMetadataHolder(Filter f) {
		throw new SystemErrorException(sErrorString);
	}

	@Override
	public List<MetadataHolder> getMetadataHolder(MetadataType type) {
		throw new SystemErrorException(sErrorString);
	}

	@Override
	public List<MetadataHolder> getMetadataHolderInGraph() {
		throw new SystemErrorException(sErrorString);
	}

	@Override
	public List<MetadataHolder> getMetadataHolderInGraph(Filter f) {
		throw new SystemErrorException(sErrorString);
	}

	@Override
	public List<MetadataHolder> getMetadataHolderNext(Filter f) {
		throw new SystemErrorException(sErrorString);
	}

	@Override
	public List<MetadataHolder> getMetadataHolderNew(Filter f) {
		throw new SystemErrorException(sErrorString);
	}

	@Override
	public void addMetadataHolder(MetadataHolder m) {
		throw new SystemErrorException(sErrorString);
	}

	@Override
	public void removeMetadataHolder(MetadataHolder m) {
		throw new SystemErrorException(sErrorString);
	}

	@Override
	public void removeAllMetadataHolders() {
		throw new SystemErrorException(sErrorString);
	}

	@Override
	public List<SubscriptionEntry> getSubscriptionEntries() {
		throw new SystemErrorException(sErrorString);
	}

	@Override
	public SubscriptionEntry getSubscriptionEntry(Subscription sub) {
		throw new SystemErrorException(sErrorString);
	}

	@Override
	public void addSubscriptionEntry(SubscriptionEntry sub) {
		throw new SystemErrorException(sErrorString);
	}

	@Override
	public void removeSubscriptionEntry(Subscription sub) {
		throw new SystemErrorException(sErrorString);
	}

	@Override
	public void removeAllSubscriptionEntries() {
		throw new SystemErrorException(sErrorString);
	}

	@Override
	public SubscriptionEntry getRemovedSubscriptionEntry(Subscription sub) {
		throw new SystemErrorException(sErrorString);
	}

	@Override
	public List <SubscriptionEntry> getRemovedSubscriptionEntries() {
		throw new SystemErrorException(sErrorString);
	}

	@Override
	public void addRemovedSubscriptionEntry(SubscriptionEntry entry) {
		throw new SystemErrorException(sErrorString);
	}

	@Override
	public void removeRemovedSubscriptionEntry(Subscription sub) {
		throw new SystemErrorException(sErrorString);
	}

	@Override
	public void removeAllRemovedSubscriptionEntries() {
		throw new SystemErrorException(sErrorString);
	}
}
