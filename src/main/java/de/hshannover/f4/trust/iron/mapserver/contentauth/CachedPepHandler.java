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
 * This file is part of irond, version 0.5.8, implemented by the Trust@HsH
 * research group at the Hochschule Hannover.
 * %%
 * Copyright (C) 2010 - 2016 Trust@HsH
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
package de.hshannover.f4.trust.iron.mapserver.contentauth;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.log4j.Logger;

import de.hshannover.f4.trust.iron.mapserver.exceptions.SystemErrorException;
import de.hshannover.f4.trust.iron.mapserver.provider.LoggingProvider;
import de.hshannover.f4.trust.iron.mapserver.utils.NullCheck;

/**
 * The {@link CachedPepHandler} caches the results obtained by a request
 * to a {@link IfmapPepHandler}. Further, cached results are reused to
 * limit the number of requests that possibly go over the network.
 *
 * The cache might have more than maxEntries at some points, but we do this
 * to relax synchronization a bit. Also, it'll never really be a lot more.
 *
 * The general eviction idea is to first drop all invalid entries, i.e. those
 * that are older than the given TTL from the cache. After that we keep the
 * least recently used ones and drop all others until there's at least one
 * slot in the cache.
 *
 * @author aw
 */
class CachedPepHandler implements IfmapPepHandler {

	private static Logger sLogger = LoggingProvider.getDecisionRequestLogger();
	private static final String sName = "CachedPEP";

	private class CacheStatistics {

		private final Map<IfmapDecisionRequest, CacheEntry> mMap;
		private AtomicLong mQueries = new AtomicLong();
		private AtomicLong mHits = new AtomicLong();
		private AtomicLong mMisses = new AtomicLong();
		private AtomicLong mEvictionRuns = new AtomicLong();

		public CacheStatistics(Map<IfmapDecisionRequest, CacheEntry> map) {
			mMap = map;
		}

		@Override
		public String toString() {
			return String.format("decisioncache{queries=%d hits=%d misses=%d "
								 + "hit ratio=%f eviction runs=%d map entries=%d}",
								 mQueries.get(), mHits.get(), mMisses.get(),
								 (double)mHits.get() / mQueries.get(),
								 mEvictionRuns.get(), mMap.size());
		}
	}

	private class CacheEntry {

		private AtomicLong mLastUsed;
		private final long mTimeStamp;
		private final boolean mResult;

		public CacheEntry(long ts, boolean res) {
			mLastUsed = new AtomicLong(ts);
			mTimeStamp = ts;
			mResult = res;
		}

		public long getTimeStamp() {
			return mTimeStamp;
		}

		public long getLastUsed() {
			return mLastUsed.get();
		}

		public void setLastUsed(long ts) {
			mLastUsed.set(ts);
		}

		public boolean getResult() {
			return mResult;
		}

		@Override
		public String toString() {
			return String.format("cacheentry{timestamp=%d lastused=%d result=%s}",
								  mTimeStamp, mLastUsed.get(), mResult ? "true" : "false");
		}
	}

	/**
	 * Compares entries by least recently used.
	 *
	 * @author aw
	 *
	 */
	private class CacheEntryComperator implements
					Comparator<Entry<IfmapDecisionRequest, CacheEntry>> {


		@Override
		public int compare(Entry<IfmapDecisionRequest, CacheEntry> o1,
				Entry<IfmapDecisionRequest, CacheEntry> o2) {

			return (int)(o1.getValue().getLastUsed() - o2.getValue().getLastUsed());
		}
	}

	private final IfmapPepHandler mPep;
	private final Map<IfmapDecisionRequest, CacheEntry> mMap;
	private final long mTtl;
	private final long mMaxEntries;
	private final CacheStatistics mStats;
	private final CacheEntryComperator mComparator;

	/**
	 * @param pep
	 * @param ttl time to live for entries in seconds
	 */
	public CachedPepHandler(IfmapPepHandler pep, long ttl, long maxEntries) {
		NullCheck.check(pep, "pep is null");
		mMap = new ConcurrentHashMap<IfmapDecisionRequest, CacheEntry>();
		mPep = pep;
		mTtl = ttl;
		mMaxEntries = maxEntries;
		mStats = new CacheStatistics(mMap);
		mComparator = new CacheEntryComperator();
	}

	@Override
	@SuppressWarnings("unused")
	public boolean isAuthorized(IfmapDecisionRequest dreq) {

		boolean res = false;
		long now = now();

		CacheEntry e = mMap.get(dreq);

		if (e != null && isValid(e.getTimeStamp(), now)) {
			e.setLastUsed(now);
			res = e.getResult();
			mStats.mHits.incrementAndGet();
		} else {
			res  = mPep.isAuthorized(dreq);

			if (mMap.size() >= mMaxEntries) {
				evictCacheEntries();
			}

			mMap.put(dreq, new CacheEntry(now(), res));
			mStats.mMisses.incrementAndGet();
		}

		mStats.mQueries.incrementAndGet();
		sLogger.debug(sName + ": " + mStats);


		// If you want to see each cache entry, use this...
		if (false) {
			synchronized (this) {
				for (Entry<IfmapDecisionRequest, CacheEntry> entry : mMap.entrySet()) {
					sLogger.debug(sName + ": " + entry.getKey() + " --> " + entry.getValue());
				}
			}
		}

		return res;
	}

	private void evictCacheEntries() {

		synchronized (mMap) {

			// If there was another thread cleaning up things, skip our run
			if (mMap.size() < mMaxEntries) {
				return;
			}

			long now = now();

			List<Entry<IfmapDecisionRequest, CacheEntry>> entries =
					new ArrayList<Entry<IfmapDecisionRequest, CacheEntry>>(
							mMap.entrySet());

			Collections.sort(entries, mComparator);
			Iterator<Entry<IfmapDecisionRequest, CacheEntry>> it = entries.iterator();

			while (it.hasNext()) {
				Entry<IfmapDecisionRequest, CacheEntry> entry = it.next();

				// After the first valid one, there are only valid ones to
				// follow, so break out.
				if (isValid(entry.getValue().getTimeStamp(), now)) {
					break;
				}

				mMap.remove(entry.getKey());
			}

			// If we were able to clean some entries, jump out...
			if (mMap.size() < mMaxEntries) {
				return;
			}

			// If we didn't, we need to drop some valid ones.
			it = entries.iterator();
			while (mMap.size() >= mMaxEntries && it.hasNext()) {
				mMap.remove(it.next().getKey());
			}

			// Sanity: We should have at least cleaned up some...
			if (mMap.size() >= mMaxEntries) {
				throw new SystemErrorException(sName + ": Cache eviction failed");
			}

			mStats.mEvictionRuns.incrementAndGet();
		}
	}

	private boolean isValid(long ttlEntry, long now) {
		return now - ttlEntry < mTtl * 1000;

	}

	private long now() {
		return System.currentTimeMillis();
	}
}
