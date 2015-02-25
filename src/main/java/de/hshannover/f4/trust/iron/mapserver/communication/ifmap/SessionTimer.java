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
package de.hshannover.f4.trust.iron.mapserver.communication.ifmap;


import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import de.hshannover.f4.trust.iron.mapserver.communication.bus.Queue;
import de.hshannover.f4.trust.iron.mapserver.communication.bus.messages.Event;
import de.hshannover.f4.trust.iron.mapserver.communication.bus.messages.TimerExpiredEvent;
import de.hshannover.f4.trust.iron.mapserver.utils.NullCheck;

/**
 * SessionTimer implementation.
 *
 * We use a {@link ScheduledExecutorService} to schedule our timers.
 * As soon as a timer gets started, we add it to the executor and get
 * a {@link Future} object back.
 * When a timer is canceled, we use the {@link Future} object to instruct
 * the executor to remove the timer.
 * If the timer expired, run() is executed. If this is the case we put
 * a {@link TimerExpiredEvent} with the associated session-id into the
 * queue. The {@link EventProcessor} is then responsible for handling
 * the rest.
 *
 * TODO:
 * Currently a timer may only be started once, i.e for every new timeout
 * a client may be faced with a new {@link SessionTimer} object has to
 * be created. Should we change this?
 *
 * @author aw
 *
 */
class SessionTimer implements Runnable {

	/**
	 * The executor service where the timers will get scheduled.
	 */
	private static ScheduledExecutorService sScheduleService;
	static {
		sScheduleService = Executors.newSingleThreadScheduledExecutor();
	}

	/**
	 * The session-id this timer belongs to.
	 */
	private final String mSessionId;

	/**
	 * The future object used to cancel the timer.
	 */
	private ScheduledFuture<?> mFuture;

	/**
	 * Indicates whether the timer is running
	 */
	private volatile boolean mRunning;

	/**
	 * The queue to put in the {@link TimerExpiredEvent}
	 */
	private Queue<Event> mActionQueue;

	/**
	 * Time in milliseconds before the timer will be executed.
	 */
	private long mDelay;

	/**
	 * Constructs a session timer.
	 *
	 * @param sessionId the session-id this timer belongs to
	 * @param actionQueue the action queue to be used to put in the {@link TimerExpiredEvent}
	 * @param delay in milliseconds before the run() will run
	 */
	public SessionTimer(String sessionId, Queue<Event> actionQueue, long delay) {
		NullCheck.check(sessionId, "session is null");
		NullCheck.check(actionQueue, "action queue is null");
		mSessionId = sessionId;
		mActionQueue = actionQueue;
		mDelay = delay;
		mFuture = null;
		mRunning = false;
	}

	/**
	 * Puts this {@link SessionTimer} to execution by the scheduled service.
	 */
	public void start() {
		if (mFuture != null) {
			throw new RuntimeException("A timer can only be started once");
		}

		mRunning = true;
		mFuture = sScheduleService.schedule(this, mDelay, TimeUnit.MILLISECONDS);
	}


	/**
	 * Cancels the timer if it was already started.
	 */
	public void cancel() {
		if (mFuture != null) {
			mFuture.cancel(true);
		}
		mFuture = null;
		mRunning = false;
	}

	public boolean isRunning() {
		return mRunning;
	}

	@Override
	public void run() {
		try {
			mRunning = false;
			mActionQueue.put(new TimerExpiredEvent(mSessionId));
		} catch (InterruptedException e) {
			// if we are interrupted we were canceled by the execution service
			// so we are fine not putting our Event into the queue.
		}
	}
}
