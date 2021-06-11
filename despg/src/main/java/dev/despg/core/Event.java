package dev.despg.core;

public class Event implements Comparable<Event>
{
	private Integer timeStep = null;
	private SimulationObject objectAttached = null;
	
	private Class<? extends SimulationObject> receivingClass = null;
	private SimulationObject receivingObject = null;
	 
	private UniqueEventDescription description = null;
	
	public Event(Integer timeStep, 
			UniqueEventDescription description, 
			SimulationObject objectAttached, 
			Class<? extends SimulationObject> receiverClass, 
			SimulationObject receiverObject)
	{
		this.timeStep = timeStep;
		this.description = description;
		this.objectAttached = objectAttached;
		this.receivingClass = receiverClass;
		this.receivingObject = receiverObject;
	}

	public Integer getTimeStep()
	{
		return timeStep;
	}

	public SimulationObject getObjectAttached()
	{
		return objectAttached;
	}

	public Class<? extends SimulationObject> getReceiverClass()
	{
		return receivingClass;
	}
	
	public SimulationObject getReceiver()
	{
		return receivingObject;
	}

	public UniqueEventDescription getEventDescription()
	{
		return description;
	}
	
	@Override
	public String toString()
	{
		return Time.stepsToString(timeStep) + " "+ description;
	}

	@Override
	public int compareTo(Event event)
	{
		return timeStep.compareTo(event.timeStep);		
	}
}
