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
 * This file is part of irond, version 0.5.4, implemented by the Trust@HsH
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
package de.hshannover.f4.trust.iron.mapserver.utils;


import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import de.hshannover.f4.trust.iron.mapserver.datamodel.search.Filter;

/**
 * Class to validate created {@link Filter} objects.
 *
 * TODO:
 * For now, this includes only testing whether we can construct a
 * {@link XPath} object from the given filter if it is not
 * matchNothing or matchAll.
 * Additional functionality might include checking the syntax before
 * constructing the filter.
 *
 * @author aw
 *
 */
public class FilterValidator {

	/**
	 * represents the static {@link XPathFactory} to be used
	 */
	private static XPathFactory xpathfac = XPathFactory.newInstance();

	/**
	 * Check if we can create a XPath instance from this {@link Filter} object
	 * if it is not a matchAll or matchNothing filter.
	 *
	 * @param f
	 * @return true if filter is ok, else false
	 */
	public static boolean validateFilter(Filter f) {
		NullCheck.check(f, "filter object is null");

		if (f.isMatchEverything() || f.isMatchNothing()) {
			return true;
		}
		String filterStr = FilterAdaption.adaptFilterString(f.getFilterString());
		XPath xpath = xpathfac.newXPath();
		NamespaceContext nsCtx = new SimpleNamespaceContext(f.getNamespaceMap());
		xpath.setNamespaceContext(nsCtx);
		try {
			xpath.compile(filterStr);
		} catch (XPathExpressionException e) {
			return false;
		}
		return true;
	}



}
