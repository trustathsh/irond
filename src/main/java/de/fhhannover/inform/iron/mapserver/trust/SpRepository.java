package de.fhhannover.inform.iron.mapserver.trust;

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

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;

import de.fhhannover.inform.iron.mapserver.trust.domain.SecurityProperty;

public class SpRepository {

	private HashMap<String, SecurityProperty> mSpMap;

//	private String[] propertyNames = { "basicAuth", "smartphone",
//			"certAuth", "certExpired", "certSignedByTpmCa" };

	public SpRepository() {
		init();
	}

	private void init() {
		mSpMap = new HashMap<String, SecurityProperty>();

		Properties properties = new Properties();
		try {
			properties.load(new FileInputStream("security.properties"));
			
			Enumeration<Object> keys = properties.keys();
			while (keys.hasMoreElements()) {
				String key = (String)keys.nextElement();
				SecurityProperty sp = new SecurityProperty(key,
						Integer.parseInt(properties.getProperty(key)));
				mSpMap.put(sp.getPropertyName(), sp);
				
			}
			
//			for (int i = 0; i < propertyNames.length; i++) {
//				SecurityProperty sp = new SecurityProperty(propertyNames[i],
//						Integer.parseInt(properties
//								.getProperty(propertyNames[i])));
//				mSpMap.put(sp.getPropertyName(), sp);
//			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public SecurityProperty getSp(String propertyName) {
		return mSpMap.get(propertyName);
	}

	public void reloadSpFile() {
		Properties properties = new Properties();
		try {
			properties.load(new FileInputStream("security.properties"));
			
			Enumeration<Object> keys = properties.keys();
			while (keys.hasMoreElements()) {
				String key = (String)keys.nextElement();
				SecurityProperty sp = new SecurityProperty(key,
						Integer.parseInt(properties.getProperty(key)));
				mSpMap.put(sp.getPropertyName(), sp);
				
			}
			
//			for (int i = 0; i < propertyNames.length; i++) {
//				SecurityProperty sp = mSpMap.get(propertyNames[i]);
//				sp.setRating(Integer.parseInt(properties
//						.getProperty(propertyNames[i])));
//			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
