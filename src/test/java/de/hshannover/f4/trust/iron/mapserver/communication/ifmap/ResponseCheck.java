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
 * This file is part of irond, version 0.5.6, implemented by the Trust@HsH
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
package de.hshannover.f4.trust.iron.mapserver.communication.ifmap;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import de.hshannover.f4.trust.iron.mapserver.communication.ChannelIdentifier;
import de.hshannover.f4.trust.iron.mapserver.communication.bus.messages.Action;
import de.hshannover.f4.trust.iron.mapserver.communication.bus.messages.SendResponseAction;


/**
 * Rudimentary checks if a Action is what we are expecting it to be.
 */
public class ResponseCheck {

	private static final boolean DEBUG = false;

	/**
	 * go through the given inputstream line for line and check if these lines
	 * contain one of the given strings.
	 * Only if all strings were found return true.
	 *
	 * @param strings
	 * @param is
	 * @return
	 */
	private static boolean findStringsInInputStream(String[] strings, InputStream is) {
		boolean found[] = new boolean[strings.length];

		for (int i = 0; i < found.length; i++) {
			found[i] = false;
		}
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		try {
			String line = br.readLine();
			while (line != null) {
				if (DEBUG) {
					System.out.println(line);
				}
				for (int i = 0; i < strings.length; i++) {
					if (line.contains(strings[i])) {
						found[i] = true;
					}
				}
				line = br.readLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

		for (boolean val : found) {
			if (!val) {
				return false;
			}
		}
		return true;

	}

	/**
	 * Expect a SendResponseAction, Check if to is equal to the channelIdentifier
	 * in the action. check if the stream contains the strings.
	 *
	 * @param a
	 * @param strings
	 * @param to
	 * @return
	 */
	private static boolean checkSendResponseContains(Action a, String[] strings,
			ChannelIdentifier to) {

		if (!(a instanceof SendResponseAction)) {
			System.out.println("WHAT?");
			return false;
		}

		SendResponseAction sra = (SendResponseAction) a;

		if (!sra.getChannelIdentifier().equals(to)) {
				return false;
		}
		if (DEBUG) {
			System.out.println("SendResponseAction to " + to);
		}
		return findStringsInInputStream(strings, sra.getResponseContent());
	}

	public static boolean checkEndSessionResult(Action a, ChannelIdentifier to) {
		String[] strings = { "Envelope", "Body", "response", "endSessionResult" };
		return checkSendResponseContains(a, strings, to);
	}

	public static boolean checkRenewSessionResult(Action a, ChannelIdentifier to) {
		String[] strings = { "Envelope", "Body", "response", "renewSessionResult" };
		return checkSendResponseContains(a, strings, to);
	}

	public static boolean checkNewSessionResult(Action a, ChannelIdentifier to) {
		String[] strings = { "Envelope", "Body", "response", "newSessionResult",
				"session-id", "ifmap-publisher-id"};
		return checkSendResponseContains(a, strings, to);
	}
	public static boolean checkNewSessionResultWithMprs(Action a, ChannelIdentifier to) {
		String[] strings = { "Envelope", "Body", "response", "newSessionResult",
				"session-id", "ifmap-publisher-id", "max-poll-result-size"};
		return checkSendResponseContains(a, strings, to);
	}

	public static  boolean checkErrorResponse(String errCode,
			Action a, ChannelIdentifier to) {
		String[] strings = { "Envelope", "Body", "response", "errorResult", "" };
		strings[strings.length - 1] = errCode;
		return checkSendResponseContains(a, strings, to);
	}

	public static boolean checkSubscribeReceived(Action a, ChannelIdentifier to) {
		String[] strings = { "Envelope", "Body", "response", "subscribeReceived" };
		return checkSendResponseContains(a, strings, to);
	}

	public static boolean checkPollResult(Action a, ChannelIdentifier to) {
		String[] strings = { "Envelope", "Body", "response", "pollResult" };
		return checkSendResponseContains(a, strings, to);
	}
}
