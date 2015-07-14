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
 * This file is part of irond, version 0.5.5, implemented by the Trust@HsH
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

import java.util.Map;
import java.util.Map.Entry;

import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;

import de.hshannover.f4.trust.iron.mapserver.datamodel.meta.Metadata;
import de.hshannover.f4.trust.iron.mapserver.exceptions.SystemErrorException;
import de.hshannover.f4.trust.iron.mapserver.provider.LoggingProvider;
import de.hshannover.f4.trust.iron.mapserver.utils.FilterAdaption;
import de.hshannover.f4.trust.iron.mapserver.utils.NullCheck;
import de.hshannover.f4.trust.iron.mapserver.utils.SimpleNamespaceContext;

/**
 * A Filter object simple represents a filter string given by some MAPC.
 *
 * A Filter string can also be used to match nothing or match everything.
 *
 * To construct a filter that matches everything no filter string has to be
 * given (e.g. null).
 *
 * If a filter should match nothing, a empty filter string has to be given.
 * (i.e. a string with length zero).
 *
 * The above mentioned cases can be asked with isMatchEverything() and
 * isMatchNothing(). Note that !isMatchEverything() does not imply
 * isMatchNothing() (or the other way round) as both can be false.
 *
 * Besides having a string which represents a filter we need the namespace
 * prefix namespace uri mapping. Having this we can make our Filter namespace
 * aware. This implementation started without such a mapping. Having no
 * namespace awareness.
 *
 * TODO: Check whether or not this filter has the correct form
 *
 * @author aw
 * @version 0.1
 */

public abstract class Filter {

	protected final String mFilterString;
	protected final Map<String, String> mPrefixUriMap;
	protected final static XPathFactory xPathFactory = XPathFactory.newInstance();
	protected final static Logger sLogger = LoggingProvider.getTheLogger();

	/**
	 * toStringString cache;
	 */
	private String toStringString;

	/**
	 * Construct a filter from a given filter string and a map of namespace
	 * prefixes with namespace uris.
	 *
	 * If no map is given a map with only the meta namespace mapping is used
	 *
	 *
	 * @param fs
	 * @param hu
	 */
	public Filter(String fs, Map<String, String> hu) {
		NullCheck.check(hu, "No Namespace Mapping given");
		mPrefixUriMap = hu;
		mFilterString = fs;
		toStringString = null;
	}

	public String getFilterString() {
		if (isMatchEverything() || isMatchNothing()) {
			throw new SystemErrorException("get filter for match all/nothing");
		}

		return mFilterString;
	}

	public abstract boolean isMatchNothing();

	public abstract boolean isMatchEverything();

	/**
	 * Returns a hashmap with all namespace prefix namespace uri mappgings known
	 * to this filter.
	 *
	 * @return
	 */
	public Map<String, String> getNamespaceMap() {
		return mPrefixUriMap;
	}

	@Override
	public String toString() {
		if (toStringString == null) {
			toStringString = buildToStringString();
		}
		return toStringString;
	}

	private String buildToStringString() {
		StringBuffer sb = new StringBuffer("filter{");

		if (isMatchEverything()) {
			sb.append("match everything");
		} else if (isMatchNothing()) {
			sb.append("match nothing");
		} else {
			sb.append(mFilterString);
		}

		sb.append("}");
		return sb.toString();
	}

	/**
	 * This method should contain the logic to check
	 * whether or not this {@link Metadata} object matches the
	 * given {@link Filter} f.
	 *
	 * @param metadata
	 * @param filter
	 * @return
	 */
	public boolean matches(Metadata metadata) {
		
		//shortcut
		if(isMatchEverything()) {
			return true;
		}
		
		//shortcut
		if(isMatchNothing()) {
			return false;
		}
		
//		NullCheck.check(f, "filter is null");
		sLogger.trace("matching with filter " + this.toString());

		String fs = this.getFilterString();
		XPath xpath = xPathFactory.newXPath();

		Map<String, String> nsMap = this.getNamespaceMap();

		if (sLogger.isTraceEnabled()) {
			int cnt = 1;
			sLogger.trace("Namespace map used for matching:");
			for (Entry<String, String> e : nsMap.entrySet()) {
				sLogger.trace(cnt++ + ":\t" + e.getKey() + " -- "
						+ e.getValue());
			}
		}

		NamespaceContext nsCtx = new SimpleNamespaceContext(nsMap);
		xpath.setNamespaceContext(nsCtx);

		sLogger.trace("Filter before adaption: " + fs);

		// add * to lonely brackets
		fs = FilterAdaption.adaptFilterString(fs);

		sLogger.trace("Filter after adaption: " + fs);

		XPathExpression expr = null;

		// this should never happen, as we checked it before
		try {
			expr = xpath.compile(fs);
		} catch (XPathExpressionException e1) {
			sLogger.error("UNEXPECTED: Could not compile filterstring" + fs);
			return false;
		}

		Object ret = null;
		try {
			ret = expr.evaluate(metadata.toW3cDocument(), XPathConstants.BOOLEAN);
			sLogger.trace("matching result is "
					+ ((Boolean) ret).booleanValue());
		} catch (XPathExpressionException e) {
			e.printStackTrace();
			sLogger.error("evaluate failed badly: " + e.getMessage());
			return false;
		}
		return ((Boolean) ret).booleanValue();
	}
}
