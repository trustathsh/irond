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
 * This file is part of irond, version 0.5.7, implemented by the Trust@HsH
 * research group at the Hochschule Hannover.
 * %%
 * Copyright (C) 2010 - 2016 Trust@HsH
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


import de.hshannover.f4.trust.iron.mapserver.communication.ChannelIdentifier;
import de.hshannover.f4.trust.iron.mapserver.communication.ClientIdentifier;
import de.hshannover.f4.trust.iron.mapserver.exceptions.AlreadyMappedException;
import de.hshannover.f4.trust.iron.mapserver.exceptions.AlreadyStoredException;
import de.hshannover.f4.trust.iron.mapserver.exceptions.NoMappingException;
import de.hshannover.f4.trust.iron.mapserver.exceptions.SessionNotFoundException;
import de.hshannover.f4.trust.iron.mapserver.exceptions.StillMappedException;

/**
 * Provides access to currently managed IF-MAP sessions.
 *
 * We use three mappings in order to have quick access based on
 * <ul>
 *  <li>ClientIdentifier</li>
 *  <li>ChannelIdentifier</li>
 *  <li>session-id</li>
 *
 * </ul>
 *
 * <b>This may be accessed by multiple threads at the same time. Be aware!</b>
 *
 * The operations are rather basic and no reference counting is performed.
 *
 * All these {@link Exception}s are there to make sure everything works
 * correct, hopefully.
 *
 * @author aw
 *
 */
public interface SessionRepository {

	/**
	 * @return stored session or null
	 */
	public Session getBy(ClientIdentifier clientId);

	/**
	 * @return stored session or null
	 */
	public Session getBy(ChannelIdentifier channelId);

	/**
	 * @return stored session or null
	 */
	public Session getBy(String sessionId);

	/**
	 * Stores a {@link Session} in the repository. This will only map it
	 * with the {@link ClientIdentifier} because that's the only one that
	 * will be set for sure and is stable.
	 *
	 * @param session
	 * @throws AlreadyStoredException if session is already stored
	 */
	public void store(Session session) throws AlreadyStoredException;

	/**
	 * Remove a {@link Session} from the repository.
	 * The only mapping that will be removed is the {@link ClientIdentifier} to
	 * {@link Session} mapping.
	 *
	 * <br>
	 * <b>Please make sure you don't have any dangling mappings!</b>
	 *
	 * @param session
	 * @throws SessionNotFoundException if session is not stored
	 * @throws StillMappedException if mappings have not been removed
	 */
	public void drop(Session session) throws SessionNotFoundException, StillMappedException;

	/**
	 * Maps a Session to a sessionId.
	 *
	 * @param session
	 * @param sessionId
	 * @throws SessionNotFoundException if {@link Session} is not in repository
	 * @throws AlreadyMappedException if session-id is already mapped
	 */
	public void map(Session session, String sessionId) throws SessionNotFoundException, AlreadyMappedException;

	/**
	 * Remove the mapping between a session-id and a {@link Session}
	 *
	 * @param session
	 * @param sessionId
	 * @throws SessionNotFoundException if session is not stored
	 * @throws NoMappingException if mapping did not exist
	 */
	public void unmap(Session session, String sessionId) throws SessionNotFoundException, NoMappingException;

	/**
	 * Map a {@link Session} to a {@link ChannelIdentifier}
	 *
	 * @param session
	 * @param channelId
	 * @throws SessionNotFoundException if session is not stored
	 * @throws AlreadyMappedException if channelid is already mapped
	 */
	public void map(Session session, ChannelIdentifier channelId) throws SessionNotFoundException, AlreadyMappedException;

	/**
	 * Remove the mapping between a {@link ChannelIdentifier} and a {@link SessionNotFoundException}
	 *
	 * @param session
	 * @param channelId
	 * @throws SessionNotFoundException if session is not stored
	 * @throws NoMappingException if mapping did not exist
	 */
	public void unmap(Session session, ChannelIdentifier channelId) throws SessionNotFoundException, NoMappingException;
}
