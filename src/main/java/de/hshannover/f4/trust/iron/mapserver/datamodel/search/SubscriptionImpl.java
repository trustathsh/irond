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
 * This file is part of irond, version 0.5.5, implemented by the Trust@HsH
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
package de.hshannover.f4.trust.iron.mapserver.datamodel.search;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hshannover.f4.trust.iron.mapserver.datamodel.Publisher;
import de.hshannover.f4.trust.iron.mapserver.datamodel.graph.GraphElement;
import de.hshannover.f4.trust.iron.mapserver.exceptions.SystemErrorException;
import de.hshannover.f4.trust.iron.mapserver.messages.SearchRequest;
import de.hshannover.f4.trust.iron.mapserver.utils.LengthCheck;
import de.hshannover.f4.trust.iron.mapserver.utils.NullCheck;
/**
 * A subscription object encapsulates a {@link SearchRequest} and
 * four different {@link SearchRequest} objects. Update, delete, notify and
 * search.
 *
 *
 * @version 0.1
 * @author aw, vp
 */

/*
 * created: 03.05.10
 *
 * changes:
 *  03.05.10 aw, vp  ...
 *  08.06.10 aw - some commenting and minor changes
 *  11.06.10 aw - add names to the search results :-/
 *
 *
 */
class SubscriptionImpl implements Subscription {

	/**
	 * Name of the subscription
	 */
	private final String mName;

	/**
	 * Corresponding searchRequest
	 */
	private final SearchRequest mSearchRequest;

	/**
	 * {@link LinkedMap} of {@link MetadataContainer} for this subscription
	 * contains.
	 */
	private final Map<GraphElement, GraphElement> mGraphElements;

	private boolean changed;

	private boolean exceededSize;

	/**
	 * Reference to the publisher who is the owner of this subscription.
	 */
	private final Publisher mPublisherReference;

	private SearchResult reRunOldResult;
	private SearchResult reRunNewResult;

	SubscriptionImpl(Publisher publisher, String name, SearchRequest searchRequest) {
		NullCheck.check(publisher, "publisher is null");
		NullCheck.check(name, "name is null");
		NullCheck.check(searchRequest, "searchRequest is null");
		LengthCheck.checkMinMax(name, 1, 20, "subscription name length bad");

		mName = name;
		mPublisherReference = publisher;
		mSearchRequest = searchRequest;
		mGraphElements = new HashMap<GraphElement, GraphElement>();

		changed = false;
		exceededSize = false;
	}


	/* (non-Javadoc)
	 * @see de.hshannover.f4.trust.iron.mapserver.datamodel.search.Subscription#getName()
	 */
	@Override
	public String getName() {
		return mName;
	}

	/*
	 * auto generated getters and setters
	 */
	/* (non-Javadoc)
	 * @see de.hshannover.f4.trust.iron.mapserver.datamodel.search.Subscription#getSearchRequest()
	 */
	@Override
	public SearchRequest getSearchRequest() {
		return mSearchRequest;
	}

	/* (non-Javadoc)
	 * @see de.hshannover.f4.trust.iron.mapserver.datamodel.search.Subscription#getContainers()
	 */
	@Override
	public List<GraphElement> getContainers() {
		return new ArrayList<GraphElement>(mGraphElements.values());
	}

	/* (non-Javadoc)
	 * @see de.hshannover.f4.trust.iron.mapserver.datamodel.search.Subscription#addContainer(de.hshannover.f4.trust.iron.mapserver.datamodel.graph.GraphElement)
	 */
	@Override
	public void addGraphElement(GraphElement ge) {
		NullCheck.check(ge, "mc is null");
		if (mGraphElements.put(ge, ge) != null) {
			throw new SystemErrorException("Container " + ge + " was already on"
					+ " on " + this);
		}

	}

	/* (non-Javadoc)
	 * @see de.hshannover.f4.trust.iron.mapserver.datamodel.search.Subscription#removeContainer(de.hshannover.f4.trust.iron.mapserver.datamodel.graph.GraphElement)
	 */
	@Override
	public void removeGraphElement(GraphElement ge) {
		NullCheck.check(ge, "mc is null");

		if (mGraphElements.remove(ge) == null) {
			throw new SystemErrorException("Tried to remove non-existend " + ge
					+ " from " + this);
		}
	}

	/* (non-Javadoc)
	 * @see de.hshannover.f4.trust.iron.mapserver.datamodel.search.Subscription#setReRunOldResult(de.hshannover.f4.trust.iron.mapserver.datamodel.search.SearchResult)
	 */
	@Override
	public void setReRunOldResult(SearchResult reRunOldResult) {
		this.reRunOldResult = reRunOldResult;
	}

	/* (non-Javadoc)
	 * @see de.hshannover.f4.trust.iron.mapserver.datamodel.search.Subscription#getReRunOldResult()
	 */
	@Override
	public SearchResult getReRunOldResult() {
		return reRunOldResult;
	}

	/* (non-Javadoc)
	 * @see de.hshannover.f4.trust.iron.mapserver.datamodel.search.Subscription#setReRunNewResult(de.hshannover.f4.trust.iron.mapserver.datamodel.search.SearchResult)
	 */
	@Override
	public void setReRunNewResult(SearchResult reRunNewResult) {
		this.reRunNewResult = reRunNewResult;
	}

	/* (non-Javadoc)
	 * @see de.hshannover.f4.trust.iron.mapserver.datamodel.search.Subscription#getReRunNewResult()
	 */
	@Override
	public SearchResult getReRunNewResult() {
		return reRunNewResult;
	}

	/* (non-Javadoc)
	 * @see de.hshannover.f4.trust.iron.mapserver.datamodel.search.Subscription#setChanged()
	 */
	@Override
	public void setChanged() {
		changed = true;
	}

	/* (non-Javadoc)
	 * @see de.hshannover.f4.trust.iron.mapserver.datamodel.search.Subscription#isChanged()
	 */
	@Override
	public boolean isChanged() {
		return changed;
	}

	/* (non-Javadoc)
	 * @see de.hshannover.f4.trust.iron.mapserver.datamodel.search.Subscription#getPublisherReference()
	 */
	@Override
	public Publisher getPublisherReference() {
		return mPublisherReference;
	}

	/* (non-Javadoc)
	 * @see de.hshannover.f4.trust.iron.mapserver.datamodel.search.Subscription#getMaxResultSize()
	 */
	@Override
	public Integer getMaxResultSize() {
		return mSearchRequest.getMaxResultSize();
	}

	/* (non-Javadoc)
	 * @see de.hshannover.f4.trust.iron.mapserver.datamodel.search.Subscription#exceededSize()
	 */
	@Override
	public boolean exceededSize() {
		return exceededSize;
	}

	/* (non-Javadoc)
	 * @see de.hshannover.f4.trust.iron.mapserver.datamodel.search.Subscription#setExceededSize()
	 */
	@Override
	public void setExceededSize() {
		exceededSize = true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		// TODO: Cache
		return "sub{" + getName() + ", " + getPublisherReference() + "}";
	}
}
