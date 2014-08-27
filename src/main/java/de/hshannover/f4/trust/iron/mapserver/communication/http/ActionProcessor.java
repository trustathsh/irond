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
package de.hshannover.f4.trust.iron.mapserver.communication.http;


import org.apache.log4j.Logger;

import de.hshannover.f4.trust.iron.mapserver.communication.ChannelIdentifier;
import de.hshannover.f4.trust.iron.mapserver.communication.bus.Processor;
import de.hshannover.f4.trust.iron.mapserver.communication.bus.Queue;
import de.hshannover.f4.trust.iron.mapserver.communication.bus.messages.Action;
import de.hshannover.f4.trust.iron.mapserver.communication.bus.messages.ActionSeries;
import de.hshannover.f4.trust.iron.mapserver.communication.bus.messages.BadChannelEvent;
import de.hshannover.f4.trust.iron.mapserver.communication.bus.messages.CloseChannelAction;
import de.hshannover.f4.trust.iron.mapserver.communication.bus.messages.Event;
import de.hshannover.f4.trust.iron.mapserver.communication.bus.messages.SendResponseAction;
import de.hshannover.f4.trust.iron.mapserver.communication.ifmap.EventProcessor;
import de.hshannover.f4.trust.iron.mapserver.provider.LoggingProvider;
import de.hshannover.f4.trust.iron.mapserver.utils.NullCheck;

/**
 * Process {@link ActionSeries} originating from the IF-MAP layer
 */
public class ActionProcessor extends Processor<ActionSeries> {

	private static final String sProcName = "ActionProcessor";

	private static Logger sLogger = LoggingProvider.getTheLogger();

	/**
	 * In case we have to notify the {@link EventProcessor} about something,
	 * we need to use this queue.
	 */
	private Queue<Event> mEventQueue;

	/**
	 * represents the global {@link ChannelRep} instance to find the channels.
	 */
	private ChannelRep mChannelRep;

	public ActionProcessor(Queue<ActionSeries> actionQ, int workers, int forwarders) {
		super(actionQ, workers, forwarders);

		if (workers != 1) {
			sLogger.warn(sProcName + ": More than 1 worker? I'm not thread-safe :'-(");
		}
	}

	/* (non-Javadoc)
	 * @see de.hshannover.f4.trust.iron.mapserver.communication.bus.Processor#start()
	 *
	 * Overwrite to have some checks whether everything is here...
	 */
	@Override
	public void start() {
		NullCheck.check(mChannelRep, "channel repository not initialized");
		NullCheck.check(mEventQueue, "event queue not initialized");
		super.start();
		sLogger.info(sProcName + ": Running with " + getWorkersCount() +
				" workers and " + getForwardersCount() + " forwarders");
	}

	/* (non-Javadoc)
	 * @see de.hshannover.f4.trust.iron.mapserver.communication.bus.Processor#stop()
	 *
	 * Give us a logging statement.
	 */
	@Override
	public void stop() {
		super.stop();
		sLogger.info(sProcName +": stopped");
	}

	@Override
	public void processWork(ActionSeries e) {
		for (Action action : e.getActions()) {
			if (action instanceof SendResponseAction) {
				processSendResponseAction((SendResponseAction)action);
			} else if (action instanceof CloseChannelAction) {
				processCloseChannelAction((CloseChannelAction)action);
			}
		}
	}

	private void processSendResponseAction(SendResponseAction action) {

		ChannelIdentifier channelIdent = action.getChannelIdentifier();
		ChannelThread channel = mChannelRep.getByChannelId(channelIdent);
		if (channel != null) {
			sLogger.trace(sProcName + ": Forward response to channel "
					+ channelIdent);
			channel.reply(action.getResponseContent());
		} else {
			sLogger.warn(sProcName + ": Action for channel " + channelIdent
					+ ", but it does not exist anymore.");
			sLogger.warn(sProcName + ": Notifying EventProcessor by creating" +
					" a BadChannelEvent for " + channelIdent);

			putIntoEventQueue(new BadChannelEvent(channelIdent));
		}
	}

	private void putIntoEventQueue(BadChannelEvent event) {
		NullCheck.check(event, "event is null");

		do {
			try {
				mEventQueue.put(event);
				event = null;
			} catch (InterruptedException e) { /* try harder */ }
		} while (event != null);
	}

	private void processCloseChannelAction(CloseChannelAction action) {
		ChannelIdentifier channelIdent = action.getChannelIdentifier();
		ChannelThread channel = mChannelRep.getByChannelId(channelIdent);

		if (channel != null) {
			channel.abort();
		}
	}


	public void setChannelRepository(ChannelRep channelRep) {
		NullCheck.check(channelRep, "channelRep is null");
		mChannelRep = channelRep;
	}

	public void setEventQueue(Queue<Event> queue) {
		NullCheck.check(queue, "queue is null");
		mEventQueue = queue;
	}

}
