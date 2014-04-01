package de.fhhannover.inform.iron.mapserver.communication.ifmap;

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
 * This file is part of irond, version 0.4.1, implemented by the Trust@FHH
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

import java.io.ByteArrayInputStream;

import de.fhhannover.inform.iron.mapserver.communication.ChannelIdentifier;
import de.fhhannover.inform.iron.mapserver.communication.ClientIdentifier;
import de.fhhannover.inform.iron.mapserver.communication.bus.messages.BadChannelEvent;
import de.fhhannover.inform.iron.mapserver.communication.bus.messages.ClosedChannelEvent;
import de.fhhannover.inform.iron.mapserver.communication.bus.messages.Event;
import de.fhhannover.inform.iron.mapserver.communication.bus.messages.RequestChannelEvent;

/**
 * Create some test events
 * 
 * @author aw
 *
 */
public class TestEventCreator {

	public static RequestChannelEvent createRequest(ClientIdentifier clId, ChannelIdentifier chId, boolean first,
			byte[] content) {
		ByteArrayInputStream bais = new ByteArrayInputStream(content);
		return new RequestChannelEvent(chId, clId, bais, first);
	}
	
	public static RequestChannelEvent createNewSessionRequest(ClientIdentifier clId, ChannelIdentifier chId, boolean first) {
		return createRequest(clId, chId, first, NEW_SESSION_REQUEST.getBytes());
	}

	public static RequestChannelEvent createEndSessionRequest(ClientIdentifier clId, ChannelIdentifier chId, String sessionId, boolean first) {
		return createRequest(clId, chId, first, (END_SESSION_REQUEST_FIRST + sessionId + END_SESSION_REQUEST_SECOND).getBytes());
	}

	public static RequestChannelEvent createRenewSessionRequest(ClientIdentifier clId, ChannelIdentifier chId, String sessionId, boolean first) {
		return createRequest(clId, chId, first, (RENEW_SESSION_REQUEST_FIRST + sessionId + RENEW_SESSION_REQUEST_SECOND).getBytes());
	}
	
	public static RequestChannelEvent createPublishRequest(ClientIdentifier clId, ChannelIdentifier chId, String sessionId, String publishContent, boolean first) {
		return createRequest(clId, chId, first, (PUBLISH_REQUEST_FIRST + sessionId + PUBLISH_REQUEST_SECOND + publishContent + PUBLISH_REQUEST_THIRD).getBytes());
	}
	
	public static RequestChannelEvent createSearchRequest(ClientIdentifier clId,
			ChannelIdentifier chId, String sessionId, String matchlinks,
			String maxdepth, String terminalid, String maxsize, String resfilter,
			String identifier, boolean first) {
		StringBuffer between = new StringBuffer(sessionId + "\"");
		
		if (matchlinks != null) {
			between.append(" match-links=\"" + matchlinks + "\"");
		}

		if (maxdepth != null) {
			between.append(" max-depth=\"" + maxdepth + "\"");
		}

		if (terminalid != null) {
			between.append(" terminal-identifier-type=\"" + terminalid + "\"");
		}
		
		if (maxsize != null) {
			between.append(" max-size=\"" + maxsize + "\"");
		}

		if (resfilter != null) {
			between.append(" result-filter=\"" + resfilter + "\"");
		}
		
		between.append(">\n");
		between.append(identifier);

		return createRequest(clId, chId, first, (SEARCH_REQUEST_FIRST + between.toString() + SEARCH_REQUEST_SECOND).getBytes());
	}
	
	public static String createSubscrbeUpdateElement(String subName, String matchlinks,
			String maxdepth, String terminalid, String maxsize, String resfilter,
			String identifier) {
		
		StringBuffer sb = new StringBuffer();
		sb.append("<update");
		sb.append(" name=\"" + subName + "\"");
		
		if (matchlinks != null) {
			sb.append(" match-links=\"" + matchlinks + "\"");
		}

		if (maxdepth != null) {
			sb.append(" max-depth=\"" + maxdepth + "\"");
		}

		if (terminalid != null) {
			sb.append(" terminal-identifier-type=\"" + terminalid + "\"");
		}
		
		if (maxsize != null) {
			sb.append(" max-size=\"" + maxsize + "\"");
		}

		if (resfilter != null) {
			sb.append(" result-filter=\"" + resfilter + "\"");
		}
		
		sb.append(">\n");
		sb.append(identifier);
		sb.append("\n</update>");
		
		return sb.toString();
	}
	
	public static String createSubscrbeDeleteElement(String subName) {
		
		StringBuffer sb = new StringBuffer();
		sb.append("<delete");
		sb.append(" name=\"" + subName + "\"");
		
		sb.append("/>\n");
		return sb.toString();
	}
	
	public static RequestChannelEvent createSubscribeRequest(ClientIdentifier clId, ChannelIdentifier chId, String sessionId, String subscribeContent, boolean first) {
		ByteArrayInputStream bais = new ByteArrayInputStream((SUBSCRIBE_REQUEST_FIRST + sessionId + SUBSCRIBE_REQUEST_SECOND + subscribeContent+ SUBSCRIBE_REQUEST_THIRD).getBytes());
		return new RequestChannelEvent(chId, clId, bais, first);
	}
	
