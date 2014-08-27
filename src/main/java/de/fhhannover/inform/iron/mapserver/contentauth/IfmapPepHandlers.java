package de.fhhannover.inform.iron.mapserver.contentauth;

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

import de.fhhannover.inform.iron.mapserver.contentauth.IfmapPepHandler.PdpType;
import de.fhhannover.inform.iron.mapserver.exceptions.ServerInitialException;

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
