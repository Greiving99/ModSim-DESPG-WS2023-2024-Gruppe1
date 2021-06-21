package dev.despg.core;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import dev.despg.core.exception.SimulationException;

class TimeTest {


	
	/**
	 * Checks if stepsToString returns the time formatted correctly
	 */
	@Test
	void returnsCorrectTime() {
		String expectedString = "1d:5h:50m"; //1790 min
		
		assertThat(Time.stepsToString(1790)).isEqualTo(expectedString);
	}
	
	/**
	 * Checks if stepsToString throws the correct SimulationException when the time to format is negative
	 */
	@Test
	void ThrowsBecauseNegativeInt() {
		assertThatThrownBy(() -> { Time.stepsToString(-1790); }).isInstanceOf(SimulationException.class)
        .hasMessageContaining("can't be negative");
	}

}
