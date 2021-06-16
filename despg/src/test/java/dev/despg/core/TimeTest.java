package dev.despg.core;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import dev.despg.core.exception.SimulationException;

class TimeTest {


	@BeforeEach
	void init() {
		
	}
	@Test
	void returnsCorrectTime() {
		String expectedString = "1d:5h:50m"; //1790 min
		
		assertThat(Time.stepsToString(1790)).isEqualTo(expectedString);
	}
	@Test
	void ThrowsBecauseNegativeInt() {
		assertThatThrownBy(() -> { Time.stepsToString(-1790); }).isInstanceOf(SimulationException.class)
        .hasMessageContaining("can't be negative");
	}

}
