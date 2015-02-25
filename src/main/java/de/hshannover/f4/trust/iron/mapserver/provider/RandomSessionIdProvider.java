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
 * This file is part of irond, version 0.5.2, implemented by the Trust@HsH
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
package de.hshannover.f4.trust.iron.mapserver.provider;


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
