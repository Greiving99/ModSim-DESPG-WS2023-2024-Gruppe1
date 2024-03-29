/**
 * Copyright (C) 2021 despg.dev, Ralf Buschermöhle
 * <p>
 * DESPG is made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * <p>
 * see LICENSE
 *
 */
package dev.despg.core;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public abstract class Simulation
{
	private static final Logger LOG = Logger.getLogger(Simulation.class.getName());

	static
	{
		/*
		try
		{
			// location required under src/main/resources/logging.properties
			String path = Simulation.class.getClassLoader().getResource("logging.properties").getFile();
			System.setProperty("java.util.logging.config.file", path);
		}
		catch (Exception e)
		{
			logger.setLevel(Level.INFO);
			System.setProperty("java.util.logging.SimpleFormatter.format", "%4$s: %5$s %n");
		}
		*/

		Level level = Level.INFO;

		// global vs. local in each class
		Logger globalLogger = Logger.getLogger("");

		globalLogger.setLevel(level);
		for (Handler handler : globalLogger.getHandlers())
		{
			// String.format(format, date, source, logger, level, message, thrown);
			System.setProperty("java.util.logging.SimpleFormatter.format", "%4$s: %5$s %n");
			handler.setFormatter(new SimpleFormatter());
			handler.setLevel(level);
		}
	}

	/**
	 * Called at every timeStep where one or more events occurred.
	 *
	 * @param numberOfSteps number of simulation steps (main-loop)
	 * @param timeStep current timeStep
	 */
	protected abstract void printEveryStep(long numberOfSteps, long timeStep);

	/**
	 * The simulate() method contains the main loop of the simulation. For every
	 * timeStep it iterates over every SimulationObject and triggers its simulate()
	 * method. If no Event occurred during that timeStep, it switches to the
	 * timeStep of the next Event in the EventQueue.
	 *
	 * @return timeStep after the simulation is over
	 */
	public long simulate()
	{
		EventQueue eventqueue = EventQueue.getInstance();
		SimulationObjects simulationObjects = SimulationObjects.getInstance();
		long numberOfSteps = 1;
		long timeStep = 0;
		Event e;

		do
		{
			printEveryStep(numberOfSteps, timeStep);

			numberOfSteps++;

			boolean eventQueueStateChanged;
			do
			{
				eventQueueStateChanged = false;

				for (SimulationObject so : simulationObjects)
				{
					long eventQueueState = eventqueue.hashCode();
					so.simulate(timeStep);

					if (eventQueueState != eventqueue.hashCode())
					{
						eventQueueStateChanged = true;
						LOG.log(Level.FINEST, timeStep + " switched" + so);
					}
				}
			} while (eventQueueStateChanged);

			// progressing in time (step is 'done')
			timeStep++;

			// switch time to next event
			e = eventqueue.getNextEvent(timeStep, false, null, null, null);
			if (e != null)
				timeStep = e.timeStep();

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
	private void printPostSimStats(long timeStep)
	{
 		LOG.log(Level.INFO, "----------------------------------");
		double trackedSumClassValue = 0.0;

		Class<? extends SimulationObject> simulationObjectClass = null;
		final SimulationObjects simulationObjects = SimulationObjects.getInstance();

		for (TrackerType trackerType : TrackerType.values())
		{
			long sumObjectsSimClass = 0;

			// only works if all simulation objects of one class are adjacent stored in simulationobjects
			for (SimulationObject simulationObject : simulationObjects)
			{
				double trackedSimObjectValue = simulationObject.getTracker(trackerType).doubleValue() / timeStep * 100;

				if (simulationObjectClass == simulationObject.getClass())
				{
					trackedSumClassValue += trackedSimObjectValue;
					sumObjectsSimClass++;
				}
				else
				{
					if (simulationObjectClass != null && sumObjectsSimClass > 1)
						LOG.log(Level.INFO, String.format("%s Class %s = %.2f %%", trackerType,
								simulationObjectClass.getName(), trackedSumClassValue / sumObjectsSimClass));

					simulationObjectClass = simulationObject.getClass();
					trackedSumClassValue = trackedSimObjectValue;
					sumObjectsSimClass = 1;
				}

				LOG.log(Level.INFO, String.format("%s %s = %.2f %%", trackerType, simulationObject, trackedSimObjectValue));
			}

			if (sumObjectsSimClass > 1)
				LOG.log(Level.INFO, String.format("%s Class %s = %.2f %%", trackerType, simulationObjectClass.getName(),
						trackedSumClassValue / sumObjectsSimClass));
			LOG.log(Level.INFO, "----------------------------------");
		}
	}
}
