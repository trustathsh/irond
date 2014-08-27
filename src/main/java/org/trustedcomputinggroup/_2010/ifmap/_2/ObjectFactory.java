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


import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each
 * Java content interface and Java element interface
 * generated in the org.trustedcomputinggroup._2010.ifmap._2 package.
 * <p>An ObjectFactory allows you to programatically
 * construct new instances of the Java representation
 * for XML content. The Java representation of XML
 * content can consist of schema derived interfaces
 * and classes representing the binding of schema
 * type definitions, element declarations and model
 * groups.  Factory methods for each of these are
 * provided in this class.
 *
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _Dump_QNAME = new QName("http://www.trustedcomputinggroup.org/2010/IFMAP/2", "dump");
    private final static QName _PurgePublisher_QNAME = new QName("http://www.trustedcomputinggroup.org/2010/IFMAP/2", "purgePublisher");
    private final static QName _Search_QNAME = new QName("http://www.trustedcomputinggroup.org/2010/IFMAP/2", "search");
    private final static QName _Subscribe_QNAME = new QName("http://www.trustedcomputinggroup.org/2010/IFMAP/2", "subscribe");
    private final static QName _EndSession_QNAME = new QName("http://www.trustedcomputinggroup.org/2010/IFMAP/2", "endSession");
    private final static QName _Response_QNAME = new QName("http://www.trustedcomputinggroup.org/2010/IFMAP/2", "response");
    private final static QName _Poll_QNAME = new QName("http://www.trustedcomputinggroup.org/2010/IFMAP/2", "poll");
    private final static QName _RenewSession_QNAME = new QName("http://www.trustedcomputinggroup.org/2010/IFMAP/2", "renewSession");
    private final static QName _Publish_QNAME = new QName("http://www.trustedcomputinggroup.org/2010/IFMAP/2", "publish");
    private final static QName _NewSession_QNAME = new QName("http://www.trustedcomputinggroup.org/2010/IFMAP/2", "newSession");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.trustedcomputinggroup._2010.ifmap._2
     *
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link MACAddressType }
     *
     */
    public MACAddressType createMACAddressType() {
        return new MACAddressType();
    }

    /**
     * Create an instance of {@link PollResultType }
     *
     */
    public PollResultType createPollResultType() {
        return new PollResultType();
    }

    /**
     * Create an instance of {@link UpdateType }
     *
     */
    public UpdateType createUpdateType() {
        return new UpdateType();
    }

    /**
     * Create an instance of {@link PurgePublisherRequestType }
     *
     */
    public PurgePublisherRequestType createPurgePublisherRequestType() {
        return new PurgePublisherRequestType();
    }

    /**
     * Create an instance of {@link SessionResultType }
     *
     */
    public SessionResultType createSessionResultType() {
        return new SessionResultType();
    }

    /**
     * Create an instance of {@link UpdateResultType }
     *
     */
    public UpdateResultType createUpdateResultType() {
        return new UpdateResultType();
    }

    /**
     * Create an instance of {@link NewSessionResultType }
     *
     */
    public NewSessionResultType createNewSessionResultType() {
        return new NewSessionResultType();
    }

    /**
     * Create an instance of {@link ResponseType }
     *
     */
    public ResponseType createResponseType() {
        return new ResponseType();
    }

    /**
     * Create an instance of {@link DeleteSearchRequestType }
     *
     */
    public DeleteSearchRequestType createDeleteSearchRequestType() {
        return new DeleteSearchRequestType();
    }

    /**
     * Create an instance of {@link RenewSessionType }
     *
     */
    public RenewSessionType createRenewSessionType() {
        return new RenewSessionType();
    }

    /**
     * Create an instance of {@link AccessRequestType }
     *
     */
    public AccessRequestType createAccessRequestType() {
        return new AccessRequestType();
    }

    /**
     * Create an instance of {@link ResponseType.RenewSessionResult }
     *
     */
    public ResponseType.RenewSessionResult createResponseTypeRenewSessionResult() {
        return new ResponseType.RenewSessionResult();
    }

    /**
     * Create an instance of {@link ResponseType.SubscribeReceived }
     *
     */
    public ResponseType.SubscribeReceived createResponseTypeSubscribeReceived() {
        return new ResponseType.SubscribeReceived();
    }

    /**
     * Create an instance of {@link SubscribeRequestType.Update }
     *
     */
    public SubscribeRequestType.Update createSubscribeRequestTypeUpdate() {
        return new SubscribeRequestType.Update();
    }

    /**
     * Create an instance of {@link DumpRequestType }
     *
     */
    public DumpRequestType createDumpRequestType() {
        return new DumpRequestType();
    }

    /**
     * Create an instance of {@link DeleteType }
     *
     */
    public DeleteType createDeleteType() {
        return new DeleteType();
    }

    /**
     * Create an instance of {@link DeviceType }
     *
     */
    public DeviceType createDeviceType() {
        return new DeviceType();
    }

    /**
     * Create an instance of {@link ErrorResultType }
     *
     */
    public ErrorResultType createErrorResultType() {
        return new ErrorResultType();
    }

    /**
     * Create an instance of {@link ResponseType.EndSessionResult }
     *
     */
    public ResponseType.EndSessionResult createResponseTypeEndSessionResult() {
        return new ResponseType.EndSessionResult();
    }

    /**
     * Create an instance of {@link ResultItemType }
     *
     */
    public ResultItemType createResultItemType() {
        return new ResultItemType();
    }

    /**
     * Create an instance of {@link PollRequestType }
     *
     */
    public PollRequestType createPollRequestType() {
        return new PollRequestType();
    }

    /**
     * Create an instance of {@link SessionRequestType }
     *
     */
    public SessionRequestType createSessionRequestType() {
        return new SessionRequestType();
    }

    /**
     * Create an instance of {@link ResponseType.PurgePublisherReceived }
     *
     */
    public ResponseType.PurgePublisherReceived createResponseTypePurgePublisherReceived() {
        return new ResponseType.PurgePublisherReceived();
    }

    /**
     * Create an instance of {@link SearchRequestType }
     *
     */
    public SearchRequestType createSearchRequestType() {
        return new SearchRequestType();
    }

    /**
     * Create an instance of {@link IPAddressType }
     *
     */
    public IPAddressType createIPAddressType() {
        return new IPAddressType();
    }

    /**
     * Create an instance of {@link DeleteResultType }
     *
     */
    public DeleteResultType createDeleteResultType() {
        return new DeleteResultType();
    }

    /**
     * Create an instance of {@link ResponseType.PublishReceived }
     *
     */
    public ResponseType.PublishReceived createResponseTypePublishReceived() {
        return new ResponseType.PublishReceived();
    }

    /**
     * Create an instance of {@link SearchResultType }
     *
     */
    public SearchResultType createSearchResultType() {
        return new SearchResultType();
    }

    /**
     * Create an instance of {@link IdentityType }
     *
     */
    public IdentityType createIdentityType() {
        return new IdentityType();
    }

    /**
     * Create an instance of {@link PublishRequestType }
     *
     */
    public PublishRequestType createPublishRequestType() {
        return new PublishRequestType();
    }

    /**
     * Create an instance of {@link NotifyType }
     *
     */
    public NotifyType createNotifyType() {
        return new NotifyType();
    }

    /**
     * Create an instance of {@link NewSessionRequestType }
     *
     */
    public NewSessionRequestType createNewSessionRequestType() {
        return new NewSessionRequestType();
    }

    /**
     * Create an instance of {@link SearchType }
     *
     */
    public SearchType createSearchType() {
        return new SearchType();
    }

    /**
     * Create an instance of {@link MetadataListType }
     *
     */
    public MetadataListType createMetadataListType() {
        return new MetadataListType();
    }

    /**
     * Create an instance of {@link SubscribeRequestType }
     *
     */
    public SubscribeRequestType createSubscribeRequestType() {
        return new SubscribeRequestType();
    }

    /**
     * Create an instance of {@link EndSessionType }
     *
     */
    public EndSessionType createEndSessionType() {
        return new EndSessionType();
    }

    /**
     * Create an instance of {@link DumpResponseType }
     *
     */
    public DumpResponseType createDumpResponseType() {
        return new DumpResponseType();
    }

    /**
     * Create an instance of {@link AttachSession }
     *
     */
    public AttachSession createAttachSession() {
        return new AttachSession();
    }

    /**
     * Create an instance of {@link NotifyResultType }
     *
     */
    public NotifyResultType createNotifyResultType() {
        return new NotifyResultType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DumpRequestType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.trustedcomputinggroup.org/2010/IFMAP/2", name = "dump")
    public JAXBElement<DumpRequestType> createDump(DumpRequestType value) {
        return new JAXBElement<DumpRequestType>(_Dump_QNAME, DumpRequestType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PurgePublisherRequestType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.trustedcomputinggroup.org/2010/IFMAP/2", name = "purgePublisher")
    public JAXBElement<PurgePublisherRequestType> createPurgePublisher(PurgePublisherRequestType value) {
        return new JAXBElement<PurgePublisherRequestType>(_PurgePublisher_QNAME, PurgePublisherRequestType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SearchRequestType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.trustedcomputinggroup.org/2010/IFMAP/2", name = "search")
    public JAXBElement<SearchRequestType> createSearch(SearchRequestType value) {
        return new JAXBElement<SearchRequestType>(_Search_QNAME, SearchRequestType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SubscribeRequestType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.trustedcomputinggroup.org/2010/IFMAP/2", name = "subscribe")
    public JAXBElement<SubscribeRequestType> createSubscribe(SubscribeRequestType value) {
        return new JAXBElement<SubscribeRequestType>(_Subscribe_QNAME, SubscribeRequestType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EndSessionType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.trustedcomputinggroup.org/2010/IFMAP/2", name = "endSession")
    public JAXBElement<EndSessionType> createEndSession(EndSessionType value) {
        return new JAXBElement<EndSessionType>(_EndSession_QNAME, EndSessionType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ResponseType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.trustedcomputinggroup.org/2010/IFMAP/2", name = "response")
    public JAXBElement<ResponseType> createResponse(ResponseType value) {
        return new JAXBElement<ResponseType>(_Response_QNAME, ResponseType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PollRequestType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.trustedcomputinggroup.org/2010/IFMAP/2", name = "poll")
    public JAXBElement<PollRequestType> createPoll(PollRequestType value) {
        return new JAXBElement<PollRequestType>(_Poll_QNAME, PollRequestType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RenewSessionType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.trustedcomputinggroup.org/2010/IFMAP/2", name = "renewSession")
    public JAXBElement<RenewSessionType> createRenewSession(RenewSessionType value) {
        return new JAXBElement<RenewSessionType>(_RenewSession_QNAME, RenewSessionType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PublishRequestType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.trustedcomputinggroup.org/2010/IFMAP/2", name = "publish")
    public JAXBElement<PublishRequestType> createPublish(PublishRequestType value) {
        return new JAXBElement<PublishRequestType>(_Publish_QNAME, PublishRequestType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link NewSessionRequestType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.trustedcomputinggroup.org/2010/IFMAP/2", name = "newSession")
    public JAXBElement<NewSessionRequestType> createNewSession(NewSessionRequestType value) {
        return new JAXBElement<NewSessionRequestType>(_NewSession_QNAME, NewSessionRequestType.class, null, value);
    }

}
