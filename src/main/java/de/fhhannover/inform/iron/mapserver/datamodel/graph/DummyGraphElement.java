package de.fhhannover.inform.iron.mapserver.datamodel.graph;

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

import de.fhhannover.inform.iron.mapserver.datamodel.meta.MetadataHolder;
import de.fhhannover.inform.iron.mapserver.datamodel.meta.MetadataType;
import de.fhhannover.inform.iron.mapserver.datamodel.search.Filter;
import de.fhhannover.inform.iron.mapserver.datamodel.search.SearchResult;
import de.fhhannover.inform.iron.mapserver.datamodel.search.Subscription;
import de.fhhannover.inform.iron.mapserver.datamodel.search.SubscriptionEntry;
import de.fhhannover.inform.iron.mapserver.exceptions.SystemErrorException;

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
