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
	private static final int MINUTES_PER_HOUR = 60;
	private static final int HOURS_PER_DAY = 24;
	private static final int MINUTES_PER_DAY = HOURS_PER_DAY * MINUTES_PER_HOUR;

	private Time()
	{

	}

	/**
	 * This method computes units of given time scales (all are related to minutes).
	 * @param result appending the partial string
	 * @param steps to map
	 * @param appendix for time unit i.e., "d = days h = hours m = minutes"
	 * @param factor (e.g., an hour has 60 minutes)
	 * @return reduced steps (and indirectly the extended result string(builder))
	 */
	private static int stepsToPartialString(StringBuilder result, int steps, String appendix, int factor)
	{
		int timeUnits = steps / factor;
		if (timeUnits > 0)
		{
			if (result.length() > 0)
				result.append(":");
			result.append(timeUnits + appendix);
			steps -= timeUnits * factor;
		}

		return steps;
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

		StringBuilder result = new StringBuilder();

		// days
		steps = stepsToPartialString(result, steps, "d", MINUTES_PER_DAY);
		// hours
		steps = stepsToPartialString(result, steps, "h", MINUTES_PER_HOUR);
		// minutes
		steps = stepsToPartialString(result, steps, "m", 1);

		return result.toString();
	}

	public static int convertStandardTimeUnitToSteps(double value, int number)
	{
			return (int) (value * number);
	}
}
