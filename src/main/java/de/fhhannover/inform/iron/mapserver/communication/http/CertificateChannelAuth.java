package de.fhhannover.inform.iron.mapserver.communication.http;

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

import java.net.Socket;

import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSocket;
import javax.security.auth.x500.X500Principal;
import javax.security.cert.CertificateEncodingException;
import javax.security.cert.X509Certificate;

import org.apache.http.HttpRequest;

import de.fhhannover.inform.iron.mapserver.communication.ClientIdentifier;
import de.fhhannover.inform.iron.mapserver.exceptions.ChannelAuthException;
import de.fhhannover.inform.iron.mapserver.utils.Digests;

/**
 * Implementation for the certificate-based authentication.
 * 
 * @author tr
 *
 */
public class CertificateChannelAuth extends ChannelAuth {

	private ClientIdentifier mClientId;

	public CertificateChannelAuth(Socket socket) {
		super(socket);
	}
	
	@Override
	public void authenticate(HttpRequest request) throws ChannelAuthException {	
		// Extract the DN name only on the first call of this method.
		// Further calls are simply nops.
		if (mClientId == null) {
			X509Certificate[] x509;
			String subject = null;
			String issuer = null;
			String fp = null;
			try {
				x509 = ((SSLSocket) getSocket()).getSession().getPeerCertificateChain();
				subject = x509[x509.length - 1].getSubjectDN().getName();
				issuer = x509[0].getIssuerDN().getName();
				fp = Digests.sha1(x509[0].getEncoded());
			} catch (CertificateEncodingException e) {
				throw new ChannelAuthException("SSL Cert encoding error!");
			} catch (SSLPeerUnverifiedException e) {
				throw new ChannelAuthException("SSL verification failed!");
			}
			
			mClientId = new ClientIdentifier(getDottedCommonName(subject),
					new X500Principal(subject).getName(),
					new X500Principal(issuer).getName(),
					fp);
		}
	}

	@Override
	public ClientIdentifier getClientIdentifier() {		
		return mClientId;
	}
	

	private static final String CN_ATTR = "CN=";
	
	/**
	 * From the issuer name, extract the Common name
	 * 
	 * @param dn
	 * @return
	 */
	private String getDottedCommonName(String dn) {
		StringBuilder ret = new StringBuilder();
		
		int cnIdx = dn.indexOf(CN_ATTR);
		
		if (cnIdx > -1) {
			cnIdx += CN_ATTR.length();
			String cn = dn.substring(cnIdx);
			ret.append(cutOffAtFirstComma(cn));
		}
		
		if (ret.length() == 0)
			ret.append("CommonName");
	
		// return result, replace spaces with underscores
		return ret.toString().replace(' ', '_');
	}

	private String cutOffAtFirstComma(String str) {
		int commaIdx = str.indexOf(',');
		if (commaIdx > -1) {
			return str.substring(0, commaIdx);
		}
		return str;
	}
}
