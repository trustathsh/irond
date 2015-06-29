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
package de.hshannover.f4.trust.iron.mapserver.binding;


import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;

/**
 * Provides {@link ValidationEventHandler} handler.
 *
 * Currently using a inner class to do so...
 *
 * @since 0.3.0
 * @author aw
 *
 */
class SimpleValidationEventHandlerFactory implements
		ValidationEventHandlerFactory {

	private SimpleValidationEventHandlerFactory() { }

	public static SimpleValidationEventHandlerFactory newInstance() {
		return new SimpleValidationEventHandlerFactory();
	}

	@Override
	public SimpleValidationEventHandler newValidationEventHandler() {
		return new SimpleValidationEventHandlerImpl();
	}

	private class SimpleValidationEventHandlerImpl implements SimpleValidationEventHandler {


		private boolean mErrorOccurred = false;
		private String mErrorMessage;

		@Override
		public boolean handleEvent(ValidationEvent event) {
			int eventSeverity = event.getSeverity();

			// We always bail out on fatal errors...
			if (eventSeverity == ValidationEvent.FATAL_ERROR ||
					eventSeverity == ValidationEvent.ERROR) {
				mErrorOccurred = true;
				mErrorMessage = event.getMessage();
			}

			return !mErrorOccurred;
		}

		@Override
		public boolean hasErrorOccured() {
			return mErrorOccurred;
		}

		@Override
		public String getErrorMessage() {
			return mErrorMessage;
		}
	}
}
