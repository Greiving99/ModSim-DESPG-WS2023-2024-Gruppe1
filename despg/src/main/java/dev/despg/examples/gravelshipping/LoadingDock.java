package dev.despg.examples.gravelshipping;
import dev.despg.core.Event;
import dev.despg.core.EventQueue;
import dev.despg.core.Randomizer;
import dev.despg.core.SimulationObject;
import dev.despg.core.SimulationObjects;

public class LoadingDock extends SimulationObject
{
	private String name = null;
	private Truck truckCurrentlyLoaded = null;
	
	private static EventQueue eventQueue = null;
	
	private static Randomizer loadingWeight = null;
	private static Randomizer loadingTime = null;
	private static Randomizer drivingToWeighingStation = null;
	

	public LoadingDock(String name)
	{
		this.name = name;

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
	}

	@Override
	public String toString()
	{
		return "Loading Dock:" + name + " Truck:" + (truckCurrentlyLoaded != null ? truckCurrentlyLoaded : "---");
	}

	@Override
	public boolean simulate(int timeStep)
	{	
		if (truckCurrentlyLoaded == null 
				&& GravelShipping.gravelToShip > 0)
		{
			Event event = eventQueue.getNextEvent(timeStep, true, GravelLoadingEventTypes.Loading, this.getClass(), null);
			if (event != null 
					&& event.getObjectAttached() != null 
					&& event.getObjectAttached().getClass() == Truck.class)
			{
				eventQueue.remove(event);
				
				truckCurrentlyLoaded = (Truck) event.getObjectAttached();
				truckCurrentlyLoaded.load(Math.min(loadingWeight.nextInt(), GravelShipping.gravelToShip));
				GravelShipping.gravelToShip -= truckCurrentlyLoaded.getLoad();

				eventQueue.add(new Event(timeStep + truckCurrentlyLoaded.addUtilization(loadingTime.nextInt()), 
						GravelLoadingEventTypes.LoadingDone, truckCurrentlyLoaded, null, this));
								
				utilStart(timeStep);
				return true;
			}
		}
		else
		{
			Event event = eventQueue.getNextEvent(timeStep, true, GravelLoadingEventTypes.LoadingDone, null, this);
			if (event != null 
					&& event.getObjectAttached() != null 
					&& event.getObjectAttached().getClass() == Truck.class)
			{
				eventQueue.remove(event);				
				eventQueue.add(new Event(timeStep + event.getObjectAttached().addUtilization(drivingToWeighingStation.nextInt()), 
						GravelLoadingEventTypes.Weighing, truckCurrentlyLoaded, WeighingStation.class, null));
				
				truckCurrentlyLoaded = null;
				utilStop(timeStep);
				return true;
			}
		}

		return false;
	}
}
