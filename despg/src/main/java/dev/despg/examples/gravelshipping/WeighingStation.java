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
				GravelShipping.unsuccessfullLoadingSizes += truckToWeighLoad;
				GravelShipping.unsuccessfullLoadings++;
				driveToLoadingStation = truckInWeighingStation.addUtilization(drivingToLoadingDock.nextInt());				
			}
			else
			{
				GravelShipping.gravelShipped += truckToWeighLoad;
				GravelShipping.successfullLoadingSizes += truckToWeighLoad;
				GravelShipping.successfullLoadings++;
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
