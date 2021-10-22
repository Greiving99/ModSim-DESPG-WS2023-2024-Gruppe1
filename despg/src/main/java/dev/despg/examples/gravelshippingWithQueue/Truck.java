package dev.despg.examples.gravelshippingWithQueue;

import dev.despg.core.SimulationObject;
import dev.despg.core.SimulationObjects;

public class Truck extends SimulationObject
{
	private String name = null;
	private Integer loadedWithTons = null;

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
	public boolean simulate(int timeStep)
	{
		return false;
	}
}
