//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Any modifications to this file will be lost upon recompilation of the source schema.
// Generated on: 2010.08.12 at 05:23:43 PM CEST
//


package org.w3._2003._05.soap_envelope;

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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 *
 * 	    Fault reporting structure
 *
 *
 * <p>Java class for Fault complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="Fault">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Code" type="{http://www.w3.org/2003/05/soap-envelope}faultcode"/>
 *         &lt;element name="Reason" type="{http://www.w3.org/2003/05/soap-envelope}faultreason"/>
 *         &lt;element name="Node" type="{http://www.w3.org/2001/XMLSchema}anyURI" minOccurs="0"/>
 *         &lt;element name="Role" type="{http://www.w3.org/2001/XMLSchema}anyURI" minOccurs="0"/>
 *         &lt;element name="Detail" type="{http://www.w3.org/2003/05/soap-envelope}detail" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Fault", propOrder = {
    "code",
    "reason",
    "node",
    "role",
    "detail"
})
public class Fault {

    @XmlElement(name = "Code", required = true)
    protected Faultcode code;
    @XmlElement(name = "Reason", required = true)
    protected Faultreason reason;
    @XmlElement(name = "Node")
    @XmlSchemaType(name = "anyURI")
    protected String node;
    @XmlElement(name = "Role")
    @XmlSchemaType(name = "anyURI")
    protected String role;
    @XmlElement(name = "Detail")
    protected Detail detail;

    /**
     * Gets the value of the code property.
     *
     * @return
     *     possible object is
     *     {@link Faultcode }
     *
     */
    public Faultcode getCode() {
        return code;
    }

    /**
     * Sets the value of the code property.
     *
     * @param value
     *     allowed object is
     *     {@link Faultcode }
     *
     */
    public void setCode(Faultcode value) {
        this.code = value;
    }

    /**
     * Gets the value of the reason property.
     *
     * @return
     *     possible object is
     *     {@link Faultreason }
     *
     */
    public Faultreason getReason() {
        return reason;
    }

    /**
     * Sets the value of the reason property.
     *
     * @param value
     *     allowed object is
     *     {@link Faultreason }
     *
     */
    public void setReason(Faultreason value) {
        this.reason = value;
    }

    /**
     * Gets the value of the node property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getNode() {
        return node;
    }

    /**
     * Sets the value of the node property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setNode(String value) {
        this.node = value;
    }

    /**
     * Gets the value of the role property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getRole() {
        return role;
    }

    /**
     * Sets the value of the role property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setRole(String value) {
        this.role = value;
    }

    /**
     * Gets the value of the detail property.
     *
     * @return
     *     possible object is
     *     {@link Detail }
     *
     */
    public Detail getDetail() {
        return detail;
    }

    /**
     * Sets the value of the detail property.
     *
     * @param value
     *     allowed object is
     *     {@link Detail }
     *
     */
    public void setDetail(Detail value) {
        this.detail = value;
    }

}
