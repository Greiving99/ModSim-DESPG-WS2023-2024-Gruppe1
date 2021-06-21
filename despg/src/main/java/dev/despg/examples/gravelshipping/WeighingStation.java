package dev.despg.examples.gravelshipping;
import dev.despg.core.Event;
import dev.despg.core.EventQueue;
import dev.despg.core.Randomizer;
import dev.despg.core.SimulationObject;
import dev.despg.core.SimulationObjects;

public class WeighingStation extends SimulationObject
{
	private static final int TIME_TO_WEIGH_TRUCK = 10;
	private static final int MAXLOAD = 40;

	private String name = null;
	private Truck truckInWeighingStation = null;

	private static Randomizer drivingToUnloadDock = null;
	private static Randomizer drivingToLoadingDock = null;
	private static EventQueue eventQueue = null;

	/**
	 * Constructor for new WeightingStations, injects its dependency to SimulationObjects and creates the required randomizer instances.
	 * @param name Name of the WeightingStation instance
	 */
	public WeighingStation(String name)
	{
		this.name = name;

		eventQueue = EventQueue.getInstance(); 

		drivingToUnloadDock = new Randomizer();
		drivingToUnloadDock.addProbInt(0.5, 120);
		drivingToUnloadDock.addProbInt(0.8, 150);
		drivingToUnloadDock.addProbInt(1.0, 180);

		drivingToLoadingDock = new Randomizer();
		drivingToLoadingDock.addProbInt(0.5, 30);
		drivingToLoadingDock.addProbInt(1.0, 45);

		SimulationObjects.getInstance().add(this);
	}

	@Override
	public String toString()
	{
		return "Weighing Station:" + name + ", Truck:" + (truckInWeighingStation != null ? truckInWeighingStation : "---");
	}

	/**
	 *Gets called every timeStep
	 *
	 * Checks events from the event queue that either are assigned to this class or to an object of this class. If it is assigned to this class, the object
	 * of which the simulate function got called, checks if it is currently occupied and if the attached object is indeed a truck. In that case,
	 * the event gets removed from the queue, gets executed and a new event gets added to the queue which gets triggered when the weighting is done.
	 * 
	 * A "weighting is done" event gets pulled from the queue if the receiving object is the object on which the simulate function got called on. 
	 * In that case the event gets removed from the queue and handled by checking if trucks load is above the maximum allowed load or not. If it is above, 
	 * it will count as an unsuccessful loading, else it will count ass successful and be shipped. In either case there will be a new event added to the
	 * event queue with no difference in parameters.
	 * 
	 * @return true if an assignable event got found and handled, false if no event could get assigned
	 */
	@Override
	public boolean simulate(int timeStep)
	{
		Event event = eventQueue.getNextEvent(timeStep, true, GravelLoadingEventTypes.Weighing, this.getClass(), null);
		if (truckInWeighingStation == null 
				&& event != null && event.getObjectAttached() != null && event.getObjectAttached().getClass() == Truck.class)
		{
			eventQueue.remove(event);
			truckInWeighingStation = (Truck) event.getObjectAttached();
			eventQueue.add(new Event(timeStep + truckInWeighingStation.addUtilization(TIME_TO_WEIGH_TRUCK), 
					GravelLoadingEventTypes.WeighingDone, truckInWeighingStation, null, this));
			utilStart(timeStep);
			return true;
		}

		event = eventQueue.getNextEvent(timeStep, true, GravelLoadingEventTypes.WeighingDone, null, this);
		if (event != null && event.getObjectAttached() != null && event.getObjectAttached().getClass() == Truck.class)
		{
			eventQueue.remove(event);
			final Integer truckToWeighLoad = truckInWeighingStation.getLoad();
			int driveToLoadingStation;
			
			if (truckToWeighLoad != null && truckToWeighLoad > MAXLOAD)
			{
				GravelShipping.gravelToShip += truckToWeighLoad;					
				GravelShipping.unsuccessfulLoadingSizes += truckToWeighLoad;
				GravelShipping.unsuccessfulLoadings++;
				driveToLoadingStation = truckInWeighingStation.addUtilization(drivingToLoadingDock.nextInt());				
			}
			else
			{
				GravelShipping.gravelShipped += truckToWeighLoad;
				GravelShipping.successfulLoadingSizes += truckToWeighLoad;
				GravelShipping.successfulLoadings++;
				driveToLoadingStation = truckInWeighingStation.addUtilization(drivingToLoadingDock.nextInt());
			}
			eventQueue.add(new Event(timeStep + driveToLoadingStation, GravelLoadingEventTypes.Loading, 
					truckInWeighingStation, LoadingDock.class, null));
			
			truckInWeighingStation.unload();
			truckInWeighingStation = null;
			utilStop(timeStep);
			return true;
		}

		return false;
	}
}
