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
package de.hshannover.f4.trust.iron.mapserver.communication.ifmap;


import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.log4j.Logger;

import de.hshannover.f4.trust.iron.mapserver.binding.RequestUnmarshaller;
import de.hshannover.f4.trust.iron.mapserver.binding.ResultMarshaller;
import de.hshannover.f4.trust.iron.mapserver.communication.ChannelIdentifier;
import de.hshannover.f4.trust.iron.mapserver.communication.ClientIdentifier;
import de.hshannover.f4.trust.iron.mapserver.communication.bus.Processor;
import de.hshannover.f4.trust.iron.mapserver.communication.bus.Queue;
import de.hshannover.f4.trust.iron.mapserver.communication.bus.messages.Action;
import de.hshannover.f4.trust.iron.mapserver.communication.bus.messages.ActionSeries;
import de.hshannover.f4.trust.iron.mapserver.communication.bus.messages.BadChannelEvent;
import de.hshannover.f4.trust.iron.mapserver.communication.bus.messages.ClosedChannelEvent;
import de.hshannover.f4.trust.iron.mapserver.communication.bus.messages.Event;
import de.hshannover.f4.trust.iron.mapserver.communication.bus.messages.PollResultAvailableEvent;
import de.hshannover.f4.trust.iron.mapserver.communication.bus.messages.RequestChannelEvent;
import de.hshannover.f4.trust.iron.mapserver.communication.bus.messages.SendResponseAction;
import de.hshannover.f4.trust.iron.mapserver.communication.bus.messages.TimerExpiredEvent;
import de.hshannover.f4.trust.iron.mapserver.communication.http.ActionProcessor;
import de.hshannover.f4.trust.iron.mapserver.datamodel.DataModelService;
import de.hshannover.f4.trust.iron.mapserver.datamodel.search.PollResult;
import de.hshannover.f4.trust.iron.mapserver.datamodel.search.SearchResult;
import de.hshannover.f4.trust.iron.mapserver.exceptions.AbortRequestException;
import de.hshannover.f4.trust.iron.mapserver.exceptions.AccessDeniedException;
import de.hshannover.f4.trust.iron.mapserver.exceptions.InvalidFilterException;
import de.hshannover.f4.trust.iron.mapserver.exceptions.InvalidIdentifierException;
import de.hshannover.f4.trust.iron.mapserver.exceptions.InvalidMetadataException;
import de.hshannover.f4.trust.iron.mapserver.exceptions.NoPollResultAvailableException;
import de.hshannover.f4.trust.iron.mapserver.exceptions.NoSuchSubscriptionException;
import de.hshannover.f4.trust.iron.mapserver.exceptions.PollResultsTooBigException;
import de.hshannover.f4.trust.iron.mapserver.exceptions.PurgePublisherNoAllowedException;
import de.hshannover.f4.trust.iron.mapserver.exceptions.SearchException;
import de.hshannover.f4.trust.iron.mapserver.exceptions.SearchResultsTooBigException;
import de.hshannover.f4.trust.iron.mapserver.exceptions.StorePublisherIdException;
import de.hshannover.f4.trust.iron.mapserver.exceptions.SystemErrorException;
import de.hshannover.f4.trust.iron.mapserver.exceptions.UnmarshalException;
import de.hshannover.f4.trust.iron.mapserver.messages.AddressedDumpResult;
import de.hshannover.f4.trust.iron.mapserver.messages.AddressedPollResult;
import de.hshannover.f4.trust.iron.mapserver.messages.AddressedSearchResult;
import de.hshannover.f4.trust.iron.mapserver.messages.DumpRequest;
import de.hshannover.f4.trust.iron.mapserver.messages.DumpResult;
import de.hshannover.f4.trust.iron.mapserver.messages.EndSessionRequest;
import de.hshannover.f4.trust.iron.mapserver.messages.EndSessionResult;
import de.hshannover.f4.trust.iron.mapserver.messages.ErrorCode;
import de.hshannover.f4.trust.iron.mapserver.messages.ErrorResult;
import de.hshannover.f4.trust.iron.mapserver.messages.NewSessionRequest;
import de.hshannover.f4.trust.iron.mapserver.messages.NewSessionResult;
import de.hshannover.f4.trust.iron.mapserver.messages.PollRequest;
import de.hshannover.f4.trust.iron.mapserver.messages.PollResultsTooBigResult;
import de.hshannover.f4.trust.iron.mapserver.messages.PublishReceivedResult;
import de.hshannover.f4.trust.iron.mapserver.messages.PublishRequest;
import de.hshannover.f4.trust.iron.mapserver.messages.PurgePublishReceivedResult;
import de.hshannover.f4.trust.iron.mapserver.messages.PurgePublisherRequest;
import de.hshannover.f4.trust.iron.mapserver.messages.RenewSessionRequest;
import de.hshannover.f4.trust.iron.mapserver.messages.RenewSessionResult;
import de.hshannover.f4.trust.iron.mapserver.messages.Request;
import de.hshannover.f4.trust.iron.mapserver.messages.RequestWithSessionId;
import de.hshannover.f4.trust.iron.mapserver.messages.Result;
import de.hshannover.f4.trust.iron.mapserver.messages.SearchRequest;
import de.hshannover.f4.trust.iron.mapserver.messages.SubscribeReceivedResult;
import de.hshannover.f4.trust.iron.mapserver.messages.SubscribeRequest;
import de.hshannover.f4.trust.iron.mapserver.provider.AuthorizationProvider;
import de.hshannover.f4.trust.iron.mapserver.provider.LoggingProvider;
import de.hshannover.f4.trust.iron.mapserver.provider.PublisherIdGenerator;
import de.hshannover.f4.trust.iron.mapserver.provider.PublisherIdProvider;
import de.hshannover.f4.trust.iron.mapserver.provider.ServerConfigurationProvider;
import de.hshannover.f4.trust.iron.mapserver.provider.SessionIdProvider;
import de.hshannover.f4.trust.iron.mapserver.utils.CollectionHelper;
import de.hshannover.f4.trust.iron.mapserver.utils.NullCheck;

/**
 * Process {@link Event} objects originating from the HTTP layer or SessionTimer.
 *
 * <p>
 * Note:  Exceptions coming from the {@link SessionRepository} are sanity
 * checks which in the end indicate programming errors, don't be irritated by
 * all these try/catch clauses.
 * </p>
 * <p>
 * TODO: Somebody should really think about locking and stuff :-(
 * </p>
 *
 * @author aw
 *
 */
public class EventProcessor extends Processor<Event> {

	private static final String sName = "EventProcessor";

	private static Logger sLogger = LoggingProvider.getTheLogger();
	private static Logger sRawLogger = LoggingProvider.getRawRequestLogger();

