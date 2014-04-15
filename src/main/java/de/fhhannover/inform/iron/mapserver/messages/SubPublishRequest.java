package de.fhhannover.inform.iron.mapserver.messages;

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

import de.fhhannover.inform.iron.mapserver.datamodel.identifiers.Identifier;
import de.fhhannover.inform.iron.mapserver.exceptions.RequestCreationException;

/**
 * SubPublishRequest
 * 
 * Superclass which may contain two identifiers and needs at least one
 * identifier.
 * If both identifiers are set a link is described.
 * If only one identifier is set a single identifier is described.
 * 
 * @author aw, vp
 * @version 0.1
 */

/* 
 * created: 19.11.09
 * changes:
 *  22.11.09 vp - Added methods:
 * 		setIdent1(), setIdent2()
 *  28.11.09 aw - Class should be abstract, created constructor
 *  05.12.09 aw - indentation, added publisher.
 *  22.12.09 aw - Exception with message added.
 *  30.04.10 aw - Removed reference to publisher, does not belong here
 */
abstract public class SubPublishRequest {
 
	private Identifier ident1;
	private Identifier ident2;
	
	/**
	 * Constructor when two identifiers are available;
	 * If one is null we set ident1 to the one that is *not* null.
	 * 
	 * @param ident1
	 * @param ident2
	 * @throws RequestCreationException 
	 */
	public SubPublishRequest(Identifier ident1, Identifier ident2) throws
											RequestCreationException {
		if (ident1 == null && ident2 == null) {
			throw new RequestCreationException("Both identifiers null");
		} else if (ident1 == null || ident2 == null) {
			if (ident1 == null) {
				ident1 = ident2;
				ident2 = null;
			}
		}
		this.ident1 = ident1;
		this.ident2 = ident2;
	}
	
	/**
	 * Constructing a SubPublishRequest from only one identifier.
	 * 
	 * @param ident
	 * @throws RequestCreationException 
	 */
	public SubPublishRequest(Identifier ident) throws RequestCreationException {
		this(ident, null);
	}
	
	public void setIdent1(Identifier ident1){
		this.ident1 = ident1;
	}
	
	public Identifier getIdent1() {
		return ident1;
	}
	
	public void setIdent2(Identifier ident2){
		this.ident2 = ident2;
	}
	
	public Identifier getIdent2() {
		return ident2;
	}
}
 

