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

/**
 * Maintains an ArrayList of every initialized SimulationObject.
 */
public final class SimulationObjects extends ArrayList<SimulationObject>
{
	@Serial
	private static final long serialVersionUID = 1L;

	private SimulationObjects()
	{
		// ...
	}

	private static class Inner
	{
		private static final SimulationObjects SIMULATION_OBJECTS = new SimulationObjects();
	}

	/**
	 * Gets the instance of SimulationObjects.
	 *
	 * @return The Singleton instance of SimulationObjects
	 */
	public static SimulationObjects getInstance()
	{
		return Inner.SIMULATION_OBJECTS;
	}

}
