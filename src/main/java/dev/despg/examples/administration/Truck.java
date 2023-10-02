/**
 * Copyright (C) 2021 despg.dev, Ralf Buschermöhle
 *
 * DESPG is made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * see LICENSE
 *
 */
package dev.despg.examples.administration;


import java.util.ArrayList;

import dev.despg.core.SimulationObject;
import dev.despg.core.SimulationObjects;

public final class Truck extends SimulationObject
{
	private String name;
	private Integer loadedWithTons;
	private Driver driver;
	@SuppressWarnings("unused")
	private String nameDriver;
	@SuppressWarnings("unused")
	private double quality;
	private static final double FUEL_COST = 1.21;
	private static final double TRUCK_REPAIR_COST = 125;

	private static ArrayList<Truck> truckList = new ArrayList<>();

	public Truck(String name, String nameDriver, double quality)
	{
		this.name = name;
		this.nameDriver = nameDriver;
		this.quality = quality;
		//When creating the truck, a driver is directly created and assigned to the truck.
		driver = new Driver(nameDriver, quality);
		this.setDriver(driver);
		truckList.add(this);
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
	public boolean simulate(long timeStep)
	{
		return false;
	}

	public Driver getDriver()
	{
		return this.driver;
	}

	public void setDriver(Driver driver)
	{
		this.driver = driver;
	}
	public static double getFuelCost()
	{
		return FUEL_COST;
	}

	public static double getTruckRepairCost()
	{
		return TRUCK_REPAIR_COST;
	}
	public static ArrayList<Truck> getTruckList()
	{
		return truckList;
	}

	public static void setTruckList(ArrayList<Truck> truckList)
	{
		Truck.truckList = truckList;
	}
}
