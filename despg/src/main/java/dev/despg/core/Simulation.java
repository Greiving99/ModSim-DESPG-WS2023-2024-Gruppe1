package dev.despg.core;

public abstract class Simulation
{
	/**
	 * Called at every timeStep where one or more events occurred.
	 */
	protected abstract void printEveryStep();

	/**
	 * After the Simulation loop is done, this method prints the utilization statistic of every Server as well as the average utilization of
	 * every Server class that initialized two or more Objects.
	 * @param timeStep TimeStep at the end of the simulation
	 */
	private void printPostSimStats(int timeStep)
	{
		System.out.println("----------------------------------");
		double utilSumPerSimClass = 0.0;
		int SumObjectsSimClass = 0;
		Class<? extends SimulationObject> simulationObjectClass = null;
		final SimulationObjects simulationObjects = SimulationObjects.getInstance();

		// only works all simulation objects of one class are adjacent stored in simulationobjects
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
					System.out.println(String.format("Utilization Class %s = %.2f %%", 
							simulationObjectClass.getName(), utilSumPerSimClass / SumObjectsSimClass));

				simulationObjectClass = simulationObject.getClass();
				utilSumPerSimClass = utilSimObject;
				SumObjectsSimClass = 1;
			}

			System.out.println(String.format("Utilization %s = %.2f %%", simulationObject, utilSimObject));
		}

		if (SumObjectsSimClass > 1)
			System.out.println(String.format("Utilization Class %s = %.2f %%", 
					simulationObjectClass.getName(), utilSumPerSimClass / SumObjectsSimClass));
		System.out.println("----------------------------------");
	}

	/** 
	 * The simulate() method contains the main loop of the simulation. For every timeStep it iterates
	 * over every SimulationObject and triggers its simulate() method. If no Event occurred during
	 * that timeStep, it switches to the timeStep of the next Event in the EventQueue.
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
			System.out.print(numberOfSteps++ + ". " + Time.stepsToString(timeStep) + " " + eventqueue);
			printEveryStep();

			boolean oneSwitched;
			do 
			{
				oneSwitched = false;

				for (SimulationObject so : simulationObjects)
				{
					if (so.simulate(timeStep)) 
					{
						oneSwitched = true;
						System.out.println("= " + so);
					}
				}	
			} while (oneSwitched);

			System.out.println();

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
}
