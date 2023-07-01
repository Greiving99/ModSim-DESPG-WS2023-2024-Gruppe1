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

/**
 * Events are occurrences between SimulationObjects that are stored in the
 * EventQueue. When an Event occurs, the attached SimulationObject may produce
 * and/or consume Events.
 */
public record Event(
		Long timeStep,
		UniqueEventDescription eventDescription,
		SimulationObject objectAttached,
		Class<? extends SimulationObject> receiverClass,
		SimulationObject receiver
		) implements Comparable<Event>
{

	/**
	 * Describes event as String.
	 */
	@Override
	public String toString()
	{
		return Time.stepsToDateString(timeStep) + " " + eventDescription;
	}

	/**
	 * Compares two events based on their time step.
	 */
	@Override
	public int compareTo(Event event)
	{
		return timeStep.compareTo(event.timeStep);
	}
}
