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

import dev.despg.core.Event;
import dev.despg.core.EventQueue;

import dev.despg.core.SimulationObject;
import dev.despg.core.SimulationObjects;
import dev.despg.core.TrackerType;
//import dev.despg.core.UniqueEventDescription;

public final class Truck extends SimulationObject
{
	private String name;
	private Integer loadedWithTons;
	private Driver driver;
	private EventQueue eventQueue;
	private ArrayList<Event> whileFailedList;
	@SuppressWarnings("unused")
	private String nameDriver;
	@SuppressWarnings("unused")
	private double quality;
	private boolean truckFailed;
	private static int counterFailureTruck;
	private static final double FUEL_COST = 1.21;
	private static final double TRUCK_REPAIR_COST = 125;
	private static final int TRUCKREPAIRCOST = 100;

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
		truckFailed = false;
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
	/**When this method is called the Truck object evaluates if it should break down.
	 * If the Truck breaks down will evaluate how long the repair will take, notify the
	 * Administration class about the repair costs and set its truckFailed flag to true and
	 * updates calls its addTimeStepDelt method inherited and tracks its failure time. Then
	 * it removes all Events that it is attached to from the eventQueue and stores them in
	 * the whileFailed list.
	 * It adds a TruckRepaired Event for itself into the eventQueue.
	 *
	 * @param timeStep The timeStep when Truck might fail.
	 */
	public void truckFailed(long timeStep)
	{
		eventQueue = EventQueue.getInstance();
		int	repairTimeTruck = LoadingDock.getTruckMechanic().getTruckFailureRepairTime();
		if (repairTimeTruck <= 0)
		{
			return;
		}
		whileFailedList = new ArrayList<Event>();
		ArrayList<Event> tempList = new ArrayList<Event>();
		counterFailureTruck++;
		Administration.setRepairCost(Administration.getRepairCost() + TRUCKREPAIRCOST * repairTimeTruck);
		truckFailed = true;
		this.addTimeStepDelta(TrackerType.Failure, repairTimeTruck);

		Event event = eventQueue.getNextEvent(0, false, null, null, null);
		while (event != null)
		{
			if (event.objectAttached() == this)
			{
				eventQueue.remove(event);
				whileFailedList.add(event);
			}
			else
			{
				eventQueue.remove(event);
				tempList.add(event);
			}
			event = eventQueue.getNextEvent(0, false, null, null, null);
		}
		eventQueue.addAll(tempList);
		eventQueue.add(new Event(timeStep + repairTimeTruck, GravelLoadingEventTypes.TruckRepaired, null, null, this));
	}

	/**The truck object checks if there is a TruckRepaired Event in the Queue with itself as receiver.
	 * If there is it will consume the event and add all events that it removed from the Queue on
	 * breakdown back into the eventQueue.
	 *
	 * @param timeStep The timeStep the truck is simulating on.
	 */
	@Override
	public void simulate(long timeStep)
	{
		eventQueue = EventQueue.getInstance();
		Event e = eventQueue.getNextEvent(timeStep, true, GravelLoadingEventTypes.TruckRepaired,
				null, this);
		if (e != null)
		{
			eventQueue.addAll(whileFailedList);
			truckFailed = false;
			eventQueue.remove(e);
		}
	}

	public Driver getDriver()
	{
		return this.driver;
	}

	public void setDriver(Driver driver)
	{
		this.driver = driver;
	}

	public boolean getTruckFailed()
	{
		return truckFailed;
	}

	public static int getCounterFailureTruck()
	{
		return counterFailureTruck;
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
