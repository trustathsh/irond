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
package de.hshannover.f4.trust.iron.mapserver.contentauth;


import de.hshannover.f4.trust.iron.mapserver.contentauth.IfmapPepHandler.PdpType;
import de.hshannover.f4.trust.iron.mapserver.exceptions.ServerInitialException;

/**
 * Factory for {@link IfmapPepHandler} instances.
 *
 * TODO: So if somebody wants to, this could need refactoring to support
 *       register registeration of {@link IfmapPepHandler} classes.
 *
 * @author aw
 *
 */
public class IfmapPepHandlers {

	private IfmapPepHandlers() { }

	public static IfmapPepHandler handlerFor(PdpType type, String param, boolean rawLog) throws ServerInitialException {

		switch(type) {

		case permit:
		case deny:
			return new StaticIfmapPep(type == PdpType.permit ? true : false);

		case local:
			return new LocalSunXacml(param, rawLog);

		case remote:

			return new RemoteSunXacml(param, rawLog);
		default:
			throw new ServerInitialException("Unknown PDP type" + type.toString());
		}
	}

	/**
	 * Return a instance of a cached PEP.
	 *
	 * @param pep
	 * @return
	 */
	public static IfmapPepHandler getCache(IfmapPepHandler pep, long ttl, long maxEntries) {
		return new CachedPepHandler(pep, ttl, maxEntries);
	}
}
