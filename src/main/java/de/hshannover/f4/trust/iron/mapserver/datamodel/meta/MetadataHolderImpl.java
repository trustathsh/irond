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
package de.hshannover.f4.trust.iron.mapserver.datamodel.meta;


import de.hshannover.f4.trust.iron.mapserver.datamodel.Publisher;
import de.hshannover.f4.trust.iron.mapserver.datamodel.graph.GraphElement;
import de.hshannover.f4.trust.iron.mapserver.utils.NullCheck;

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
}
