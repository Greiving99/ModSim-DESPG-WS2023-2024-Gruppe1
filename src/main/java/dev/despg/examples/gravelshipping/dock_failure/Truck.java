/**
 * Copyright (C) 2021 despg.dev, Ralf Buschermöhle
 * <p>
 * DESPG is made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * <p>
 * see LICENSE
 *
 */
package dev.despg.examples.gravelshipping.dock_failure;

import dev.despg.core.SimulationObject;
import dev.despg.core.SimulationObjects;

public final class Truck extends SimulationObject
{
	private final String name;
	private Integer loadedWithTons;

	public Truck(String name)
	{
		this.name = name;
		SimulationObjects.getInstance().add(this);
	}

	public void load(int weight)
	{
		loadedWithTons = weight;
	}

	public void unload()
	{
		loadedWithTons = null;
	}

	public Integer getLoad()
	{
		return loadedWithTons;
	}

	@Override
	public String toString()
	{
		return name + (loadedWithTons != null ? "(" + loadedWithTons + "t)" : "");
	}

	@Override
	public void simulate(long timeStep)
	{
	}
}
