package de.fhhannover.inform.iron.mapserver.trust.domain;

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

public class TrustToken {
	
	private SecurityPropertyRecord mProcessSender = new SecurityPropertyRecord();
	
	private SecurityPropertyRecord mTransmitSenderProvider = new SecurityPropertyRecord();
	
	private SecurityPropertyRecord mProcessProvider = new SecurityPropertyRecord();
	
	private SecurityPropertyRecord mTransmitProviderReceiver = new SecurityPropertyRecord();
	
	private int mValue;
	
	private String mMapcId = "";
	
	private String mTimestamp = "";
	
	public TrustToken(SecurityPropertyRecord ps, SecurityPropertyRecord tsp, SecurityPropertyRecord pp, String mapcId) {
		mProcessSender = ps;
		mTransmitSenderProvider = tsp;
		mProcessProvider = pp;
		mValue = -1;
		mMapcId = mapcId;
	}
	
	public TrustToken(TrustToken tt) {
		mProcessSender = tt.getProcessSender();
		mTransmitSenderProvider = tt.getTransmitSenderProvider();
		mProcessProvider = tt.getProcessProvider();
		mTransmitProviderReceiver = tt.getTransmitProviderReceiver();
		mMapcId = tt.getMapcId();
		mValue = tt.getValue();
		mTimestamp = tt.getTimestamp();
	}
	
	public SecurityPropertyRecord getProcessSender() {
		return mProcessSender;
	}

	public void setProcessSender(SecurityPropertyRecord mProcessSender) {
		this.mProcessSender = mProcessSender;
	}

	public SecurityPropertyRecord getTransmitSenderProvider() {
		return mTransmitSenderProvider;
	}

	public void setTransmitSenderProvider(
			SecurityPropertyRecord mTransmitSenderProvider) {
		this.mTransmitSenderProvider = mTransmitSenderProvider;
	}

	public SecurityPropertyRecord getProcessProvider() {
		return mProcessProvider;
	}

	public void setProcessProvider(SecurityPropertyRecord mProcessProvider) {
		this.mProcessProvider = mProcessProvider;
	}

	public SecurityPropertyRecord getTransmitProviderReceiver() {
		return mTransmitProviderReceiver;
	}

	public void setTransmitProviderReceiver(
			SecurityPropertyRecord mTransmitProviderReceiver) {
		this.mTransmitProviderReceiver = mTransmitProviderReceiver;
	}

	public int getValue() {
		return mValue;
	}

	public void setValue(int mValue) {
		this.mValue = mValue;
	}

	public String getMapcId() {
		return mMapcId;
	}

	public void setMapcId(String mMapcId) {
		this.mMapcId = mMapcId;
	}

	public String getTimestamp() {
		return mTimestamp;
	}

	public void setTimestamp(String timestamp) {
		this.mTimestamp = timestamp;
	}

}
