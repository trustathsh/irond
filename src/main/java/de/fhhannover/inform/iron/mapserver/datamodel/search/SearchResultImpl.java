package de.fhhannover.inform.iron.mapserver.datamodel.search;

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
 * This file is part of irond, version 0.4.0, implemented by the Trust@FHH 
 * research group at the Fachhochschule Hannover.
 * 
 * irond is an an *experimental* IF-MAP 2.0 compliant MAP server written in
 * JAVA. irond supports both basic authentication and certificate-based 
 * authentication (using X.509 certificates) of MAP clients. irond is
 * maintained by the Trust@FHH group at the Fachhochschule Hannover, initial
 * developement was carried out during the ESUKOM research project.
 * %%
 * Copyright (C) 2010 - 2013 Trust@FHH
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

import java.util.Collections;
import java.util.List;

import de.fhhannover.inform.iron.mapserver.IfmapConstStrings;
import de.fhhannover.inform.iron.mapserver.datamodel.graph.GraphElement;
import de.fhhannover.inform.iron.mapserver.datamodel.meta.Metadata;
import de.fhhannover.inform.iron.mapserver.messages.SearchResultType;
import de.fhhannover.inform.iron.mapserver.utils.CollectionHelper;
import de.fhhannover.inform.iron.mapserver.utils.LengthCheck;
import de.fhhannover.inform.iron.mapserver.utils.NullCheck;

/**
 * TODO
 * 
 * @since 0.1.0
 * @author aw
 *
 */
abstract class SearchResultImpl implements ModifiableSearchResult {
	
	/**
	 * The name of this {@link SearchResult}. May be null.
	 */
	private final String mName;
	
	/**
	 * The type of this {@link SearchResult}.
	 */
	private final SearchResultType mType;

	/**
	 * Represents the ordered list of {@link ResultItem} objects.
	 */
	protected final List<ResultItem> mResultItems;
	
	/**
	 * Represents the last {@link ResultItem} instance used.
	 */
	protected ResultItem mLastResultItem;
	
	/**
	 * Just a quick reference to the {@link GraphElement} instance in the
	 * last {@link ResultItem} used.
	 */
	protected GraphElement mLastGraphElement;

	/**
	 * Constructor to create {@link SearchResult} instances.
	 * 
	 * @param name
	 * @param type
	 */
	protected SearchResultImpl(String name, SearchResultType type) {
		NullCheck.check(type, "type is null");
		
		if (name != null)
			LengthCheck.checkMinMax(name, 1, 20, "name is set, so length should" +
					" be > 0 && <= 20");
		
		mName = name;
		mType = type;
		mResultItems = CollectionHelper.provideListFor(ResultItem.class);
		mLastResultItem = null;
	}
	
	/**
	 * Constructor to create a {@link SearchResult} with name and type
	 * {@link SearchResultType#SEARCH}.
	 * 
	 * @param name
	 */
	protected SearchResultImpl(String name) {
		this(name, SearchResultType.SEARCH);
	}
	
	/**
	 * Constructor to create a {@link SearchResult} without a name and type
	 * {@link SearchResultType#SEARCH}.
	 */
	protected SearchResultImpl() {
		this(null, SearchResultType.SEARCH);
	}
	
	/* (non-Javadoc)
	 * @see de.fhhannover.inform.iron.mapserver.datamodel.search.SearchResult#getResultItems()
	 */
	@Override
	public final List<ResultItem> getResultItems() {
		return Collections.unmodifiableList(mResultItems);
	}
	
	/* (non-Javadoc)
	 * @see de.fhhannover.inform.iron.mapserver.datamodel.search.SearchResult#getName()
	 */
	@Override
	public final String getName() {
		return mName;
	}

	/* (non-Javadoc)
	 * @see de.fhhannover.inform.iron.mapserver.datamodel.search.SearchResult#getType()
	 */
	@Override
	public final SearchResultType getType() {
		return mType;
	}
	

	/* (non-Javadoc)
	 * @see de.fhhannover.inform.iron.mapserver.datamodel.search.SearchResult#isEmpty()
	 */
	@Override
	public final boolean isEmpty() {
		return getResultItems().isEmpty();
	}

