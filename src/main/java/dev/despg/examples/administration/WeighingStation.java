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

import dev.despg.core.Event;
import dev.despg.core.EventQueue;
import dev.despg.core.SimulationObject;
import dev.despg.core.SimulationObjects;
import dev.despg.core.TrackerType;


public final class WeighingStation extends SimulationObject
{
	//private static final int TRUCKREPAIRCOST = 100;
	private static final int STATIONREPAIRCOST = 125;
	private static final double TIMETOWEIGHCOST = 0.34;
	private static final int TIME_TO_WEIGH_TRUCK = 1000;
	private static final int MAXLOAD = 40;

	private String name;
	private Truck truckInWeighingStation;
	private Boolean stationFailed;
	private static Mechanic mechanic;
	private static boolean newMechanic;
	private static int numMechanic;


	private static double fixKosten;
	//private static int counterFailureTruck;
	private static int counterFailureStation;


	private static EventQueue eventQueue;

	/**
	 * Constructor for new WeightingStations, injects its dependency to
	 * SimulationObjects and creates the required randomizer instances.
	 *
	 * @param name Name of the WeightingStation instance
	 */
	public WeighingStation(String name)
	{
		this.name = name;
		stationFailed = false;
		eventQueue = EventQueue.getInstance();
		SimulationObjects.getInstance().add(this);
	}

	@Override
	public String toString()
	{
		String toString = "Weighing Station:" + name;
		if (truckInWeighingStation != null)
			toString += " " + "loading: " + truckInWeighingStation;
		return toString;
	}

