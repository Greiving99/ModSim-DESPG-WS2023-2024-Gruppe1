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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public final class Time
{
	private static final long MILLISECONDS_PER_SECOND = 1000;
	private static final long SECONDS_PER_MINUTE = 60;
	private static final long MINUTES_PER_HOUR = 60;
	private static final long HOURS_PER_DAY = 24;
	private static final long DAYS_PER_YEAR = 365;

	private static final long MILLISECONDS_PER_MINUTE = SECONDS_PER_MINUTE * MILLISECONDS_PER_SECOND;
	private static final long MILLISECONDS_PER_HOUR = MINUTES_PER_HOUR * MILLISECONDS_PER_MINUTE;
    private static final long MILLISECONDS_PER_DAY = HOURS_PER_DAY * MILLISECONDS_PER_HOUR;
    private static final long MILLISECONDS_PER_YEAR = MILLISECONDS_PER_DAY * DAYS_PER_YEAR;

	private static final long STEP_LENGTH_IN_MILLISECONDS = 60000L;	// = 1 minute

	private static final SimpleDateFormat DATEFORMAT = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
	private static final String STARTDATESTRING = "01-01-2023 08:00:00";

	private static Date startDateOfSimulation;


	static
	{
		try
		{
			startDateOfSimulation = new Date(DATEFORMAT.parse(STARTDATESTRING).getTime());
		}
		catch (ParseException e)
		{
			e.printStackTrace();
		}
	}


	private Time()
	{

	}


	private static long convertStepsToMilliseconds(long steps)
	{
		return steps * STEP_LENGTH_IN_MILLISECONDS;
	}

	/**
	 * This method takes an number of steps and converts it into a defined Date String.
	 *
	 * @param steps The steps are related to the time unit specified and converted to ...
	 * @return Date according to Date String
	 */
	public static String stepsToTimeString(long steps) throws SimulationException
	{
		if (steps < 0)
			throw new SimulationException("Parameter can't be negative");

		long milliseconds = steps * STEP_LENGTH_IN_MILLISECONDS;


		long[] days = completeTimeUnits(milliseconds, MILLISECONDS_PER_DAY);
		long[] hours = completeTimeUnits(days[0], MILLISECONDS_PER_HOUR);
		long[] minutes = completeTimeUnits(hours[0], MILLISECONDS_PER_MINUTE);
		long[] seconds = completeTimeUnits(minutes[0], MILLISECONDS_PER_SECOND);

		return String.format("%02d days %02d:%02d:%02d.%d", days[1], hours[1], minutes[1], seconds[1], seconds[0]);
	}


	private static long[] completeTimeUnits(long milliseconds, long millisecondsOfTemporalUnit)
	{
		long temporalUnits = milliseconds / millisecondsOfTemporalUnit;
		milliseconds -= temporalUnits * millisecondsOfTemporalUnit;

		return new long[] {milliseconds, temporalUnits};
	}

	/**
	 * This method takes an number of steps and converts it into a defined Date String.
	 *
	 * @param steps The steps are related to the time unit specified and converted to ...
	 * @return Date according to Date String
	 */
	public static String stepsToDateString(long steps) throws SimulationException
	{
		if (steps < 0)
			throw new SimulationException("Parameter can't be negative");

		long stepsInSeconds = convertStepsToMilliseconds(steps);
		Date date = new Date(startDateOfSimulation.getTime() + stepsInSeconds);

		return DATEFORMAT.format(date);
	}

	private static Calendar setCalendar(long steps)
	{
		Calendar calendar = new GregorianCalendar();
		calendar.setTimeInMillis(convertStepsToMilliseconds(steps));
		return calendar;
	}

	public static int getHourOfDay(long steps)
	{
		return setCalendar(steps).get(Calendar.HOUR_OF_DAY);
	}

	/**
	 *
	 * @param steps
	 * @return 1 = Sunday, ... , 7 = Saturday
	 */
	public static int getDayOfWeek(long steps)
	{
		return setCalendar(steps).get(Calendar.DAY_OF_WEEK);
	}

	/**
	 *
	 * @param steps
	 * @return
	 */
	public static int getDayOfMonth(long steps)
	{
		return setCalendar(steps).get(Calendar.DAY_OF_MONTH);
	}

	/**
	 *
	 * @param steps
	 * @return 1 = Sunday, ... , 7 = Saturday
	 */
	public static int getMonthOfYear(long steps)
	{
		return setCalendar(steps).get(Calendar.MONTH);
	}

	/**
	 *
	 * @param steps
	 * @return
	 */
	public static int getYear(long steps)
	{
		return setCalendar(steps).get(Calendar.YEAR);
	}


	public static long stepsToDay(long steps, DayOfWeek dayOfWeek)
	{
		Date stepDate = new Date(steps * STEP_LENGTH_IN_MILLISECONDS);

		System.out.println(stepDate);

		LocalDateTime ld = stepDate.toInstant()
	      .atZone(ZoneId.systemDefault())
	      .toLocalDateTime();

		ld = ld.with(TemporalAdjusters.next(dayOfWeek));
		System.out.println(ld);
		
		long difference = ld.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() - stepDate.getTime();

		return difference / STEP_LENGTH_IN_MILLISECONDS;
	}


	public static long secondsToSteps(long seconds)
	{
		return (long) seconds *  MILLISECONDS_PER_SECOND / STEP_LENGTH_IN_MILLISECONDS;
	}

	public static long minutesToSteps(long minutes)
	{
		return (long) minutes * MILLISECONDS_PER_MINUTE / STEP_LENGTH_IN_MILLISECONDS;
	}

	public static long minutesToSteps(double minutes)
	{
		return (long) (minutes * MILLISECONDS_PER_MINUTE / STEP_LENGTH_IN_MILLISECONDS);
	}

	public static long hoursToSteps(long hours)
	{
		return (long) hours * MILLISECONDS_PER_HOUR / STEP_LENGTH_IN_MILLISECONDS;
	}

	public static long hoursToSteps(double hours)
	{
		return (long) (hours * MILLISECONDS_PER_HOUR / STEP_LENGTH_IN_MILLISECONDS);
	}

	public static long daysToSteps(long days)
	{
		return (long) days * MILLISECONDS_PER_DAY / STEP_LENGTH_IN_MILLISECONDS;
	}

	public static long daysToSteps(double days)
	{
		return (long) days * MILLISECONDS_PER_DAY / STEP_LENGTH_IN_MILLISECONDS;
	}

	public static long yearsToSteps(long years)
	{
		return (long) years * MILLISECONDS_PER_YEAR / STEP_LENGTH_IN_MILLISECONDS;
	}

	public static long yearsToSteps(double years)
	{
		return (long) years * MILLISECONDS_PER_YEAR / STEP_LENGTH_IN_MILLISECONDS;
	}

}
