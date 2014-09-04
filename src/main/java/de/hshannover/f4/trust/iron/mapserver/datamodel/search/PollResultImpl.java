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
 * This file is part of irond, version 0.5.1, implemented by the Trust@HsH
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


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.hshannover.f4.trust.iron.mapserver.IfmapConstStrings;
import de.hshannover.f4.trust.iron.mapserver.utils.CollectionHelper;
import de.hshannover.f4.trust.iron.mapserver.utils.LengthCheck;
import de.hshannover.f4.trust.iron.mapserver.utils.NullCheck;

/**
 * Implementation to create {@link PollResult} objects with some logic to
 * add {@link SearchResult} objects and compress them while if possible.
 * <br/>
 * <b>Note: errorResult are not taken into account for the size calculation</b>
 * (TODO?)
 *
 * @author aw
 *
 */
class PollResultImpl implements ModifiablePollResult {

	private List<ModifiableSearchResult> mResults;
	private List<String> mErrors;
	private int mCurSize;

	PollResultImpl() {
		mResults = CollectionHelper.provideListFor(ModifiableSearchResult.class);
		mErrors = CollectionHelper.provideListFor(String.class);
		mCurSize = 0;
	}

	/* (non-Javadoc)
	 * @see de.hshannover.f4.trust.iron.mapserver.datamodel.search.PollResult#getResults()
	 */
	@Override
	public List<SearchResult> getResults() {
		return new ArrayList<SearchResult>(mResults);
	}

	/**
	 * @return a read-only list for all contained errors
	 */
	/* (non-Javadoc)
	 * @see de.hshannover.f4.trust.iron.mapserver.datamodel.search.PollResult#getErrorResults()
	 */
	@Override
	public List<String> getErrorResults() {
		return Collections.unmodifiableList(mErrors);
	}

	/* (non-Javadoc)
	 * @see de.hshannover.f4.trust.iron.mapserver.datamodel.search.ModifiablePollResult#addErrorResult(java.lang.String)
	 */
	@Override
	public void addErrorResult(String name) {
		NullCheck.check(name, "name is null");
		LengthCheck.checkMinMax(name, 1, 20, "bad sub name for error result");

		// Sanity
		for (SearchResult res : mResults) {
			if (res.getName().equals(name)) {
				throw new RuntimeException("SearchResults and ErrorResult in "
						+ "PollResult for subscription " + name);
			}
		}

		mErrors.add(name);
	}


	/* (non-Javadoc)
	 * @see de.hshannover.f4.trust.iron.mapserver.datamodel.search.ModifiablePollResult#addSearchResult(de.hshannover.f4.trust.iron.mapserver.datamodel.search.ModifiableSearchResult)
	 */
	@Override
	public void addSearchResult(ModifiableSearchResult sres) {
		NullCheck.check(sres, "sres is null");
		NullCheck.check(sres.getName(), "sres.getName() is null");
		LengthCheck.checkMinMax(sres.getName(), 1, 20, "sub name bad");

		ModifiableSearchResult lastResult = null;

		if (mResults.size() > 0) {
			lastResult = mResults.get(mResults.size() - 1);
		}

		if (lastResult != null && sameAsLastResult(sres, lastResult)) {
			mCurSize -= lastResult.getByteCount();
			lastResult.addResultItems(sres.getResultItems());
		} else {
			mResults.add(sres);
			lastResult = sres;
		}
		mCurSize += lastResult.getByteCount();
	}

	/* (non-Javadoc)
	 * @see de.hshannover.f4.trust.iron.mapserver.datamodel.SearchAble#getByteCount()
	 */
	@Override
	public int getByteCount() {
		return IfmapConstStrings.PRES_MIN_CNT + mCurSize;
	}

	/* (non-Javadoc)
	 * @see de.hshannover.f4.trust.iron.mapserver.datamodel.search.PollResult#isEmpty()
	 */
	@Override
	public boolean isEmpty() {
		return mResults.isEmpty() && mErrors.isEmpty();
	}

	/* (non-Javadoc)
	 * @see de.hshannover.f4.trust.iron.mapserver.datamodel.search.PollResult#getByteCountOf(java.lang.String)
	 */
	@Override
	public int getByteCountOf(String name) {
		int sum = 0;
		for (SearchResult sr : mResults) {
			if (sr.getName().equals(name)) {
				sum += sr.getByteCount();
			}
		}
		return sum;
	}

	@Override
	public boolean hasMetadataAndOnlyValidatedMetadata() {
		boolean result = false;
		for (ModifiableSearchResult msr : mResults) {
			result = true;
			if (!msr.hasMetadataAndOnlyValidatedMetadata()) {
				return false;
			}
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see de.hshannover.f4.trust.iron.mapserver.datamodel.search.ModifiablePollResult#removeResultsFor(java.lang.String)
	 */
	@Override
	public void removeResultsOf(String name) {
		List<ModifiableSearchResult> toRemoveSres =
			CollectionHelper.provideListFor(ModifiableSearchResult.class);
		List<String> toRemoveErrs = CollectionHelper.provideListFor(String.class);

		for (ModifiableSearchResult sr : mResults) {
			if (sr.getName().equals(name)) {
				toRemoveSres.add(sr);
				mCurSize -= sr.getByteCount();
			}
		}

		mResults.removeAll(toRemoveSres);

		for (String error : mErrors) {
			if (error.equals(name)) {
				toRemoveErrs.add(error);
			}
		}

		mErrors.removeAll(toRemoveErrs);
	}

	private boolean sameAsLastResult(SearchResult newRes, SearchResult lastRes) {
		return lastRes != null && lastRes.getType() == newRes.getType()
				&& lastRes.getName().equals(newRes.getName());
	}

}
