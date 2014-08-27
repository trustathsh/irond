package de.fhhannover.inform.iron.mapserver.messages;

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

import de.fhhannover.inform.iron.mapserver.communication.ifmap.EventProcessor;
import de.fhhannover.inform.iron.mapserver.datamodel.identifiers.Identifier;
import de.fhhannover.inform.iron.mapserver.datamodel.search.Filter;
import de.fhhannover.inform.iron.mapserver.datamodel.search.TerminalIdentifiers;
import de.fhhannover.inform.iron.mapserver.exceptions.RequestCreationException;

/**
 * Message to be passed to the DataModelService instance
 * if a search needs to be done. One should use a constructor
 * to build this Request.
 *
 * @author aw
 * @version 0.1
 */

public class SearchRequest extends RequestWithSessionId {

	private final int mMaxDepth;

	private final Integer mMaxResultSize;

	private final TerminalIdentifiers mTerminalIdentifiers;

	private final Identifier mStart;

	private final Filter mMatchLinksFilter;

	private final Filter mResultFilter;


	/**
	 * TODO: Check for null!!!
	 *
	 * @param maxDepth
	 * @param maxResultSize
	 * @param terminalIdentTypes
	 * @param start
	 * @param matchLinksFilter
	 * @param resultFilter
	 * @throws RequestCreationException
	 */
	SearchRequest(String sessionid, int maxDepth, Integer maxResultSize,
			TerminalIdentifiers terminalIdents, Identifier start,
			Filter matchLinksFilter, Filter resultFilter)
												throws RequestCreationException {

		super(sessionid);
		mMaxDepth = maxDepth;
		mMaxResultSize = maxResultSize;
		mTerminalIdentifiers = terminalIdents;
		mStart = start;
		mMatchLinksFilter = matchLinksFilter;
		mResultFilter = resultFilter;
	}

	public int getMaxDepth() {
		return mMaxDepth;
	}

	public boolean maxSizeGiven() {
		return mMaxResultSize != null;
	}

	public int getMaxResultSize() {
		return mMaxResultSize;
	}

	public TerminalIdentifiers getTerminalIdentifiers() {
		return mTerminalIdentifiers;
	}

	public Identifier getStartIdentifier() {
		return mStart;
	}

	public Filter getMatchLinksFilter() {
		return mMatchLinksFilter;
	}

	public Filter getResultFilter() {
		return mResultFilter;
	}

	@Override
	public void dispatch(EventProcessor ep) {
		ep.processSearchRequest(this);
	}
}

