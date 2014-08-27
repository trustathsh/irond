package de.fhhannover.inform.iron.mapserver.utils;

import java.util.Date;

public class TimestampFraction {

	/**
	 * Return the decimal fraction of a second of the given {@link Date}.
	 *
	 * @param dt the {@link Date} from with to extract the fraction
	 * @return a double containing the decimal fraction of a second
	 */
	public static double getSecondFraction(Date dt) {
		return dt.getTime() % 1000 / 1000.0;
	}
}
