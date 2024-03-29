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

import dev.despg.core.Event;
import dev.despg.core.EventQueue;
import dev.despg.core.Randomizer;
import dev.despg.core.SimulationObject;
import dev.despg.core.SimulationObjects;
import dev.despg.core.TrackerType;

public class LoadingDock extends SimulationObject
{
	private final String name;
	private Truck truckCurrentlyLoaded;
	private boolean dockFailed;

	private static EventQueue eventQueue;

	private static Randomizer loadingWeight;
	private static Randomizer loadingTime;
	private static Randomizer drivingToWeighingStation;

	private static Randomizer dockFailureRepairTime;

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
		loadingWeight.addProbInt(0.3, 34);
		loadingWeight.addProbInt(0.6, 38);
		loadingWeight.addProbInt(1.0, 41);

		loadingTime = new Randomizer();
		loadingTime.addProbInt(0.3, 60);
		loadingTime.addProbInt(0.8, 120);
		loadingTime.addProbInt(1.0, 180);

		drivingToWeighingStation = new Randomizer();
		drivingToWeighingStation.addProbInt(0.5, 30);
		drivingToWeighingStation.addProbInt(0.78, 45);
		drivingToWeighingStation.addProbInt(1.0, 60);

		dockFailureRepairTime = new Randomizer();
		dockFailureRepairTime.addProbInt(0.7, 0);
		dockFailureRepairTime.addProbInt(0.8, 360);		// quarter day
		dockFailureRepairTime.addProbInt(0.9, 720);		// half day
		dockFailureRepairTime.addProbInt(0.95, 1440);	// one day
		dockFailureRepairTime.addProbInt(1.0, 2880);	// two days
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
	 * <p>
	 * If it is not currently occupied ({@link #truckCurrentlyLoaded} == null) and
	 * the simulation goal still is not archived, it checks if there is an event in
	 * the queue that got assigned to this class with the correct event description.
	 * Then proceeds in checking if the attached object is indeed a Truck. In that
	 * case the event gets removed from the queue and the event will get handled:
	 * {@link #truckCurrentlyLoaded} is set to the events attached object, and the
	 * truck gets loaded. Adds a new event to the event queue for when the loading
	 * is done and returns true.
	 * <p>
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
				trackerStop(TrackerType.Failure, timeStep);
			}
		}
		else if (truckCurrentlyLoaded == null && GravelShipping.getGravelToShip() > 0)
		{
			Event event = eventQueue.getNextEvent(timeStep, true, GravelLoadingEventTypes.Loading, this.getClass(), null);
			if (event != null && event.objectAttached() != null
					&& event.objectAttached().getClass() == Truck.class)
			{
				eventQueue.remove(event);

				truckCurrentlyLoaded = (Truck) event.objectAttached();
				truckCurrentlyLoaded.load(Math.min(loadingWeight.nextIntOnProp(), GravelShipping.getGravelToShip()));
				GravelShipping.setGravelToShip(GravelShipping.getGravelToShip() - truckCurrentlyLoaded.getLoad());

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
						drivingToWeighingStation.nextIntOnProp()), GravelLoadingEventTypes.Weighing,
						truckCurrentlyLoaded, WeighingStation.class, null));

				truckCurrentlyLoaded = null;
				trackerStop(TrackerType.Utilization, timeStep);

				// failure
				int failureInMinutes = dockFailureRepairTime.nextIntOnProp();
				if (failureInMinutes > 0)
				{
					eventQueue.add(new Event(timeStep + failureInMinutes, GravelLoadingEventTypes.DockRepaired, null, null, this));
					dockFailed = true;
					trackerStart(TrackerType.Failure, timeStep);
				}
			}
		}
	}
}
