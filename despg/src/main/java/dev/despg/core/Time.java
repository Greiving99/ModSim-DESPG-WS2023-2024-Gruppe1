package dev.despg.core;

public class Time
{
	private static final int MINUTES_PER_HOUR = 60;
	private static final int HOURS_PER_DAY = 24;
	private static final int MINUTES_PER_DAY = HOURS_PER_DAY * MINUTES_PER_HOUR;

	/**
	 * This method takes an int number of minutes and converts it into a String of
	 * days:hours:minutes.
	 * 
	 * @param minutes The value of minutes that will be converted
	 * @return minutes converted to days:hours:minutes
	 */
	public static String stepsToString(int minutes) throws SimulationException
	{
		if (minutes < 0)
			throw new SimulationException("Parameter can't be negative");
		StringBuilder result = new StringBuilder();
		int days = minutes / MINUTES_PER_DAY;
		if (days > 0)
		{
			result.append(days + "d");
			minutes -= days * MINUTES_PER_DAY;
		}

		int hours = minutes / MINUTES_PER_HOUR;
		if (hours > 0)
		{
			if (result.length() > 0)
				result.append(":");

			result.append(hours + "h");
			minutes -= hours * MINUTES_PER_HOUR;
		}

		if (result.length() == 0 || minutes > 0)
		{
			if (result.length() > 0)
				result.append(":");

			result.append(minutes + "m");
		}

		return result.toString();
	}
}
