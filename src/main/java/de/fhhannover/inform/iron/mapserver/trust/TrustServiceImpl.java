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

import de.fhhannover.inform.iron.mapserver.communication.ChannelIdentifier;
import de.fhhannover.inform.iron.mapserver.communication.ClientIdentifier;
import de.fhhannover.inform.iron.mapserver.communication.ifmap.SessionRepository;
import de.fhhannover.inform.iron.mapserver.datamodel.meta.Metadata;
import de.fhhannover.inform.iron.mapserver.datamodel.meta.MetadataFactory;
import de.fhhannover.inform.iron.mapserver.trust.domain.TrustToken;
import de.fhhannover.inform.iron.mapserver.trust.utils.OperationType;

public class TrustServiceImpl implements TrustService {

	private TrustTokenManager mTrustTokenManager;
	
	private SpRepository mSpRepository;

	private SprManager mSprManager;

	public TrustServiceImpl(SessionRepository sessionRepository,
			MetadataFactory metadataFactory) {
		mSpRepository = new SpRepository();
		mSprManager = new SprManager(sessionRepository, mSpRepository);
		mTrustTokenManager = new TrustTokenManager(mSprManager, metadataFactory);
	}

	@Override
	public void addSpForMapc(ChannelIdentifier channelId, String propertyName,
			OperationType operation) {
		mSprManager.addSpForMapc(channelId, propertyName, operation);
	}

	@Override
	public void addSpForMapc(String sessionID, String propertyName,
			OperationType operation) {
		mSprManager.addSpForMapc(sessionID, propertyName, operation);
	}

	@Override
	public void addSpForMaps(String propertyName) {
		mSprManager.addSpForMaps(propertyName);
	}

	@Override
	public void removeAllSprOfMapc(String sessionId) {
		mSprManager.removeAllSprOfMapc(sessionId);
	}

	@Override
	public Metadata getP2TTI(String sessionId, TrustToken tt) {
		return mTrustTokenManager.getP2TTI(sessionId, tt);
	}

	@Override
	public Metadata getP2TTM(String sessionId, TrustToken tt, String ttId) {
		return mTrustTokenManager.getP2TTM(sessionId, tt, ttId);
	}

	@Override
	public void addSpForMapc(ClientIdentifier clientId, String propertyName,
			OperationType operation) {
		mSprManager.addSpForMapc(clientId, propertyName, operation);
	}

	@Override
	public TrustToken getP1TT(String sessionId, String mapcId) {
		return mTrustTokenManager.getP1TT(sessionId, mapcId);
	}

	/**
	 * Durch Aufruf dieser Methode wird im SprManager die vergebene SessionID
	 * auf dem dazugehörigen ClientIdentifier gemappt. Mithilfe dieses Mappings
	 * ist der TrustService in der Lage bei Aufruf der @see {@link TrustService}
	 * {@link #removeAllSprOfMapc(String)} die gemessenen SP aus der
	 * SprRepository wieder zu entfernen.
	 * 
	 * Zur Herstellung dieser Abbildung wird die {@link SessionRepository}
	 * verwendet, die eigentlich diese Abbildung bereits zur Verfügung stellt.
	 * Jedoch ist diese Abbildung nicht mehr vorhanden, wenn im ClientService
	 * die endSession-Methode aufgerufen wird, die die
	 * {@link #removeAllSprOfMapc(String)} enthält.
	 * 
	 * @param sessionId
	 *            Die sessionID der aktuellen Sitzung des MAP-Clients.
	 */
	public void mapSessionIdToClientIdentifier(String sessionId) {
		mSprManager.mapSessionIdToClientIdentifier(sessionId);
	}

	@Override
	public void reloadSpFile() {
		mSpRepository.reloadSpFile();
	}

}
