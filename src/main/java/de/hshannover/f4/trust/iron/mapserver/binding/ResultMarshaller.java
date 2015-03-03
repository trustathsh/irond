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
 * This file is part of irond, version 0.5.3, implemented by the Trust@HsH
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
package de.hshannover.f4.trust.iron.mapserver.binding;


import java.io.InputStream;

import de.hshannover.f4.trust.iron.mapserver.communication.ifmap.EventProcessor;
import de.hshannover.f4.trust.iron.mapserver.datamodel.DataModelService;
import de.hshannover.f4.trust.iron.mapserver.datamodel.meta.Metadata;
import de.hshannover.f4.trust.iron.mapserver.messages.Result;

/**
 * Interface to support marshalling of {@link Result} objects, independently
 * of the underlying marshalling method.
 *
 * A {@link ResultMarshaller} implementation is used by the {@link EventProcessor}
 * to create {@link InputStream} objects from {@link Result} objects.
 *
 * The {@link InputStream} objects content represents the appropriate representation
 * of the {@link Result} object in XML form. I.e. the {@link InputStream} objects
 * content can be used as the HTTP body in a response to a client.
 *
 * @author aw
 */
public interface ResultMarshaller {

	/**
	 * Marshal a given {@link Result} object to the appropriate XML document
	 * returned as an {@link InputStream}.
	 *
	 * Marshalling should be done <b>without</b> pretty print or any namespace
	 * optimization. The reason is that the {@link DataModelService} assumes
	 * identifiers to be send in the most compact form as possible and each
	 * {@link Metadata} object keeps it's local namespace declarations.
	 *
	 * All possible {@link Result} implementations have to be supported. If
	 * a {@link ResultMarshaller} implementation does not recognize a given
	 * implementation it should throw a {@link RuntimeException}. The
	 * {@link ResultMarshaller} has to be fixed in this case.
	 *
	 *
	 * @param result any possible {@link Result} implementation.
	 * @return an {@link InputStream} containing the resulting XML document
	 */
	InputStream marshal(Result result);
}
