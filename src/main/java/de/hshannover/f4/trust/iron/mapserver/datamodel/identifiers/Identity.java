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
package de.hshannover.f4.trust.iron.mapserver.datamodel.identifiers;


import java.io.IOException;

import javax.security.auth.x500.X500Principal;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import de.hshannover.f4.trust.iron.mapserver.IfmapConstStrings;
import de.hshannover.f4.trust.iron.mapserver.exceptions.InvalidIdentifierException;
import de.hshannover.f4.trust.iron.mapserver.exceptions.SystemErrorException;
import de.hshannover.f4.trust.iron.mapserver.exceptions.ValidationFailedException;
import de.hshannover.f4.trust.iron.mapserver.utils.DomHelpers;
import de.hshannover.f4.trust.iron.mapserver.utils.IpAddressValidator;

/**
 * Implementation of the identity identifier.
 *
 * @since 0.1.0
 * @author aw
 */
public class Identity extends  IdentifierWithAdministrativeDomainImpl
		implements IdentifierWithAdministrativeDomain {

	private final String mName;
	private final IdentityTypeEnum mType;
	private final String mOtherTypeDefinition;
	private Document mExtendedIdentifier;
	private final X500Principal mDistinguishedName;
	private	final boolean mCaseSensitive;

	/**
	 * Construct a identity object from name, administrative-domain and the type.
	 *
	 * *Note:* If we come across a Host Identity Tag (hipHit), treat it as IPv6
	 * address.
	 *
	 * @param name
	 * @param ad
	 * @param it
	 * @throws InvalidIdentifierException
	 */
	public Identity(final String name, final String ad, final String otherTypeDef,
			final IdentityTypeEnum it) throws InvalidIdentifierException {
		super(IfmapConstStrings.ID, ad);
		int byteCount = 0;

		if (name == null || name.length() == 0) {
			throw new InvalidIdentifierException("Identity: name is " +
					"null or empty string");
		}

		if (it == null) {
			throw new InvalidIdentifierException("Identity: type is null");
		}

		mName = name;
		mType = it;

		mCaseSensitive = mConf.getIdentityTypeIsCaseSensitive(mType.toString());

		if (mType == IdentityTypeEnum.other &&
				(otherTypeDef == null || otherTypeDef.length() == 0)) {
			throw new InvalidIdentifierException("Identity: type-other "
					+ "requires other-type-definition");
		}

		mOtherTypeDefinition = otherTypeDef == null ? "" : otherTypeDef;

		if (mType == IdentityTypeEnum.hipHit && !IpAddressValidator.validateIPv6(name)) {
			throw new InvalidIdentifierException("Identity: incorrect "
					+ "HIP-HIT incorrect format (" + name + ")");
		}

		if (isExtendedIdentifier()) {

			mExtendedIdentifier = prepareExtendedIdentifier(name);

			checkExtendedIdentifierAdminDomain();


			// If we want to validate extended identfiers, do it.
			// Throws InvalidIdentifierException if not schema compliant
			if(mConf.getXmlValidationExtendedIdentity()) {
				validateExtendedIdentifier();
			}
		}

		if (isDistinguishedName()) {
			mDistinguishedName = prepareDistinguishedName(name);

			// let the MAPC know if X500 was invalid
			if (mDistinguishedName == null && mConf.getStrictDistinguishedName()) {
				throw new InvalidIdentifierException("distinguished-name " +
						" not in X500 format: " + name);
			}

		} else {
			mDistinguishedName = null;
		}

		byteCount += IfmapConstStrings.ID_CNT + mName.length()
		+ mType.toString().length() + getByteCountForAdministrativeDomain();
		if (mType == IdentityTypeEnum.other) {
			byteCount += IfmapConstStrings.ID_OTHER_TYPE_DEF_ATTR_CNT;
			byteCount += mOtherTypeDefinition.length();
		}

		setByteCount(byteCount);
	}

	public Identity(String name, IdentityTypeEnum it) throws InvalidIdentifierException {
		this(name, "", null, it);
	}

	public IdentityTypeEnum getIdentityType() {
		return mType;
	}

	public final String getName() {
		return mName;
	}

	/**
	 * @return the other-type-definition attribute if type is other, otherwise
	 * 		we die.
	 */
	public String getOtherTypeDefinition() {
		if (mType != IdentityTypeEnum.other) {
			throw new SystemErrorException("Identity: Error calling " +
					"getOtherTypeDefinition() on type=" + mType.toString());
		}

		return mOtherTypeDefinition;
	}

	/* (non-Javadoc)
	 * @see de.hshannover.f4.trust.iron.mapserver.datamodel.identifiers.IdentifierWithAdministrativeDomainImpl#equals(java.lang.Object)
	 *
	 * we ceck the other type def case sensitive... ?
	 * FIXME: What is the specification saying?
	 */
	@Override
	public boolean equals(Object o) {
		if (o == null) {
			return false;
		}

		if (this == o) {
			return true;
		}

		// is it Identity at all?
		if (!(o instanceof Identity)) {
			return false;
		}

		// check administrative domain in super-class
		if (!super.equals(o)) {
			return false;
		}

		Identity oI = (Identity)o;

		// same type?
		if (mType != oI.getIdentityType()) {
			return false;
		}

		// compare other-type-definitions, if type="other"
		if (mType == IdentityTypeEnum.other) {
			String otherTypeDef = oI.getOtherTypeDefinition();
			if (!mOtherTypeDefinition.equals(otherTypeDef)) {
				return false;
			}
		}

		// compare name according to its type extended
		if (isDistinguishedName()) {
			if (mDistinguishedName != null && oI.mDistinguishedName != null) {
				return mDistinguishedName.equals(oI.mDistinguishedName);
			} else {

				// Sanity check: One of the DNs was null, so this implies
				// strict handling of DNs is disabled

				if (mConf.getStrictDistinguishedName()) {
					throw new SystemErrorException("DN null, but strict enabled");
				}

				return mCaseSensitive ? mName.equals(oI.mName) :
						mName.equalsIgnoreCase(oI.mName);
			}
		// all others
		} else {
			String otherName = oI.getName();
			return mCaseSensitive ? mName.equals(otherName) :
					mName.equalsIgnoreCase(otherName);
		}
	}

	/**
	* Calculate a hash code based on the identifier parameters
	* If there is an extended identifier or DSN identity the hashcode
	* ignores the name field which needs to get inspected closer
	* by using equals().
	*
	* @return hash value
	*/
	@Override
	protected final int getHashCode() {
		int hash = 11 * 97 + (mType != null ? mType.hashCode() : 0);

		hash = 11 * hash + mOtherTypeDefinition.hashCode();

		if (mType == IdentityTypeEnum.distinguishedName && mDistinguishedName != null) {
			hash = 11 * hash + mDistinguishedName.getName().hashCode();
		} else {
			hash = 11 * hash + (mCaseSensitive ? mName.hashCode() :
					mName.toLowerCase().hashCode());
		}

		return 11 * hash + super.getHashCode();
	}

	/* (non-Javadoc)
	 * @see de.hshannover.f4.trust.iron.mapserver.datamodel.identifiers.IdentifierImpl#getPrintableString()
	 */
	@Override
	protected String getPrintableString() {
		String ad = getAdministrativeDomain();
		StringBuilder sb = new StringBuilder("id{");

		if (isExtendedIdentifier()) {
			sb.append(DomHelpers.unescapeXml(mName));
		} else {
			sb.append(mName);
		}

		if (ad != null && ad.length() > 0) {
			sb.append(", ");
			sb.append(ad);
		}

		sb.append(", ");
		sb.append(mType.toString());

		if (mType == IdentityTypeEnum.other) {
			sb.append(", ");
			sb.append(getOtherTypeDefinition());
		}
		sb.append("}");
		return sb.toString();
	}

	/**
	 * Prepare an extended identifier as w3c.dom.Document. While at it, check
	 * if the representation send by the MAPC is the same as irond would
	 * produce itself.
	 *
	 * @param xml XML string
	 * @return XML document
	 * @throws InvalidIdentifierException  If something goes wrong while
	 * 		   parsing the XML document, or if encoding the {@link Document}
	 *		   does not result in the same {@link String} as the MAPC did
	 *		   send.
	 */
	private Document prepareExtendedIdentifier(String xml) throws InvalidIdentifierException {

		Document ret = null;
		String myXml = null;
		String unescaped = DomHelpers.unescapeXml(xml);

		try {
			ret = DomHelpers.toDocument(unescaped, null);

			myXml = DomHelpers.toExtendedIdentifierValue(ret);

			// If irond thinks the result of encoding should look different,
			// send this to the MAPC.
			if (!myXml.equals(xml)) {
				throw new InvalidIdentifierException("Extended Identifier: " +
						"encoding resulted in different value." +
						"Your value:\"" + xml + "\" Mine: \"" + myXml + "\". " +
						"If you think this is an error in irond, please report " +
						"it. Thanks!");
			}

		} catch (SAXException e) {
			throw new InvalidIdentifierException("extended identifier error: " +
						e.getMessage());
		} catch (IOException e) {
			throw new InvalidIdentifierException("extended identifier error: " +
						e.getMessage());
		}

		return ret;
	}

	/**
	 * Prepare an DSN
	 * @param dsn DSN string
	 * @return X500 prinicipal
	 * @throws InvalidIdentifierException
	 */
	private X500Principal prepareDistinguishedName(String dsn) {
		if (!mCaseSensitive) {
			dsn = dsn.toLowerCase();
		}

		try {
			return new X500Principal(dsn);
		} catch (Exception e) {
			// Too bad
		}
		return null;
	}

	/**
	 * An extended identifier is not allowed to have NO
	 * administrative-domain attribute *in* the encoded XML, but it is allowed
	 * to submit an empty string.
	 * <p><blockquote><pre>
	 * "If the administrative-domain is not used, it MUST be set to an empty string in an
	 * extended identifier instance." [1, page 20]
	 * </pre></blockquote></p>
	 * On the other hand, the encapsulating identity identifier is not allowed to have
	 * an administrative-domain set.
	 *
	 * <p>
	 * [1] TNC IF-MAP Binding for SOAP, Specification Version 2.1, Revision 15, 7 May 2012.
	 * </p>
	 *
	 * @throws InvalidIdentifierException
	 */
	private void checkExtendedIdentifierAdminDomain() throws InvalidIdentifierException {

		String ad = super.getAdministrativeDomain();

		if (mExtendedIdentifier == null) {
			throw new SystemErrorException("Trying to check admin-domain, but " +
										   "extended identifier is null?!");
		}

		if (ad != null && ad.length() > 0) {
			throw new InvalidIdentifierException(
					"Identity: extended identity with " +
					"administrative-domain (" + ad + ") found");
		}

		Node n = mExtendedIdentifier.getFirstChild();

		if (n.getNodeType() != Node.ELEMENT_NODE) {
			throw new SystemErrorException("Extended identifier root node not elemnt?");
		}

		Element el = (Element)n;

		Attr admAttr = el.getAttributeNodeNS(null, "administrative-domain");

		if (admAttr == null)
		 {
			throw new InvalidIdentifierException("No administrative-domain " +
												 "for extended identifier.");
		// all good, hopefully.
		}
	}

	private void validateExtendedIdentifier() throws InvalidIdentifierException {
		String ns = mExtendedIdentifier.lookupNamespaceURI(null);

		if (ns == null || ns.isEmpty()) {
			throw new InvalidIdentifierException("Extended identifier without namespace");
		}

		StreamSource schema = mConf.getExtendedIdentitySchema(ns);
		boolean lockdown = mConf.getXmlValidationExtendedIdentityLockDownMode();

		if (lockdown && schema == null) {
			throw new InvalidIdentifierException("extended identifier: " +
				"lock-down: missing schema ");
		}

		try {
			if (schema != null) {
				DomHelpers.validate(mExtendedIdentifier, schema);
			}
		} catch (ValidationFailedException e) {
			throw new InvalidIdentifierException("extended identfier" +
					" validation failed: " + e.getMessage());
		}
	}


	/* (non-Javadoc)
	 * @see de.hshannover.f4.trust.iron.mapserver.datamodel.identifiers.IdentifierImpl#getXmlDocument()
	 */
	@Override
	public Document getXmlDocument() {

		// If we have an extended identifier, make sure we return the Document
		// that is stored in the name attribute
		if (isExtendedIdentifier()) {
			return mExtendedIdentifier;
		}


		// Otherwise, whatever is there originally...
		return super.getXmlDocument();
	}

	private boolean isExtendedIdentifier() {
		return mType == IdentityTypeEnum.other
				&& mOtherTypeDefinition.equals(IfmapConstStrings.ID_OTHER_EXT);
	}

	private boolean isDistinguishedName() {
		return mType == IdentityTypeEnum.distinguishedName;
	}
}
