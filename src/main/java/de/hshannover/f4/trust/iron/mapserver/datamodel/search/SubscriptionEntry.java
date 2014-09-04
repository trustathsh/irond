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


import java.util.List;

import de.hshannover.f4.trust.iron.mapserver.datamodel.graph.Link;
import de.hshannover.f4.trust.iron.mapserver.datamodel.meta.MetadataHolder;
import de.hshannover.f4.trust.iron.mapserver.exceptions.SystemErrorException;
import de.hshannover.f4.trust.iron.mapserver.utils.CollectionHelper;
import de.hshannover.f4.trust.iron.mapserver.utils.NullCheck;

// TODO: use HashMap for metadata entries for efficiency
public class SubscriptionEntry {

	public class LinkEntry {
		public int depth;
		public Link link;

		private LinkEntry(Link l, int d) {
			NullCheck.check(l, "l is null");

			if (d < 0) {
				throw new SystemErrorException("depth can't be < 0");
			}

			link = l;
			depth = d;
		}
	}

	private int mDepth;
	private final Subscription mSubscription;
	private final List<MetadataHolder> mMetadata;

	public SubscriptionEntry(Subscription sub) {
		NullCheck.check(sub, "sub is null");
		mSubscription = sub;
		mDepth = 0;
		mMetadata = CollectionHelper.provideListFor(MetadataHolder.class);
	}

	public Subscription getSubscription() {
		return mSubscription;
	}

	public void setDepth(int curDepth) {
		mDepth = curDepth;
	}

	public int getDepth() {
		return mDepth;
	}

	public List<MetadataHolder> getMetadataHolder() {
		return CollectionHelper.copy(mMetadata);
	}

	public void addMetadataHolder(MetadataHolder mh) {
		NullCheck.check(mh, "mh is null");

		if (mMetadata.contains(mh)) {
			throw new SystemErrorException("mh already here");
		}

		mMetadata.add(mh);
	}

	public void addMetadataHolder(List<MetadataHolder> mhlist) {
		NullCheck.check(mhlist, "mhlist is null");

		for (MetadataHolder mh : mhlist) {
			addMetadataHolder(mh);
		}
	}

	public void removeMetadataHolder(MetadataHolder mh) {
		if (!mMetadata.contains(mh)) {
			throw new SystemErrorException("mh not here");
		}
		mMetadata.remove(mh);
	}
}