	/**
	 * A queue where we can put in stuff
	 */
	private Queue<ActionSeries> mActionQueue;

	/**
	 * Provides us with {@link ClientIdentifier} publisher-id mappings.
	 */
	private PublisherIdProvider mPublisherIdProv;

	/**
	 * Provides us with "fresh" session-ids.
	 */
	private SessionIdProvider mSessionIdProv;

	/**
	 * Transforming incoming requests to {@link Request} objects.
	 */
	private RequestUnmarshaller mRequestUnmarshaller;

	/**
	 * Used to build the responses to be sent back.
	 */
	private ResultMarshaller mResultMarshaller;

	/**
	 * Provides configuration
	 */
	private ServerConfigurationProvider mServerConf;

	/**
	 * Stores Information about the IF-MAP sessions currently active
	 */
	private SessionRepository mSessionRep;

	/**
	 * Accessing the data model layer
	 */
	private DataModelService mDataModel;

	/**
	 * Used to create timers for sessions.
	 */
	private SessionTimerFactory mSessionTimerFactory;

	/**
	 * Used to generate publisher-id's for new MAPCs
	 */
	private PublisherIdGenerator mPublisherIdGenerator;

	/**
	 * Used to check whether a MAPC is allowed to do changing operations
	 * like publish and purgePublisher.
	 */
	private AuthorizationProvider mAuthorizationProv;

	/**
	 * Used to protect the critical part, including {@link SessionRepository}
	 * and {@link DataModelService}
	 */
	private Object mBigProcessLock;

	/**
	 * This list is to be filled with all the {@link Result} objects that need
	 * to be sent out. It can be processed without holding the
	 * {@link #mBigProcessLock} so we get some more parallelism here.
	 */
	private ThreadLocal<List<Result>> mResultList = new ThreadLocalResults();

	/**
	 * Constructor
	 *
	 * @param eventQueue
	 * @param workers
	 * @param forwarders
	 */
	public EventProcessor(Queue<Event> eventQueue, int workers, int forwarders) {
		super(eventQueue, workers, forwarders);
		mSessionRep = new SessionRepositoryImpl();
		mBigProcessLock = new Object();
	}