	public static RequestChannelEvent createPurgePublisherRequest(ClientIdentifier clId, ChannelIdentifier chId, String sessionId, String publisherId, boolean first) {
		ByteArrayInputStream bais = new ByteArrayInputStream((PURGE_PUBLISHER_REQUEST_FIRST + sessionId + "\" ifmap-publisher-id=\"" + publisherId + PURGE_PUBLISHER_REQUEST_SECOND).getBytes());
		return new RequestChannelEvent(chId, clId, bais, first);
	}
	
	public static RequestChannelEvent createPollRequest(ClientIdentifier clId, ChannelIdentifier chId, String sessionId, boolean first) {
		ByteArrayInputStream bais = new ByteArrayInputStream((POLL_REQUEST_FIRST +  sessionId + POLL_REQUEST_SECOND).getBytes());
		return new RequestChannelEvent(chId, clId, bais, first);
	}
	
	
	private static final String NEW_SESSION_REQUEST =
	"<env:Envelope xmlns:env=\"http://www.w3.org/2003/05/soap-envelope\" xmlns:ifmap=\"http://www.trustedcomputinggroup.org/2010/IFMAP/2\">\n" +
	  "<env:Body>\n" + 
	    "<ifmap:newSession/>\n" +
	  "</env:Body>\n" +
	"</env:Envelope>";
	
	
	private static final String END_SESSION_REQUEST_FIRST =
	"<env:Envelope xmlns:env=\"http://www.w3.org/2003/05/soap-envelope\" xmlns:ifmap=\"http://www.trustedcomputinggroup.org/2010/IFMAP/2\">\n" +
	 "<env:Body>\n" +
	    "<ifmap:endSession session-id=\"";
	    

	private static final String END_SESSION_REQUEST_SECOND =
	    "\"/>\n" +
	  "</env:Body>\n" +
	"</env:Envelope>";
	
	private static final String RENEW_SESSION_REQUEST_FIRST =
	"<env:Envelope xmlns:env=\"http://www.w3.org/2003/05/soap-envelope\" xmlns:ifmap=\"http://www.trustedcomputinggroup.org/2010/IFMAP/2\">\n" +
	 "<env:Body>\n" +
	    "<ifmap:renewSession session-id=\"";

	private static final String RENEW_SESSION_REQUEST_SECOND =
	    "\"/>\n" +
	  "</env:Body>\n" +
	"</env:Envelope>";
	
	private static final String PUBLISH_REQUEST_FIRST = 
	"<env:Envelope xmlns:env=\"http://www.w3.org/2003/05/soap-envelope\" xmlns:ifmap=\"http://www.trustedcomputinggroup.org/2010/IFMAP/2\">\n" +
	 "<env:Body>\n" +
	    "<ifmap:publish session-id=\"";

	private static final String PUBLISH_REQUEST_SECOND =
		"\">\n";
	
	private static final String PUBLISH_REQUEST_THIRD =
	    "</ifmap:publish>\n" +
	  "</env:Body>\n" +
	"</env:Envelope>";
	
	private static final String SEARCH_REQUEST_FIRST =
		"<env:Envelope xmlns:env=\"http://www.w3.org/2003/05/soap-envelope\" xmlns:ifmap=\"http://www.trustedcomputinggroup.org/2010/IFMAP/2\">\n" +
		 "<env:Body>\n" +
		    "<ifmap:search session-id=\"";

	private static final String SEARCH_REQUEST_SECOND =
	    "\n</ifmap:search>\n" +
	  "</env:Body>\n" +
	"</env:Envelope>";
	
	private static final String SUBSCRIBE_REQUEST_FIRST = 
		"<env:Envelope xmlns:env=\"http://www.w3.org/2003/05/soap-envelope\" xmlns:ifmap=\"http://www.trustedcomputinggroup.org/2010/IFMAP/2\">\n" +
		 "<env:Body>\n" +
		    "<ifmap:subscribe session-id=\"";

	private static final String SUBSCRIBE_REQUEST_SECOND =
			"\">\n";
		
	private static final String SUBSCRIBE_REQUEST_THIRD =
		    "\n</ifmap:subscribe>\n" +
		  "</env:Body>\n" +
		"</env:Envelope>";
	private static final String PURGE_PUBLISHER_REQUEST_FIRST =
			"<env:Envelope xmlns:env=\"http://www.w3.org/2003/05/soap-envelope\" xmlns:ifmap=\"http://www.trustedcomputinggroup.org/2010/IFMAP/2\">\n" +
			 "<env:Body>\n" +
			    "<ifmap:purgePublisher session-id=\"";
			    

	private static final String PURGE_PUBLISHER_REQUEST_SECOND =
			    "\"/>\n" +
			  "</env:Body>\n" +
			"</env:Envelope>";	
	
	private static final String POLL_REQUEST_FIRST =
		"<env:Envelope xmlns:env=\"http://www.w3.org/2003/05/soap-envelope\" xmlns:ifmap=\"http://www.trustedcomputinggroup.org/2010/IFMAP/2\">\n" +
		 "<env:Body>\n" +
		    "<ifmap:poll session-id=\"";
		    

		private static final String POLL_REQUEST_SECOND =
		    "\"/>\n" +
		  "</env:Body>\n" +
		"</env:Envelope>";
		
	
	public static Event createClosedChannelEvent(ChannelIdentifier channel) {
		return new ClosedChannelEvent(channel);
	}

	public static Event createBadChannelEvent(ChannelIdentifier channel) {
		return new BadChannelEvent(channel);
	}
}
