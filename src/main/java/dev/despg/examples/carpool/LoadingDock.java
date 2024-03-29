/**
 * Copyright (C) 2021 despg.dev, Ralf Buschermöhle
 *
 * DESPG is made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * see LICENSE
 *
 */
package dev.despg.examples.carpool;

import java.util.HashMap;

import dev.despg.core.Event;
import dev.despg.core.EventQueue;
import dev.despg.core.Randomizer;
import dev.despg.core.SimulationObject;
import dev.despg.core.SimulationObjects;
import dev.despg.core.TrackerType;

public class LoadingDock extends SimulationObject
{
	private String name;
	private Truck truckCurrentlyLoaded;
	private boolean dockFailed;

	private static EventQueue eventQueue;

	private static Randomizer loadingWeight;
	private static Randomizer loadingTime;
	private static Randomizer drivingToWeighingStation;
	private static Randomizer dockFailureRepairTime;
	private static int countRepairs;
	private static HashMap<Integer, Long> allRepairTime = new HashMap<Integer, Long>();

	/**
	 * Constructor for new LoadingDocks, injects its dependency to SimulationObjects
	 * and creates the required randomizer instances.
	 *
	 * @param name Name of the LoadingDock instance
	 */
	public LoadingDock(String name)
	{
		this.name = name;
		dockFailed = false;

		eventQueue = EventQueue.getInstance();
		SimulationObjects.getInstance().add(this);

		loadingWeight = new Randomizer();
		//loadingWeight.addProbInt(0.9, 34);
		//loadingWeight.addProbInt(0.95, 38);
		loadingWeight.addProbInt(1.0, 34);

		loadingTime = new Randomizer();
		loadingTime.addProbInt(0.3, 60);
		loadingTime.addProbInt(0.8, 120);
		loadingTime.addProbInt(1.0, 180);

		drivingToWeighingStation = new Randomizer();
		drivingToWeighingStation.addProbInt(0.5, 30);
		drivingToWeighingStation.addProbInt(0.78, 45);
		drivingToWeighingStation.addProbInt(1.0, 60);
		//fail
		dockFailureRepairTime = new Randomizer();
		dockFailureRepairTime.addProbInt(1.0, 0);
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
	 * is done and returns true.
	 *
	 * When the loading is done, it grabs the corresponding event from the event
	 * queue and handles it by removing it from the queue, setting
	 * {@link truckCurrentlyLoaded} to null and adding a new event to the event
	 * queue assigned to the {@link WeighingStation} class.
	 *
	 * @return true if an assignable event got found and handled, false if no event
	 *         could get assigned
	 */
	@Override
	public void simulate(long timeStep)
	{
		if (dockFailed)
		{
			Event event = eventQueue.getNextEvent(timeStep, true, GravelLoadingEventTypes.DockRepaired, null, this);

				if (event != null)
				{
					eventQueue.remove(event);
					dockFailed = false;
				}
		}
		else if (truckCurrentlyLoaded == null /*&& GravelShipping.getGravelToShip() > 0*/)
		{
			Event event = eventQueue.getNextEvent(timeStep, true, GravelLoadingEventTypes.Loading, this.getClass(), null); //event aus queue holen
			if (event != null && event.objectAttached() != null
					&& event.objectAttached().getClass() == Truck.class) //überprüfen ob es ein event ist was verarbeitet werden kann
			{
				eventQueue.remove(event);

				truckCurrentlyLoaded = (Truck) event.objectAttached();
				truckCurrentlyLoaded.load(Math.min(loadingWeight.nextIntOnProp(), GravelShipping.getGravelToShip()));
				//GravelShipping.setGravelToShip(GravelShipping.getGravelToShip() - truckCurrentlyLoaded.getLoad());

				eventQueue.add(new Event(timeStep + truckCurrentlyLoaded.addTimeStepDelta(TrackerType.Utilization, loadingTime.nextIntOnProp()),
						GravelLoadingEventTypes.LoadingDone, truckCurrentlyLoaded, null, this));

				trackerStart(TrackerType.Utilization, timeStep);
			}
		}
		else
		{
			Event event = eventQueue.getNextEvent(timeStep, true, GravelLoadingEventTypes.LoadingDone, null, this);
			if (event != null && event.objectAttached() != null
					&& event.objectAttached().getClass() == Truck.class)
			{
				eventQueue.remove(event);
				eventQueue.add(new Event(
						timeStep + event.objectAttached().addTimeStepDelta(TrackerType.Utilization,
								drivingToWeighingStation.nextIntOnProp()),
						GravelLoadingEventTypes.Weighing, truckCurrentlyLoaded, WeighingStation.class, null));

				truckCurrentlyLoaded = null;
				trackerStop(TrackerType.Utilization, timeStep);

				//Failure

				int repairTime =  dockFailureRepairTime.nextIntOnProp();
				//allRepairtime += repairTime;

				if (repairTime > 0)
				{
					dockFailed = true;
					eventQueue.add(new Event(timeStep + repairTime, GravelLoadingEventTypes.DockRepaired, null, null, this));
					setCountRepairs(countRepairs, repairTime);
					countRepairs++;
				}

			}
		}
	}

	public static void setCountRepairs(int repairNumber, long repairTime)
	{

		allRepairTime.put(repairNumber, repairTime);


	}
	public static HashMap<Integer, Long> getRepairTime()
	{


		return allRepairTime;


	}


}
