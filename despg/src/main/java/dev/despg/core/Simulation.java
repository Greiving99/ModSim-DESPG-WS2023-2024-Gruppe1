package dev.despg.core;

import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class Simulation
{
	static
	{
		String path = Simulation.class.getClassLoader().getResource("logging.properties").getFile();
		System.setProperty("java.util.logging.config.file", path);
	}

	private static Logger logger = Logger.getLogger("dev.despg.core.Simulation");

	/**
	 * Called at every timeStep where one or more events occurred.
	 */
	protected abstract void printEveryStep(int numberOfSteps, int timeStep);

	/**
	 * The simulate() method contains the main loop of the simulation. For every
	 * timeStep it iterates over every SimulationObject and triggers its simulate()
	 * method. If no Event occurred during that timeStep, it switches to the
	 * timeStep of the next Event in the EventQueue.
	 * 
	 * @return timeStep after the simulation is over
	 */
	public int simulate()
	{
		EventQueue eventqueue = EventQueue.getInstance();
		SimulationObjects simulationObjects = SimulationObjects.getInstance();
		int numberOfSteps = 1;
		int timeStep = 0;
		Event e = null;

		do
		{
			printEveryStep(numberOfSteps, timeStep);

			numberOfSteps++;

			boolean oneSwitched;
			do
			{
				oneSwitched = false;

				for (SimulationObject so : simulationObjects)
				{
					if (so.simulate(timeStep))
					{
						oneSwitched = true;
						logger.log(Level.FINEST, timeStep + " switched" + so);
					}
				}
			} while (oneSwitched);

			// progressing in time (step is 'done')
			timeStep++;

			// switch time to next event
			e = eventqueue.getNextEvent(timeStep, false, null, null, null);
			if (e != null)
				timeStep = e.getTimeStep();

		} while (e != null);

		timeStep--; // correction after last step
		printPostSimStats(timeStep);
		return timeStep;
	}

	/**
	 * After the Simulation loop is done, this method prints the utilization
	 * statistic of every Server as well as the average utilization of every Server
	 * class that initialized two or more Objects.
	 * 
	 * @param timeStep TimeStep at the end of the simulation
	 */
	private void printPostSimStats(int timeStep)
	{
		logger.log(Level.INFO, "----------------------------------");
		double utilSumPerSimClass = 0.0;
		int SumObjectsSimClass = 0;
		Class<? extends SimulationObject> simulationObjectClass = null;
		final SimulationObjects simulationObjects = SimulationObjects.getInstance();

		// only works all simulation objects of one class are adjacent stored in
		// simulationobjects
		for (SimulationObject simulationObject : simulationObjects)
		{
			double utilSimObject = (double) simulationObject.getTimeUtilized() / timeStep * 100;

			if (simulationObjectClass == simulationObject.getClass())
			{
				utilSumPerSimClass += utilSimObject;
				SumObjectsSimClass++;
			}
			else
			{
				if (simulationObjectClass != null && SumObjectsSimClass > 1)
					logger.log(Level.INFO, String.format("Utilization Class %s = %.2f %%",
							simulationObjectClass.getName(), utilSumPerSimClass / SumObjectsSimClass));

				simulationObjectClass = simulationObject.getClass();
				utilSumPerSimClass = utilSimObject;
				SumObjectsSimClass = 1;
			}

			logger.log(Level.INFO, String.format("Utilization %s = %.2f %%", simulationObject, utilSimObject));
		}

		if (SumObjectsSimClass > 1)
			logger.log(Level.INFO, String.format("Utilization Class %s = %.2f %%", simulationObjectClass.getName(),
					utilSumPerSimClass / SumObjectsSimClass));
		logger.log(Level.INFO, "----------------------------------");
	}
}
