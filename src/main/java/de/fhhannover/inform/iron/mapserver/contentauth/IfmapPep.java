package de.fhhannover.inform.iron.mapserver.contentauth;

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

import java.util.List;

import de.fhhannover.inform.iron.mapserver.datamodel.Publisher;
import de.fhhannover.inform.iron.mapserver.datamodel.graph.GraphElementRepository;
import de.fhhannover.inform.iron.mapserver.datamodel.meta.Metadata;
import de.fhhannover.inform.iron.mapserver.datamodel.meta.MetadataHolder;
import de.fhhannover.inform.iron.mapserver.messages.PublishRequest;

public interface IfmapPep {

	/**
	 * Check if {@link Publisher} pub is allowed to execute the {@link PublishRequest}
	 * req.
	 *
	 * @param pub
	 * @param req
	 * @param graph
	 * @return
	 */
	boolean isAuthorized(Publisher pub, PublishRequest req, GraphElementRepository graph);

	/**
	 * Check if {@link Publisher} pub is allowed to see metadata of the
	 * given {@link MetadataHolder} objects.
	 *
	 * @param pub
	 * @param mh
	 * @return the {@link MetadataHolder} objects that are ok to be shown
	 */
	List<MetadataHolder> isSearchAuthorized(Publisher pub, List<MetadataHolder> mh);

	/**
	 * Check if {@link Publisher} purger is allowed to purge all {@link Metadata} of
	 * {@link Publisher} purgee.
	 *
	 * @param purger
	 * @param purgee
	 * @return
	 */
	boolean isAuthorized(Publisher purger, String publisherId);
}
