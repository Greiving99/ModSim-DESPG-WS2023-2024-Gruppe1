package dev.despg.examples.administration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import dev.despg.core.Event;
import dev.despg.core.EventQueue;

final class WeighingStationFailedTest
{

	private WeighingStation weighingStation;
	private EventQueue eventQueue;
	private static final long NUMBER_OF_TESTS = 100000;

	/**
	 * Initializes a new weighingStation and initializes essential pointers to allow the simulate Method to work.
	 */
	@BeforeEach
	void init()
	{
		LoadingDock.setTruckMechanic(new Mechanic("MechanicTruck" + LoadingDock.getNumTruckMechanic(),
				Employee.randomQuality())); LoadingDock.setNumTruckMechanic(1);
				LoadingDock.setMechanic(new Mechanic("MechanicLD" + LoadingDock.getTruckMechanic(),
						Employee.randomQuality())); LoadingDock.setNumMechanic(1);
						weighingStation = new WeighingStation("weighingStation");
						eventQueue = EventQueue.getInstance();
						WeighingStation.setMechanic(new Mechanic("MechanicWS" + WeighingStation.getNumMechanic(),
								Employee.randomQuality())); WeighingStation.setNumMechanic(1);
	}

	/**
	 * Clearing up the EventQueue.
	 */
	@AfterEach
	void clear()
	{
		weighingStation = null;
		eventQueue.clear();
	}
	/**
	 * Tests if the station doesn't weigh trucks while its attribute stationFailed is true which marks that it needs to be repaired.
	 */
	@Test
	void stationFailedTests()
	{
		for (int i = 0; i < NUMBER_OF_TESTS; i++)
		{
			statioFailedTest();
			clear();
			init();
		}
	}

	void statioFailedTest()
	{
		long stationFailuresBefore = WeighingStation.getCounterFailureStation();
		/*
		 * We let the weighingStation weigh one Truck so that it has a chance to break down.
		 */
		eventQueue.add(new Event(0L, GravelLoadingEventTypes.Weighing, new Truck("Truck 1", "Driver 1", 1), weighingStation.getClass(), null));
		weighingStation.simulate(0L);
		long weighingFinishedTimeStep = eventQueue.getNextEvent(0L, false, GravelLoadingEventTypes.WeighingDone, null, weighingStation)
				.timeStep();
		weighingStation.simulate(weighingFinishedTimeStep);
		assert (eventQueue.getNextEvent(0L, false, GravelLoadingEventTypes.WeighingDone, null, weighingStation) == null);

		if (WeighingStation.getCounterFailureStation() == stationFailuresBefore + 1)
		{
			/* If the weighingStation has a failure then it shouldn't consume an event with
			 * GravelLoadingEventTypes.Weighing as UniqueEventDescription in the same TimeStep.
			 * Therefore we assert that an added event with GravelLoadingEventTypes.Weighing as
			 * UniqueEventDescription still exists after letting the weighingStation simulate that TimeStep.
			 * If it does exist then the eventQueue shouldn't return a null pointer as the next event with the
			 * UniqueEventDescription of GravelLoadingEventTypes.Weighing
			 */
			eventQueue.add(new Event(weighingFinishedTimeStep, GravelLoadingEventTypes.Weighing, new Truck("Truck 2", "Driver 2", 1),
					weighingStation.getClass(), null));
			weighingStation.simulate(weighingFinishedTimeStep);
			weighingStation.simulate(weighingFinishedTimeStep);
			assert (eventQueue.getNextEvent(0L, false, GravelLoadingEventTypes.Weighing, null, null) != null);
		}
	}
}
