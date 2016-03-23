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
package de.hshannover.f4.trust.iron.mapserver;


import java.util.Arrays;
import java.util.List;

import de.hshannover.f4.trust.iron.mapserver.messages.ErrorCode;

/**
 * Some string and byte count constants used here and there.
 *
 * @author aw
 *
 */
public class IfmapConstStrings {

	/*
	 * The identifier element names as constants
	 */
	public static final String AR = "access-request";
	public static final String IP = "ip-address";
	public static final String MAC = "mac-address";
	public static final String DEV = "device";
	public static final String ID = "identity";
	public static final String ID_AIK = "identity:aik-name";
	public static final String ID_DIST = "identity:distinguished-name";
	public static final String ID_DNS = "identity:dns-name";
	public static final String ID_MAIL = "identity:email-address";
	public static final String ID_KERBEROS = "identity:kerberos-principal";
	public static final String ID_USER = "identity:username";
	public static final String ID_SIP = "identity:sip-uri";
	public static final String ID_TEL = "identity:tel-uri";
	public static final String ID_HIP_HIT = "identity:hip-hit";
	public static final String ID_EXT = "identity:other:extended";
	public static final String ID_NON_EXT = "identity:nonextended";

	public static final String REGEX_ID_EXT = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]#.*";
	public static final String REGEX_ID_OTHER_VENDOR = "^identity:other:[0-9]*:.*";
	public static final String REGEX_ID_OTHER_NAME = "^identity:other:.*";

	/*
	 * The other type name for extended identifier
	 */
	public static final String ID_OTHER_EXT = "extended";

	/**
	 * Has to be synchronized with the IDENTIFIER_CLASSES below!
	 */
	public static final List<String> IDENTIFIERS = Arrays.asList(AR, IP, MAC,
			DEV, ID, ID_AIK, ID_DIST, ID_DNS, ID_MAIL, ID_KERBEROS, ID_USER, ID_SIP, ID_TEL, ID_HIP_HIT, ID_EXT,
			ID_NON_EXT);

	@SuppressWarnings("rawtypes")
	// public static final Class IDENTIFIER_CLASSES[] = {
	// AccessRequest.class,
	// IpAddress.class,
	// MacAddress.class,
	// Identity.class,
	// Device.class };
	//
	public static final String SRES_BEG_ELEM = "<searchResult>";
	public static final String SRES_END_ELEM = "</searchResult>";
	public static final String URES_BEG_ELEM = "<updateResult>";
	public static final String URES_END_ELEM = "</updateResult>";
	public static final String DRES_BEG_ELEM = "<deleteResult>";
	public static final String DRES_END_ELEM = "</deleteResult>";
	public static final String NRES_BEG_ELEM = "<notifyResult>";
	public static final String NRES_END_ELEM = "</notifyResult>";
	public static final String PRES_BEG_ELEM = "<pollyResult>";
	public static final String PRES_END_ELEM = "</pollyResult>";
	public static final String RITEM_BEG_ELEM = "<resultItem>";
	public static final String RITEM_END_ELEM = "</resultItem>";
	public static final String MLIST_BEG_ELEM = "<metadata>";
	public static final String MLIST_END_ELEM = "</metadata>";

	/**
	 * the minimum size of a searchResult, containing no ResultItems
	 */
	public static final int SRES_MIN_CNT = SRES_BEG_ELEM.length()
			+ SRES_END_ELEM.length();
	/**
	 * the minimum size of a updateResult, containing no ResultItems
	 */
	public static final int URES_MIN_CNT = URES_BEG_ELEM.length()
			+ URES_END_ELEM.length();
	/**
	 * the minimum size of a deleteResult, containing no ResultItems
	 */
	public static final int DRES_MIN_CNT = DRES_BEG_ELEM.length()
			+ DRES_END_ELEM.length();
	/**
	 * the minimum size of a notifyResult, containing no ResultItems
	 */
	public static final int NRES_MIN_CNT = NRES_BEG_ELEM.length()
			+ NRES_END_ELEM.length();

	/**
	 * the minimum size of a pollResult, contining no searchResults
	 */
	public static final int PRES_MIN_CNT = PRES_BEG_ELEM.length()
			+ PRES_END_ELEM.length();

	/**
	 * the minium size of a ResultItem
	 */
	public static final int RITEM_MIN_CNT = RITEM_BEG_ELEM.length()
			+ RITEM_END_ELEM.length();

	public static final int MLIST_MIN_CNT = RITEM_BEG_ELEM.length()
			+ RITEM_END_ELEM.length();
	/**
	 * single element overhead
	 */
	private final static String ELEM_OH = "</>";

	/**
	 * enclosing element overhead
	 */
	private final static String EELEM_OH = "<></>";

	/**
	 * overhead when having attributes
	 */
	private final static String ATTR_OH = "=\"\"";

	public final static String ADOM_ATTR = " administrative-domain";
	public final static String OTHER_TYPE_DEF_ATTR = " other-type-definition";
	public final static String NAME_ATTR = " name";
	public final static String VALUE_ATTR = " value";
	public final static String TYPE_ATTR = " type";
	public final static String NAME_ELEM = "name";
	public final static String ERR_CODE_ATTR = " errorCode";

	/**
	 * the byte count overhead for the administrativeDomain, if set
	 */
	public static final int ADMDOM_CNT = ADOM_ATTR.length() + ATTR_OH.length();

	/**
	 * <access-request name="XXXX"/>
	 */
	public static final int AR_CNT = AR.length() + ELEM_OH.length()
			+ NAME_ATTR.length() + ATTR_OH.length();

	/**
	 * <ip-address value="XXX" type="XXX"/>
	 */
	public static final int IP_CNT = IP.length() + ELEM_OH.length()
			+ VALUE_ATTR.length() + TYPE_ATTR.length() + 2 * ATTR_OH.length();

	/**
	 * <mac-address value="XXXX" />
	 */
	public static final int MAC_CNT = MAC.length() + ELEM_OH.length()
			+ VALUE_ATTR.length() + ATTR_OH.length();

	/**
	 * <device> <name> XXXX </name> </device>
	 */
	public static final int DEV_CNT = 2 * DEV.length() + EELEM_OH.length() + 2
			* NAME_ELEM.length() + EELEM_OH.length();

	/**
	 * <identity name="XXX" type="XXXX"/>
	 *
	 */
	public static final int ID_CNT = ID.length() + ELEM_OH.length()
			+ NAME_ATTR.length() + TYPE_ATTR.length() + 2 * ATTR_OH.length();

	public static final int ID_OTHER_TYPE_DEF_ATTR_CNT = OTHER_TYPE_DEF_ATTR
			.length();

	/**
	 * the constant size of a errorResult for a subscriptions that grew too big
	 * <errorResult errorCode="SearchResultsTooBig" name="XXX"/>
	 */
	public static final int SERR_MIN_CNT = ELEM_OH.length()
			+ ERR_CODE_ATTR.length() + NAME_ATTR.length() + 2
			* ATTR_OH.length()
			+ ErrorCode.SearchResultsTooBig.toString().length();

}
