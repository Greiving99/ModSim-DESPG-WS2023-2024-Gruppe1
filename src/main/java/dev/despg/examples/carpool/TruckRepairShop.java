package dev.despg.examples.carpool;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import dev.despg.core.Event;
import dev.despg.core.EventQueue;
import dev.despg.core.Randomizer;
import dev.despg.core.SimulationObject;
import dev.despg.core.SimulationObjects;
import dev.despg.core.Time;



public final class TruckRepairShop extends SimulationObject
{

	private static final int REPAIR_TIME_ACCIDENT_DAMAGE = 20160 * 3;
	private static Logger logger = Logger.getLogger("GravelShipping");
	private String name;
	private static EventQueue eventQueue;
	private Truck currentTruck;
	private static Randomizer repairTime;
	private static Randomizer truckTotalDamage;
	private static final int INSPECTION_TIME = 1440;
	private static final int DIAGNOSE_TIME = 60;
	private static int allRepairTime;
	private static List<String> allRepairs = new ArrayList<String>();
	private static List<String> allShutDownTrucks = new ArrayList<String>();

	public TruckRepairShop(String name)
	{
		this.name = name;
		eventQueue = EventQueue.getInstance();

		repairTime = new Randomizer();
		repairTime.addProbInt(0.3, 180); // z.B. reifenpanne
		repairTime.addProbInt(0.5, 720);
		repairTime.addProbInt(0.90, 1440);
		repairTime.addProbInt(0.97, 7200);
		repairTime.addProbInt(0.95, 12960);
		repairTime.addProbInt(1.0, 20160); //schwere schäden 14 Tage


		truckTotalDamage = new Randomizer();
		truckTotalDamage.addProbInt(0.9, 0);
		truckTotalDamage.addProbInt(1.0, 1);

		SimulationObjects.getInstance().add(this);
	}

