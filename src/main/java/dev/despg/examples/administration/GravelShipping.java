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

import java.util.logging.Level;

import java.util.logging.Logger;

import dev.despg.core.Event;
import dev.despg.core.EventQueue;
import dev.despg.core.Simulation;
import dev.despg.core.Time;

public class GravelShipping extends Simulation
{
	private static Logger logger = Logger.getLogger("GravelShipping");

	private static Integer gravelToShip = 2000;
	private static Integer gravelShipped = 0;
	private final int gravelToShippedFinal = gravelToShip;

	private static Integer successfulLoadings = 0;
	private static Integer successfulLoadingSizes = 0;

	private static Integer unsuccessfulLoadings = 0;
	private static Integer unsuccessfulLoadingSizes = 0;

	private static Integer successfulOrder = 0;

	private static final int NUM_TRUCKS = 5;
	private static final int NUM_LOADING_DOCKS = 3;
	private static final int NUM_WEIGHING_STATIONS = 2;
	private static int numDriver = NUM_TRUCKS;
	private static int numLoadingDocks = NUM_LOADING_DOCKS;
	private static int numMechanic;
	private static int numLoadingDockWorker;

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
				{
				eventqueue.add(new Event(0L, GravelLoadingEventTypes.Loading,
					new Truck("T" + i, "Fahrer" + i, Employee.randomQuality()), LoadingDock.class, null));
				}

		for (int i = 0; i < NUM_LOADING_DOCKS; i++)
			new LoadingDock("LD" + i, "Lagerarbeiter" + i, Employee.randomQuality());

		for (int i = 0; i < NUM_WEIGHING_STATIONS; i++)
			new WeighingStation("WS" + i);

			new Customer("Kunde");

		LoadingDock.setTruckMechanic(new Mechanic("MechanicTruck" + LoadingDock.getNumTruckMechanic(),
				Employee.randomQuality())); LoadingDock.setNumTruckMechanic(1);
		LoadingDock.setMechanic(new Mechanic("MechanicLD" + LoadingDock.getTruckMechanic(),
				Employee.randomQuality())); LoadingDock.setNumMechanic(1);
		WeighingStation.setMechanic(new Mechanic("MechanicWS" + WeighingStation.getNumMechanic(),
				Employee.randomQuality())); WeighingStation.setNumMechanic(1);

		GravelShipping gs = new GravelShipping();

		long timeStep = gs.simulate();

		// output some statistics after simulation run

		logger.log(Level.INFO, "Gravel shipped\t\t = " + gravelShipped + " tons");

		logger.log(Level.INFO, "Mean Time / Gravel Unit\t = " + ((double) timeStep / gravelShipped) + " minutes");

		logger.log(Level.INFO,
				String.format("Successfull loadings\t = %d(%.2f%%), mean size %.2ft", successfulLoadings,
						(double) successfulLoadings / (successfulLoadings + unsuccessfulLoadings) * 100,
						(double) successfulLoadingSizes / successfulLoadings));

		logger.log(Level.INFO,
				String.format("Unsuccessfull loadings\t = %d(%.2f%%), mean size %.2ft", unsuccessfulLoadings,
						(double) unsuccessfulLoadings / (successfulLoadings + unsuccessfulLoadings) * 100,
						(double) unsuccessfulLoadingSizes / unsuccessfulLoadings));
		logger.log(Level.INFO,
				String.format("Successfull Orders\t \t = " + successfulOrder));

		logger.log(Level.INFO,
					String.format("Bewerber \t\t\t = Lagerarbeiter = " + Administration.getNumApplicantLoadingDockWorker()
					+ "\t Fahrer = " + Administration.getNumApplicantDriver()
					+ "\t Mechaniker = " + Administration.getNumApplicantMechanic()));

		/* Here, the hired workers should be displayed.
	logger.log(Level.INFO,
			String.format("Eingestellt \t\t = Lagerarbeiter = " + Verwaltung.getLagerarbeiterEingestellt()
				+ "\t Fahrer = " + Verwaltung.getFahrerEingestellt() + "\t Mechaniker = "
				+  Verwaltung.getMechanikerEingestellt()));*/
		logger.log(Level.INFO,
				String.format("Outage \t\t\t = LKW = " + WeighingStation.getCounterFailureTruck()
							+ "\t \t Station = " + WeighingStation.getCounterFailureStation()
							+ "\t Dock = " +  LoadingDock.getCounterFailureDock()
							+ "\t Sick = " + Administration.getCounterEmployeeSick()));
		logger.log(Level.INFO,
				String.format("Totalcost\t\t =  %.2f\' Euro", Administration.getTotalCost()));
		logger.log(Level.INFO,
				String.format("Revenue\t\t\t =  %.2f\' Euro", Administration.getRevenue()));
		logger.log(Level.INFO,
				String.format("Profit\t\t\t =  %.2f\' Euro", Administration.getRevenue() - Administration.getTotalCost()));
		logger.log(Level.INFO,
				String.format("Sum credit\t\t =  %.2f\' Euro", Businessaccount.getSumCredit()));

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
		int numberOfTrucksDeloadingQueue = EventQueue.getInstance().countEvents(timeStep, true, GravelLoadingEventTypes.Deloading, null, null);

		String shipped = String.format("- %dt / %dt (%.2f%%)", gravelShipped, gravelToShip,
				(double) gravelShipped / gravelToShippedFinal * 100);

		logger.log(Level.INFO, time + " " + shipped
				+ " #Loading: " + numberOfTrucksLoadingQueue
				+ ", #Weighing: " + numberOfTrucksWeighingQueue
				+ ", #Customer: " + numberOfTrucksDeloadingQueue
				+ ", #timeStep: " + timeStep);
		logger.log(Level.CONFIG, eventQueue);
	}

	public static Integer getGravelToShip()
	{
		return gravelToShip;
	}

	public static void setGravelToShip(Integer gravelToShip)
	{
		GravelShipping.gravelToShip = gravelToShip;
	}
	public static Integer getsuccessfulOrder()
	{
		return successfulOrder;
	}

	public static void setsuccessfulOrder(Integer successfulOrder)
	{
		GravelShipping.successfulOrder = successfulOrder;
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
	public static int getNumDriver()
	{
		return numDriver;
	}
	public static void setNumberDriver(int numberDriver)
	{
		numDriver = numberDriver;
	}

	public static int getNumberLoadingDocks()
	{
		return numLoadingDocks;
	}

	public static void setNumberLoadingDocks(int numberLoadingDocks)
	{
		numLoadingDocks = numberLoadingDocks;
	}

	public static int getNumberMechaniker()
	{
		return numMechanic;
	}

	public static void setNumberMechaniker(int numMechanic)
	{
		GravelShipping.numMechanic = numMechanic;
	}

	public static int getNumberLagerarbeiter()
	{
		return numLoadingDockWorker;
	}

	public static void setNumberLagerarbeiter(int numLoadingDockWorker)
	{
		GravelShipping.numLoadingDockWorker = numLoadingDockWorker;
	}
}



