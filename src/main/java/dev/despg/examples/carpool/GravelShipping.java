/**
 * Copyright (C) 2021 despg.dev, Ralf Buschermöhle & Fuhrpark Grupppe 2023
 *
 * DESPG is made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * see LICENSE
 *
 */
package dev.despg.examples.carpool;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import dev.despg.core.Event;
import dev.despg.core.EventQueue;
import dev.despg.core.Simulation;
import dev.despg.core.Time;

public class GravelShipping extends Simulation
{
	private static Logger logger = Logger.getLogger("GravelShipping");
	private static List<Truck> allInstances = new ArrayList<>();
	private static List<TruckModels> alltruckModels = new ArrayList<>();

	private static Integer gravelToShip = 2000;
	private static Integer gravelShipped = 0;
	private final int gravelToShippedFinal = gravelToShip;

	private static final int SIM_TIME = 525600 * 5;
	// 7200 = 5 days / 525600 = 1 year
	private static final int VMAX = 75;
	//average speed
	private static final int PROB_TO_FAIL = 350;
	private static final int MAINTENANCE_INTERVAL = 100000;
	private static final int MAX_DRIVING_TIME = 480;
	// 480 = 8 hours // 600 = 10 hours
	private static final int PAUSE = 720;
	//12 hours
	private static final double DIESEL_PRICE = 1.828;

	private static Integer successfulLoadings = 0;
	private static Integer successfulLoadingSizes = 0;

	private static Integer unsuccessfulLoadings = 0;
	private static Integer unsuccessfulLoadingSizes = 0;

	private static final int NUM_TRUCKS = 5;
	private static final int NUM_LOADING_DOCKS = 3;
	private static final int NUM_WEIGHING_STATIONS = 2;

	private static final int REPAIR_SHOP_CAPACITY = 2;

	private static final boolean TRUCK_PAUSE_MESSAGE = false;
	private static final boolean TRUCK_DRIVING_MESSAGE = false;
	private static final boolean TRUCK_FAILED_REPAIR_MESSAGE = false;
	private static final boolean NEW_TRUCK_MESSAGE = false;
	private static final boolean TRUCK_INSPECTION_MESSAGE = false;

	private static int numberOfFailure;

	/**
	 * Defines the setup of simulation objects and starting events before executing
	 * the simulation. Prints utilization statistics afterwards
	 *
	 * @param args not used
	 */
	public static void main(String[] args)
	{
		EventQueue eventqueue = EventQueue.getInstance();

		fillTruckModels();
		for (int i = 0; i < NUM_TRUCKS; i++)
		{
			Truck n = new Truck("Truck " + i);
			eventqueue.add(new Event(0L, GravelLoadingEventTypes.Loading, n, LoadingDock.class, null));
			allInstances.add(n);
		}

		for (int i = 0; i < NUM_LOADING_DOCKS; i++)
			new LoadingDock("LD" + i);

		for (int i = 0; i < NUM_WEIGHING_STATIONS; i++)
			new WeighingStation("WS" + i);

		for (int i = 0; i < REPAIR_SHOP_CAPACITY; i++)
			new TruckRepairShop("RS" + i);

		new PurchasingDepartment("PD 1");


		GravelShipping gs = new GravelShipping();
		long timeStep = gs.simulate();

		// output some statistics after simulation run
		logger.log(Level.INFO, "-------------------------------");
		logger.log(Level.INFO, "Simulation duration: " + Time.stepsToTimeString(SIM_TIME));
		logger.log(Level.INFO, "Gravel shipped: " + gravelShipped + " tons");
		logger.log(Level.INFO, "Driven routes: " + successfulLoadings);
		logger.log(Level.INFO, "-------------------------------");
		logger.log(Level.INFO, "Truck - Informationen");
		logger.log(Level.INFO, TruckRepairShop.tString());
		logger.log(Level.INFO, "Total failures = " + numberOfFailure);
		if (getMTBF() == 0.00)
		{
			logger.log(Level.INFO, "MTBF could not be calculated as there were no failures.");
		}
		else
		{
			logger.log(Level.INFO, "MTBF = " + getMTBF() + " Corresponds to: " + Time.stepsToTimeString((long) getMTBF()));
		}
		logger.log(Level.INFO, "-------------------------------");
		for (Truck truck : allInstances)
		{
			logger.log(Level.INFO, truck.getTotalAccidents());
		}
		logger.log(Level.INFO, "-------------------------------");
		for (Truck truck : allInstances)
		{
			logger.log(Level.INFO, truck.getFinalOdometer()
					+ " Total travel time: " +  Time.stepsToTimeString((long) truck.getAllDrivingTime()));
		}
		logger.log(Level.INFO, "-------------------------------");
		for (Truck truck : allInstances)
		{
			logger.log(Level.INFO, truck.getFinalConsumption());
		}
		logger.log(Level.INFO, "-------------------------------");
		logger.log(Level.INFO, "Available truck offers");

		for (Offer s : PurchasingDepartment.getTruckOfferCopy())
		{
			logger.log(Level.INFO, s.toString());
		}
		logger.log(Level.INFO, "-------------------------------");
		logger.log(Level.INFO, "All performed repairs");

		for (String s : TruckRepairShop.getAllRepairs())
		{
			logger.log(Level.INFO, s);

		}
		logger.log(Level.INFO, "-------------------------------");
		logger.log(Level.INFO, "All decommissioned trucks");

		for (String s : TruckRepairShop.getAllShutDownTrucks())
		{

			logger.log(Level.INFO, s);
		}
	}

