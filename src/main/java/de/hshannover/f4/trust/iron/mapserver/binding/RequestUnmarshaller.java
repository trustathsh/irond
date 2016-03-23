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
 * This file is part of irond, version 0.5.8, implemented by the Trust@HsH
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
package de.hshannover.f4.trust.iron.mapserver.binding;


import java.io.InputStream;

import de.hshannover.f4.trust.iron.mapserver.exceptions.InvalidFilterException;
import de.hshannover.f4.trust.iron.mapserver.exceptions.InvalidIdentifierException;
import de.hshannover.f4.trust.iron.mapserver.exceptions.InvalidMetadataException;
import de.hshannover.f4.trust.iron.mapserver.exceptions.RequestCreationException;
import de.hshannover.f4.trust.iron.mapserver.exceptions.UnmarshalException;
import de.hshannover.f4.trust.iron.mapserver.messages.Request;

/**
 * Interface to unmarshal incoming XML documents into {@link Request}
 * implementations independent of the underlying unmarshalling technology.
 *
 * @author aw
 */
public interface RequestUnmarshaller {

	/**
	 * The content of the {@link InputStream} is to be used to create an
	 * appropriate object of any of the {@link Request} implementations.
	 * This includes unmarshalling of all include identifiers.
	 *
	 * During the process of unmarshalling identifiers are validated.
	 *
	 * The content of the given {@link InputStream} will be the XML document
	 * of the HTTP body.
	 *
	 * @param is the XML document containing SOAP Envelope, SOAP Body, and a
	 *	single element in the SOAP Body indicating the requested IF-MAP
	 *	operation.
	 *
	 * @return object of an appropriate {@link Request} implementation
	 *
	 * @throws RequestCreationException if something goes wrong during the
	 *	 instantiation of the {@link Request} implementation.
	 * @throws IdentifierConstructionException if an identifier in the XML
	 *	 document has a bad format, e.g bad IPv4 format.
	 * @throws InvalidMetadataExceptionn if something is wrong with the
	 *	 contained metadata. E.g. no ifmap-cardinality attribute, or
	 *	 a wrong cardinality.
	 * @throws InvalidFilterException if the syntax of a filter is not valid or
	 *	 the namespaces for a filter are not declared correctly.
	 */
	public Request unmarshal(InputStream is)
		throws UnmarshalException, InvalidIdentifierException,
		InvalidMetadataException, InvalidFilterException;
}
