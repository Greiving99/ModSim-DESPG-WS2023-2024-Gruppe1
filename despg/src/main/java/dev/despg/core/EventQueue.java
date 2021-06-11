package dev.despg.core;
import java.util.ArrayList;
import java.util.Collections;

public class EventQueue extends ArrayList<Event>
{
	private static final long serialVersionUID = 1L;
	private static final int MAX_EVENTS = 10000;
		
	private EventQueue()
	{
		super(MAX_EVENTS);
	}
	
	private static class Inner 
	{
		private static EventQueue eventqueue = new EventQueue();
	}
	
	public static EventQueue getInstance()
	{
		return Inner.eventqueue;
	}
	
	public boolean add(Event e)
	{
		boolean success = super.add(e);
		System.out.println("- addEvent '" + e + "' " + this);
		return success;
	}
	
	public void remove(Event e)
	{
		super.remove(e);
		System.out.println("- remEvent '" + e + "' " + this);
	}
	
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