	/**
	 * Gets called every timeStep
	 *
	 * Checks events from the event queue that either are assigned to this class or
	 * to an object of this class. If it is assigned to this class, the object of
	 * which the simulate function got called, checks if it is currently occupied
	 * and if the attached object is indeed a truck. In that case, the event gets
	 * removed from the queue, gets executed and a new event gets added to the queue
	 * which gets triggered when the weighting is done.
	 *
	 * A "weighting is done" event gets pulled from the queue if the receiving
	 * object is the object on which the simulate function got called on. In that
	 * case the event gets removed from the queue and handled by checking if trucks
	 * load is above the maximum allowed load or not. If it is above, it will count
	 * as an unsuccessful loading, else it will count ass successful and be shipped.
	 * In either case there will be a new event added to the event queue with no
	 * difference in parameters.
	 *
	 * @return true if an assignable event got found and handled, false if no event
	 *         could get assigned
	 */
	@SuppressWarnings("static-access")
	@Override
	public void simulate(long timeStep)
	{

		//When we hire a new mechanic for the weighing station, it is created here.

		if (newMechanic)
		{
			setMechanic(new Mechanic("MechanikerWS" + numMechanic, Administration.getQualityMechanic()));
			Administration.setMechanicHired(Administration.getMechanicHired() + 1);
			numMechanic++;
			newMechanic = false;

		}

		//When a station is out of order, the 'StationRepaired' event is removed from the EventQueue.

		if (stationFailed)
		{
			Event event = eventQueue.getNextEvent(timeStep, true, GravelLoadingEventTypes.StationRepaired, null, this);
			if (event != null)
			{
				eventQueue.remove(event);
				stationFailed = false;
				return;
			}
		}
		else
		{

			/* Here, the 'Weighing' event is removed from the EventQueue.
			 * The truck is weighed and added to the queue with the 'WeighingDone' event.
			 * Then, the costs for weighing are calculated at 0.34 euros per minute.*/

			//Event event = eventQueue.getNextEvent(timeStep, true, GravelLoadingEventTypes.Weighing, this.getClass(), null);
			Event event = eventQueue.getNextEvent(timeStep, true, GravelLoadingEventTypes.Weighing, this.getClass(), null);
			if (truckInWeighingStation == null && event != null && event.objectAttached() != null)
			{
				eventQueue.remove(event);
				truckInWeighingStation = (Truck) event.objectAttached();

				eventQueue.add(new Event(timeStep + truckInWeighingStation.addTimeStepDelta(TrackerType.Utilization, TIME_TO_WEIGH_TRUCK),
						GravelLoadingEventTypes.WeighingDone, truckInWeighingStation, null, this));

				trackerStart(TrackerType.Utilization, timeStep);
				setFixCost(getFixCost() + TIMETOWEIGHCOST * TIME_TO_WEIGH_TRUCK);
				return;
			}

			/* Here, the 'WeighingDone' event is dequeued from the queue. Then, it is checked whether the cargo in the truck exceeds the allowed
			 * value.
			 * If yes, the truck is sent back to the loading dock;
			 * if not, the truck is added to the EventQueue with the 'Deloading' event, which means the truck is sent to the customer to unload
			 * the cargo.*/
			event = eventQueue.getNextEvent(timeStep, true, GravelLoadingEventTypes.WeighingDone, null, this);
			if (event != null && event.objectAttached() != null && event.objectAttached().getClass() == Truck.class)
			{
				eventQueue.remove(event);
				final Integer truckToWeighLoad = truckInWeighingStation.getLoad();
				long driveToCustomer;
				long driveToLoadingStation;

				if (truckToWeighLoad != null && truckToWeighLoad > MAXLOAD)
				{
					GravelShipping.setGravelToShip(GravelShipping.getGravelToShip() + truckToWeighLoad);
					GravelShipping.increaseUnsuccessfulLoadingSizes(truckToWeighLoad);
					GravelShipping.increaseUnsuccessfulLoadings();
					driveToLoadingStation = truckInWeighingStation.addTimeStepDelta(TrackerType.Utilization,
							truckInWeighingStation.getDriver().getDrivingToLoadingDock());
					truckInWeighingStation.unload();
					eventQueue.add(new Event(timeStep + driveToLoadingStation, GravelLoadingEventTypes.Loading,
							truckInWeighingStation, LoadingDock.class, null));
				}
				else if (truckToWeighLoad != null && truckToWeighLoad < MAXLOAD)
				{

					driveToCustomer = truckInWeighingStation.addTimeStepDelta(TrackerType.Utilization,
							truckInWeighingStation.getDriver().getDrivingToCustomer());
					eventQueue.add(new Event(timeStep + driveToCustomer, GravelLoadingEventTypes.Deloading,
							truckInWeighingStation, Customer.class, null));
				}

				trackerStop(TrackerType.Utilization, timeStep);

				//Here, the breakdown of the weighing station and/or the mechanic is simulated, and the repair costs are calculated.

				int	repairTime = getMechanic().getStationFailureRepairTime();
				if (repairTime > 0)
				{
					counterFailureStation++;
					repairTime += getMechanic().failureTime();
					Administration.setRepairCost(Administration.getRepairCost() + STATIONREPAIRCOST * repairTime);
					stationFailed = true;
					this.addTimeStepDelta(TrackerType.Failure, repairTime);
					eventQueue.add(new Event(timeStep + repairTime, GravelLoadingEventTypes.StationRepaired, null, null, this));
				}

				truckInWeighingStation.truckFailed(timeStep);
				truckInWeighingStation = null;

			}
		}
	}
	/**
	 * This method recursively filters for events that have Truck Objects attacked to filter out events with trucks that need to be repaired.
	 * @param timeStep        Gets events from this timeStep forwards
	 * @param past            Include events from the past
	 * @param eventTypeNumber Filter for specific event type
	 * @param receiverClass   Filter for specific receiving class
	 * @param receiverObject  Filter for specific receiving object
	 * @return returns the Event in the EventQueue with the lowest timeStep which
	 *         matches the defined filters or null of no Event could be filtered
	 */
	/*
	private Event findNonFailedTruckEvent(long timeStep, boolean past,
			UniqueEventDescription eventDescription, Class<? extends SimulationObject> receiverClass, SimulationObject receiver)
	{
		Event event = eventQueue.getNextEvent(timeStep, past, eventDescription, receiverClass, receiver);
		Truck truck = null;
		if (event != null)
			truck = (Truck) event.objectAttached();
		LinkedList<Event> events = new LinkedList<Event>();
		while (event != null && truck != null && truck.getTruckFailed())
		{
			eventQueue.remove(event);
			events.add(event);

			event = eventQueue.getNextEvent(timeStep, past, eventDescription, receiverClass, receiver);
			if (event != null)
				truck = (Truck) event.objectAttached();
		}

		for (Event e : events)
		{
			eventQueue.add(e);
		}

		return event;
	}
	*/

	public static Mechanic getMechanic()
	{
		return mechanic;
	}
	public static void setMechanic(Mechanic mechanic)
	{
		WeighingStation.mechanic = mechanic;
	}

	public static Double getFixCost()
	{
		return fixKosten;
	}

	public static void setFixCost(double fixCost)
	{
		WeighingStation.fixKosten = fixCost;
	}
	public static int getCounterFailureTruck()
	{
		return Truck.getCounterFailureTruck();
	}


	public static int getCounterFailureStation()
	{
		return counterFailureStation;
	}

	public static void setCounterFailureStation(int counterFailureStation)
	{
		WeighingStation.counterFailureStation = counterFailureStation;
	}
	public static void setNewMechanic(boolean newMechanic)
	{
		WeighingStation.newMechanic = newMechanic;
	}
	public static int getNumMechanic()
	{
		return numMechanic;
	}

	public static void setNumMechanic(int numMechanic)
	{
		WeighingStation.numMechanic = numMechanic;
	}

}
