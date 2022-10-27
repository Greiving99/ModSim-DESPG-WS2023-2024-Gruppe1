/**
 * Copyright (C) 2021 despg.dev, Ralf Buschermöhle
 *
 * DESPG is made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * see LICENSE
 *
 */
package dev.despg.core;

public final class Time
{
	private static final long SECONDS_PER_MINUTE = 60;
	private static final long MINUTES_PER_HOUR = 60;
	private static final long HOURS_PER_DAY = 24;

	private static final long SECONDS_PER_HOUR = MINUTES_PER_HOUR * SECONDS_PER_MINUTE;
   private static final long SECONDS_PER_DAY = HOURS_PER_DAY * SECONDS_PER_HOUR;
	private static final long STEP_LENGTH_IN_SECONDS = 60;

	private Time()
	{

	}

	/**
	 * This method computes units of given time scales (all are related to minutes).
	 * @param result appending the partial string
	 * @param seconds to map
	 * @param appendix for time unit i.e., "d = days h = hours m = minutes"
	 * @param factor (e.g., an hour has 60 minutes)
	 * @return reduced steps (and indirectly the extended result string(builder))
	 */
	private static long stepsToPartialString(StringBuilder result, long seconds, String appendix, long factor)
	{
		long timeUnits = seconds / factor;
		if (timeUnits > 0)
		{
			if (result.length() > 0)
				result.append(":");
			result.append(timeUnits + appendix);
			seconds -= timeUnits * factor;
		}

		return seconds;
	}

	/**
	 * This method takes an int number of minutes and converts it into a String of
	 * days:hours:minutes.
	 *
	 * @param steps The steps are related to the time unit specified and converted to ...
	 * @return days:hours:minutes:seconds
	 */
	public static String stepsToString(int steps) throws SimulationException
	{
		if (steps < 0)
			throw new SimulationException("Parameter can't be negative");

		long stepsInSeconds = steps * STEP_LENGTH_IN_SECONDS;

		StringBuilder result = new StringBuilder();

		// days
		stepsInSeconds = stepsToPartialString(result, stepsInSeconds, "d", SECONDS_PER_DAY);
		// hours
		stepsInSeconds = stepsToPartialString(result, stepsInSeconds, "h", SECONDS_PER_HOUR);
		// minutes
		stepsInSeconds = stepsToPartialString(result, stepsInSeconds, "m", SECONDS_PER_MINUTE);

		return result.toString();
	}

	public static int convertStandardTimeUnitToSteps(double value, int number)
	{
			return (int) (value * number);
	}
}
