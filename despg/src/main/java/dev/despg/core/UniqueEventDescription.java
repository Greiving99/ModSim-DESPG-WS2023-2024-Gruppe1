package dev.despg.core;

/**
 * Interface for event description. Event descriptions serve as filters in 
 * {@link EventQueue#getNextEvent(int, boolean, UniqueEventDescription, Class, SimulationObject) }
 */
public interface UniqueEventDescription
{
	String get();
}
