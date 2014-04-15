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
 * This file is part of irond, version 0.4.2, implemented by the Trust@FHH
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

import de.fhhannover.inform.iron.mapserver.communication.ChannelIdentifier;
import de.fhhannover.inform.iron.mapserver.communication.ClientIdentifier;
import de.fhhannover.inform.iron.mapserver.exceptions.AlreadyMappedException;
import de.fhhannover.inform.iron.mapserver.exceptions.AlreadyStoredException;
import de.fhhannover.inform.iron.mapserver.exceptions.NoMappingException;
import de.fhhannover.inform.iron.mapserver.exceptions.SessionNotFoundException;
import de.fhhannover.inform.iron.mapserver.exceptions.StillMappedException;

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
