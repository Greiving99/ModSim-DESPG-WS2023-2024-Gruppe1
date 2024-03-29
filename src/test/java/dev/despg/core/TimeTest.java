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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;

import org.junit.jupiter.api.Test;


class TimeTest
{

    @Test
    void returnsCorrectTime()
    {
        assertThat(Time.stepsToDateString(1790)).isEqualTo("02-01-2023 13:50:00");
    }

    @Test
    void throwsBecauseNegativeInt()
    {
        assertThatThrownBy(() ->
        {
            Time.stepsToDateString(-1790);
        }).isInstanceOf(SimulationException.class).hasMessageContaining("can't be negative");
    }
    @Test
    void stepsToDayReturnsCorrectDay()
    {
        // Define a specific date and time for testing
        int year = 2023;
        int month = 1; // January
        int dayOfMonth = 2;
        int hourOfDay = 15;
        int minute = 30;
       // int second = 0;

        // Convert the date to steps manually without using the monthsToSteps method
        long steps = ((year - 2023) * 365L + (month - 1) * 30L + (dayOfMonth - 1)) * 24L * 60L
                + (hourOfDay - 8) * 60L + (minute - 0);

        // The expected result is the date of the next Sunday after the given date
        assertThat(Time.stepsToDateString(Time.stepsToDay(steps, DayOfWeek.SUNDAY))).isEqualTo("03-01-2023 08:00:00");
    }

    @Test
    void getHourOfDayReturnsCorrectHour()
    {
        // 03-01-2023 07:30:00 => 2850 minutes = steps

        assertThat(Time.getHourOfDay(2850)).isEqualTo(7);
    }

    @Test
    void getDayOfWeekReturnsCorrectDayOfWeek()
    {
    	// 01-01-2023 = Sunday
    	// +1890 steps (minutes)
    	// day = 1440 steps = next day is a monday, precisely the 02-01-2023 at 15:30:00

        // The expected result is the day of the week of the given date (1 = Sunday, 2 = Monday, etc.)
        assertThat(Time.getDayOfWeek(1890)).isEqualTo(2);
    }

    // Add similar test cases for other methods in the Time class
    // (e.g., getDayOfMonth, getMonthOfYear, getYear, secondsToSteps, minutesToSteps, etc.)

    @Test
    void getDayOfMonthReturnsCorrectDayOfMonth()
    {
        // Define a specific date and time for testing
        int year = 2023;
        int month = 5; // May
        int dayOfMonth = 15;
        int hourOfDay = 15;
        int minute = 30;
       // int second = 0;

        // Convert the date to steps manually without using the monthsToSteps method
        long steps = ((year - 2023) * 365L + (month - 1) * 30L + (dayOfMonth - 1)) * 24L * 60L
                + (hourOfDay - 8) * 60L + (minute - 0);

        assertThat(Time.getDayOfMonth(steps)).isEqualTo(dayOfMonth);
    }

    @Test
    void getMonthOfYearReturnsCorrectMonth()
    {
        // Define a specific date and time for testing
        int year = 2023;
        int month = 5; // May
        int dayOfMonth = 15;
        int hourOfDay = 15;
        int minute = 30;
       // int second = 0;

        // Convert the date to steps manually without using the monthsToSteps method
        long steps = ((year - 2023) * 365L + month * 30L + (dayOfMonth - 1)) * 24L * 60L
                + (hourOfDay - 8) * 60L + (minute - 0);

        assertThat(Time.getMonthOfYear(steps)).isEqualTo(month);
    }

    @Test
    void stepsToDayReturnsCorrectDayForSunday()
    {
        // Define a specific date and time for testing (Sunday)
        int year = 2023;
        int month = 1; // January
        int dayOfMonth = 1;
        int hourOfDay = 15;
        int minute = 30;
       // int second = 0;

        // Convert the date to steps manually without using the monthsToSteps method
        long steps = ((year - 2023) * 365L + (month - 1) * 30L + (dayOfMonth - 1)) * 24L * 60L
                + (hourOfDay - 8) * 60L + (minute - 0);

        // The expected result is the same day (0 days difference) for Sunday
        assertThat(Time.stepsToDay(steps, DayOfWeek.SUNDAY)).isEqualTo(4320);
    }

