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
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ResponseType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="ResponseType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice>
 *         &lt;element name="errorResult" type="{http://www.trustedcomputinggroup.org/2010/IFMAP/2}ErrorResultType"/>
 *         &lt;element name="pollResult" type="{http://www.trustedcomputinggroup.org/2010/IFMAP/2}PollResultType"/>
 *         &lt;element name="searchResult" type="{http://www.trustedcomputinggroup.org/2010/IFMAP/2}SearchResultType"/>
 *         &lt;element name="subscribeReceived">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="publishReceived">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="purgePublisherReceived">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="newSessionResult" type="{http://www.trustedcomputinggroup.org/2010/IFMAP/2}NewSessionResultType"/>
 *         &lt;element name="attachSessionResult" type="{http://www.trustedcomputinggroup.org/2010/IFMAP/2}SessionResultType"/>
 *         &lt;element name="renewSessionResult">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="endSessionResult">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="dumpResult" type="{http://www.trustedcomputinggroup.org/2010/IFMAP/2}DumpResponseType"/>
 *       &lt;/choice>
 *       &lt;attGroup ref="{http://www.trustedcomputinggroup.org/2010/IFMAP/2}validationAttributes"/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ResponseType", propOrder = {
    "errorResult",
    "pollResult",
    "searchResult",
    "subscribeReceived",
    "publishReceived",
    "purgePublisherReceived",
    "newSessionResult",
    "attachSessionResult",
    "renewSessionResult",
    "endSessionResult",
    "dumpResult"
})
public class ResponseType {

    protected ErrorResultType errorResult;
    protected PollResultType pollResult;
    protected SearchResultType searchResult;
    protected ResponseType.SubscribeReceived subscribeReceived;
    protected ResponseType.PublishReceived publishReceived;
    protected ResponseType.PurgePublisherReceived purgePublisherReceived;
    protected NewSessionResultType newSessionResult;
    protected SessionResultType attachSessionResult;
    protected ResponseType.RenewSessionResult renewSessionResult;
    protected ResponseType.EndSessionResult endSessionResult;
    protected DumpResponseType dumpResult;
    @XmlAttribute
    protected String validation;

    /**
     * Gets the value of the errorResult property.
     *
     * @return
     *     possible object is
     *     {@link ErrorResultType }
     *
     */
    public ErrorResultType getErrorResult() {
        return errorResult;
    }

    /**
     * Sets the value of the errorResult property.
     *
     * @param value
     *     allowed object is
     *     {@link ErrorResultType }
     *
     */
    public void setErrorResult(ErrorResultType value) {
        this.errorResult = value;
    }

    /**
     * Gets the value of the pollResult property.
     *
     * @return
     *     possible object is
     *     {@link PollResultType }
     *
     */
    public PollResultType getPollResult() {
        return pollResult;
    }

    /**
     * Sets the value of the pollResult property.
     *
     * @param value
     *     allowed object is
     *     {@link PollResultType }
     *
     */
    public void setPollResult(PollResultType value) {
        this.pollResult = value;
    }

    /**
     * Gets the value of the searchResult property.
     *
     * @return
     *     possible object is
     *     {@link SearchResultType }
     *
     */
    public SearchResultType getSearchResult() {
        return searchResult;
    }

    /**
     * Sets the value of the searchResult property.
     *
     * @param value
     *     allowed object is
     *     {@link SearchResultType }
     *
     */
    public void setSearchResult(SearchResultType value) {
        this.searchResult = value;
    }

    /**
     * Gets the value of the subscribeReceived property.
     *
     * @return
     *     possible object is
     *     {@link ResponseType.SubscribeReceived }
     *
     */
    public ResponseType.SubscribeReceived getSubscribeReceived() {
        return subscribeReceived;
    }

    /**
     * Sets the value of the subscribeReceived property.
     *
     * @param value
     *     allowed object is
     *     {@link ResponseType.SubscribeReceived }
     *
     */
    public void setSubscribeReceived(ResponseType.SubscribeReceived value) {
        this.subscribeReceived = value;
    }

    /**
     * Gets the value of the publishReceived property.
     *
     * @return
     *     possible object is
     *     {@link ResponseType.PublishReceived }
     *
     */
    public ResponseType.PublishReceived getPublishReceived() {
        return publishReceived;
    }

    /**
     * Sets the value of the publishReceived property.
     *
     * @param value
     *     allowed object is
     *     {@link ResponseType.PublishReceived }
     *
     */
    public void setPublishReceived(ResponseType.PublishReceived value) {
        this.publishReceived = value;
    }