	/* (non-Javadoc)
	 * @see de.hshannover.f4.trust.iron.mapserver.communication.bus.Processor#start()
	 *
	 * Overwrite to have some checks whether everything is here...
	 */
	@Override
	public void start() {
		NullCheck.check(mActionQueue, "action queue not initialized");
		NullCheck.check(mPublisherIdProv, "publisher-id provider not initialized");
		NullCheck.check(mSessionIdProv, "session-id provider not initialized");
		NullCheck.check(mRequestUnmarshaller, "request unmarshaller not initialized");
		NullCheck.check(mResultMarshaller, "result marshaller not initialized");
		NullCheck.check(mServerConf, "server configuration not initialized");
		NullCheck.check(mSessionRep, "session repository not initialized");
		NullCheck.check(mDataModel, "data model not initialized");
		NullCheck.check(mSessionTimerFactory, "timer factory not initialized");
		NullCheck.check(mPublisherIdGenerator, "publisherid generator not initialized");
		NullCheck.check(mAuthorizationProv, "authorization provider not initialized");
		super.start();
		sLogger.info(sName + ": Running with " + getWorkersCount() +
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
		sLogger.info(sName + " stopped");
	}

	/**
	 * Dispatches between {@link Event} implementations. This is the entry
	 * point for the {@link Event} processing.
	 */
	@Override
	public void processWork(Event e) {

		// double dispatch mechanism for events
		e.dispatch(this);

		// if any results have to be processed, after the event
		// processing has completed, do that now.
		processDeferredResults();
	}

	/**
	 * Processes a {@link RequestChannelEvent}.
	 *
	 * <ul>
	 * <li>Unmarshal the request</li>
	 * <li>Dispatch between SSRC and ARC</li>
	 * </ul>
	 *
	 * <p>
	 * Note: We send out a InvalidIdentifier or InvalidMetadata error, before
	 * we even check if this guy has a {@link Session}. That is because we
	 * detect these errors during unmarshalling and handle them before looking
	 * up the {@link Session}.
	 * </p>
	 *
	 * @param event
	 */
	public void processRequestChannelEvent(RequestChannelEvent event) {
		ChannelIdentifier channelId = event.getChannelIdentifier();
		ClientIdentifier clientId = event.getClientIdentifier();
		Request request = null;

		sLogger.trace(sName + ": RequestChannelEvent on channel " + channelId);

		try {
			if (mServerConf.isLogRaw()) {
				logRawRequest(event);
			}

			request = mRequestUnmarshaller.unmarshal(event.getRequestContent());

		// catch, handle, return, don't do anything special!
		} catch (InvalidIdentifierException e) {
			sLogger.warn(sName + ": Invalid identifier from " + clientId + ": " + e.getMessage());
			deferErrorResult(channelId, clientId, ErrorCode.InvalidIdentifier,
					e.getMessage());
			return;
		} catch (InvalidMetadataException e) {
			sLogger.warn(sName + ": Invalid metadata from " + clientId + ": " + e.getMessage());
			deferErrorResult(channelId, clientId, ErrorCode.InvalidMetadata,
					e.getMessage());
			return;
		} catch (InvalidFilterException e) {
			sLogger.warn(sName + ": Invalid filter from " + clientId + ": " + e.getMessage());
			deferErrorResult(channelId, clientId, ErrorCode.Failure, e.getMessage());
			return;
		} catch (UnmarshalException e) {
			// FIXME:
			// The problem is, that this can also be due to a "non soap message",
			// in this case we should maybe react differently?
			sLogger.error(sName + ": " + e.getMessage());
			deferErrorResult(channelId, clientId, ErrorCode.Failure, e.getMessage());
			return;
		}

		request.setChannelId(event.getChannelIdentifier());
		request.setClientId(event.getClientIdentifier());
		request.setFirst(event.isFirstRequest());

		// At this point we start working with the repository and possibly with
		// the DataModelService, so this part is critical.

		// Only reached if unmarshalling was successful, dispatch between
		// SSRC and ARC operations
		synchronized (mBigProcessLock) {
			try {
				request.dispatch(this);
			} catch (AccessDeniedException e) {
				deferErrorResult(channelId, clientId, ErrorCode.AccessDenied,
						e.getMessage());
			} catch (SystemErrorException e) {
				// TODO: This is actually way too late and we might be in bad
				// state
				sLogger.error(sName + ": " + e.getMessage());
				e.printStackTrace();
				deferErrorResult(channelId, clientId, ErrorCode.SystemError,
						e.getMessage());

				// Make sure this doesn't go unnoticed
				sLogger.error("): IROND IS BROKEN :(");
				sLogger.error("): IROND IS BROKEN :(");
				sLogger.error("): IROND IS BROKEN :(");
				sLogger.error("): IROND IS BROKEN :(");
				sLogger.error("): IROND IS BROKEN :(");
			}
		}
	}

	/**
	 * Processes a newSession request.
	 *
	 * @param clientId
	 * @param channelId
	 * @param request
	 */
	public void processNewSessionRequest(NewSessionRequest request) {

		ClientIdentifier clientId = request.getClientIdentifier();
		ChannelIdentifier channelId = request.getChannelIdentifier();
		Session session = getSessionValidated(clientId);
		sLogger.debug(sName + ": newSession for " + clientId +" on " + channelId);

		if (session != null) {
			// This guy has a running session, lets see...
			if (channelId.equals(session.getSsrc())
					|| session.getSsrc() == null
					|| request.isFirstRequest()) {
				sLogger.debug(sName + ": newSession from " + clientId + " while having an" +
						" active session. That's fine, we end the old and create a new.");
				// the request is on the same channel or on a new one because the
				// old one was closed. Process it, but kill the old session before,
				// send endSessionResult on an existing ARC.
				if (session.isPollPending()) {
					sLogger.debug(sName + ": Sending endSession on ARC to "
							+ clientId);
					ChannelIdentifier arc = session.getArc();
					deferEndSessionResult(arc, clientId);
				}

				// remove this session
				endSessionLocal(session);
				session = null;
			} else {
				// We get here, if a MAPC tries to run newSession on an oldChannel
				sLogger.warn(sName + ": " + clientId.toString() + " tries to" +
						" create a new session on a channel that was used before" +
						" We send AccessDenied.");

				// FIXME:
				// I don't know what to do in that case, because a client really
				// shouldn't do that. (It could happen because of a race condition, though)
				// We simply sent him an AccessDenied for now.
				deferErrorResult(channelId, clientId, ErrorCode.AccessDenied,
						"Only one SSRC allowed.");
				return;
			}
		}

		// If this guy had a session before, at this point this session is away.
		// Create the new one and register it.
		session = newSessionLocal(clientId, channelId, request);

		String sessionId = session.getSessionId();
		String publisherId = session.getPublisherId();
		Integer reqMprs = request.getMaxPollResultSize();

		deferNewSessionResult(channelId, clientId, sessionId, publisherId, reqMprs);
	}

	/**
	 * Process an endSession request.
	 *
	 * @param request
	 */
	public void processEndSessionRequest(EndSessionRequest request) {
		ChannelIdentifier channelId = request.getChannelIdentifier();
		ClientIdentifier clientId = request.getClientIdentifier();
		sLogger.debug(sName + ": endSession for " + clientId + " on " + channelId);

		try {
			Session session = getSessionValidated(request);

			checkAndSetSsrc(request, session);

			// send endSessionResult on an existing ARC, if a poll is pending
			if (session.isPollPending()) {
				// bit of a sanity check
				if (session.hasArc()) {
					deferEndSessionResult(session.getArc(), clientId);
				} else {
					sLogger.error("UNEXPECTED: poll pending but no ARC associated");
				}
			}

			endSessionLocal(session);

			deferEndSessionResult(channelId, clientId);

		} catch (AbortRequestException e) {
			// abort
		}
	}

	/**
	 * Process a renewSession request.
	 *
	 * @param clientId
	 * @param channelId
	 * @param firstRequest
	 * @param request
	 */
	public void processRenewSessionRequest(RenewSessionRequest request) {
		ChannelIdentifier channelId = request.getChannelIdentifier();
		ClientIdentifier clientId = request.getClientIdentifier();
		sLogger.debug(sName + ": renewSession for " + clientId + " on " + channelId);

		try {
			Session session = getSessionValidated(request);

			checkAndSetSsrc(request, session);

			deferRenewSessionReceived(channelId, clientId);

		} catch (AbortRequestException e) {
			// abort
		}
	}

	/**
	 * Process a publish request.
	 *
	 * @param clientId
	 * @param channelId
	 * @param request
	 * @throws AccessDeniedException
	 */
	public void processPublishRequest(PublishRequest request) throws AccessDeniedException {
		ChannelIdentifier channelId = request.getChannelIdentifier();
		ClientIdentifier clientId = request.getClientIdentifier();
		sLogger.debug(sName + ": publish for " + clientId +  " on " + channelId);
		try {
			if (!mAuthorizationProv.isWriteAllowed(clientId)) {
				throw new AccessDeniedException("read-only MAPC - publish denied");
			}

			Session session = getSessionValidated(request);

			checkAndSetSsrc(request, session);

			mDataModel.publish(request);

			deferPublishReceived(channelId, clientId);

		} catch (AbortRequestException e) {
			// abort, error message is already on the way
		} catch (InvalidMetadataException e) {
			// this happens if that guy publishes contradictionary cardinality
			// for metadata, send him the error response.
			// IT SHOULD BE REDUNDANT NOW..., because we check that before
			deferErrorResult(request.getChannelIdentifier(),
					clientId, ErrorCode.InvalidMetadata, e.getMessage());
		}
	}

	/**
	 * Process a search request.
	 *
	 * @param clientId
	 * @param channelId
	 * @param firstRequest
	 * @param request
	 */
	public void processSearchRequest(SearchRequest request) {
		ChannelIdentifier channelId = request.getChannelIdentifier();
		ClientIdentifier clientId = request.getClientIdentifier();
		sLogger.debug(sName + ": search for " + clientId + " on " + channelId);

		try {
			Session session = getSessionValidated(request);

			checkAndSetSsrc(request, session);

			SearchResult result = mDataModel.search(request);

			deferSearchResult(channelId, clientId, result);

		} catch (SearchResultsTooBigException e) {
			sLogger.trace(sName + ": SearchResultsTooBig for " + clientId
					+ "(" + e.getMessage() + ")");
			deferErrorResult(channelId, clientId, ErrorCode.SearchResultsTooBig,
					e.getMessage());

		} catch (SearchException e) {
			deferErrorResult(channelId, clientId, ErrorCode.Failure,
					e.getMessage());
		} catch (AbortRequestException e) {
			// abort
		}
	}

	/**
	 * Process a subscribe request.
	 *
	 * @param clientId
	 * @param channelId
	 * @param request
	 */
	public void processSubscribeRequest(SubscribeRequest request) {
		ChannelIdentifier channelId = request.getChannelIdentifier();
		ClientIdentifier clientId = request.getClientIdentifier();
		sLogger.debug(sName + ": subscribe for " + clientId + " on " + channelId);
		try {
			Session session = getSessionValidated(request);
			checkAndSetSsrc(request, session);

			try {
				mDataModel.subscribe(request);
			} catch (NoSuchSubscriptionException e) {
				// not sure how to handle this case anyway.
				// Simply send a subscribeReceived, because it'll only
				// happen on delete, and then the subscription isn't there
				// anyway.
				sLogger.info(sLogger + ": " + clientId + " wrong subscription: "
						+ e.getMessage());
			} catch (SearchException e) {
				deferErrorResult(channelId, clientId, ErrorCode.Failure,
						e.getMessage());
			}

			deferSubscribeReceived(channelId, clientId);

		} catch (AbortRequestException e) {
			// abort
		}
	}

	/**
	 * Process a purgePublisher request.
	 *
	 * @param clientId
	 * @param channelId
	 * @param request
	 */
	public void processPurgePublisherRequest(PurgePublisherRequest request)
													throws AccessDeniedException {
		ChannelIdentifier channelId = request.getChannelIdentifier();
		ClientIdentifier clientId = request.getClientIdentifier();
		sLogger.debug(sName + ": purgePublisher for " + clientId + " on " + channelId);
		try {
			if (!mAuthorizationProv.isWriteAllowed(clientId)) {
				throw new AccessDeniedException("read-only MAPC - purgePublisher denied");
			}

			Session session = getSessionValidated(request);
			String sId = request.getSessionId();
			String pId = request.getPublisherId();

			checkAndSetSsrc(request, session);

			mDataModel.purgePublisher(sId, pId);

			deferPurgePublisherReceived(channelId, clientId);

		} catch (AbortRequestException e) {
			// abort
		} catch (PurgePublisherNoAllowedException e) {
			sLogger.warn(sName + ": " + request.getClientIdentifier() +
					" not allowed to do purgePublisher: " + e.getMessage());
			throw new AccessDeniedException(e.getMessage());
		}
	}

	/**
	 * Process a dump request.
	 *
	 * @param clientId
	 * @param channelId
	 * @param request
	 */
	public void processDumpRequest(DumpRequest request) {
		ChannelIdentifier channelId = request.getChannelIdentifier();
		ClientIdentifier clientId = request.getClientIdentifier();
		sLogger.debug(sName + ": dump for " + clientId + " on " + channelId);

		try {
			Session session = getSessionValidated(request);
			checkAndSetSsrc(request, session);
			DumpResult result = mDataModel.dump(session.getSessionId());
			result.setFilter(request.getIdentifier());
			deferDumpResult(channelId, clientId, result);
		} catch (AbortRequestException e) {
			// abort
		}
	}



	/**
	 * Process an event which indicates that a new PollResult for a MAPC is
	 * available. Either we send out the result directly, in this case the MAPC
	 * has a poll pending, or we mark the {@link Session} as having a poll
	 * available.
	 *
	 * @param e
	 */
	public void processPollResultAvailableEvent(PollResultAvailableEvent e) {
		synchronized (mBigProcessLock) {
			String sessionId = e.getSessionId();
			Session session = mSessionRep.getBy(sessionId);

			if (session == null) {
				sLogger.trace(sName + ": PollResultAvailable for session-id "
						+ sessionId + " but no session object for this session-id."
						+ " exists. Don't worry, that's O.K.!");
				// That's actually not really bad, it can happen as of simple
				// racing situations where a subscription of a MAPC gets updated
				// shortly before this client is doing an endSession and the
				// endSession request is processed before the PollResultAvailableEvent
				//
				// ==> It's O.K. Don't worry...
			} else {
				if (session.isPollPending()) {
					// MAPC is waiting for a response of a previous poll request
					ChannelIdentifier arc = session.getArc();
					if (arc == null) {
						// This should never be the case if isPollPending() is true.
						sLogger.error("UNEXPECTED: We have a pending poll but no" +
								" ARC associated");
						// Can we do something about it?
						// For now, reset the poll state and mark it having a
						// poll result available.
						//
						// In any case, something is seriously broken if we get here.
						sLogger.warn(sName + ": Clearing PollState and"
								+ " setting PollResAvail for client "
								+ session.getClientIdentifier());
						session.unsetPollState();
						session.setPollResultAvailable();
					} else {
						// send the poll result out directly
						handleSendPollRes(arc, session);
					}
				} else {
					session.setPollResultAvailable();
				}
			}
		}
	}

	/**
	 * Process a poll request.
	 *
	 * We try to find the {@link Session} for the client doing the poll request.
	 * If there is a {@link PollResult} available, put it into the {@link Queue}
	 * and reset the state. Else, we mark a pending poll for the {@link Session}.
	 *
	 * @see EventProcessor#getSessionValidated(ClientIdentifier)
	 * @see EventProcessor#handleDoubleArcFailure(ChannelIdentifier, Session)
	 * @see EventProcessor#handleSendPollRes(ChannelIdentifier, Session)
	 * @see EventProcessor#handlePollPending(ChannelIdentifier, Session)
	 *
	 * @param request
	 * @param arc
	 * @param clientId
	 */
	public void processPollRequest(PollRequest request) {

		ChannelIdentifier arc = request.getChannelIdentifier();

		try {
			Session session = getSessionValidated(request);

			checkAndSetArc(request, session);

			if (session.isPollResultAvailable()) {
				handleSendPollRes(arc, session);
			} else {
				handlePollPending(arc, session);
			}
		} catch (AbortRequestException e) {
			// the client is already notified
		}
	}

	/**
	 * This method takes care of the case when a client has opened a second
	 * ARC while a poll is still pending.
	 *
	 * The following steps are done:
	 *
	 * <ul>
	 * <li>end the session session</li>
	 * <li>respond to the poll on the older ARC with end session result</li>
	 * <li>respond to the poll on the new ARC with errorResult InvalidSessionId</li>
	 * </ul>
	 *
	 * Don't send anything out on the SSRC, just remove the session, we are not
	 * able to do so, anyway.
	 *
	 * TODO: Should we instruct the HTTP-layer to remove the channel?
	 *       Better not, because the client may want to reuse it?
	 *
	 * @param newArc
	 * @param session
	 * @throws AbortRequestException
	 */
	private void handleDoubleArcFailure(ChannelIdentifier newArc, Session session)
													throws AbortRequestException {
		ChannelIdentifier oldArc = session.getArc();
		sLogger.warn(sName + ": " + session.getClientIdentifier() +
				"created a second ARC while another poll was pending. Ending the " +
				"session and sending responses out.");

		endSessionLocal(session);

		deferEndSessionResult(oldArc, session.getClientIdentifier());

		deferErrorResult(newArc, session.getClientIdentifier(),
				ErrorCode.InvalidSessionID, "two times ARC");
	}



	/**
	 * Sends a {@link PollResult} to a client and clears the state of the
	 * {@link Session} regarding pending polls afterwards.
	 * If sending is not possible because the {@link PollResult} is too big,
	 * we call call a helper.
	 *
	 * @see EventProcessor#handlePollResultTooBig(ChannelIdentifier, Session);
	 *
	 * @param arc the channel where the {@link PollResult} ist to be send out
	 * @param session the corresponding session.
	 */
	private void handleSendPollRes(ChannelIdentifier arc, Session session) {
		try {
			sLogger.trace(sName + ": sending out poll result");
			String sessionId = session.getSessionId();

			// sanity check
			if (session.hasArc() && !session.getArc().equals(arc)) {
				throw new SystemErrorException("Missed a double ARC failure...");
			}

			session.unsetPollState();

			// get reply and put it into the local list.
			PollResult pollResult = mDataModel.getPollResultFor(sessionId);
			sLogger.trace(sName + ": PollResults results= "
					+ pollResult.getResults().size());
			deferPollResult(arc, session.getClientIdentifier(), pollResult);
		} catch (PollResultsTooBigException e) {
			// The result was too big, send a error response
			sLogger.trace(sName + ": " + session.getClientIdentifier() +
					" will get PollResultsTooBig");
			deferPollResultError(arc, session.getClientIdentifier(),
					ErrorCode.PollResultsTooBig, e.getMessage());

		} catch (NoPollResultAvailableException e) {
			// Most likely this is a programming error, try to do some workaround:
			// Don't send anything back and set poll pending state for this
			// session.
			sLogger.error("UNEXEPECTED: poll avl set in session, but no PollResult" +
					" found in  DataModel");
			handlePollPending(arc, session);
		}
	}

	/**
	 * Sets the state of the {@link Session} such that a pending poll
	 * is indicated. Map the ARC on which the poll is pending to the
	 * {@link Session} using the {@link SessionRepository}.
	 * If we had a timer running because the SSRC is closed, cancel this timer.
	 *
	 * @param arc
	 * @param session
	 */
	private void handlePollPending(ChannelIdentifier arc, Session session) {
		NullCheck.check(session, "session is null");
		NullCheck.check(arc, "arc is null");
		sLogger.trace(sName + ": Setting poll pending for "
				+ session.getClientIdentifier() + " on " + arc);
		session.setPollPending();
		session.setArc(arc);
	}

	/**
	 * Process a {@link ClosedChannelEvent}.
	 *
	 * TODO: Some documentation.
	 *
	 * @param e
	 */
	public void processClosedChannelEvent(ClosedChannelEvent e) {
		synchronized (mBigProcessLock) {
			ChannelIdentifier channel = e.getChannelIdentifier();
			sLogger.debug(sName + ": Got ClosedChannelEvent for " + channel);

			// try to look up the session associated with this channel.

			Session session = mSessionRep.getBy(channel);

			// if we don't find an associated session, we don't care
			if (session == null) {
				sLogger.trace(sName + ": No session found for channel");
				return;
			}
			ChannelIdentifier arc = session.getArc();
			ChannelIdentifier ssrc = session.getSsrc();


			if (channel.equals(arc) && session.isPollPending()) {
				// in this case we have a problem, because the ARC was closed
				// while a poll was pending
				sLogger.warn(sName + ": ARC for " + session.getClientIdentifier()
						+ " closed while poll was pending. Calling endSession.");

				endSessionLocal(session);

				// No need to start a timer, the Session is gone anyway

				return;

			} else if (channel.equals(arc) && !session.isPollPending()) {
				// the ARC was closed, but that's ok, no poll was pending
				session.setArc(null);
				mSessionRep.unmap(session, channel);
			} else if (channel.equals(ssrc)) {
				// the SSRC was closed
				session.setSsrc(null);
				mSessionRep.unmap(session, channel);
			}

			if (session.hasTimer()) {
				sLogger.error("UNEXPECTED: Have a running timer for a Session" +
						" which just had a channel associated");
				session.getTimer().cancel();
				session.setTimer(null);
			}

			// only if we have no channel associated, start a timer.

			arc = session.getArc();
			ssrc = session.getSsrc();

			if (arc == null && ssrc == null) {
				sLogger.debug(sName + ": Creating Timer for " +
						session.getClientIdentifier());
				String sessionId = session.getSessionId();
				session.setTimer(mSessionTimerFactory.newTimer(sessionId));
				session.getTimer().start();
			}
		}
	}

	/**
	 * Process a {@link TimerExpiredEvent}.
	 *
	 * We try to find the corresponding {@link Session} with the given
	 * session-id and then run a normal endSession.
	 *
	 * @param e
	 */
	public void processTimerExpiredEvent(TimerExpiredEvent e) {
		synchronized (mBigProcessLock) {
			sLogger.debug(sName + ": Processing TimerEvent with session-id "
					+ e.getSessionId());

			Session session = mSessionRep.getBy(e.getSessionId());

			if (session == null) {
				// That's weird
				sLogger.warn(sName + ": Could not find a associated session"
						+ " for the session-id of the TimerEvent");
				return;
			}

			// Do checks to see if the timer is really expired, or if it's just
			// a nasty race condition.
			if (!session.hasTimer()) {
				if (!session.hasSsrc() && !session.hasArc()) {
					sLogger.error(sName + ": UNEXPECTED: Timer should have"
							+ "been running for this session");
				}
				return;
			} else if (session.hasTimer() && session.getTimer().isRunning()) {
				// huh... there is a timer, but it is still running. Can happen due
				// to racing of events, shouldn't happen too often, though.
				return;
			}

			sLogger.debug(sName + ":Session for " + session.getClientIdentifier()
					+ " will be ended as of a timeout.");

			// Sanity check
			if (session.isPollPending()) {
				sLogger.error("UNEXPECTED: Timeout for a session having a poll pending");
			}

			endSessionLocal(session);
		}
	}

	/**
	 * Process a {@link BadChannelEvent} e
	 *
	 * This event can happen if a MAPC tries to do a request while the channel
	 * expected a response, something fails with a request on the channel or
	 * sending of a response on a channel fails.
	 *
	 * We handle all of these cases by ending the session, optionally sending
	 * a endSessionResult on an ARC with a pending poll if the ARC is not the
	 * channel that caused the problem.
	 *
	 * The channel that caused the problem is going to be closed by the
	 * http layer. If a {@link Session} had two channels attached, the second
	 * channel is not explicitly closed. Therefore an unaffected  SSRC or ARC
	 * may later be reused, given a new session is created by the MPAC.
	 *
	 * @param e
	 */
	public void processBadChannelEvent(BadChannelEvent e) {
		synchronized (mBigProcessLock) {
			ChannelIdentifier chId = e.getChannelIdentifier();
			sLogger.debug(sName + ": Processing BadChannelEvent on " + chId);

			Session session = mSessionRep.getBy(chId);

			// do nothing if no session is known for this channel
			if (session == null) {
				sLogger.debug(sName + ": No session found for " + chId);
				return;
			}

			sLogger.trace(sName + ": Session for " + session.getClientIdentifier()
					+ " will be closed because of a BadChannelEvent");

			// if this session has an ARC with a poll pending, and the channel which
			// caused the problem is not the ARC, send out an endSessionResult.
			if (session.hasArc() && !session.getArc().equals(chId)
					&& session.isPollPending()) {
				deferEndSessionResult(session.getArc(), session.getClientIdentifier());
			}

			// forget the session
			endSessionLocal(session);
		}
	}

	/**
	 * Get the right {@link Session} object from the {@link SessionRepository}.
	 *
	 * We identify a MAPC by the session-id and the {@link ClientIdentifier}
	 * we get. If no session is stored belonging to the {@link ClientIdentifier}
	 * we send a AccessDenied error.
	 * If the MAPC uses a session-id belonging to a different MAPC we send
	 * a InvalidSessionId error.
	 * If we don't find a {@link Session} object for either the session-id or
	 * the {@link ClientIdentifier} we send InvalidSessionId.
	 * If we find a {@link Session} for the {@link ClientIdentifier} but not
	 * for the session-id given we send a InvalidSessionId, but indicate in
	 * the error string that a {@link Session} for this client exists.
	 *
	 * If this method throws a {@link AbortRequestException} an error response
	 * was created and is on the way to the client.
	 * <b>Dont't put another {@link ActionSeries} into the action queue!</b>
	 *
	 * @param clientId {@link ClientIdentifier} of the {@link Event}
	 * @param channelId {@link ChannelIdentifier} of the {@link Event}
	 * @param sessionId session-id found in the {@link Request} we are processing
	 * @return the {@link Session} for this MAPC or null in case no {@link Session}
	 *	 was found.
	 * @throws AbortRequestException if the MAPC did something wrong.
	 */
	private Session getSessionValidated(RequestWithSessionId request)
	throws AbortRequestException {

		ClientIdentifier clientId = request.getClientIdentifier();
		ChannelIdentifier channelId = request.getChannelIdentifier();
		String sessionId = request.getSessionId();

		Session sessionClientId = mSessionRep.getBy(clientId);
		Session sessionSessionId = mSessionRep.getBy(sessionId);

		if (sessionClientId != sessionSessionId) {
			// this shouldn't be the case
			if (sessionSessionId != null) {
				// Now that's fishy, this guy knows about a session-id that's
				// from a different client...
				sLogger.warn(sName + ": Ughh... Why does \"" + clientId
						+ "\" know about the session  of \""
						+ sessionSessionId.getClientIdentifier() + "\" ?");

				// Let us be friendly and tell him that he just found a valid
				// session-id. We shouldn't do that, should we?
				deferErrorResult(channelId, clientId, ErrorCode.InvalidSessionID,
						"You are _not_ supposed to use the session-id of " +
						" somebody else.");
			} else {
				// FIXME TC65 ... what if channel is an ARC?
				// we found a session for the client, but the provided session
				// id was not valid. this will normally trigger an invalid session
				// id error. except: if the channel being used was an old channel
				// in this case the spec demands an access-denied error
				ChannelIdentifier clientChannel = request.getChannelIdentifier();
				if (clientChannel != sessionClientId.getArc() &&
					clientChannel != sessionClientId.getSsrc()) {
					sLogger.warn(sName + ": " + clientId +
							" uses an old channel as SSRC. Sending out AccessDenied");
					deferErrorResult(clientChannel, clientId,
							ErrorCode.AccessDenied, "Existing SSRC");
				} else {
					// sessionSessionId is null, so sessionClientId can't be.
					// Therefore we found a session for the MAPC, but the wrong
					// session-id was used. Transpose digits?
					// return invalid session id if channel is valid but the session
					// id is not
					sLogger.warn(sName + ": Client " + clientId + " uses wrong session-id");
					deferErrorResult(channelId, clientId, ErrorCode.InvalidSessionID,
							"You have a session, but used the wrong session-id.");
				}
			}
		} else {
			// That's fine both are the same
			if (sessionSessionId == null) {
				sLogger.warn(sName + ": " + clientId + " tried to operate "
						+ "without having an active session");
				// Now that's not so cool, both are null, indicating neither
				// for the session-id nor for the clientId is a Session in the
				// repository. Send InvalidSessionId.
				deferErrorResult(channelId, clientId, ErrorCode.InvalidSessionID,
						"Session not found.");
			} else {
				// We found a valid Session, return it;
				return sessionSessionId;
			}
		}

		// Ending up here means we did a responseWithError() call, throw the
		// AbortRequestException.
		throw new AbortRequestException();
	}

	private Session getSessionValidated(ClientIdentifier clientId) {
		return mSessionRep.getBy(clientId);
	}

	/**
	 * Creates a new {@link Session}, stores it, creates mappings and
	 * returns the new created {@link Session}.
	 * If now max-poll-result-size was given, get the one from the
	 * server configuration.
	 * If the {@link PublisherIdProvider} does not know about the {@link ClientIdentifier}
	 * we generate a new publisher-id and store it.
	 *
	 * @param clientId
	 * @param channelId
	 * @param request
	 * @return
	 */
	private Session newSessionLocal(ClientIdentifier clientId,
			ChannelIdentifier channelId, NewSessionRequest request) {
		String publisherId = mPublisherIdProv.getPublisherIdFor(clientId);

		// if no publisherId is available we have to ask the generator to create
		// a new one.
		if (publisherId == null) {
			publisherId = mPublisherIdGenerator.generatePublisherIdFor(clientId,
					mPublisherIdProv);
			NullCheck.check(publisherId, "generated publisher-id is null");

			try {
				mPublisherIdProv.storePublisherIdFor(clientId, publisherId);
			} catch (StorePublisherIdException e) {
				sLogger.fatal("Could not store generated publisher-id for " + clientId);
				// we continue anyway
			}
		}

		String sessionId = mSessionIdProv.getSessionId();
		Session session = new Session(clientId, publisherId);

		session.setSsrc(channelId);
		session.setSessionId(sessionId);

		mSessionRep.store(session);
		mSessionRep.map(session, channelId);
		mSessionRep.map(session, sessionId);

		mDataModel.newSession(sessionId, publisherId,
							  request.getMaxPollResultSize(),
							  clientId);

		return session;
	}

	/**
	 * Cleans up traces of a {@link Session}
	 *
	 * @param session the {@link Session} to forget
	 */
	private void endSessionLocal(Session session) {

		if (session.hasArc()) {
			mSessionRep.unmap(session, session.getArc());
		}

		if (session.hasSsrc()) {
			mSessionRep.unmap(session, session.getSsrc());
		}

		if (session.hasTimer()) {
			session.getTimer().cancel();
		}

		String sessionId = session.getSessionId();
		if (sessionId != null) {
			mSessionRep.unmap(session, sessionId);
			// forward call to data model layer
			mDataModel.endSession(session.getSessionId());
		} else {
			sLogger.error("UNEXPECTED: Session without session-id for "
					+ session.getClientIdentifier() + " found");
		}

		// forget about it
		mSessionRep.drop(session);
	}

	/**
	 * Update the SSRC if the MAPC is starting to use a new connection
	 * for the SSRC.
	 *
	 * We also do canceling of the {@link SessionTimer} here, so this
	 * method can be used to implement renewSession.
	 *
	 * @param channelId
	 * @param session
	 * @param request
	 * @throws AbortRequestException
	 */
	private void checkAndSetSsrc(Request request, Session session) throws AbortRequestException {
		NullCheck.check(session, "session is null");

		SessionTimer timer = session.getTimer();
		ChannelIdentifier ssrc = request.getChannelIdentifier();
		boolean firstRequest = request.isFirstRequest();

		checkSsrcNotArc(ssrc, session);

		if (firstRequest && !ssrc.equals(session.getSsrc())) {
			sLogger.trace(sName + ": " + session.getClientIdentifier()
					+ " uses " + ssrc + " as new SSRC");

			if (session.hasSsrc()) {
				// FIXME TC65 ... racy !
				// close old connections
				//putActionSeriesIntoActionQueue(new ActionSeries(new  CloseChannelAction(session.getSsrc(), session.getClientIdentifier())));
				mSessionRep.unmap(session, session.getSsrc());
				session.setSsrc(null);
			}

			// map in the new SSRC
			session.setSsrc(ssrc);
			mSessionRep.map(session, ssrc);

			if (session.hasTimer()) {
				sLogger.trace(sName +": Cancel timer for " + session.getClientIdentifier());
				timer.cancel();
				session.setTimer(null);
			}

		} else if (ssrc.equals(session.getSsrc())) {
			sLogger.trace(sName + ": " + session.getClientIdentifier()
					+ " uses existing SSRC for request");
			// that's good, we don't have to do anything
		} else {
			sLogger.warn(sName + ": " + session.getClientIdentifier() +
					" uses an old channel as SSRC. Sending out AccessDenied");
			deferErrorResult(ssrc, session.getClientIdentifier(),
					ErrorCode.AccessDenied, "Existing SSRC");
			throw new AbortRequestException();


		}
	}

	/**
	 * Check if a client uses a good ARC to send us a poll request.
	 * If the SSRC is used send an error.
	 * If a second ARC is used while a poll is pending,
	 * {@link #handleDoubleArcFailure(ChannelIdentifier, Session)}
	 * manages everything.
	 * If no poll is pending, but a second ARC is used we send a AccessDenied
	 * "Existing ARC" error.
	 *
	 * @param arc
	 * @param session
	 * @throws AbortRequestException
	 */
	private void checkAndSetArc(Request request, Session session) throws AbortRequestException {

		ChannelIdentifier arc = request.getChannelIdentifier();
		boolean firstRequest = request.isFirstRequest();
		checkArcNotSsrc(arc, session);

		if (session.hasArc() && session.isPollPending()) {
				handleDoubleArcFailure(arc, session);
				throw new AbortRequestException();
		} else if (firstRequest && !arc.equals(session.getArc())) {
			sLogger.trace(sName + ": " + session.getClientIdentifier()
					+ " uses " + arc + " as new ARC");

			// unmap the old ARC
			if (session.hasArc()) {
				mSessionRep.unmap(session, session.getArc());
			}

			session.setArc(arc);
			mSessionRep.map(session, arc);

			if (session.hasTimer()) {
				session.getTimer().cancel();
				session.setTimer(null);
			}
		} else if (arc.equals(session.getArc())) {
			sLogger.trace(sName + ": " + session.getClientIdentifier()
					+ " uses existing ARC for request");
			// that's good, we don't have to do anything
		} else {
			sLogger.warn(sName + ": " + session.getClientIdentifier() +
					" uses an old channel as ARC. Sending out AccessDenied");
			deferErrorResult(arc, session.getClientIdentifier(),
					ErrorCode.AccessDenied, "Existing SSRC");
			throw new AbortRequestException();
		}
	}

	/**
	 * Check if a client used its SSRC to send us a Poll request. If so,
	 * send an Error / Failure.
	 *
	 * @param arc
	 * @param session
	 * @throws AbortRequestException
	 */
	private void checkArcNotSsrc(ChannelIdentifier arc, Session session)
													throws AbortRequestException {
		if (session.hasSsrc()) {
			if (session.getSsrc().equals(arc)) {
				sLogger.warn(sName + ": " + session.getClientIdentifier() +
						" uses its SSRC as ARC. Sending out a errorResult/Failure");
				deferErrorResult(arc, session.getClientIdentifier(),
						ErrorCode.Failure, "SSRC used as ARC");
				throw new AbortRequestException();
			}
		}
	}

	/**
	 * Check if a client used its ARC to send us a SSRC operation.
	 * If so, send an Error / Failure.
	 *
	 * @param arc
	 * @param session
	 * @throws AbortRequestException
	 */
	private void checkSsrcNotArc(ChannelIdentifier ssrc, Session session)
													throws AbortRequestException {
		if (session.hasArc()) {
			if (session.getArc().equals(ssrc)) {
				sLogger.warn(sName + ": " + session.getClientIdentifier() +
						" uses its ARC as SSRC. Sending out a errorResult/Failure");
				deferErrorResult(session.getArc(), session.getClientIdentifier(),
						ErrorCode.Failure, "ARC used as SSRC");
				throw new AbortRequestException();
			}
		}
	}



	/**********************************************************************
	 * From here on everything is trivial				 *
	 **********************************************************************/

	private void logRawRequest(RequestChannelEvent event) {
		logRaw(event.getClientIdentifier(), event.getChannelIdentifier(),
				event.getRequestContent(), true);
	}

	private void logRawResponse(SendResponseAction sra) {
		logRaw(sra.getClientIdentifier(), sra.getChannelIdentifier(),
				sra.getResponseContent(), false);
	}

	/**
	 * WARNING: THIS IS SYNCHRONIZED!!!
	 *
	 * @param clientId
	 * @param chId
	 * @param is
	 * @param isRequest
	 */
	private synchronized void logRaw(ClientIdentifier clientId, ChannelIdentifier chId,
			InputStream is, boolean isRequest) {

		int ret;
		StringBuilder sb = new StringBuilder();

		sRawLogger.info(String.format("%s %s on channel %s",
				isRequest ?  "Request from" : "Response to",
				clientId, chId));

		// HACK, we rely on the InputStream being reset()able
		try {
			is.mark(is.available());
			while ((ret = is.read()) >= 0) {
				sb.append((char)ret);
			}
			is.reset();
			sRawLogger.info(sb.toString());
		} catch (IOException e) {
			sLogger.warn(sName + ": Could not do raw request logging :(");
		}
	}

	/**
	 * Append a {@link Result} object to the current threads local
	 * {@link #mResultList}
	 */
	private void deferResultSending(Result res) {
		NullCheck.check(res, "res is null");
		mResultList.get().add(res);
	}

	private void deferErrorResult(ChannelIdentifier chId, ClientIdentifier clId, ErrorCode errCode,
			String errMsg) {
		deferResultSending(new ErrorResult(chId, clId, errCode, errMsg));
	}

	private void deferEndSessionResult(ChannelIdentifier channelId, ClientIdentifier clId) {
		deferResultSending(new EndSessionResult(channelId, clId));
	}

	private void deferPollResult(ChannelIdentifier channelId, ClientIdentifier clId, PollResult pr) {
		deferResultSending(new AddressedPollResult(channelId, clId, pr));
	}

	private void deferSearchResult(ChannelIdentifier channelId, ClientIdentifier clId, SearchResult sr) {
		deferResultSending(new AddressedSearchResult(channelId, clId, sr));
	}

	private void deferPollResultError(ChannelIdentifier channelId, ClientIdentifier clId,
			ErrorCode errCode, String errMsg) {
		deferResultSending(new PollResultsTooBigResult(channelId, clId));
	}

	private void deferPublishReceived(ChannelIdentifier channelId, ClientIdentifier clId) {
		deferResultSending(new PublishReceivedResult(channelId, clId));
	}

	private void deferSubscribeReceived(ChannelIdentifier channelId, ClientIdentifier clId) {
		deferResultSending(new SubscribeReceivedResult(channelId, clId));
	}

	private void deferPurgePublisherReceived(ChannelIdentifier channelId, ClientIdentifier clId) {
		deferResultSending(new PurgePublishReceivedResult(channelId, clId));
	}

	private void deferRenewSessionReceived(ChannelIdentifier channelId, ClientIdentifier clId) {
		deferResultSending(new RenewSessionResult(channelId, clId));
	}

	private void deferDumpResult(ChannelIdentifier channelId, ClientIdentifier clId, DumpResult result) {
		deferResultSending(new AddressedDumpResult(channelId, clId, result));
	}

	private void deferNewSessionResult(ChannelIdentifier channelId, ClientIdentifier clId,
			String sessId, String pubId, Integer mprs) {
		deferResultSending(new NewSessionResult(channelId, clId, sessId, pubId, mprs));
	}

	/**
	 * Look at {@link #mResultList} and, if needed, construct a {@link ActionSeries}
	 * with necessary {@link Action} to be given to the {@link ActionProcessor}.
	 */
	private void processDeferredResults() {
		List<Result> list = mResultList.get();


		// Early jump out
		if (list.size() == 0) {
			return;
		}

		ActionSeries as = new ActionSeries();
		InputStream is = null;
		for (Result r : list) {
			SendResponseAction sra;
			ChannelIdentifier chId = r.getChannelIdentifier();
			ClientIdentifier clientId= r.getClientIdentifier();
			is = mResultMarshaller.marshal(r);
			sra = new SendResponseAction(chId, clientId, is);

			if (mServerConf.isLogRaw()) {
				logRawResponse(sra);
			}

			as.add(sra);
		}

		if (as.getActions().size() > 0) {
			putActionSeriesIntoActionQueue(as);
		}

		// Everything is processed
		list.clear();
	}

	/**
	 * Helper to put a {@link ActionSeries} into the action queue.
	 *
	 * This is the point where we order the {@link ActionProcessor} on the
	 * other side to do something for us.
	 *
	 * TODO: If we want to shutdown cleanly, we should probably honor
	 * a {@link InterruptedException} and the {@link Thread#isInterrupted()}
	 * method.
	 *
	 * @param as the actions to be put into the queue
	 */
	private void putActionSeriesIntoActionQueue(ActionSeries as) {
		NullCheck.check(as, "actionseries is null");
		boolean added = false;
		do {
			try {
				mActionQueue.put(as);
				added = true;
			} catch (InterruptedException e) { }
		} while (!added);
	}

	/**********************************************************************
	 * A bunch of getters and setters				     *
	 **********************************************************************/

	public void setActionQueue(Queue<ActionSeries> actionQueue) {
		NullCheck.check(actionQueue, "param actionQueue null");
		mActionQueue = actionQueue;
	}

	public void setPublisherIdProv(PublisherIdProvider publisherIdProv) {
		NullCheck.check(publisherIdProv, "param publisherIdProv null");
		mPublisherIdProv = publisherIdProv;
	}

	public void setSessionIdProv(SessionIdProvider sessionIdProv) {
		NullCheck.check(sessionIdProv, "param sessionIdProv null");
		mSessionIdProv = sessionIdProv;
	}

	public void setRequestUnmarshaller(RequestUnmarshaller requnm) {
		NullCheck.check(requnm, "param request unmarshaller null");
		mRequestUnmarshaller = requnm;
	}

	public void setResultMarshaller(ResultMarshaller resmarsh) {
		NullCheck.check(resmarsh, "param response marhaller null");
		mResultMarshaller = resmarsh;
	}

	public void setServerConfiguration(ServerConfigurationProvider serverConf) {
		NullCheck.check(serverConf, "param serverConf null");
		mServerConf = serverConf;
	}

	public void setDataModel(DataModelService dataModel) {
		NullCheck.check(dataModel, "param dataModel null");
		mDataModel = dataModel;
	}

	public void setSessionTimerFactory(SessionTimerFactory timerfactory) {
		NullCheck.check(timerfactory, "param timerfactory null");
		mSessionTimerFactory = timerfactory;
	}


	public void setPublisherIdGenerator(PublisherIdGenerator pubIdGen) {
		NullCheck.check(pubIdGen, "param pubIdGen null");
		mPublisherIdGenerator = pubIdGen;
	}

	public void setAuthorizationProv(AuthorizationProvider authProv) {
		NullCheck.check(authProv, "authProv null");
		mAuthorizationProv = authProv;
	}

	/**
	 * Simple private {@link ThreadLocal} implementation for a list.
	 */
	private class ThreadLocalResults extends ThreadLocal<List<Result>> {

		@Override
		protected List<Result> initialValue() {
			return CollectionHelper.provideListFor(Result.class);
		}
	}
}
