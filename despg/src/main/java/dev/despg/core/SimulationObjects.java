package dev.despg.core;

import java.util.ArrayList;

/**
 * Maintains an ArrayList of every initialized SimulationObject
 */
public class SimulationObjects extends ArrayList<SimulationObject>
{
	private static final long serialVersionUID = 1L;

	private SimulationObjects()
	{
		// ...
	}

	private static class Inner
	{
		private static SimulationObjects simulationObjects = new SimulationObjects();
	}

	/**
	 * Gets the instance of SimulationObjects
	 * 
	 * @return The Singleton instance of SimulationObjects
	 */
	public static SimulationObjects getInstance()
	{
		return Inner.simulationObjects;
	}

}
