/**
 * Copyright (C) 2021 despg.dev, Ralf Buschermöhle
 * <p>
 * DESPG is made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * <p>
 * see LICENSE
 *
 */
package dev.despg.core;

import java.io.Serial;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The EventQueue class manages an ArrayList of all scheduled Events.
 */
public final class EventQueue extends ArrayList<Event>
{
	private static final Logger logger = Logger.getLogger("dev.despg.core.EventQueue");

	@Serial
	private static final long serialVersionUID = 1L;
	private static final int MAX_EVENTS = 10000;

	private EventQueue()
	{
		super(MAX_EVENTS);
	}

	/**
	 * Nested class that holds the EventQueue instance.
	 */
	private static class Inner
	{
		private static final EventQueue eventqueue = new EventQueue();
	}

	/**
	 * Gets the Instance of the EventQueue.
	 *
	 * @return The EventQueue Instance
	 */
	public static EventQueue getInstance()
	{
		return Inner.eventqueue;
	}

	/**
	 * Adds an Event to the EventQueue.
	 *
	 * @param e the event to add
	 */
	public boolean add(Event e)
	{
		boolean success = super.add(e);
		logger.log(Level.FINEST, "addEvent '" + e + "' " + this);
		return success;
	}

	/**
	 * Removes an Event from the EventQueue.
	 *
	 * @param e Event to be removed
	 */
	public void remove(Event e)
	{
		super.remove(e);
		logger.log(Level.FINEST, "removeEvent '" + e + "' " + this);
	}

	private List<Event> filterEvents(long timeStep, boolean past, UniqueEventDescription eventTypeNumber,
			Class<? extends SimulationObject> receiverClass, SimulationObject receiverObject)
	{
		List<Event> subevents = new ArrayList<>(this.size());

		for (Event e : this)
		{
			if ((past && timeStep >= e.timeStep() || !past && timeStep <= e.timeStep())
					&& (receiverClass == null || receiverClass == e.receiverClass())
					&& (receiverObject == null || receiverObject == e.receiver())
					&& (eventTypeNumber == null || eventTypeNumber == e.eventDescription()))
				subevents.add(e);
		}
		return subevents;
	}

	/**
	 * The getNextEvent method creates an ArrayList of all sub events that match the
	 * defined filters and returns the Event with the lowest timeStep from that List.
	 *
	 * @param timeStep        Gets events from this timeStep forwards
	 * @param past            Include events from the past
	 * @param eventTypeNumber Filter for specific event type
	 * @param receiverClass   Filter for specific receiving class
	 * @param receiverObject  Filter for specific receiving object
	 * @return returns the Event in the EventQueue with the lowest timeStep which
	 *         matches the defined filters or null of no Event could be filtered
	 */
	public Event getNextEvent(long timeStep, boolean past, UniqueEventDescription eventTypeNumber,
			Class<? extends SimulationObject> receiverClass, SimulationObject receiverObject)
	{
		List<Event> events = filterEvents(timeStep, past, eventTypeNumber, receiverClass, receiverObject);

		if (events.size() > 0)
		{
			Collections.sort(events);
			return events.get(0);
		}

		return null;
	}

	public int countEvents(long timeStep, boolean past, UniqueEventDescription eventTypeNumber,
			Class<? extends SimulationObject> receiverClass, SimulationObject receiverObject)
	{
		return filterEvents(timeStep, past, eventTypeNumber, receiverClass, receiverObject).size();
	}
}
