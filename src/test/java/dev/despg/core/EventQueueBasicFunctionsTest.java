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

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;


class EventQueueBasicFunctionsTest
{
	private EventQueue eventQueue;


	/**
	 * Initializes an empty EventQueue.
	 */
	@BeforeEach
	void init()
	{
		eventQueue = EventQueue.getInstance();
	}


	/**
	 * Clearing up the EventQueue.
	 */
	@AfterEach
	void clear()
	{
		eventQueue.clear();
	}


	/**
	 * Checks if an event is successfully added.
	 */
	@Test
	void addEvent()
	{
		Event event = new Event(2L, null, null, null, null);
		eventQueue.add(event);

		Event eventStored = eventQueue.getNextEvent(0, false, null, null, null);
		assertThat(event).isEqualTo(eventStored);
	}

	/**
	 * Checks if an event is successfully removed.
	 */
	@Test
	void removeEvent()
	{
		Event event = new Event(2L, null, null, null, null);
		eventQueue.add(event);

		boolean successfullyRemoved = eventQueue.remove(event);
		assertTrue(successfullyRemoved);

		Event eventStored = eventQueue.getNextEvent(0, false, null, null, null);
		assertNull(eventStored);
	}


}
