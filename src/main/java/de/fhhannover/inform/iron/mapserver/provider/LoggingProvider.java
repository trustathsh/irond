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
package de.fhhannover.inform.iron.mapserver.provider;


import org.apache.log4j.Logger;
/**
 * Provide a method to a Root Logger
 *
 * Initialization is provided here.
 *
 *
 * @author aw
 *
 * created: 04.02.10
 */
public class LoggingProvider {

	public static Logger getTheLogger() {
		return Logger.getLogger("de.fhhannover.inform.irond.proc");
	}

	public static Logger getRawRequestLogger() {
		return Logger.getLogger("de.fhhannover.inform.irond.rawrequests");
	}

	public static Logger getDecisionRequestLogger() {
		return Logger.getLogger("de.fhhannover.inform.irond.pdprequests");
	}

	public static Logger getRawDecisionRequestLogger() {
		return Logger.getLogger("de.fhhannover.inform.irond.pdprequests.raw");
	}
}
