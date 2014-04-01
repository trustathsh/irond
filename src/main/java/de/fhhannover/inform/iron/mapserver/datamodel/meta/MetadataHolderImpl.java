package de.fhhannover.inform.iron.mapserver.datamodel.meta;

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

import de.fhhannover.inform.iron.mapserver.datamodel.Publisher;
import de.fhhannover.inform.iron.mapserver.datamodel.graph.GraphElement;
import de.fhhannover.inform.iron.mapserver.trust.domain.TrustToken;
import de.fhhannover.inform.iron.mapserver.utils.NullCheck;

/**
 * 
 * @since 0.3.0
 * @author aw
 *
 */
class MetadataHolderImpl implements MetadataHolder {
	
	private final Metadata mMetadata;
	private final MetadataLifeTime mLifeTime;
	private final Publisher mPublisher;
	private final GraphElement mGraphElement;
	
	private MetadataState mState;
	
	private TrustToken mTrustToken;
	
	MetadataHolderImpl(Metadata m, MetadataLifeTime lt, GraphElement ge, Publisher p) {
		NullCheck.check(m, "metadata is null");
		NullCheck.check(lt, "lifetime is null");
		NullCheck.check(ge, "graphElement is null");
		NullCheck.check(p, "publisher is null");
		
		mMetadata = m;
		mLifeTime = lt;
		mGraphElement = ge;
		mPublisher = p;
	}

	@Override
	public Metadata getMetadata() {
		return mMetadata;
	}

	@Override
	public GraphElement getGraphElement() {
		return mGraphElement;
	}

	@Override
	public Publisher getPublisher() {
		return mPublisher;
	}

	@Override
	public MetadataState getState() {
		return mState;
	}

	@Override
	public void setState(MetadataState state) {
		mState = state;
	}

	@Override
	public boolean isNotify() {
		return mState == MetadataState.NOTIFY;
	}

	@Override
	public boolean isNew() {
		return mState == MetadataState.NEW;
	}

	@Override
	public boolean isDeleted() {
		return mState == MetadataState.DELETED;
	}

	@Override
	public boolean isUnchanged() {
		return mState == MetadataState.UNCHANGED;
	}

	@Override
	public MetadataLifeTime getLifetime() {
		return mLifeTime;
	}

	@Override
	public TrustToken getTrustToken() {
		return mTrustToken;
	}

	@Override
	public void setTrustToken(TrustToken tt) {
		mTrustToken = tt;
	}
}
