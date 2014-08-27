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

import de.fhhannover.inform.iron.mapserver.datamodel.graph.GraphElement;
import de.fhhannover.inform.iron.mapserver.datamodel.meta.Metadata;
import de.fhhannover.inform.iron.mapserver.exceptions.SystemErrorException;

/**
 * {@link SearchResult} implementation which does not copy the added
 * {@link GraphElement} and {@link Metadata} objects.
 *
 *
 * As a result, a {@link SearchResult} created with this implementation will
 * contain references to objects in the real graph for by-reference comparison.
 * <br/><b>Be careful!</b>
 *
 * @version 0.1
 * @author aw, vp
 */
public class ReferenceSearchResultImpl extends SearchResultImpl {

	ReferenceSearchResultImpl(String name) {
		super(name);

		if (name != null)
			throw new SystemErrorException("this should never contain a name");
	}

	ReferenceSearchResultImpl() {
		this(null);
	}

	/* (non-Javadoc)
	 * @see de.fhhannover.inform.iron.mapserver.messages.SearchResult#addMetadata(de.fhhannover.inform.iron.mapserver.datamodel.graph.MetadataContainer, de.fhhannover.inform.iron.mapserver.datamodel.graph.meta.Metadata)
	 */
	public void addMetadata(GraphElement ge, Metadata m) {

		if (ge == null)
			throw new SystemErrorException("addMetadata() with no MetadataContainer");

		// If mc is not the same as used in the last ResultItem, we have
		// to put a new one in the list.
		if (mLastResultItem == null || ge != mLastResultItem.getGraphElement()) {
			mLastResultItem = new ResultItem(ge);
			mResultItems.add(mLastResultItem);
		}

		if (m != null)
			mLastResultItem.addMetadata(m);
	}
}