    /**
     * Gets the value of the purgePublisherReceived property.
     *
     * @return
     *     possible object is
     *     {@link ResponseType.PurgePublisherReceived }
     *
     */
    public ResponseType.PurgePublisherReceived getPurgePublisherReceived() {
        return purgePublisherReceived;
    }

    /**
     * Sets the value of the purgePublisherReceived property.
     *
     * @param value
     *     allowed object is
     *     {@link ResponseType.PurgePublisherReceived }
     *
     */
    public void setPurgePublisherReceived(ResponseType.PurgePublisherReceived value) {
        this.purgePublisherReceived = value;
    }

    /**
     * Gets the value of the newSessionResult property.
     *
     * @return
     *     possible object is
     *     {@link NewSessionResultType }
     *
     */
    public NewSessionResultType getNewSessionResult() {
        return newSessionResult;
    }

    /**
     * Sets the value of the newSessionResult property.
     *
     * @param value
     *     allowed object is
     *     {@link NewSessionResultType }
     *
     */
    public void setNewSessionResult(NewSessionResultType value) {
        this.newSessionResult = value;
    }

    /**
     * Gets the value of the attachSessionResult property.
     *
     * @return
     *     possible object is
     *     {@link SessionResultType }
     *
     */
    public SessionResultType getAttachSessionResult() {
        return attachSessionResult;
    }

    /**
     * Sets the value of the attachSessionResult property.
     *
     * @param value
     *     allowed object is
     *     {@link SessionResultType }
     *
     */
    public void setAttachSessionResult(SessionResultType value) {
        this.attachSessionResult = value;
    }

    /**
     * Gets the value of the renewSessionResult property.
     *
     * @return
     *     possible object is
     *     {@link ResponseType.RenewSessionResult }
     *
     */
    public ResponseType.RenewSessionResult getRenewSessionResult() {
        return renewSessionResult;
    }

    /**
     * Sets the value of the renewSessionResult property.
     *
     * @param value
     *     allowed object is
     *     {@link ResponseType.RenewSessionResult }
     *
     */
    public void setRenewSessionResult(ResponseType.RenewSessionResult value) {
        this.renewSessionResult = value;
    }

    /**
     * Gets the value of the endSessionResult property.
     *
     * @return
     *     possible object is
     *     {@link ResponseType.EndSessionResult }
     *
     */
    public ResponseType.EndSessionResult getEndSessionResult() {
        return endSessionResult;
    }

    /**
     * Sets the value of the endSessionResult property.
     *
     * @param value
     *     allowed object is
     *     {@link ResponseType.EndSessionResult }
     *
     */
    public void setEndSessionResult(ResponseType.EndSessionResult value) {
        this.endSessionResult = value;
    }

    /**
     * Gets the value of the dumpResult property.
     *
     * @return
     *     possible object is
     *     {@link DumpResponseType }
     *
     */
    public DumpResponseType getDumpResult() {
        return dumpResult;
    }

    /**
     * Sets the value of the dumpResult property.
     *
     * @param value
     *     allowed object is
     *     {@link DumpResponseType }
     *
     */
    public void setDumpResult(DumpResponseType value) {
        this.dumpResult = value;
    }

    /**
     * Gets the value of the validation property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getValidation() {
        return validation;
    }

    /**
     * Sets the value of the validation property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setValidation(String value) {
        this.validation = value;
    }


    /**
     * <p>Java class for anonymous complex type.
     *
     * <p>The following schema fragment specifies the expected content contained within this class.
     *
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     *
     *
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class EndSessionResult {


    }


    /**
     * <p>Java class for anonymous complex type.
     *
     * <p>The following schema fragment specifies the expected content contained within this class.
     *
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     *
     *
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class PublishReceived {


    }


    /**
     * <p>Java class for anonymous complex type.
     *
     * <p>The following schema fragment specifies the expected content contained within this class.
     *
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     *
     *
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class PurgePublisherReceived {


    }


    /**
     * <p>Java class for anonymous complex type.
     *
     * <p>The following schema fragment specifies the expected content contained within this class.
     *
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     *
     *
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class RenewSessionResult {


    }


    /**
     * <p>Java class for anonymous complex type.
     *
     * <p>The following schema fragment specifies the expected content contained within this class.
     *
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     *
     *
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class SubscribeReceived {


    }

}