	public static String tString()
	{
		String toString = "Insgesamt sind LKWs " + (allRepairTime / 60) / 24 + " Tage durch Reperaturen/Inspektion ausgefallen";
	
		return toString;
	}
	@Override
	public boolean simulate(long timeStep)
	{
		Event event = eventQueue.getNextEvent(timeStep, true, GravelLoadingEventTypes.TruckFailed, this.getClass(), null); // failed trucks aus queue

		if (event != null)
		{
			eventQueue.remove(event);
			currentTruck = (Truck) event.objectAttached();
			eventQueue.add(new Event(timeStep + DIAGNOSE_TIME, GravelLoadingEventTypes.TruckDiagnosis,
					currentTruck, TruckRepairShop.class, this)); // diagnose starten
			return true;
		}
		event = eventQueue.getNextEvent(timeStep, true, GravelLoadingEventTypes.TruckDiagnosis, this.getClass(), null);

		if (event != null)
		{
			eventQueue.remove(event);
			currentTruck = (Truck) event.objectAttached();
			int tempRepairTime = repairTime.nextInt();
			if (currentTruck.isAccident())
			{
				if (truckTotalDamage.nextInt() == 1)
				{
					
					logger.log(Level.INFO, Time.stepsToTimeString(timeStep) + " " + Truck.getName(currentTruck)
					+ " Totalschaden -> LKW kann nicht mehr repariert werden");

					currentTruck.setShutDown(true);
					shutDownTrucks(currentTruck, timeStep);
					eventQueue.add(new Event(timeStep, GravelLoadingEventTypes.GetBestOfferForNewTruck,
							currentTruck, PurchasingDepartment.class, null));
					return true;
				}
				else
				{
					tempRepairTime = REPAIR_TIME_ACCIDENT_DAMAGE; // bei unfall höchste reparaturzeit * 3
				}

			}
			allRepairTime += tempRepairTime;
			if (currentTruck.getDrivingTimeSinceLastPause() - tempRepairTime <= 0)
			{
				currentTruck.setDrivingTimeSinceLastPause(0);
			}
			else
			{
				currentTruck.setDrivingTimeSinceLastPause(currentTruck.getDrivingTimeSinceLastPause() - tempRepairTime);
			}
			if (Truck.getNumberAccidents(currentTruck) > 2)
			{

				logger.log(Level.INFO, Time.stepsToTimeString(timeStep) + " "
				+ Truck.getName(currentTruck) + " lohnt sich nicht reperiert zu werden");
				shutDownTrucks(currentTruck, timeStep);
				eventQueue.add(new Event(timeStep, GravelLoadingEventTypes.GetBestOfferForNewTruck,
						currentTruck, PurchasingDepartment.class, null));
				return true;
			}
			else
				if (GravelShipping.isTruckFailedRepairMessage())
				{
					logger.log(Level.INFO, Time.stepsToTimeString(timeStep) + " " + GravelLoadingEventTypes.TruckInRepair.get() + " "
						+ Truck.getName(currentTruck) + " Repair - Time: " + Time.stepsToTimeString(tempRepairTime));
				}
			performedRepair(currentTruck, tempRepairTime);
			eventQueue.add(new Event(timeStep + tempRepairTime, GravelLoadingEventTypes.TruckInRepair,
					currentTruck, TruckRepairShop.class, this)); // repairtime festlegen
			GravelShipping.setNumberOfFailure(); // ausfall erhöhen für mtbf
			currentTruck.setAccident(false);
			return true;
		}
		event = eventQueue.getNextEvent(timeStep, true, GravelLoadingEventTypes.TruckInRepair, this.getClass(), null);

		if (event != null)
		{
			eventQueue.remove(event);
			currentTruck = (Truck) event.objectAttached();
			eventQueue.add(new Event(timeStep + 30, GravelLoadingEventTypes.TruckRepaired,
					currentTruck, TruckRepairShop.class, this)); // repair fertig
			return true;
		}

		event = eventQueue.getNextEvent(timeStep, true, GravelLoadingEventTypes.TruckRepaired, this.getClass(), null);

		if (event != null)
		{
			eventQueue.remove(event);
			currentTruck = (Truck) event.objectAttached();
			//currentTruck.setKmStandWartung(0);
			currentTruck.setProbToFail();
			if (GravelShipping.isTruckFailedRepairMessage())
			{
				logger.log(Level.INFO, Time.stepsToTimeString(timeStep) + " "
					+ GravelLoadingEventTypes.TruckRepaired.get() + " " + Truck.getName(currentTruck));
			}
			eventQueue.add(new Event(timeStep + 30, GravelLoadingEventTypes.Loading,
					currentTruck, LoadingDock.class, null)); //reparieter truck wird beladen
			return true;
		}
		event = eventQueue.getNextEvent(timeStep, true, GravelLoadingEventTypes.TruckInspection, this.getClass(), null);

		if (event != null)
		{
			eventQueue.remove(event);
			currentTruck = (Truck) event.objectAttached();
			allRepairTime += INSPECTION_TIME;
			currentTruck.setDrivingTimeSinceLastPause(0);
			eventQueue.add(new Event(timeStep + INSPECTION_TIME, GravelLoadingEventTypes.TruckInspectionDone,
					currentTruck, TruckRepairShop.class, this)); // inspektion fertig
			return true;
		}
		event = eventQueue.getNextEvent(timeStep, true, GravelLoadingEventTypes.TruckInspectionDone, this.getClass(), null);

		if (event != null)
		{
			eventQueue.remove(event);
			currentTruck = (Truck) event.objectAttached();
			currentTruck.setKmCounterToMaintaince(0);
			currentTruck.setProbToFail();
			if (GravelShipping.isTruckInspectionMessage())
			{
				logger.log(Level.INFO, Time.stepsToTimeString(timeStep) + " " + GravelLoadingEventTypes.TruckInspectionDone.get() + " "
					+ Truck.getName(currentTruck));
			}
			eventQueue.add(new Event(timeStep + 30, GravelLoadingEventTypes.Loading, currentTruck, LoadingDock.class, null));
			return true;
		}
		return false;

	}

