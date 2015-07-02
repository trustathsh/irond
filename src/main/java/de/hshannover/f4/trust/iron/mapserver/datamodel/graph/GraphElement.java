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
 * This file is part of irond, version 0.5.5, implemented by the Trust@HsH
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
package de.hshannover.f4.trust.iron.mapserver.datamodel.graph;


import java.util.List;

import de.hshannover.f4.trust.iron.mapserver.datamodel.SearchAble;
import de.hshannover.f4.trust.iron.mapserver.datamodel.identifiers.Identifier;
import de.hshannover.f4.trust.iron.mapserver.datamodel.meta.MetadataHolder;
import de.hshannover.f4.trust.iron.mapserver.datamodel.meta.MetadataType;
import de.hshannover.f4.trust.iron.mapserver.datamodel.search.Filter;
import de.hshannover.f4.trust.iron.mapserver.datamodel.search.Subscription;
import de.hshannover.f4.trust.iron.mapserver.datamodel.search.SubscriptionEntry;

/**
 * {@link GraphElement} either represents a {@link Link} or {@link Node}.
 * This interface basically provides access to the metadata stored on
 * a {@link GraphElement} instance.
 * {@link GraphElement} instances are compared by reference, always.
 *
 * @author aw
 *
 */
public interface GraphElement extends SearchAble {

	/**
	 * @return a list of {@link MetadataHolder} objects attached to this
	 * 			{@link GraphElement} object.
	 */
	public List<MetadataHolder> getMetadataHolder();

	/**
	 * @param f
	 * @return a list of {@link MetadataHolder} objects attached to this
	 * 			{@link GraphElement} object, matching the given {@link Filter}
	 * 			instance.
	 */
	public List<MetadataHolder> getMetadataHolder(Filter f);


	/**
	 * @param type a list of {@link MetadataHolder} objects attached to this
	 * 			{@link GraphElement} object, where the {@link MetadataType}
	 * 			object has the given type.
	 * @return
	 */
	public List<MetadataHolder> getMetadataHolder(MetadataType type);

	/**
	 * @param f
	 * @return a list of {@link MetadataHolder} objects attached to this
	 * 			{@link GraphElement} object, having either state UNCHANGED
	 * 			or DELETED.
	 */
	public List<MetadataHolder> getMetadataHolderInGraph();

	/**
	 * @param f
	 * @return a list of {@link MetadataHolder} objects attached to this
	 * 			{@link GraphElement} object, having either state UNCHANGED
	 * 			or DELETED and matching the given {@link Filter} instance.
	 */
	public List<MetadataHolder> getMetadataHolderInGraph(Filter f);

	/**
	 * @param f
	 * @return a list of {@link MetadataHolder} objects attached to this
	 * 			{@link GraphElement} object, having either state UNCHANGED
	 * 			or NEW and matching the given {@link Filter} instance.
	 */
	public List<MetadataHolder> getMetadataHolderNext(Filter f);

	/**
	 * @param f
	 * @return a list of {@link MetadataHolder} objects attached to this
	 * 			{@link GraphElement} object, having state NEW and matching the
	 * 			given {@link Filter} instance.
	 */
	public List<MetadataHolder> getMetadataHolderNew(Filter f);

	/**
	 * Add a {@link MetadataHolder} object to this {@link GraphElement} object.
	 *
	 * @param m
	 */
	public void addMetadataHolder(MetadataHolder m);

	/**
	 * Remove a {@link MetadataHolder} object from this {@link GraphElement} object.
	 *
	 * @param m
	 */
	public void removeMetadataHolder(MetadataHolder m);

	/**
	 * Remove all {@link MetadataHolder} objects attached to this {@link GraphElement}.
	 */
	public void removeAllMetadataHolders();

	/**
	 * @return a list of {@link SubscriptionEntry} objects attached to this
	 * 			{@link GraphElement} object.
	 */
	public SubscriptionEntry getSubscriptionEntry(Subscription sub);

	/**
	 * @return a the {@link SubscriptionEntry} for the given {@link Subscription}
	 * 			or null if none is there.
	 */
	public List <SubscriptionEntry> getSubscriptionEntries();

	/**
	 * Add a {@link SubscriptionEntry} instance to this {@link GraphElement} object.
	 *
	 * @param sub
	 */
	public void addSubscriptionEntry(SubscriptionEntry entry);

	/**
	 * Remove a {@link SubscriptionEntry} object from this {@link GraphElement}
	 * 			object.
	 *
	 * @param subscription
	 */
	public void removeSubscriptionEntry(Subscription sub);

	/**
	 * Remove all {@link SubscriptionEntry} objects attached to this
	 * 			{@link GraphElement} object.
	 */
	public void removeAllSubscriptionEntries();

	/**
	 * Compare {@link GraphElement} objects based on their {@link Identifier}
	 * instances.
	 *
	 * @param graphElement
	 * @return
	 */
	public boolean equalsIdentifiers(GraphElement o);

	/**
	 * Return an immutable {@link GraphElement}, which does not contain any
	 * references to mutable objects.
	 */
	public GraphElement dummy();

	/**
	 * @return a the removed {@link SubscriptionEntry} for the given
	 *			{@link Subscription} or null if none is there.
	 */
	public SubscriptionEntry getRemovedSubscriptionEntry(Subscription sub);

	/**
	 * @return a list of {@link SubscriptionEntry} objects attached to this
	 * 			{@link GraphElement} object, which were removed when subgraphs
	 * 			were removed.
	 */
	public List <SubscriptionEntry> getRemovedSubscriptionEntries();

	/**
	 * Add a removed {@link SubscriptionEntry} instance to this {@link GraphElement}
	 * object.
	 *
	 * @param sub
	 */
	public void addRemovedSubscriptionEntry(SubscriptionEntry entry);

	/**
	 * Remove a removed {@link SubscriptionEntry} object from this
	 * {@link GraphElement} object.
	 *
	 * @param subscription
	 */
	public void removeRemovedSubscriptionEntry(Subscription sub);

	/**
	 * Remove all removed {@link SubscriptionEntry} objects attached to this
	 * 			{@link GraphElement} object.
	 */
	public void removeAllRemovedSubscriptionEntries();
}
