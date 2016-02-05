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
 * This file is part of irond, version 0.5.7, implemented by the Trust@HsH
 * research group at the Hochschule Hannover.
 * %%
 * Copyright (C) 2010 - 2016 Trust@HsH
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
package de.hshannover.f4.trust.iron.mapserver.communication.bus.messages;


import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import de.hshannover.f4.trust.iron.mapserver.utils.NullCheck;

/**
 * A {@link ActionSeries} object simply encapsulates a number of {@link Action}
 * objects in order to process them in a series.
 * We use a {@link List} instead of a {@link Collection} in order to enforce
 * some ordering if needed.
 *
 * @author aw
 */
public class ActionSeries {

	/**
	 * Stores all {@link Action} objects of this {@link ActionSeries}.
	 */
	private final List<Action> mActions;

	/**
	 * Construct a {@link ActionSeries} with an empty list of {@link Action}
	 * objects.
	 */
	public ActionSeries() {
		mActions = new LinkedList<Action>();
	}

	/**
	 * Construct a {@link ActionSeries} where the list contains the given
	 * {@link Action} object in the beginning.
	 *
	 * @param action
	 * @throws NullPointerException if action is null;
	 */
	public ActionSeries(Action action) {
		this();
		add(action);
	}

	/**
	 * Construct a {@link ActionSeries} where the list containing all the
	 * {@link Action} objects given in the actions list.
	 *
	 * @param action
	 * @throws NullPointerException if actions is null;
	 */
	public ActionSeries(List<Action> actions) {
		this();
		NullCheck.check(actions, "actions is null");
		add(actions);
	}

	/**
	 * @return copy of the list containing {@link Action} objects.
	 */
	public List<Action> getActions() {
		return new LinkedList<Action>(mActions);
	}

	/**
	 * Add a {@link Action} to this {@link ActionSeries}.
	 *
	 * @param action
	 * @throws NullPointerException if the given {@link Action} object is null.
	 */
	public void add(Action action) {
		NullCheck.check(action, "action is null");
		mActions.add(action);
	}

	/**
	 * Add all {@link Action} objects in the given list to this
	 * {@link ActionSeries}.
	 *
	 * @param actions
	 * @throws NullPointerException if actions is null or any element in the
	 *			      list is null
	 */
	public void add(List<Action> actions) {
		NullCheck.check(actions, "actions is null");
		for (Action action : actions) {
			add(action);
		}
	}
}
