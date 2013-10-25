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

import de.fhhannover.inform.iron.mapserver.datamodel.meta.Metadata;
import de.fhhannover.inform.iron.mapserver.datamodel.meta.MetadataFactory;
import de.fhhannover.inform.iron.mapserver.trust.domain.SecurityPropertyRecord;
import de.fhhannover.inform.iron.mapserver.trust.domain.TrustToken;
import de.fhhannover.inform.iron.mapserver.trust.utils.OperationType;
import de.fhhannover.inform.iron.mapserver.utils.Iso8601DateTime;

public class TrustTokenManager {

	private TrustTokenFactory mTrustTokenFactory;

	private SprManager mSprManager;

	private TlFunction mTlFunction;

	public TrustTokenManager(SprManager sprm, MetadataFactory metadatafactory) {
		mSprManager = sprm;
		mTlFunction = new RealTlFunction();
		mTrustTokenFactory = new TrustTokenFactory(metadatafactory);
	}

	public TrustToken getP1TT(String sessionId, String mapcId) {
		SecurityPropertyRecord sprPs = mSprManager.getSprOfMapc(sessionId,
				OperationType.PROCESS_MAPC);
		SecurityPropertyRecord sprTsp = mSprManager.getSprOfMapc(sessionId,
				OperationType.TRANSMIT_MAPC_MAPS);
		SecurityPropertyRecord sprPp = mSprManager.getSprOfMaps();

		TrustToken tt = new TrustToken(sprPs, sprTsp, sprPp, mapcId);

		int value = mTlFunction.calculateTl(sprPs.getSl(), sprTsp.getSl(),
				sprPp.getSl(), 0);
		tt.setValue(value);
		tt.setTimestamp(Iso8601DateTime.getTimeNow());
		return tt;
	}

	public Metadata getP2TTM(String sessionId, TrustToken p1tt, String ttId) {
		TrustToken p2tt = new TrustToken(p1tt);
		buildP2TTAndRecalculateTl(sessionId, p2tt);

		return mTrustTokenFactory.createTtmMetadata(p2tt, ttId,
				p1tt.getTimestamp());
	}

	public Metadata getP2TTI(String sessionId, TrustToken p1tt) {
		TrustToken p2tt = new TrustToken(p1tt);
		buildP2TTAndRecalculateTl(sessionId, p2tt);

		return mTrustTokenFactory.createTtiMetadata(p2tt, p1tt.getTimestamp());
	}

	private void buildP2TTAndRecalculateTl(String sessionId, TrustToken tt) {
		mSprManager.recalculateSpr(tt.getProcessSender());
		mSprManager.recalculateSpr(tt.getTransmitSenderProvider());
		mSprManager.recalculateSpr(tt.getProcessProvider());
		tt.setTransmitProviderReceiver(mSprManager.getSprOfMapc(sessionId,
				OperationType.TRANSMIT_MAPC_MAPS));

		int value = mTlFunction.calculateTl(tt.getProcessSender().getSl(), tt
				.getTransmitSenderProvider().getSl(), tt.getProcessProvider()
				.getSl(), tt.getTransmitProviderReceiver().getSl());
		tt.setValue(value);
	}

}
