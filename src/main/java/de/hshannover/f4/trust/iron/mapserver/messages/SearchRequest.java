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
package de.hshannover.f4.trust.iron.mapserver.messages;


import de.hshannover.f4.trust.iron.mapserver.communication.ifmap.EventProcessor;
import de.hshannover.f4.trust.iron.mapserver.datamodel.identifiers.Identifier;
import de.hshannover.f4.trust.iron.mapserver.datamodel.search.Filter;
import de.hshannover.f4.trust.iron.mapserver.datamodel.search.TerminalIdentifiers;
import de.hshannover.f4.trust.iron.mapserver.exceptions.RequestCreationException;

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