	public static void performedRepair(Truck t, int tempRepairTime)
	{
		String name = Truck.getName(t);
		String repName = getRepairName(tempRepairTime);
		String formattedName = String.format("%-30s", name);
		String formaRepName = String.format("%-40s", repName);
		String repair = String.format("%s druchgeführte Reparatur: %s | Dauer der Reparatur: "
				+ Time.stepsToTimeString(tempRepairTime), formattedName, formaRepName);

		allRepairs.add(repair);

	}
	public static void shutDownTrucks(Truck t, long timeStep)
	{
		String truckName = Truck.getName(t);
		String formattedName = String.format("%-30s", truckName);
		String shutdownMessage;
		//DecimalFormat f = new DecimalFormat("#0.00");
		if (t.isShutDown())
		{
		shutdownMessage = String.format(Time.stepsToTimeString(timeStep)
				+ "\t%s Totalschaden!! Konnte nicht mehr repariert werden! Finaler KM-Stand: %.2f km",
				formattedName, Truck.getKMStandOnly(t));
		}
		else
		{
		shutdownMessage = String.format(Time.stepsToTimeString(timeStep)
				+ "\t%s stillgelgt. Lohnt nicht mehr zu reparieren, Finaler KM-Stand: %.2f km",
				formattedName, Truck.getKMStandOnly(t));
		}
		allShutDownTrucks.add(shutdownMessage);
	}
	public static String getRepairName(int repairTime)
	{
		String repairName = " ";
		Random random = new Random();

		if (repairTime == 180)
		{
		    // Array of possible repairs for 180 minutes (3 hours)
		    String[] repairs = {"Austausch von Glühbirnen",
		                        "Auswechseln von Luftfiltern",
		                        "Austausch von Scheibenwischern",
		                        "Austausch von Bremsbelägen"};
		    // Random selection of a repair from the array.
		    repairName = repairs[random.nextInt(repairs.length)];
		} else if (repairTime == 720)
		{
		    // Array of possible repairs for 720 minutes (12 hours)
		    String[] repairs = {"Austausch von Bremsen",
		                        "Austausch von Kühlerschläuchen",
		                        "Reparatur von Lichtmaschinen",
		                        "Austausch von Stoßdämpfern"};
		    repairName = repairs[random.nextInt(repairs.length)];
		} else if (repairTime == 1440)
		{
		    //  Array of possible repairs for 1440 minutes (24 hours)
		    String[] repairs = {"Austausch der Antriebswelle",
		                        "Austausch von Stoßfängern",
		                        "Ersetzen von Lenkgetrieben",
		                        "Reparatur von Motoren"};
		    repairName = repairs[random.nextInt(repairs.length)];
		} else if (repairTime == 7200)
		{
		    // Array of possible repairs for 7200 minutes (5 days)
		    String[] repairs = {"Austausch von Getriebe",
		                        "Reparatur von Karosseriebeschädigungen",
		                        "Reparatur von Elektronikproblemen"};
		    repairName = repairs[random.nextInt(repairs.length)];
		} else if (repairTime == 12960)
		{
		    // Array of possible repairs for 12960 minutes (9 days)
		    String[] repairs = {"Rostbeseitigung und Neulackierung",
		                        "Austausch Chassis",
		                        "Einbau von Spezialteilen"};
		    repairName = repairs[random.nextInt(repairs.length)];
		} else if (repairTime == 20160)
		{
		    //  Array of possible repairs for 20160 minutes (14 days)
		    String[] repairs = {"Austausch Antriebswelle",
		                        "Überholung des ganzen Fahrzeug"};
		    repairName = repairs[random.nextInt(repairs.length)];

		} else repairName = "Wiederaufbau nach schweren Unfallschäden";


		return repairName;

	}
	public static List<String> getAllRepairs()
	{
		return allRepairs;
	}

	public static List<String> getAllShutDownTrucks()
	{
		return allShutDownTrucks;
	}
}
