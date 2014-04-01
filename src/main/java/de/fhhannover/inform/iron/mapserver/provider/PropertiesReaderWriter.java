package de.fhhannover.inform.iron.mapserver.provider;

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
 * This file is part of irond, version 0.4.1, implemented by the Trust@FHH
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import de.fhhannover.inform.iron.mapserver.utils.NullCheck;
import de.fhhannover.inform.iron.mapserver.utils.SortedProperties;

/**
 * Class that encapsulates reading and writing of {@link Properties} files.
 * 
 * @author aw
 *
 */
class PropertiesReaderWriter {

	private final String mFilename;
	private SortedProperties mProperties;
	
	public PropertiesReaderWriter(String fileName, boolean create) throws IOException {
		NullCheck.check(fileName, "filename is null");
		mFilename = fileName;
		mProperties = new SortedProperties();
		loadProperties(create);
	}

	private void loadProperties(boolean create) throws IOException {
		FileReader fileReader = null;
		File f = null;
		
		try {
			fileReader = new FileReader(mFilename);
		} catch (FileNotFoundException e) {
			f = new File(mFilename);
			if (f.isDirectory()) {
				throw new IOException(mFilename + " is a directory");
			} else if (f.isFile()) {
				throw new IOException("Could not open " + mFilename + ": " +
						e.getMessage());
			}
			// If it doens't exist and it's not a directory, try to
			// create it. If it fails, well, then it fails...
			else if (create) {
				try {
					new FileWriter(mFilename).close();
					fileReader = new FileReader(mFilename);
				} catch (IOException e1) {
				throw new IOException("Could not create " + mFilename + ": " +
						e.getMessage());
				}
			} else {
				throw new IOException("Could not open " + mFilename + ": " +
						e.getMessage());
			}
		}
		
		mProperties.load(fileReader);
		fileReader.close();
	}
	
	/**
	 * Get a value out of the {@link Properties} file.
	 * 
	 * @param key
	 * @return the value corresponding to key or null if nothing was found.
	 */
	public String getProperty(String key) {
		NullCheck.check(key, "key is null");
		return mProperties.getProperty(key);
	}
	
	/**
	 * Stores a key value pair and writes a new corresponding
	 * {@link Properties} file.
	 * 
	 * @param key
	 * @param value
	 * @throws IOException
	 */
	public void storeProperty(String key, String value) throws IOException {
		NullCheck.check(key, "key is null");
		NullCheck.check(key, "value is null");
		mProperties.setProperty(key, value);
		FileWriter fileWriter = new FileWriter(mFilename);
		mProperties.store(fileWriter, null);
		fileWriter.close();
	}
	
	/**
	 * @return a list of all keys in the {@link Properties} map.
	 */
	public List<String> getAllKeys() {
		return createStringList(mProperties.keySet());
	}

	/**
	 * @return a list of all values in the {@link Properties} map.
	 */
	public List<String> getAllValues() {
		return createStringList(mProperties.values());
	}
	
	/**
	 * Checks whether the given key is contained in the properties file.
	 * @param key
	 * @return
	 */
	public boolean containsKey(String key) {
		return mProperties.containsKey(key);
	}
	
	/**
	 * @return a list of all String objects in the given {@link Collection}
	 */
	private List<String> createStringList(Collection<Object> collection) {
		List<String> ret = new LinkedList<String>();
		for (Object val : collection) {
			// we are only interested in Strings
			if (val instanceof String) {
				String sval = (String) val;
				ret.add(sval);
			}
		}
		return ret;
	}
}
