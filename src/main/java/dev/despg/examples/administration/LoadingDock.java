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


public class LoadingDock extends SimulationObject
{
	private static final double LOADINGFIXCOST = 50.43;
	private static final int EMPLOYEEFAILURECOST = 5;
	private static final double DOCHREPAIRCOST = 0.25;
	private static final int REPAIRTIMECOST = 100;
	private String nameEmployee;
	private String name;
	private Truck truckCurrentlyLoaded;
	private boolean dockFailed;
	private boolean truckFailed;

	private static LoadingDockWorker loadingDockWorker;
	private static Mechanic mechanic;
	private static Mechanic truckMechanic;

	private static int numMechanic;
	private static boolean newMechanic;
	private static boolean newTruckMechanic;
	private static int numTruckMechanic;

	private int counterFailureLKW = WeighingStation.getCounterFailureTruck();
	private static double fixedCost;

	private boolean employeeFailed;

	private double quality;
	private static int counterEmployeeSick;

	private static EventQueue eventQueue;
	private static int counterFailureDock;

	private static ArrayList<LoadingDock> loadingDockList = new ArrayList<>();

	/**
	 * Constructor for new LoadingDocks, injects its dependency to SimulationObjects
	 * and creates the required randomizer instances.
	 *
	 * @param name Name of the LoadingDock instance
	 * @param
	 * @param nameEmployee
	 * @param quality
	 */
	public LoadingDock(String name, String nameEmployee, double quality)
	{
		this.name = name;
		this.nameEmployee = nameEmployee;
		this.quality = quality;
		loadingDockWorker = new LoadingDockWorker(nameEmployee, quality);
		this.setLoadingDockWorker(loadingDockWorker);
		dockFailed = false;
		truckFailed = false;
		newTruckMechanic = false;
		newMechanic = false;
		employeeFailed = false;

		eventQueue = EventQueue.getInstance();
		SimulationObjects.getInstance().add(this);
		loadingDockList.add(this);
	}

	@Override
	public final String toString()
	{
		String toString = "Loading Dock:" + name;
		if (truckCurrentlyLoaded != null)
			toString += " " + "loading: " + truckCurrentlyLoaded;
		return toString;
	}

	/**
	 * Gets called every timeStep.
	 *
	 * If it is not currently occupied ({@link #truckCurrentlyLoaded} == null) and
	 * the simulation goal still is not archived, it checks if there is an event in
	 * the queue that got assigned to this class with the correct event description.
	 * Then proceeds in checking if the attached object is indeed a Truck. In that
	 * case the event gets removed from the queue and the event will get handled:
	 * {@link #truckCurrentlyLoaded} is set to the events attached object, and the
	 * truck gets loaded. Adds a new event to the event queue for when the loading
	 * is done and returns true.*/