	/**
	 * Prints information after every timeStep in which an event got triggered.
	 */
	protected void printEveryStep(long numberOfSteps, long timeStep)
	{

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

	public static void setNumberOfFailure()
	{
		numberOfFailure++;
	}
	public static double getMTBF()
	{
		int summedDrivingTime = 0;

		for (Truck truck : allInstances)
		{
			summedDrivingTime += truck.getAllDrivingTime();
		}
		if (numberOfFailure == 0)
			return 0.00;

		return summedDrivingTime / numberOfFailure;
	}

	public static void fillTruckModels()
	{
		alltruckModels.add(new TruckModels("Mercedes", "Arctros", 37.83));
		alltruckModels.add(new TruckModels("MAN", "TGX", 41.34));
		alltruckModels.add(new TruckModels("Volvo", "FH16", 40.12));
		alltruckModels.add(new TruckModels("Scania", "R730", 38.87));
		alltruckModels.add(new TruckModels("Reault", "T", 32.59));

	}
	public static TruckModels getTruckModel(int index)
	{

		return alltruckModels.get(index);

	}
	public static int getTruckModelsSize()
	{
		return alltruckModels.size();
	}
	public static void setAllInstances(Truck t)
	{
		allInstances.add(t);
	}

	public static int getSimTime()
	{
		return SIM_TIME;
	}
	public static int getVmax()
	{
		return VMAX;
	}
	public static int getMaintenanceInterval()
	{
		return MAINTENANCE_INTERVAL;
	}
	public static int getProbtoFail()
	{
		return PROB_TO_FAIL;
	}
	public static int getMaxDrivingTime()
	{
		return MAX_DRIVING_TIME;
	}
	public static int getPause()
	{
		return PAUSE;
	}
	public static double getDieselPrice()
	{
		return DIESEL_PRICE;
	}
	public static boolean isTruckPauseMessage()
	{
		return TRUCK_PAUSE_MESSAGE;
	}

	public static boolean isTruckDrivingMessage()
	{
		return TRUCK_DRIVING_MESSAGE;
	}

	public static boolean isNewTruckMessage()
	{
		return NEW_TRUCK_MESSAGE;
	}

	public static boolean isTruckInspectionMessage()
	{
		return TRUCK_INSPECTION_MESSAGE;
	}

	public static boolean isTruckFailedRepairMessage()
	{
		return TRUCK_FAILED_REPAIR_MESSAGE;
	}
}

