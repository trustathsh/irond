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
package de.hshannover.f4.trust.iron.mapserver.datamodel.search;


import java.util.Collections;
import java.util.List;

import de.hshannover.f4.trust.iron.mapserver.IfmapConstStrings;
import de.hshannover.f4.trust.iron.mapserver.datamodel.SearchAble;
import de.hshannover.f4.trust.iron.mapserver.datamodel.graph.GraphElement;
import de.hshannover.f4.trust.iron.mapserver.datamodel.meta.Metadata;
import de.hshannover.f4.trust.iron.mapserver.utils.CollectionHelper;
import de.hshannover.f4.trust.iron.mapserver.utils.NullCheck;

/**
 * A {@link ResultItem} is a simple object encapsulating a {@link MetadataContainer}
 * and a list of {@link Metadata} objects.
 *
 * @author aw
 *
 */
public class ResultItem implements SearchAble {

	/**
	 * Represents the {@link MetadataContainer} in the {@link ResultItem}.
	 */
	private GraphElement mGraphElement;

	/**
	 * Represents the {@link Metadata} objects. Can be empty.
	 */
	private List<Metadata> mMetadata;

	public ResultItem(GraphElement ge) {
		NullCheck.check(ge, "ge not allowed to be null");
		mGraphElement = ge;
		mMetadata = CollectionHelper.provideListFor(Metadata.class);
	}

	public GraphElement getGraphElement() {
		return mGraphElement;
	}

	/**
	 * Add a {@link Metadata} object to this {@link ResultItem}. No copying.
	 * @param md
	 */
	public void addMetadata(Metadata md) {
		NullCheck.check(md, "md not allowed to be null");
		mMetadata.add(md);
	}

	/**
	 * @return a read-only list of the {@link Metadata} objects
	 */
	public List<Metadata> getMetdata() {
		return Collections.unmodifiableList(mMetadata);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		int i = 0;
		sb.append("ri{");
		sb.append(mGraphElement);
		sb.append(" #md=");
		sb.append(mMetadata.size());
		if (mMetadata.size() > 0) {
			sb.append(" (");
			for (Metadata m : mMetadata) {
				sb.append(m.getPrefixAndElement());
				i++;
				if (i != mMetadata.size()) {
					sb.append(", ");
				}
			}
			sb.append(")");
		}

		sb.append("}");
		return sb.toString();

	}

	/* (non-Javadoc)
	 * @see de.hshannover.f4.trust.iron.mapserver.datamodel.SearchAble#getByteCount()
	 */
	@Override
	public int getByteCount() {
		return resultItemSize() + identifierSize() + metadataSize();
	}

	private int identifierSize() {
		return getGraphElement().getByteCount();
	}

	private int resultItemSize() {
		return IfmapConstStrings.RITEM_MIN_CNT;
	}

	private int metadataSize() {
		int ret = IfmapConstStrings.MLIST_MIN_CNT;

		if (getMetdata().isEmpty()) {
			return 0;
		}

		for (Metadata m : getMetdata()) {
			ret += m.getByteCount();
		}

		return ret;
	}
}
