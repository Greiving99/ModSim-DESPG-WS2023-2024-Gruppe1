package dev.despg.examples.administration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import dev.despg.core.Event;
import dev.despg.core.EventQueue;

final class LoadingDockFailedTest
{

	private LoadingDock loadingDock;
	private EventQueue eventQueue;
	private static final long NUMBER_OF_TESTS = 100000;

	/**
	 * Initializes a new LoadingDock object and initializes essential pointers to allow the simulate Method to work.
	 */
	@BeforeEach
	void init()
	{
		GravelShipping.setGravelToShip(100);
		LoadingDock.setTruckMechanic(new Mechanic("MechanicTruck" + LoadingDock.getNumTruckMechanic(),
				Employee.randomQuality())); LoadingDock.setNumTruckMechanic(1);
				LoadingDock.setMechanic(new Mechanic("MechanicLD" + LoadingDock.getTruckMechanic(),
						Employee.randomQuality())); LoadingDock.setNumMechanic(1);
						loadingDock = new LoadingDock("loadingDock", "Worker 1", 1);
						eventQueue = EventQueue.getInstance();
						LoadingDock.setMechanic(new Mechanic("MechanicWS" + loadingDock.getNumMechanic(),
								Employee.randomQuality())); LoadingDock.setNumMechanic(1);
	}

	/**
	 * Clearing up the EventQueue.
	 */
	@AfterEach
	void clear()
	{
		loadingDock = null;
		eventQueue.clear();
	}
	/**
	 * Tests if the station doesn't load trucks while its attribute stationFailed is true which marks that it needs to be repaired.
	 */
	@Test
	void dockFailedTests()
	{
		for (int i = 0; i < NUMBER_OF_TESTS; i++)
		{
			dockFailedTest();
			clear();
			init();
		}
	}

	void dockFailedTest()
	{
		long dockFailuresBefore = LoadingDock.getCounterFailureDock();
		/*
		 * We let the loadingDock load one Truck so that it has a chance to break down.
		 */
		eventQueue.add(new Event(0L, GravelLoadingEventTypes.Loading, new Truck("Truck 1", "Driver 1", 1), loadingDock.getClass(), null));
		loadingDock.simulate(0L);
		long loadingDoneTimeStep = eventQueue.getNextEvent(0L, false, GravelLoadingEventTypes.LoadingDone, null, loadingDock)
				.timeStep();
		loadingDock.simulate(loadingDoneTimeStep);
		assert (eventQueue.getNextEvent(0L, false, GravelLoadingEventTypes.WeighingDone, null, loadingDock) == null);

		if (LoadingDock.getCounterFailureDock() == dockFailuresBefore + 1)
		{
			/* If the loadingDock has a failure then it shouldn't consume an event with
			 * GravelLoadingEventTypes.Loading as UniqueEventDescription in the same TimeStep.
			 * Therefore we assert that an added event with GravelLoadingEventTypes.Loading as
			 * UniqueEventDescription still exists after letting the loadingDock simulate on that TimeStep.
			 * If it does exist then the eventQueue shouldn't return a null pointer as the next event with the
			 * UniqueEventDescription of GravelLoadingEventTypes.Loading. If it does return a null pointer then
			 * the loadingDock consumed the event while being broken and the tests fails.
			 */
			eventQueue.add(new Event(loadingDoneTimeStep, GravelLoadingEventTypes.Loading, new Truck("Truck 2", "Driver 2", 1),
					loadingDock.getClass(), null));
			loadingDock.simulate(loadingDoneTimeStep);
			loadingDock.simulate(loadingDoneTimeStep);
			assert (eventQueue.getNextEvent(0L, false, GravelLoadingEventTypes.Loading, null, null) != null);
		}
	}
}