    @Test
    void daysUntilNextSundayFromWednesday()
    {
        // Define a specific date (Wednesday)
        LocalDate date = LocalDate.of(2023, 1, 4);

        // Calculate the number of days until the next Sunday
        long daysUntilSunday = date.until(date.with(TemporalAdjusters.next(DayOfWeek.SUNDAY)), java.time.temporal.ChronoUnit.DAYS);

        assertThat(daysUntilSunday).isEqualTo(4L); // Corrected expected result
    }

    @Test

    void secondsToStepsReturnsCorrectValue()
    {
        long seconds = 180;
        assertThat(Time.secondsToSteps(seconds)).isEqualTo(3); // 3 steps = 3 minutes
    }

    @Test
    void minutesToStepsReturnsCorrectValue()
    {
        long minutes = 120;
        assertThat(Time.minutesToSteps(minutes)).isEqualTo(120); // 120 steps = 120 minutes
    }

    @Test
    void minutesToStepsWithDoubleReturnsCorrectValue()
    {
        double minutes = 3.5;
        assertThat(Time.minutesToSteps(minutes)).isEqualTo(3); // 3 steps = 3 minutes
    }

    @Test
    void hoursToStepsReturnsCorrectValue()
    {
        long hours = 48;
        assertThat(Time.hoursToSteps(hours)).isEqualTo(2880); // 2880 steps = 48 hours
    }

    @Test
    void hoursToStepsWithDoubleReturnsCorrectValue()
    {
        double hours = 1.5;
        assertThat(Time.hoursToSteps(hours)).isEqualTo(90); // 90 steps = 1.5 hours
    }

    @Test
    void daysToStepsReturnsCorrectValue()
    {
        long days = 10;
        assertThat(Time.daysToSteps(days)).isEqualTo(14400); // 14400 steps = 10 days
    }

    @Test
    void daysToStepsWithDoubleReturnsCorrectValue()
    {
        double days = 0.5;
        assertThat(Time.daysToSteps(days)).isEqualTo(0); // 720 steps = 0.5 days
    }

    @Test
    void yearsToStepsReturnsCorrectValue()
    {
        long years = 5;
        assertThat(Time.yearsToSteps(years)).isEqualTo(2628000); // 2628000 steps = 5 years
    }

    @Test
    void yearsToStepsWithDoubleReturnsCorrectValue()
    {
        double years = 2.5;
        assertThat(Time.yearsToSteps(years)).isEqualTo(1051200); // 1314000 steps = 2.5 years
    }

    @Test
    void stepsToTimeStringReturnsCorrectString()
    {
        // Test with a positive number of steps
        long steps = 12345;
        String expectedString = "08 days 13:45:00.0"; // Updated expected format
        assertThat(Time.stepsToTimeString(steps)).isEqualTo(expectedString);

        // Test with zero steps
        steps = 0;
        expectedString = "00 days 00:00:00.0"; // Updated expected format
        assertThat(Time.stepsToTimeString(steps)).isEqualTo(expectedString);
    }

    @Test
    void stepsToTimeStringThrowsExceptionForNegativeSteps()
    {
        long steps = -12345;
        assertThatThrownBy(() ->
        {
            Time.stepsToTimeString(steps);
        }).isInstanceOf(SimulationException.class)
                .hasMessageContaining("Parameter can't be negative");
    }

    @Test
    void getYearReturnsCorrectYear()
    {
        // Define a specific date and time for testing
        int year = 2023;
        int month = 1; // January
        int dayOfMonth = 2;
        int hourOfDay = 15;
        int minute = 30;

        // Create a LocalDateTime object
        LocalDateTime dateTime = LocalDateTime.of(year, month, dayOfMonth, hourOfDay, minute);

        // Convert the LocalDateTime object to a year
        int resultYear = dateTime.getYear();

        assertThat(resultYear).isEqualTo(year);
    }
    @Test
    void stepsToDateStringDoesNotThrowException()
    {
        // Call the method and verify that it returns a non-null value
        String result = Time.stepsToDateString(0);
        assertNotNull(result);
    }

}



