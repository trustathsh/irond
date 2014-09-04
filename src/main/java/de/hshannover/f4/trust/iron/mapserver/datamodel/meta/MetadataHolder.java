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
 * This file is part of irond, version 0.5.1, implemented by the Trust@HsH
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
package de.hshannover.f4.trust.iron.mapserver.datamodel.meta;


import de.hshannover.f4.trust.iron.mapserver.datamodel.Publisher;
import de.hshannover.f4.trust.iron.mapserver.datamodel.graph.GraphElement;

/**
 * A {@link MetadataHolder} encapsulates a real {@link Metadata} object and
 * offers access to the {@link Publisher} instance, {@link GraphElement}
 * instance, time-stamp and further administrative attributes.
 *
 * @since 0.3.0
 * @author aw
 *
 */
public interface MetadataHolder {

	/**
	 * @return the {@link Metadata} instance that is encapsulated by this
	 * 			{@link MetadataHolder} instance.
	 */
	public Metadata getMetadata();

	/**
	 * @return the {@link GraphElement} instance this {@link MetadataHolder}
	 * 			instance is attached to.
	 */
	public GraphElement getGraphElement();

	/**
	 * @return the {@link Publisher} instance indicating who published this
	 * 			{@link MetadataHolder} instance.
	 */
	public Publisher getPublisher();

	/**
	 * @return the state this {@link MetadataHolder} instance is in.
	 */
	public MetadataState getState();

	/**
	 * Set the sate of this {@link MetadataHolder} instance.
	 * @param state
	 */
	public void setState(MetadataState state);

	public boolean isNotify();

	public boolean isNew();

	public boolean isDeleted();

	public boolean isUnchanged();

	/**
	 * @return the lifetime of the attached {@link Metadata} object.
	 */
	public MetadataLifeTime getLifetime();

}
