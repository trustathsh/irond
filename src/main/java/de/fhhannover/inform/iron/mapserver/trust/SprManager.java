package de.fhhannover.inform.iron.mapserver.trust;

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

import java.util.HashMap;

import de.fhhannover.inform.iron.mapserver.communication.ChannelIdentifier;
import de.fhhannover.inform.iron.mapserver.communication.ClientIdentifier;
import de.fhhannover.inform.iron.mapserver.communication.ifmap.SessionRepository;
import de.fhhannover.inform.iron.mapserver.trust.domain.SecurityProperty;
import de.fhhannover.inform.iron.mapserver.trust.domain.SecurityPropertyRecord;
import de.fhhannover.inform.iron.mapserver.trust.utils.OperationType;

public class SprManager {

	private SprRepository mProcessMapc;

	private SprRepository mTransmitMapcMaps;

	private SecurityPropertyRecord mProcessMaps;

	private SpRepository mSpRepository;
	
	private HashMap<String, ClientIdentifier> mSessionClientIdentifierMap;
	
	private SessionRepository mSessionRepository;

	private SlFunction mSlFunction;

	public SprManager(SessionRepository sessionRepo, SpRepository spRepo) {
		mProcessMapc = new SprRepository(sessionRepo);
		mTransmitMapcMaps = new SprRepository(sessionRepo);
		mProcessMaps = new SecurityPropertyRecord();
		mSessionClientIdentifierMap = new HashMap<String, ClientIdentifier>();
		mSessionRepository = sessionRepo;
		mSpRepository = spRepo;
		mSlFunction = new RealSlFunction();
	}

	public void addSpForMapc(String sid, String propertyName,
			OperationType opType) {
		SecurityProperty sp = mSpRepository.getSp(propertyName);
		ClientIdentifier cid = getClientIdBySessionId(sid);

		switch (opType) {
		case PROCESS_MAPC:
			mProcessMapc.addSpToSpr(cid, sp);
			break;
		case TRANSMIT_MAPC_MAPS:
			mTransmitMapcMaps.addSpToSpr(cid, sp);
			break;
		default:
			System.err.println("OperationType nicht vorhanden!");
			break;
		}

	}

	public void addSpForMapc(ChannelIdentifier chid, String propertyName,
			OperationType opType) {
		SecurityProperty sp = mSpRepository.getSp(propertyName);
		ClientIdentifier cid = getClientIdByChannelId(chid);

		switch (opType) {
		case PROCESS_MAPC:
			mProcessMapc.addSpToSpr(cid, sp);
			break;
		case TRANSMIT_MAPC_MAPS:
			mTransmitMapcMaps.addSpToSpr(cid, sp);
			break;
		default:
			System.err.println("OperationType nicht vorhanden!");
			break;
		}

	}
	
	public void addSpForMapc(ClientIdentifier cid, String propertyName,
			OperationType opType) {
		SecurityProperty sp = mSpRepository.getSp(propertyName);

		switch (opType) {
		case PROCESS_MAPC:
			sp = mSpRepository.getSp("user." + propertyName);
                        SecurityProperty t1 = null;
                        SecurityProperty t2 = null;
                        SecurityProperty t3 = null;
			boolean trendClient = false;
                        if (sp == null) {
				sp = mSpRepository.getSp("user.default");
			} else {
                            //TODO hack
                            if (sp.getPropertyName().equals("user.trend")) {
                                t1 = mSpRepository.getSp("vulnerabilityLevel");
                                t2 = mSpRepository.getSp("appCount");
                                t3 = mSpRepository.getSp("processCount");
                                trendClient = true;
                            }
                        }
			mProcessMapc.addSpToSpr(cid, sp);
                        if (trendClient) {
                            mProcessMapc.addSpToSpr(cid, t1);
                            mProcessMapc.addSpToSpr(cid, t2);
                            mProcessMapc.addSpToSpr(cid, t3);
                        }
			break;
		case TRANSMIT_MAPC_MAPS:
			mTransmitMapcMaps.addSpToSpr(cid, sp);
			break;
		default:
			System.err.println("OperationType nicht vorhanden!");
			break;
		}

	}

	public void addSpForMaps(String propertyName) {
		mProcessMaps.addSp(mSpRepository.getSp(propertyName));
	}

	public SecurityPropertyRecord getSprOfMapc(String sid,
			OperationType opType) {
		SecurityPropertyRecord spr;
		int sl;
		ClientIdentifier cid = getClientIdBySessionId(sid);
		
		switch (opType) {
		case PROCESS_MAPC:
			spr = mProcessMapc.getSpr(cid);
			if(spr != null) {
				sl = mSlFunction.calculateSl(spr.getListOfSp());
				spr.setSl(sl);
				return spr;
			}
			return new SecurityPropertyRecord();
		case TRANSMIT_MAPC_MAPS:
			spr = mTransmitMapcMaps.getSpr(cid);
			if(spr != null) {
				sl = mSlFunction.calculateSl(spr.getListOfSp());
				spr.setSl(sl);
				return spr;
			}
			return new SecurityPropertyRecord();
		default:
			System.err.println("OperationType nicht vorhanden!");
			return null;
		}
	}

	public SecurityPropertyRecord getSprOfMaps() {
		int sl = mSlFunction.calculateSl(mProcessMaps.getListOfSp());
		mProcessMaps.setSl(sl);
		return mProcessMaps;
	}

	public void removeAllSprOfMapc(String sessionId) {
		ClientIdentifier cId = mSessionClientIdentifierMap.get(sessionId);
		mProcessMapc.removeSpr(cId);
		mTransmitMapcMaps.removeSpr(cId);
		mSessionClientIdentifierMap.remove(sessionId);
	}

	public void recalculateSpr(SecurityPropertyRecord spr) {
		int sl = mSlFunction.calculateSl(spr.getListOfSp());
		spr.setSl(sl);
	}
	
	public void mapSessionIdToClientIdentifier(String sessionId) {
		mSessionClientIdentifierMap.put(sessionId, mSessionRepository.getBy(sessionId).getClientIdentifier());
	}
	
	private ClientIdentifier getClientIdBySessionId(String sessionId) {
		return mSessionRepository.getBy(sessionId).getClientIdentifier();
	}
	
	private ClientIdentifier getClientIdByChannelId(ChannelIdentifier channelId) {
		return mSessionRepository.getBy(channelId).getClientIdentifier();
	}
}
