package dev.despg.core;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.when;

import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SimulationObjectTest {
	SimulationObject simObject;

	@BeforeEach
	void init() {
		simObject = Mockito.mock(SimulationObject.class);
	}
	
	
	@Test
	void doesStartUtilization() {
		int expected = 4;
		doCallRealMethod().when(simObject).utilStart(expected);
		when(simObject.getUtilStart()).thenCallRealMethod();
		simObject.utilStart(expected);
		
		assertThat(simObject.getUtilStart()).isEqualTo(expected);
	}
	
	@Test
	void doesStopUtilizationAndReset() {
		int expected = 4;
		doCallRealMethod().when(simObject).utilStop(6);
		when(simObject.getTimeUtilized()).thenCallRealMethod();
		when(simObject.getUtilStart()).thenCallRealMethod();
		
		ReflectionTestUtils.setField(simObject, "utilStart", 2 );
		ReflectionTestUtils.setField(simObject, "timeUtilized", 0 );
		simObject.utilStop(6);

		assertThat(simObject.getTimeUtilized()).isEqualTo(expected);
		assertThat(simObject.getUtilStart()).isNull();
	}
	
	@Test
	void addsToTimeUtilized() {
		int expected = 4;
		when(simObject.addUtilization(expected)).thenCallRealMethod();
		ReflectionTestUtils.setField(simObject, "timeUtilized", 0 );
		
		assertThat(simObject.addUtilization(expected)).isEqualTo(expected);
	}

}
