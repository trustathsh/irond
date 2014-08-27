package de.fhhannover.inform.iron.mapserver.provider;

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

import java.util.Random;

/**
 * Simple {@link SessionIdProvider} implementation using the {@Random} class.
 *
 * Generate a string containing only numbers with at least SESSION_ID_MIN_LENGTH
 * bytes. Specification says maximum is 128 bytes.
 *
 * This provider only takes numbers and dashes as possible values,
 * no upper nor lower case characters are added to the session-id.
 *
 * FIXME: I doubt this is a good method to create session-ids, but think
 *	it looks cool.
 *
 * @author aw
 *
 */
public class RandomSessionIdProvider implements SessionIdProvider {

	private static final int SESSION_ID_MIN_LENGTH = 40;

	/**
	 * The random number generator to use
	 */
	private Random mRandom;

	public RandomSessionIdProvider() {
		mRandom = new Random();
	}

	@Override
	public String getSessionId() {
		mRandom.setSeed(System.nanoTime());
		StringBuilder sb = new StringBuilder();

		int next = mRandom.nextInt();
		// make sure the first one is positive
		next = next < 0 ? -next : next;

		sb.append(next);
		while (sb.length() < SESSION_ID_MIN_LENGTH) {
			next = mRandom.nextInt();
			if (next == 0)
			 {
				continue;			// don't want 0
			}
			next = next > 0 ? -next : next; // neg looks like - separated
			sb.append(next);
		}
		return sb.toString();
	}
}
