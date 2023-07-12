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
import org.junit.jupiter.api.Test;

class TimeTest
{

	/**
	 * Checks if stepsToString returns the time formatted correctly.
	 */
	@Test
	void returnsCorrectTime()
	{
		String expectedString = "02-01-2023 13:50:00"; // 1790 min

		assertThat(Time.stepsToDateString(1790)).isEqualTo(expectedString);
	}

	/**
	 * Checks if stepsToString throws the correct SimulationException when the time
	 * to format is negative.
	 */
	@Test
	void throwsBecauseNegativeInt()
	{
		assertThatThrownBy(() ->
		{
			Time.stepsToDateString(-1790);
		}).isInstanceOf(SimulationException.class).hasMessageContaining("can't be negative");
	}

	/**
	 * Checks if getDayOfWeek returns the correct day of week.
	 */
	@Test
	void determinesCorrectWeekDay()
	{
		assertThat(Time.getDayOfWeek(0)).isEqualTo(5);
	}

}