	/* (non-Javadoc)
	 * @see de.fhhannover.inform.iron.mapserver.datamodel.search.ModifieableSearchResult#addMetadataContainer(de.fhhannover.inform.iron.mapserver.datamodel.graph.GraphElement)
	 */
	@Override
	public final void addGraphElement(GraphElement ge) {
		NullCheck.check(ge, "ge is null");
		addMetadata(ge, (Metadata)null);
	}
	
	/* (non-Javadoc)
	 * @see de.fhhannover.inform.iron.mapserver.datamodel.SearchAble#getByteCount()
	 */
	@Override
	public int getByteCount() {
		return sizeEnclosingElement() + sizeResultItems();
	}
	
	private int sizeResultItems() {
		int ret = 0;
		
		for (ResultItem ri: getResultItems())
			ret += ri.getByteCount();
		
		return ret;
	}
	
	private int sizeEnclosingElement() {
		final int ret;
		
		// They are all the same, but make it explicit by switching
		switch (getType()) {
		case SEARCH:
			ret = IfmapConstStrings.SRES_MIN_CNT;
			break;
		case UPDATE:
			ret = IfmapConstStrings.URES_MIN_CNT;
			break;
		case DELETE:
			ret = IfmapConstStrings.DRES_MIN_CNT;
			break;
		case NOTIFY:
			ret = IfmapConstStrings.NRES_MIN_CNT;
			break;
		default:
			throw new RuntimeException("Unknown searchResult type");
		}
		
		return ret;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer("sr{");
		
		for (ResultItem ri : mResultItems)
			sb.append(ri.toString() + ", ");
		
		sb.setLength(sb.length() - 2);
		sb.append("}");
		return sb.toString();
	}

	/* (non-Javadoc)
	 * @see de.fhhannover.inform.iron.mapserver.datamodel.search.ModifieableSearchResult#addMetadata(de.fhhannover.inform.iron.mapserver.datamodel.graph.GraphElement, java.util.List)
	 */
	@Override
	public final void addMetadata(GraphElement ge, List<Metadata> mlist) {
		NullCheck.check(ge, "ge is null");
		NullCheck.check(mlist, "mlist is null");
		
		if (mlist.size() == 0)
			addGraphElement(ge);
		else
			for (Metadata m : mlist)
				addMetadata(ge, m);
	}

	/* (non-Javadoc)
	 * @see de.fhhannover.inform.iron.mapserver.datamodel.search.ModifieableSearchResult#addResultItem(de.fhhannover.inform.iron.mapserver.datamodel.search.ResultItem)
	 */
	@Override
	public final void addResultItem(ResultItem ri) {
		NullCheck.check(ri, "ri is null");
		addMetadata(ri.getGraphElement(), ri.getMetdata());
	}

	/* (non-Javadoc)
	 * @see de.fhhannover.inform.iron.mapserver.datamodel.search.ModifieableSearchResult#addResultItems(java.util.List)
	 */
	@Override
	public void addResultItems(List<ResultItem> rilist) {
		NullCheck.check(rilist, "rilist is null");
		
		for (ResultItem ri : rilist)
			addResultItem(ri);
	}

	/* (non-Javadoc)
	 * @see de.fhhannover.inform.iron.mapserver.datamodel.search.SearchResult#sameNameAndType(de.fhhannover.inform.iron.mapserver.datamodel.search.SearchResult)
	 */
	@Override
	public boolean sameNameAndType(SearchResult o) {
		if (o == this)
			return true;
		
		if (o.getName() == null && getName() == null)
			return true;
		
		if (o.getName() == null || getName() == null)
			return false;
		
		return o.getType() == getType() && o.getName().equals(getName());
	}
	
	@Override
	public boolean hasMetadataAndOnlyValidatedMetadata() {
		boolean result = false;
		for (ResultItem ri : mResultItems)
			for (Metadata md : ri.getMetdata()) {
				if (!md.getValidated())
					return false;
				result = true;
			}
		return result;
	}
}
