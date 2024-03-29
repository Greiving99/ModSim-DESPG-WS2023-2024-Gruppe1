/**
 * Copyright (C) 2021 despg.dev, Ralf Buschermöhle
 * <p>
 * DESPG is made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * <p>
 * see LICENSE
 *
 */
package dev.despg.examples.gravelshipping;

import java.util.logging.Level;
import java.util.logging.Logger;

import dev.despg.core.Event;
import dev.despg.core.EventQueue;
import dev.despg.core.Simulation;
import dev.despg.core.Time;

public class GravelShipping extends Simulation
{
	private static final Logger LOG = Logger.getLogger("GravelShipping");

	private static Integer gravelToShip = 2000;
	private static Integer gravelShipped = 0;
	private final int gravelToShippedFinal = gravelToShip;

	private static Integer successfulLoadings = 0;
	private static Integer successfulLoadingSizes = 0;

	private static Integer unsuccessfulLoadings = 0;
	private static Integer unsuccessfulLoadingSizes = 0;

	private static final int NUM_TRUCKS = 5;
	private static final int NUM_LOADING_DOCKS = 3;
	private static final int NUM_WEIGHING_STATIONS = 2;

	/**
	 * Defines the setup of simulation objects and starting events before executing
	 * the simulation. Prints utilization statistics afterwards
	 *
	 * @param args not used
	 */
	public static void main(String[] args)
	{
		EventQueue eventqueue = EventQueue.getInstance();

		for (int i = 0; i < NUM_TRUCKS; i++)
			eventqueue.add(new Event(0L, GravelLoadingEventTypes.Loading, new Truck("T" + i), LoadingDock.class, null));

		for (int i = 0; i < NUM_LOADING_DOCKS; i++)
			new LoadingDock("LD" + i);

		for (int i = 0; i < NUM_WEIGHING_STATIONS; i++)
			new WeighingStation("WS" + i);

		GravelShipping gs = new GravelShipping();
		long timeStep = gs.simulate();

		// output some statistics after simulation run
		LOG.log(Level.INFO, "Gravel shipped\t\t = " + gravelShipped + " tons");
		LOG.log(Level.INFO, "Mean Time / Gravel Unit\t = " + ((double) timeStep / gravelShipped) + " minutes");

		LOG.log(Level.INFO,
				String.format("Successfull loadings\t = %d(%.2f%%), mean size %.2ft", successfulLoadings,
						(double) successfulLoadings / (successfulLoadings + unsuccessfulLoadings) * 100,
						(double) successfulLoadingSizes / successfulLoadings));

		LOG.log(Level.INFO,
				String.format("Unsuccessfull loadings\t = %d(%.2f%%), mean size %.2ft", unsuccessfulLoadings,
						(double) unsuccessfulLoadings / (successfulLoadings + unsuccessfulLoadings) * 100,
						(double) unsuccessfulLoadingSizes / unsuccessfulLoadings));
	}

	/**
	 * Prints information after every timeStep in which an event got triggered.
	 */
	@Override
	protected void printEveryStep(long numberOfSteps, long timeStep)
	{
		String time = numberOfSteps + " " + Time.stepsToDateString(timeStep);
		String eventQueue = "EventQueue: " + EventQueue.getInstance().toString();

		int numberOfTrucksLoadingQueue = EventQueue.getInstance().countEvents(timeStep, true, GravelLoadingEventTypes.Loading, null, null);
		int numberOfTrucksWeighingQueue = EventQueue.getInstance().countEvents(timeStep, true, GravelLoadingEventTypes.Weighing, null, null);

		String shipped = String.format("- %dt / %dt (%.2f%%)", gravelShipped, gravelToShip,
				(double) gravelShipped / gravelToShippedFinal * 100);

		LOG.log(Level.INFO, time + " " + shipped
				+ " #Trucks Loading: " + numberOfTrucksLoadingQueue + ", #Trucks Weighing: " + numberOfTrucksWeighingQueue);
		LOG.log(Level.CONFIG, eventQueue);
	}

	public static Integer getGravelToShip()
	{
		return gravelToShip;
	}

	public static void setGravelToShip(Integer gravelToShip)
	{
		GravelShipping.gravelToShip = gravelToShip;
	}

	public static Integer getGravelShipped()
	{
		return gravelShipped;
	}

	public static void increaseGravelShipped(Integer gravelShipped)
	{
		GravelShipping.gravelShipped += gravelShipped;
	}

	public static void increaseSuccessfulLoadings()
	{
		successfulLoadings++;
	}

	public static void increaseSuccessfulLoadingSizes(Integer successfulLoadingSizes)
	{
		GravelShipping.successfulLoadingSizes += successfulLoadingSizes;
	}

	public static void increaseUnsuccessfulLoadings()
	{
		GravelShipping.unsuccessfulLoadings++;
	}

	public static void increaseUnsuccessfulLoadingSizes(Integer unsuccessfulLoadingSizes)
	{
		GravelShipping.unsuccessfulLoadingSizes += unsuccessfulLoadingSizes;
	}
}
