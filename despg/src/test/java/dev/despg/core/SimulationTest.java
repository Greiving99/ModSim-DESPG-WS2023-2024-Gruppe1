package dev.despg.core;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.util.ArrayList;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import dev.despg.core.exception.SimulationException;


class SimulationTest {
	EventQueue e;
	SimulationObjects simObjects;
	ArrayList<Event> toAdd;
	Simulation sim;
	SimulationObject simObject = Mockito.mock(SimulationObject.class);
	boolean answered;

	@BeforeEach
	void init() {
		e = EventQueue.getInstance();
		toAdd = new ArrayList<Event>();
		simObjects = SimulationObjects.getInstance();
		simObjects.add(simObject);
		
		sim = Mockito.mock(Simulation.class);
	}
	
	@AfterEach
	void clear() {
		e.clear();
		simObjects.clear();
	}
	
	@Test
	void noEventInQueue() {
		when(sim.simulate()).thenCallRealMethod();
		int actual =sim.simulate();
		int expected = 0;
		
		assertThat(actual).isEqualTo(expected);
	}
	
	@Test
	void eventGotSimulated() {
		toAdd.add(new Event(1, null, null, null, null));
		e.addAll(toAdd);
		when(simObject.simulate(1)).thenAnswer(invocation->{
			if(!answered) {
				e.clear();
				answered= true;
				return true;
			}
			else {
				return false;
			}
				
			
		});
		when(sim.simulate()).thenCallRealMethod(); //possible to ignore sysouts?
		
		
		int expected = 1;
		int actual = sim.simulate();

		assertThat(actual).isEqualTo(expected);
	}
	
	@Test
	void eventInQueueUnassignable() {
		toAdd.add(new Event(0, null, null, null, null));
		e.addAll(toAdd);
		when(sim.simulate()).thenCallRealMethod();
		
		assertThatThrownBy(() -> { sim.simulate(); }).isInstanceOf(SimulationException.class)
        .hasMessageContaining("didn't get consumed");
	}
}