	/* When the loading is done, it grabs the corresponding event from the event
	 * queue and handles it by removing it from the queue, setting
	 * {@link truckCurrentlyLoaded} to null and adding a new event to the event
	 * queue assigned to the {@link WeighingStation} class.
	 *
	 * @return true if an assignable event got found and handled, false if no event
	 *         could get assigned
	 */
	@Override
	public boolean simulate(long timeStep)
	{
		if (newTruckMechanic)
			//When a new truck mechanic is hired, it is created here.
		{
			setTruckMechanic(new Mechanic("MechanikerTruck" + numTruckMechanic, Administration.getQualityMechanic()));
			Administration.setMechanicHired(Administration.getMechanicHired() + 1);
			numTruckMechanic++;
			newTruckMechanic = false;
		}
		if (newMechanic)
			//When a new mechanic for the loading dock is hired, it is created here.
		{
			setMechanic(new Mechanic("MechanikerLD" + numMechanic, Administration.getQualityMechanic()));
			Administration.setMechanicHired(Administration.getMechanicHired() + 1);
			numMechanic++;
			newMechanic = false;
		}
		if (truckFailed)
			//Here, it is checked if a truck has broken down, and then the 'TruckRepaired' event is thrown from the EventQueue.
		{
			Event event = eventQueue.getNextEvent(timeStep, true, GravelLoadingEventTypes.TruckRepaired, null, null);
			if (event != null)
			{
				eventQueue.remove(event);
				truckFailed = false;
				return true;
			}
		}
		if (dockFailed)
			//Here, it is checked if a loading dock has broken down, and then the 'DockRepaired' event is thrown from the EventQueue.
		{
			Event event = eventQueue.getNextEvent(timeStep, true, GravelLoadingEventTypes.DockRepaired, null, this);
			if (event != null)
			{
				eventQueue.remove(event);
				dockFailed = false;
				return true;
			}
		}
		if (employeeFailed)
			/*Here, it is checked if an employee from the loading dock is unavailable,
			 * and then the 'DockRepaired' event is thrown from the EventQueue.
			 * The loading dock cannot function without the employee,
			 * and 'DockRepaired' serves the same purpose as indicating a loading dock outage.*/
		{
			Event event = eventQueue.getNextEvent(timeStep, true, GravelLoadingEventTypes.DockRepaired, null, this);
			if (event != null)
			{
				eventQueue.remove(event);
				employeeFailed = false;
				return true;
			}
		}
		else if (truckCurrentlyLoaded == null && GravelShipping.getGravelToShip() > 0)

			/*Here, the 'Loading' event is dequeued from the EventQueue. Then, the truck is loaded by the warehouse worker of the loading dock,
			 * which takes a random amount of time,
			 * and a random weight is loaded. Next,
			 * the 'LoadingDone' event is added, and the costs for the duration of loading are calculated.*/

		{
			Event event = eventQueue.getNextEvent(timeStep, true, GravelLoadingEventTypes.Loading, this.getClass(), null);

			if (event != null && event.objectAttached() != null
					&& event.objectAttached().getClass() == Truck.class)
			{

				int timeToLoad = loadingDockWorker.getTimeLoad();
				eventQueue.remove(event);
				truckCurrentlyLoaded = (Truck) event.objectAttached();
				if (truckCurrentlyLoaded.getDriver().isReplaced())
				{
					truckCurrentlyLoaded.setDriver(Driver.getDriverList().get(truckCurrentlyLoaded.getDriver().getReplacedBy()));
				}

				truckCurrentlyLoaded.load(Math.min(loadingDockWorker.getWeightLoaded(), GravelShipping.getGravelToShip()));
				GravelShipping.setGravelToShip(GravelShipping.getGravelToShip() - truckCurrentlyLoaded.getLoad());
				eventQueue.add(new Event(timeStep + truckCurrentlyLoaded.addTimeStepDelta(TrackerType.Utilization, timeToLoad),
						GravelLoadingEventTypes.LoadingDone, truckCurrentlyLoaded, null, this));
				setFixedCost(getFixedCost() + LOADINGFIXCOST * timeToLoad);
				trackerStart(TrackerType.Utilization, timeStep);
				return true;
			}
		}
		else

			/* Here, the 'LoadingDone' event is retrieved for a truck and then removed from the EventQueue.
			 * Next, the 'Weighing' event is added with the time it takes for the driver to reach the WeighingStation,
			 * assigned by a randomizer. Fuel costs are calculated based on this time.*/
		{
			Event event = eventQueue.getNextEvent(timeStep, true, GravelLoadingEventTypes.LoadingDone, null, this);
			if (event != null && event.objectAttached() != null
					&& event.objectAttached().getClass() == Truck.class)
			{
				int timeToWeighingStation = truckCurrentlyLoaded.getDriver().getDrivingToWeighingStation();
				eventQueue.remove(event);
				eventQueue.add(new Event(
					timeStep + event.objectAttached().addTimeStepDelta(TrackerType.Utilization, timeToWeighingStation),
						GravelLoadingEventTypes.Weighing, truckCurrentlyLoaded, WeighingStation.class, null));

				setFixedCost(getFixedCost() + (Truck.getFuelCost() * timeToWeighingStation));

				truckCurrentlyLoaded = null;
				trackerStop(TrackerType.Utilization, timeStep);

				/*Here, it is checked if the loading dock has broken down. The time required for repairs
				 * is added to the time the mechanic may have been unavailable due to vacation or illness.
				 * The 'DockRepaired' event is added to this loading dock and placed in the EventQueue.
				 * Finally, the repair costs are calculated here.*/

				int	repairTime = mechanic.getDockFailureRepairTime();
				if (repairTime > 0)
				{
					counterFailureDock++;
					repairTime += mechanic.failureTime();
					dockFailed = true;
					eventQueue.add(new Event(timeStep + repairTime, GravelLoadingEventTypes.DockRepaired, null, null, this));
					Administration.setRepairCost(Administration.getRepairCost() + DOCHREPAIRCOST * repairTime); // für 15 Euro die Stunde
				}

				//It follows the same principle as before, but now it checks whether a truck and/or a mechanic has broken down.

				int	repairTimeTruck = LoadingDock.getTruckMechanic().getTruckFailureRepairTime();
				if (repairTimeTruck > 0)
				{
					counterFailureLKW++;
					truckFailed = true;
					repairTimeTruck += getTruckMechanic().failureTime();
					Administration.setRepairCost(Administration.getRepairCost() + REPAIRTIMECOST * repairTime);
					eventQueue.add(new Event(timeStep + repairTimeTruck, GravelLoadingEventTypes.TruckRepaired, this, null, null));
				}

				/*Here, it is checked if a warehouse worker responsible for the loading dock is unavailable.
				 * The downtime cost is calculated at 5 euros per minute.*/

				int timeEmployeeFailure = LoadingDockWorker.duration();
				if (timeEmployeeFailure > 0)
				{
					employeeFailed = true;
					eventQueue.add(new Event(timeStep + timeEmployeeFailure, GravelLoadingEventTypes.DockRepaired, null, null, this));
					setFixedCost(getFixedCost() + (timeEmployeeFailure * EMPLOYEEFAILURECOST));
				}

				return true;
			}
		}

		return false;
	}



