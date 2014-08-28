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
 * This file is part of irond, version 0.5.0, implemented by the Trust@HsH
 * research group at the Hochschule Hannover.
 * %%
 * Copyright (C) 2010 - 2014 Trust@HsH
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


import de.hshannover.f4.trust.iron.mapserver.datamodel.graph.GraphElement;
import de.hshannover.f4.trust.iron.mapserver.datamodel.meta.Metadata;
import de.hshannover.f4.trust.iron.mapserver.messages.SearchResultType;
import de.hshannover.f4.trust.iron.mapserver.utils.NullCheck;

/**
 * A simple subclass of {@link SearchResult}.
 *
 * <b>This class does not overwrite the {@link #getByteCount()} method!</b>
 *
 * @version 0.1
 * @author aw
 *
 */
class CopySearchResultImpl extends SearchResultImpl {

	/**
	 * Constructs a {@link CopySearchResultImpl} object with a name and type.
	 */
	CopySearchResultImpl(String name, SearchResultType type) {
		super(name, type);
	}

	/**
	 * Constructs a {@link CopySearchResultImpl} object with a name.
	 */
	CopySearchResultImpl(String name) {
		this(name, SearchResultType.SEARCH);
	}

	/**
	 * Constructs a {@link CopySearchResultImpl} object without a name.
	 */
	CopySearchResultImpl() {
		this(null);
	}

	/* (non-Javadoc)
	 * @see de.hshannover.f4.trust.iron.mapserver.messages.SearchResult#addMetadata(de.hshannover.f4.trust.iron.mapserver.datamodel.graph.MetadataContainer, de.hshannover.f4.trust.iron.mapserver.datamodel.graph.meta.Metadata)
	 */
	@Override
	public void addMetadata(GraphElement ge, Metadata m) {

		NullCheck.check(ge, "addMetadata() with no GraphElement");

		// If mc is not the same as used in the last ResultItem, we have
		// to put a new one in the list.
		if (mLastResultItem == null || !ge.equalsIdentifiers(mLastGraphElement)) {
			mLastGraphElement = ge.dummy();
			mLastResultItem = new ResultItem(mLastGraphElement);
			mResultItems.add(mLastResultItem);
		}

		if (m != null) {
			mLastResultItem.addMetadata(m);
		}
	}
}
