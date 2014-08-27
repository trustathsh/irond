package de.fhhannover.inform.iron.mapserver.contentauth;

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

import java.util.List;
import java.util.Map;
import java.util.Set;

import de.fhhannover.inform.iron.mapserver.utils.CollectionHelper;
import de.fhhannover.inform.iron.mapserver.utils.NullCheck;


/**
 * Interface for a {@link IfmapPepHandler}.
 *
 * @author aw
 */
public interface IfmapPepHandler {

	public enum PdpType {
		permit,
		deny,
		local,
		remote
	}

	public enum IfmapOp {
		update,
		delete,
		notify,
		search,
		subscribe,
		purgePublisher,
	}

	public class IfmapDecisionRequest {

		private final IfmapOp mOp;

		private final String mIdentType;
		private final Map<String, String> mIdentAttrs;
		private final String mMetadataType;
		private final Map<String, String> mMetadataAttrs;

		private final boolean mOnLink;
		private final boolean mClobber;
		private final boolean mDryRun;
		private final boolean mIsClientIdent;
		private final boolean mIsSelfIdent;

		private final List<String> mRoles;

		private final int mHashCode;

		public IfmapDecisionRequest(IfmapOp op,
				String identType, Map<String, String> identAttrs,
				String metaType, Map<String, String> metaAttrs,
				boolean onlink, boolean clobber, boolean dryrun,
				boolean clientIdentfier, boolean selfIdentifier,
				List<String> roles) {

			NullCheck.check(identAttrs, "identAttrs is null");
			NullCheck.check(metaAttrs, "metaAttrs is null");
			mOp = op;
			mIdentType = identType;
			mMetadataType = metaType;
			mIdentAttrs = identAttrs;
			mMetadataAttrs = metaAttrs;

			mOnLink = onlink;
			mClobber = clobber;
			mDryRun = dryrun;
			mIsClientIdent = clientIdentfier;
			mIsSelfIdent = selfIdentifier;

			mRoles = roles;

			mHashCode = prepareHashCode();
		}

		private int prepareHashCode() {
			int ret = mOp.hashCode() +
						(mIdentType != null ? mIdentType.hashCode() : 0) +
						(mMetadataType != null ? mMetadataType.hashCode() : 0) +
						(mClobber ? 1049 : 1051) +
						(mOnLink ? 1061: 1036) +
						(mDryRun ? 1087 : 1091) +
						(mIsClientIdent ? 1093 : 1097) +
						(mIsSelfIdent ? 1103 : 1109);

			for (String role : getRoles())
				ret += role.hashCode();

			for (Map.Entry<String, String> entry : mIdentAttrs.entrySet())
				ret += (entry.getKey() + entry.getValue()).hashCode();

			for (Map.Entry<String, String> entry : mMetadataAttrs.entrySet())
				ret += (entry.getKey() + entry.getValue()).hashCode();

			return ret;
		}

		public final String toString() {
			return "dreq{roles=" + mRoles
					+ " op=" + mOp
					+ " ident=" + mIdentType
					+ " (with " + mIdentAttrs.size() + " attrs)"
					+ " md=" + mMetadataType
					+ " (with " + mMetadataAttrs.size() + " attrs)"
					+ " link=" + mOnLink
					+ " clid=" + mIsClientIdent
					+ " self=" + mIsSelfIdent
					+ " clobber=" + mClobber
					+ " dryrun=" + mDryRun
					+ "}";
		}

		public final IfmapOp getOp() {
			return mOp;
		}

		public final String getIdentifierType() {
			return mIdentType;
		}

		public final String getMetadataType() {
			return mMetadataType;
		}

		public final boolean isClobber() {
			return mClobber;
		}

		public final boolean isOnLink() {
			return mOnLink;
		}

		public final boolean isDryRun() {
			return mDryRun;
		}

		public final boolean isClientIdent() {
			return mIsClientIdent;
		}

		public final boolean isSelfIdent() {
			return mIsSelfIdent;
		}

		public final List<String> getRoles() {
			return mRoles;
		}

		public final Map<String, String> getMetadataAttrs() {
			return mMetadataAttrs;
		}

		public final Map<String, String> getIdentAttrs() {
			return mIdentAttrs;
		}

		@Override
		public boolean equals(Object o) {

			IfmapDecisionRequest ot = null;

			if (o == null)
				return false;

			if (!(o instanceof IfmapDecisionRequest))
				return false;

			ot = (IfmapDecisionRequest)o;

			// Two IF-MAP decision requests are equal if:
			//
			// 1) the operation is the same
			// 2) the clobber attribute is the same
			// 3) the metadata type is the same
			// 4) the identifier type is the same
			// 5) the roles are the same

			if (ot.getOp() != getOp())
				return false;

			if (ot.isClobber() != isClobber())
				return false;

			if (ot.isOnLink() != isOnLink())
				return false;

			if (ot.isDryRun() != isDryRun())
				return false;

			if (ot.getMetadataType() == null && getMetadataType() != null)
				return false;

			if (getMetadataType() == null && ot.getMetadataType() != null)
				return false;

			if (getMetadataType() != null && ot.getMetadataType() != null)
				if (!getMetadataType().equals(ot.getMetadataType()))
					return false;

			if (ot.getIdentifierType() == null && getIdentifierType() != null)
				return false;

			if (getIdentifierType() == null && ot.getIdentifierType() != null)
				return false;

			if (getIdentifierType() != null && ot.getIdentifierType() != null)
				if (!getIdentifierType().equals(ot.getIdentifierType()))
					return false;

			Set<String> roles1 = CollectionHelper.provideSetFor(String.class);
			Set<String> roles2 = CollectionHelper.provideSetFor(String.class);
			roles1.addAll(getRoles());
			roles2.addAll(ot.getRoles());

			if (!roles1.equals(roles2))
				return false;

			if (!getIdentAttrs().equals(ot.getIdentAttrs()))
				return false;

			if (!getMetadataAttrs().equals(ot.getMetadataAttrs()))
				return false;

			return true;
		}

		@Override
		public int hashCode() {
			return mHashCode;
		}

	}

	/**
	 * Needs to be thread safe.
	 *
	 * @param dreq
	 * @return
	 */
	boolean isAuthorized(IfmapDecisionRequest dreq);
}
