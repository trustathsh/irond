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
 * This file is part of irond, version 0.5.5, implemented by the Trust@HsH
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
package de.hshannover.f4.trust.iron.mapserver.contentauth;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import oasis.names.tc.xacml._2_0.context.schema.os.RequestType;
import oasis.names.tc.xacml._2_0.context.schema.os.ResponseType;

import com.sun.xacml.PDP;
import com.sun.xacml.PDPConfig;
import com.sun.xacml.ctx.ResponseCtx;
import com.sun.xacml.ctx.Result;
import com.sun.xacml.finder.AttributeFinder;
import com.sun.xacml.finder.PolicyFinder;
import com.sun.xacml.finder.impl.CurrentEnvModule;
import com.sun.xacml.support.finder.FilePolicyModule;


class LocalSunXacml extends AbstractSunXacml {

	private final PDP mPdp;

	LocalSunXacml(String policyFile, boolean rawLog) {

		PolicyFinder pf = new PolicyFinder();
		FilePolicyModule fpm = new FilePolicyModule();
		fpm.addPolicy(policyFile);
		Set<Object> policyModules = new HashSet<Object>();
		policyModules.add(fpm);
		pf.setModules(policyModules);


		AttributeFinder af = new AttributeFinder();
		List<Object> attrModules = new ArrayList<Object>();
		attrModules.add(new CurrentEnvModule());
		af.setModules(attrModules);

		// FIXME: Something here is supposed to throw an exception!!!
		PDPConfig cfg = new PDPConfig(af, pf, null);
		mPdp = new PDP(cfg);
	}

	@Override
	public Set<Result> doRequestHook(RequestType reqType) {
		ResponseCtx ctx = mPdp.evaluate(reqType);
		ResponseType rt = SunXacmlGlue.results2ResponseType(ctx.getResults());
		return SunXacmlGlue.responseType2Results(rt);
	}
}