	public static void setNewMechanic(boolean newMechaniker)
	{
		LoadingDock.newMechanic = newMechaniker;
	}

	public static void setNewTruckMechanic(boolean newTruckMechaniker)
	{
		LoadingDock.newTruckMechanic = newTruckMechaniker;
	}

	public static Mechanic getMechanic()
	{
		return mechanic;
	}
	public static void setMechanic(Mechanic mechaniker)
	{
		LoadingDock.mechanic = mechaniker;
	}
	public static int getCounterFailureDock()
	{
		return counterFailureDock;
	}
	public static double getFixedCost()
	{
		return fixedCost;
	}

	public static void setFixedCost(double fixKosten)
	{
		LoadingDock.fixedCost = fixKosten;
	}

	public static Mechanic getTruckMechanic()
	{
		return truckMechanic;
	}
	public static void setTruckMechanic(Mechanic truckMechaniker)
	{
		LoadingDock.truckMechanic = truckMechaniker;
	}
	public int getNumMechanic()
	{
		return numMechanic;
	}

	public static void setNumMechanic(int numMechaniker)
	{
		LoadingDock.numMechanic = numMechaniker;
	}

	public static int getNumTruckMechanic()
	{
		return numTruckMechanic;
	}

	public static void setNumTruckMechanic(int numTruckMechaniker)
	{
		LoadingDock.numTruckMechanic = numTruckMechaniker;
	}
	public LoadingDockWorker getLoadingDockWorker()
	{
		return loadingDockWorker;
	}

	public void setLoadingDockWorker(LoadingDockWorker lagerarbeiter)
	{
		LoadingDock.loadingDockWorker = lagerarbeiter;
	}

}
