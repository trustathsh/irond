package de.fhhannover.inform.iron.mapserver.utils;

import java.util.Date;

public class TimestampFraction {

	/**
	 * Return the milliseconds fraction of the given {@link Date}.
	 *
	 * @param dt the {@link Date} from with to extract the milliseconds fraction
	 * @return a double containing the milliseconds fraction
	 */
	public static double getMilliseconds(Date dt) {
		return (dt.getTime() % 1000) / 1000.0;
	}
}
