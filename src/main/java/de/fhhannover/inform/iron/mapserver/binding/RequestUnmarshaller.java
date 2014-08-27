package de.fhhannover.inform.iron.mapserver.binding;

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

import java.io.InputStream;

import de.fhhannover.inform.iron.mapserver.exceptions.InvalidFilterException;
import de.fhhannover.inform.iron.mapserver.exceptions.InvalidIdentifierException;
import de.fhhannover.inform.iron.mapserver.exceptions.InvalidMetadataException;
import de.fhhannover.inform.iron.mapserver.exceptions.RequestCreationException;
import de.fhhannover.inform.iron.mapserver.exceptions.UnmarshalException;
import de.fhhannover.inform.iron.mapserver.messages.Request;

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
