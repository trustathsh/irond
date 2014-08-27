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
 * This file is part of irond, version 0.4.2, implemented by the Trust@HsH
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
package de.fhhannover.inform.iron.mapserver.datamodel.graph;


import java.util.List;
import java.util.Map;

import de.fhhannover.inform.iron.mapserver.datamodel.meta.MetadataHolder;
import de.fhhannover.inform.iron.mapserver.datamodel.meta.MetadataType;
import de.fhhannover.inform.iron.mapserver.datamodel.search.Filter;
import de.fhhannover.inform.iron.mapserver.datamodel.search.Subscription;
import de.fhhannover.inform.iron.mapserver.datamodel.search.SubscriptionEntry;
import de.fhhannover.inform.iron.mapserver.exceptions.SystemErrorException;
import de.fhhannover.inform.iron.mapserver.utils.CollectionHelper;
import de.fhhannover.inform.iron.mapserver.utils.NullCheck;

/**
 * Implementation of the {@link GraphElement} interface.
 *
 * @since 0.3.0
 * @author aw
 */
abstract class GraphElementImpl implements GraphElement {

	private final List<MetadataHolder> mMetadataHolder;
	private final Map<Subscription, SubscriptionEntry> mSubscriptionEntries;
	private final Map<Subscription, SubscriptionEntry> mRemovedSubscriptionEntries;

	protected GraphElementImpl() {
		mMetadataHolder = CollectionHelper.provideListFor(MetadataHolder.class);
		mSubscriptionEntries = CollectionHelper.provideMapFor(Subscription.class,
				SubscriptionEntry.class);
		mRemovedSubscriptionEntries = CollectionHelper.provideMapFor(
				Subscription.class, SubscriptionEntry.class);
	}

	@Override
	public List<MetadataHolder> getMetadataHolder() {
		return CollectionHelper.copy(mMetadataHolder);
	}

	@Override
	public List<MetadataHolder> getMetadataHolder(Filter f) {
		List<MetadataHolder> ret = CollectionHelper.provideListFor(MetadataHolder.class);

		for (MetadataHolder mh : mMetadataHolder) {
			if (mh.getMetadata().matchesFilter(f)) {
				ret.add(mh);
			}
		}

		return ret;
	}

	@Override
	public List<MetadataHolder> getMetadataHolder(MetadataType type) {
		List<MetadataHolder> ret = CollectionHelper.provideListFor(MetadataHolder.class);

		for (MetadataHolder mh : mMetadataHolder) {
			if (mh.getMetadata().getType() == type) {
				ret.add(mh);
			}
		}

		return ret;
	}

	@Override
	public List<MetadataHolder> getMetadataHolderInGraph() {
		List<MetadataHolder> ret = CollectionHelper.provideListFor(MetadataHolder.class);

		for (MetadataHolder mh : mMetadataHolder) {
			if (mh.isUnchanged() || mh.isDeleted()) {
				ret.add(mh);
			}
		}

		return ret;
	}

	@Override
	public List<MetadataHolder> getMetadataHolderInGraph(Filter f) {
		List<MetadataHolder> ret = CollectionHelper.provideListFor(MetadataHolder.class);

		for (MetadataHolder mh : getMetadataHolderInGraph()) {
			if (mh.getMetadata().matchesFilter(f)) {
				ret.add(mh);
			}
		}

		return ret;
	}

	@Override
	public List<MetadataHolder> getMetadataHolderNext(Filter f) {
		List<MetadataHolder> ret = CollectionHelper.provideListFor(MetadataHolder.class);

		for (MetadataHolder mh : getMetadataHolder()) {
			if ((mh.isNew() || mh.isUnchanged()) && mh.getMetadata().matchesFilter(f)) {
				ret.add(mh);
			}
		}

		return ret;
	}

	@Override
	public List<MetadataHolder> getMetadataHolderNew(Filter f) {
		List<MetadataHolder> ret = CollectionHelper.provideListFor(MetadataHolder.class);

		for (MetadataHolder mh : getMetadataHolder()) {
			if (mh.isNew() && mh.getMetadata().matchesFilter(f)) {
				ret.add(mh);
			}
		}

		return ret;

	}

	@Override
	public void addMetadataHolder(MetadataHolder m) {
		NullCheck.check(m, "MetadataHolder is null");
		if (mMetadataHolder.contains(m)) {
			throw new SystemErrorException("MetadataHolder " + m + " already on "
					+ this);
		}

		mMetadataHolder.add(m);
	}

	@Override
	public void removeMetadataHolder(MetadataHolder m) {
		NullCheck.check(m, "MetadataHolder is null");
		int idx = mMetadataHolder.indexOf(m);

		if (idx < 0) {
			throw new SystemErrorException("MetadataHolder " + m + " not on " + this);
		}

		mMetadataHolder.remove(idx);
	}

	@Override
	public void removeAllMetadataHolders() {
		mMetadataHolder.clear();
	}

	@Override
	public List<SubscriptionEntry> getSubscriptionEntries() {
		return CollectionHelper.copy(mSubscriptionEntries.values());
	}

	@Override
	public SubscriptionEntry getSubscriptionEntry(Subscription sub) {
		return mSubscriptionEntries.get(sub);
	}

	@Override
	public void addSubscriptionEntry(SubscriptionEntry entry) {
		NullCheck.check(entry, "entry is null");
		NullCheck.check(entry.getSubscription(), "sub is null");
		if (mSubscriptionEntries.put(entry.getSubscription(), entry) != null) {
			throw new SystemErrorException("entry " + entry
					+ " already on " + this);
		}
	}

	@Override
	public void removeSubscriptionEntry(Subscription sub) {
		NullCheck.check(sub, "Subscription is null");
		if (mSubscriptionEntries.remove(sub) == null) {
			throw new SystemErrorException("entry for " + sub + " not on "  + this);
		}
	}

	@Override
	public void removeAllSubscriptionEntries() {
		mSubscriptionEntries.clear();
	}

	@Override
	public SubscriptionEntry getRemovedSubscriptionEntry(Subscription sub) {
		return mRemovedSubscriptionEntries.get(sub);
	 }

	@Override
	public List <SubscriptionEntry> getRemovedSubscriptionEntries() {
		return CollectionHelper.copy(mRemovedSubscriptionEntries.values());
	 }

	@Override
	public void addRemovedSubscriptionEntry(SubscriptionEntry entry) {
		NullCheck.check(entry, "entry is null");
		NullCheck.check(entry.getSubscription(), "sub is null");
		if (mRemovedSubscriptionEntries.put(entry.getSubscription(), entry) != null) {
			throw new SystemErrorException("entry " + entry
					+ " already removed for  " + this);
		}
	 }

	@Override
	public void removeRemovedSubscriptionEntry(Subscription sub) {
		NullCheck.check(sub, "Subscription is null");
		if (mRemovedSubscriptionEntries.remove(sub) == null) {
			throw new SystemErrorException("entry for " + sub + " not removed for "
					+ this);
		}
	}

	@Override
	public void removeAllRemovedSubscriptionEntries() {
		mRemovedSubscriptionEntries.clear();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public final String toString() {

		return provideToStringStart() + ", #md=" + mMetadataHolder.size()
				+ ", #subs=" + mSubscriptionEntries.size() + "}";
	}

	protected abstract String provideToStringStart();
}
