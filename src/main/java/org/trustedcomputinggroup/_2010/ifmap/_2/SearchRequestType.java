//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Any modifications to this file will be lost upon recompilation of the source schema.
// Generated on: 2010.08.12 at 05:23:43 PM CEST
//


package org.trustedcomputinggroup._2010.ifmap._2;

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
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for SearchRequestType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="SearchRequestType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.trustedcomputinggroup.org/2010/IFMAP/2}SearchType">
 *       &lt;attGroup ref="{http://www.trustedcomputinggroup.org/2010/IFMAP/2}validationAttributes"/>
 *       &lt;attGroup ref="{http://www.trustedcomputinggroup.org/2010/IFMAP/2}sessionAttributes"/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SearchRequestType")
public class SearchRequestType
    extends SearchType
{

    @XmlAttribute
    protected String validation;
    @XmlAttribute(name = "session-id", required = true)
    protected String sessionId;

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
     * Gets the value of the sessionId property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getSessionId() {
        return sessionId;
    }

    /**
     * Sets the value of the sessionId property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setSessionId(String value) {
        this.sessionId = value;
    }

}
