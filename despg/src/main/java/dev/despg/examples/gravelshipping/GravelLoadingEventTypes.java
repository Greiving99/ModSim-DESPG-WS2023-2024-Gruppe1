package dev.despg.examples.gravelshipping;

import dev.despg.core.UniqueEventDescription;


/**
 * Implementation of {@link dev.despg.core.UniqueEventDescription}. Defines the event types that can happen during the GravelShipping simulation
 */
public enum GravelLoadingEventTypes implements UniqueEventDescription
{
	Loading("Loading Truck"), 
	LoadingDone("Loading Truck done"), 
	Weighing("Weighing Truck"),
	WeighingDone("Weighing Truck done");
	
	String eventTypeUniqueDescription = null;
	
	GravelLoadingEventTypes(String value)
	{
		this.eventTypeUniqueDescription = value;
	}

	@Override
	public String get()
	{
		return eventTypeUniqueDescription;
	}
	
}
