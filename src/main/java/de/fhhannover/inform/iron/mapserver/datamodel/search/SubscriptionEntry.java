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

import java.util.List;

import de.fhhannover.inform.iron.mapserver.datamodel.graph.Link;
import de.fhhannover.inform.iron.mapserver.datamodel.meta.MetadataHolder;
import de.fhhannover.inform.iron.mapserver.exceptions.SystemErrorException;
import de.fhhannover.inform.iron.mapserver.utils.CollectionHelper;
import de.fhhannover.inform.iron.mapserver.utils.NullCheck;

// TODO: use HashMap for metadata entries for efficiency
public class SubscriptionEntry {
	
	public class LinkEntry {
		public int depth;
		public Link link;
		
		private LinkEntry(Link l, int d) {
			NullCheck.check(l, "l is null");
			
			if (d < 0)
				throw new SystemErrorException("depth can't be < 0");
		
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
		
		if (mMetadata.contains(mh))
			throw new SystemErrorException("mh already here");
		
		mMetadata.add(mh);
	}

	public void addMetadataHolder(List<MetadataHolder> mhlist) {
		NullCheck.check(mhlist, "mhlist is null");
		
		for (MetadataHolder mh : mhlist)
			addMetadataHolder(mh);
	}

	public void removeMetadataHolder(MetadataHolder mh) {
		if (!mMetadata.contains(mh))
				throw new SystemErrorException("mh not here");
		mMetadata.remove(mh);
	}
}
