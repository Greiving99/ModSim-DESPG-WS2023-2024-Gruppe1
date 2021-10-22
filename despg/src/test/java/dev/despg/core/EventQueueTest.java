package dev.despg.core;

import java.util.ArrayList;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.*;



class EventQueueTest {
	ArrayList<Event> toAdd;
	EventQueue e;
	SimulationObject receiving;
	UniqueEventDescription description;
	Event event;
	
	/**
	 * Initializes some Events and adds them to the EventQueue instance, each being different in at least one parameter to set up
	 * for tests on the EventQueue
	 */
	@BeforeEach
	void init() {
	e = EventQueue.getInstance();
	receiving = Mockito.mock(SimulationObject.class);
	description = Mockito.mock(UniqueEventDescription.class);
	toAdd = new ArrayList<Event>();
	toAdd.add(new Event(2, null, null, null, null));
	toAdd.add(new Event(0, null, null, null, null));
	toAdd.add(new Event(3, null, null, SimulationObject.class, null));
	toAdd.add(new Event(3, null, null, null, receiving));
	toAdd.add(new Event(3, description, null, null, null));
	e.addAll(toAdd);
	}
	
	/**
	 * Clearing up the EventQueue
	 */
	@AfterEach
	void clear() {
		e.clear();
	}
	/**
	 * Checks if getNextEvent filters correctly by timeStep 
	 */
	@Test
	void shouldFilterCorrectByTimeStep() {
		event = e.getNextEvent(0, false, null, null, null);
		
		assertThat(event).isEqualTo(toAdd.get(1));
	}
	
	/**
	 * Checks if getNextEvent filters correctly by timeStep including the past
	 */
	@Test
	void shouldFilterCorrectByTimeStepIncludingPast() {
		event = e.getNextEvent(2, true, null, null, null);
		
		assertThat(event).isEqualTo(toAdd.get(1));
	}
	
	/**
	 * Checks if getNextEvent filters correctly by receiving class
	 */
	@Test
	void shouldFilterCorrectByReceivingClass() {
		event = e.getNextEvent(0, false, null, SimulationObject.class, null);
		
		assertThat(event).isEqualTo(toAdd.get(2));
	}
	
	/**
	 * Checks if getNextEvent filters correctly by receiving object
	 */
	@Test
	void shouldFilterCorrectByReceivingObject() {
		event = e.getNextEvent(0, false, null, null, receiving);
		
		assertThat(event).isEqualTo(toAdd.get(3));
	}
	/**
	 * Checks if getNextEvent filters correctly by unique event description
	 */
	@Test
	void shouldFilterCorrectByEventType() {
		event = e.getNextEvent(0, false, description, null, null);
		
		assertThat(event).isEqualTo(toAdd.get(4));
	}
	
	/**
	 * Checks if getNextEvent does return null when the EventQueue is empty
	 */
	@Test
	void shouldReturnNull() {
		e.clear();
		event = e.getNextEvent(0, false, null, null, null);
		
		assertThat(event).isNull();
	}

	
}