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
 * This file is part of irond, version 0.4.2, implemented by the Trust@HsH
 * research group at the Hochschule Hannover.
 * %%
 * Copyright (C) 2010 - 2014 Trust@HsH
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
//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Any modifications to this file will be lost upon recompilation of the source schema.
// Generated on: 2010.08.12 at 05:23:43 PM CEST
//


package org.trustedcomputinggroup._2010.ifmap._2;


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for SearchType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="SearchType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;choice>
 *           &lt;element name="access-request" type="{http://www.trustedcomputinggroup.org/2010/IFMAP/2}AccessRequestType"/>
 *           &lt;element name="identity" type="{http://www.trustedcomputinggroup.org/2010/IFMAP/2}IdentityType"/>
 *           &lt;element name="ip-address" type="{http://www.trustedcomputinggroup.org/2010/IFMAP/2}IPAddressType"/>
 *           &lt;element name="mac-address" type="{http://www.trustedcomputinggroup.org/2010/IFMAP/2}MACAddressType"/>
 *           &lt;element name="device" type="{http://www.trustedcomputinggroup.org/2010/IFMAP/2}DeviceType"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *       &lt;attribute name="match-links" type="{http://www.trustedcomputinggroup.org/2010/IFMAP/2}FilterType" />
 *       &lt;attribute name="max-depth" type="{http://www.w3.org/2001/XMLSchema}unsignedInt" />
 *       &lt;attribute name="terminal-identifier-type" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="max-size" type="{http://www.w3.org/2001/XMLSchema}unsignedInt" />
 *       &lt;attribute name="result-filter" type="{http://www.trustedcomputinggroup.org/2010/IFMAP/2}FilterType" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SearchType", propOrder = {
    "accessRequest",
    "identity",
    "ipAddress",
    "macAddress",
    "device"
})
@XmlSeeAlso({
    org.trustedcomputinggroup._2010.ifmap._2.SubscribeRequestType.Update.class,
    SearchRequestType.class
})
public class SearchType {

    @XmlElement(name = "access-request")
    protected AccessRequestType accessRequest;
    protected IdentityType identity;
    @XmlElement(name = "ip-address")
    protected IPAddressType ipAddress;
    @XmlElement(name = "mac-address")
    protected MACAddressType macAddress;
    protected DeviceType device;
    @XmlAttribute(name = "match-links")
    protected String matchLinks;
    @XmlAttribute(name = "max-depth")
    @XmlSchemaType(name = "unsignedInt")
    protected Long maxDepth;
    @XmlAttribute(name = "terminal-identifier-type")
    protected String terminalIdentifierType;
    @XmlAttribute(name = "max-size")
    @XmlSchemaType(name = "unsignedInt")
    protected Long maxSize;
    @XmlAttribute(name = "result-filter")
    protected String resultFilter;

    /**
     * Gets the value of the accessRequest property.
     *
     * @return
     *     possible object is
     *     {@link AccessRequestType }
     *
     */
    public AccessRequestType getAccessRequest() {
        return accessRequest;
    }

    /**
     * Sets the value of the accessRequest property.
     *
     * @param value
     *     allowed object is
     *     {@link AccessRequestType }
     *
     */
    public void setAccessRequest(AccessRequestType value) {
        this.accessRequest = value;
    }

    /**
     * Gets the value of the identity property.
     *
     * @return
     *     possible object is
     *     {@link IdentityType }
     *
     */
    public IdentityType getIdentity() {
        return identity;
    }

    /**
     * Sets the value of the identity property.
     *
     * @param value
     *     allowed object is
     *     {@link IdentityType }
     *
     */
    public void setIdentity(IdentityType value) {
        this.identity = value;
    }

    /**
     * Gets the value of the ipAddress property.
     *
     * @return
     *     possible object is
     *     {@link IPAddressType }
     *
     */
    public IPAddressType getIpAddress() {
        return ipAddress;
    }

    /**
     * Sets the value of the ipAddress property.
     *
     * @param value
     *     allowed object is
     *     {@link IPAddressType }
     *
     */
    public void setIpAddress(IPAddressType value) {
        this.ipAddress = value;
    }

    /**
     * Gets the value of the macAddress property.
     *
     * @return
     *     possible object is
     *     {@link MACAddressType }
     *
     */
    public MACAddressType getMacAddress() {
        return macAddress;
    }

    /**
     * Sets the value of the macAddress property.
     *
     * @param value
     *     allowed object is
     *     {@link MACAddressType }
     *
     */
    public void setMacAddress(MACAddressType value) {
        this.macAddress = value;
    }

    /**
     * Gets the value of the device property.
     *
     * @return
     *     possible object is
     *     {@link DeviceType }
     *
     */
    public DeviceType getDevice() {
        return device;
    }

    /**
     * Sets the value of the device property.
     *
     * @param value
     *     allowed object is
     *     {@link DeviceType }
     *
     */
    public void setDevice(DeviceType value) {
        this.device = value;
    }

    /**
     * Gets the value of the matchLinks property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getMatchLinks() {
        return matchLinks;
    }

    /**
     * Sets the value of the matchLinks property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setMatchLinks(String value) {
        this.matchLinks = value;
    }

    /**
     * Gets the value of the maxDepth property.
     *
     * @return
     *     possible object is
     *     {@link Long }
     *
     */
    public Long getMaxDepth() {
        return maxDepth;
    }

    /**
     * Sets the value of the maxDepth property.
     *
     * @param value
     *     allowed object is
     *     {@link Long }
     *
     */
    public void setMaxDepth(Long value) {
        this.maxDepth = value;
    }

    /**
     * Gets the value of the terminalIdentifierType property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getTerminalIdentifierType() {
        return terminalIdentifierType;
    }

    /**
     * Sets the value of the terminalIdentifierType property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setTerminalIdentifierType(String value) {
        this.terminalIdentifierType = value;
    }

    /**
     * Gets the value of the maxSize property.
     *
     * @return
     *     possible object is
     *     {@link Long }
     *
     */
    public Long getMaxSize() {
        return maxSize;
    }

    /**
     * Sets the value of the maxSize property.
     *
     * @param value
     *     allowed object is
     *     {@link Long }
     *
     */
    public void setMaxSize(Long value) {
        this.maxSize = value;
    }

    /**
     * Gets the value of the resultFilter property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getResultFilter() {
        return resultFilter;
    }

    /**
     * Sets the value of the resultFilter property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setResultFilter(String value) {
        this.resultFilter = value;
    }

}
