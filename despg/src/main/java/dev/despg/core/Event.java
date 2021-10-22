package dev.despg.core;

import java.util.logging.Logger;

/**
 * Events are occurrences between SimulationObjects that are stored in the
 * EventQueue. When an Event occurs, the attached SimulationObject may produce
 * and/or consume Events.
 */
public class Event implements Comparable<Event>
{
	private static Logger logger = Logger.getLogger("dev.despg.core.Event");

	private Integer timeStep = null;
	private SimulationObject objectAttached = null;

	private Class<? extends SimulationObject> receivingClass = null;
	private SimulationObject receivingObject = null;

	private UniqueEventDescription description = null;

	/**
	 * Event constructor
	 * 
	 * @param timeStep       timeStep the event will occur
	 * @param description    Description of the event
	 * @param objectAttached Object that is attached to the event
	 * @param receiverClass  Receiving class for the event
	 * @param receiverObject Receiving object for the event
	 */
	public Event(Integer timeStep, UniqueEventDescription description, SimulationObject objectAttached,
			Class<? extends SimulationObject> receiverClass, SimulationObject receiverObject)
	{
		this.timeStep = timeStep;
		this.description = description;
		this.objectAttached = objectAttached;
		this.receivingClass = receiverClass;
		this.receivingObject = receiverObject;
	}

	/**
	 * @return timeStep the event will occur
	 */
	public Integer getTimeStep()
	{
		return timeStep;
	}

	/**
	 * @return Object that is attached to the event
	 */
	public SimulationObject getObjectAttached()
	{
		return objectAttached;
	}

	/**
	 * @return Receiving class for the event
	 */
	public Class<? extends SimulationObject> getReceiverClass()
	{
		return receivingClass;
	}

	/**
	 * @return Receiving object for the event
	 */
	public SimulationObject getReceiver()
	{
		return receivingObject;
	}

	/**
	 * @return Description of the event
	 */
	public UniqueEventDescription getEventDescription()
	{
		return description;
	}

	@Override
	public String toString()
	{
		return Time.stepsToString(timeStep) + " " + description;
	}

	@Override
	public int compareTo(Event event)
	{
		return timeStep.compareTo(event.timeStep);
	}
}