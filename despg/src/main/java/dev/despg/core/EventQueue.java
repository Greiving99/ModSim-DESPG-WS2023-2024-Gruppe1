package dev.despg.core;
import java.util.ArrayList;
import java.util.Collections;

/**
 * The EventQueue class manages an ArrayList of all scheduled Events
 */
public class EventQueue extends ArrayList<Event>
{
	private static final long serialVersionUID = 1L;
	private static final int MAX_EVENTS = 10000;
		
	private EventQueue()
	{
		super(MAX_EVENTS);
	}
	
	/**
	 * Nested class that holds the EventQueue instance
	 */
	private static class Inner 
	{
		private static EventQueue eventqueue = new EventQueue();
	}
	
	/**
	 * Gets the Instance of the EventQueue
	 * @return The EventQueue Instance
	 */
	public static EventQueue getInstance()
	{
		return Inner.eventqueue;
	}
	
	/**
	 * Adds an Event to the EventQueue
	 */
	public boolean add(Event e)
	{
		boolean success = super.add(e);
		System.out.println("- addEvent '" + e + "' " + this);
		return success;
	}
	
	/** 
	 * Removes an Event from the EventQueue
	 * @param e Event to be removed
	 */
	public void remove(Event e)
	{
		super.remove(e);
		System.out.println("- remEvent '" + e + "' " + this);
	}
	
	/**
	 * The getNextEvent method creates an ArrayList of all sub events that match the
	 * defined filters and returns the Event with the lowest timeStep from that List
	 * @param timeStep Gets events from this timeStep forwards
	 * @param past Include events from the past
	 * @param eventTypeNumber Filter for specific event type
	 * @param receiverClass Filter for specific receiving class
	 * @param receiverObject Filter for specific receiving object
	 * @return returns the Event in the EventQueue with the lowest timeStep which matches
	 *  the defined filters or null of no Event could be filtered
	 */
	public Event getNextEvent(int timeStep, 							// get events from this timestep forward ...
			boolean past, 														// ... including events from past ?
			UniqueEventDescription eventTypeNumber, 					// filter 
			Class<? extends SimulationObject> receiverClass, 		// filter 
			SimulationObject receiverObject)								// filter
	{
		ArrayList<Event> subevents = new ArrayList<Event>(this.size());
		
		for (Event e : this)
		{
			if ( ( past && timeStep >= e.getTimeStep() 
					|| !past && timeStep <= e.getTimeStep() )
					&& (receiverClass == null || receiverClass == e.getReceiverClass())
					&& (receiverObject == null || receiverObject == e.getReceiver()) 
					&& (eventTypeNumber == null || eventTypeNumber == e.getEventDescription()) )
				subevents.add(e);
		}
		
//		this.forEach(
//				e->{
//					if ( (past && timeStep >= e.getTimeStep() || !past && timeStep <= e.getTimeStep())
//							&& (receiverClass == null || receiverClass == e.getReceiverClass())
//							&& (receiver == null || receiver == e.getReceiver()) 
//							&& (eventTypeNumber == null || eventTypeNumber == e.getEventDescription()))
//						subevents.add(e);
//				}
//		);
			
		if (subevents.size() > 1)
			Collections.sort(subevents);
		
		if (subevents.size() >= 1)
			return subevents.get(0);
			
		return null;
	}
}
