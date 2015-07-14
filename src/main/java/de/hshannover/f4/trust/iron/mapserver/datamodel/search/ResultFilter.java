package de.hshannover.f4.trust.iron.mapserver.datamodel.search;

import java.util.Map;

import de.hshannover.f4.trust.iron.mapserver.datamodel.meta.Metadata;

public class ResultFilter extends Filter {

	public ResultFilter(String fs, Map<String, String> hu) {
		super(fs, hu);
	}

	@Override
	public boolean isMatchNothing() {
		return mFilterString == null;
	}

	@Override
	public boolean isMatchEverything() {
		return mFilterString != null && mFilterString.length() == 0;
	}
	
}
