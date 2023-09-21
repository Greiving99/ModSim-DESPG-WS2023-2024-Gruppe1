/**
 * Copyright (C) 2021 despg.dev, Ralf Buschermöhle
 *
 * DESPG is made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * see LICENSE
 *
 */
package dev.despg.examples.carpool;


import java.text.DecimalFormat;
import java.util.Random;
import java.util.logging.Logger;
import java.util.logging.Level;

import dev.despg.core.Event;
import dev.despg.core.EventQueue;
import dev.despg.core.Randomizer;
import dev.despg.core.SimulationObject;
import dev.despg.core.SimulationObjects;
import dev.despg.core.Time;


public final class Truck extends SimulationObject
{
	private static final int PROB_TO_FAIL_RESET_VALUE = 17;
	private static final double MAX_ROUTE_PERCENT_UNTIL_FAIL = 0.55;
	private static final double MIN_ROUTE_PERCENT_UNTIL_FAIL = 0.35;
	private static final int HOUR_IN_MINUTES = 60;
	private static Logger logger = Logger.getLogger("GravelShipping");
	private String name;
	private Integer loadedWithTons;
	private static Randomizer drivingDistance;
	private static Randomizer initalMaintanceCounter;
	private static Randomizer crash;
	private static EventQueue eventQueue;
	private Random random = new Random();
	private int allDrivingTime;
	private Truck currentTruck;
	private int driveTimeToLoading = 20;
	private double odometer; 
	private int currentTravelTime; 

	private double fuelConsumption;
	private double totalConsumption;
	private double averageConsumption;
	private int numberOfTrips;
	private int probToFail;
	private double kmCounterToMaintaince; 
	private int drivingTimeSinceLastPause; // < 480 -> 8 hours drive time 
	private int remainingTravelTime;
	private int numberAccidents;
	private boolean accident;
	private boolean shutDown;
	private TruckModels model;


	public Truck(String name)
	{

		eventQueue = EventQueue.getInstance(); //

		drivingDistance = new Randomizer(); //
		drivingDistance.addProbInt(0.5, 120); // Lingen - Osnabrück 2 hours
		drivingDistance.addProbInt(0.7, 280); //4,66 hours
		drivingDistance.addProbInt(0.9, 500); // 8,33 hours
		drivingDistance.addProbInt(1.0, 1440); // 1 day

		initalMaintanceCounter = new Randomizer();
		initalMaintanceCounter.addProbInt(0.15, 1200);
		initalMaintanceCounter.addProbInt(0.35, 4892);
		initalMaintanceCounter.addProbInt(0.50, 13596);
		initalMaintanceCounter.addProbInt(0.75, 27485);
		initalMaintanceCounter.addProbInt(1.0, 46939);

		crash = new Randomizer();
		crash.addProbInt(0.999, 0);
		crash.addProbInt(1.0, 1);

		int randomIndex = random.nextInt(GravelShipping.getTruckModelsSize());
		model = GravelShipping.getTruckModel(randomIndex);
		this.setName(name + " " + model.getTruckBrand() + " " + model.getModelName());
		this.setFuelConsomption(model);

		setKmCounterToMaintaince(initalMaintanceCounter.nextInt());
		SimulationObjects.getInstance().add(this);

	}

	public void load(int weight)
	{
		loadedWithTons = weight;
	}

	public void unload()
	{
		loadedWithTons = null;
	}

	public Integer getLoad()
	{
		return loadedWithTons;
	}

	@Override
	public boolean simulate(long timeStep)
	{
		Event event = eventQueue.getNextEvent(timeStep, true, GravelLoadingEventTypes.TruckStart, this.getClass(), null);
		if (event != null)
		{
			eventQueue.remove(event);
			currentTruck = (Truck) event.objectAttached();
			int travelTime = drivingDistance.nextInt();
			long arrivalTime = timeStep + travelTime;
			currentTruck.currentTravelTime = travelTime;

			if (currentTruck.drivingTimeSinceLastPause + travelTime <= GravelShipping.getMaxDrivingTime())
			{
				if (currentTruck.probToFail > GravelShipping.getProbtoFail() || crash.nextInt() == 1)
				{
					int travelTimeToFail = (int) (travelTime * (MIN_ROUTE_PERCENT_UNTIL_FAIL
							+ Math.random() * MAX_ROUTE_PERCENT_UNTIL_FAIL)); //fährt 35%-90% strecke bevor er einen schaden hat
					currentTruck.currentTravelTime = travelTimeToFail;
					increaseTruckValues(currentTruck, travelTimeToFail);
					eventQueue.add(new Event(timeStep + travelTimeToFail + 60, GravelLoadingEventTypes.TruckWillFail,
							currentTruck, Truck.class, null));
					truckWillFailDrivingMessage(timeStep, travelTime);
					return true;
				}
				else
				eventQueue.add(new Event(arrivalTime, GravelLoadingEventTypes.TruckEnRoute, currentTruck, Truck.class, this));
				truckTravelingMessage(timeStep, travelTime);
				return true;
			}
			else if (currentTruck.drivingTimeSinceLastPause == GravelShipping.getMaxDrivingTime())
			{
				currentTruck.remainingTravelTime = travelTime;
				eventQueue.add(new Event(timeStep + GravelShipping.getPause(), GravelLoadingEventTypes.TruckDriverPause,
						currentTruck, Truck.class, this));
				return true;
			}
			else
			{
				currentTruck.remainingTravelTime = travelTime - (GravelShipping.getMaxDrivingTime() - currentTruck.drivingTimeSinceLastPause);
				
				eventQueue.add(new Event(timeStep + (GravelShipping.getMaxDrivingTime() - currentTruck.drivingTimeSinceLastPause),
						GravelLoadingEventTypes.TruckStartPartRoute, currentTruck, Truck.class, this));
				truckIsDrivingMessage(timeStep);
				return true;
			}
		}
		event = eventQueue.getNextEvent(timeStep, true, GravelLoadingEventTypes.TruckStartPartRoute, this.getClass(), null);
		if (event != null)
		{
			eventQueue.remove(event);
			currentTruck = (Truck) event.objectAttached();
			eventQueue.add(new Event(timeStep + GravelShipping.getPause(), GravelLoadingEventTypes.TruckDriverPause,
					currentTruck, Truck.class, this));
			truckPauseMessage(timeStep);
			return true;
		}
		event = eventQueue.getNextEvent(timeStep, true, GravelLoadingEventTypes.TruckDriverPause, this.getClass(), null);
		if (event != null)
		{
			eventQueue.remove(event);
			currentTruck = (Truck) event.objectAttached();
			currentTruck.drivingTimeSinceLastPause = 0; // fahrzeit auf null setzen

			if (currentTruck.drivingTimeSinceLastPause + currentTruck.remainingTravelTime <= GravelShipping.getMaxDrivingTime())
			{
				currentTruck.drivingTimeSinceLastPause += remainingTravelTime;
				eventQueue.add(new Event(timeStep + currentTruck.remainingTravelTime, GravelLoadingEventTypes.TruckEnRoute,
						currentTruck, Truck.class, this));
				return true;
			}
			else
			{
				currentTruck.remainingTravelTime = currentTruck.remainingTravelTime
						- (GravelShipping.getMaxDrivingTime() - currentTruck.drivingTimeSinceLastPause);
	
				eventQueue.add(new Event(timeStep + (GravelShipping.getMaxDrivingTime() - currentTruck.drivingTimeSinceLastPause),
						GravelLoadingEventTypes.TruckStartPartRoute, currentTruck, Truck.class, this));
				return true;
			}
		}
		event = eventQueue.getNextEvent(timeStep, true, GravelLoadingEventTypes.TruckWillFail, this.getClass(), null);
		if (event != null)
		{
			eventQueue.remove(event);
			currentTruck = (Truck) event.objectAttached();
			currentTruck.unload();
			if (currentTruck.probToFail > GravelShipping.getProbtoFail())
			{
				truckFailedWearMessage(timeStep);
			}
			else
			{
				truckFailAccidentMessage(timeStep);
				currentTruck.numberAccidents++;
				currentTruck.setAccident(true);
			}
			eventQueue.add(new Event(timeStep + 60, GravelLoadingEventTypes.TruckFailed, currentTruck, TruckRepairShop.class, null));
			return true;

		}

		event = eventQueue.getNextEvent(timeStep, true, GravelLoadingEventTypes.TruckEnRoute, this.getClass(), null);
		if (event != null)
		{
			eventQueue.remove(event);
			currentTruck = (Truck) event.objectAttached();
			currentTruck.unload();
			currentTruck.allDrivingTime += currentTruck.currentTravelTime;
			currentTruck.numberOfTrips++;
			increaseTruckValues(currentTruck, currentTravelTime);
			eventQueue.add(new Event(timeStep + driveTimeToLoading, GravelLoadingEventTypes.TruckBack, currentTruck, Truck.class, null));
			if (GravelShipping.isTruckDrivingMessage())
			{
				logger.log(Level.INFO, Time.stepsToTimeString(timeStep) + " " + GravelLoadingEventTypes.TruckBack.get()
				+ " -> " + currentTruck.getName());
			}
			return true;
		}
		event = eventQueue.getNextEvent(timeStep, true, GravelLoadingEventTypes.TruckBack, this.getClass(), null);
		if (event != null)
		{
			eventQueue.remove(event);
			currentTruck = (Truck) event.objectAttached();
			if (timeStep < GravelShipping.getSimTime())
			{
				if (currentTruck.kmCounterToMaintaince + 1000 >= GravelShipping.getMaintenanceInterval())
				{
					truckNeedInspectionMessage(timeStep);
					eventQueue.add(new Event(timeStep + 60, GravelLoadingEventTypes.TruckInspection,
							currentTruck, TruckRepairShop.class, null));
					return true;
				}
				else
				{
					currentTruck.probToFail += random.nextInt((6));
					eventQueue.add(new Event(timeStep + driveTimeToLoading, GravelLoadingEventTypes.Loading,
							currentTruck, LoadingDock.class, null));
					return true;
				}
			}
			return false;
		}
		return false;
	}

	private void truckNeedInspectionMessage(long timeStep)
	{
		if (GravelShipping.isTruckInspectionMessage())
		{
			logger.log(Level.INFO,
					Time.stepsToTimeString(timeStep) + " " + GravelLoadingEventTypes.TruckInspection.get() + " "
							+ currentTruck.name + " " + currentTruck.kmCounterToMaintaince);
		}
	}

	private void truckFailAccidentMessage(long timeStep)
	{
		if (GravelShipping.isTruckFailedRepairMessage())
		{
			logger.log(Level.INFO, Time.stepsToTimeString(timeStep) + " " + GravelLoadingEventTypes.TruckFailed.get() + " "
					+ currentTruck.name + " Unfall ! " + currentTruck.numberAccidents + ". Unfall");
		}
	}

	private void truckFailedWearMessage(long timeStep)
	{
		if (GravelShipping.isTruckFailedRepairMessage())
		{
			logger.log(Level.INFO, Time.stepsToTimeString(timeStep) + " " + GravelLoadingEventTypes.TruckFailed.get() + " "
					+ currentTruck.name + " ProbToFail: " + currentTruck.probToFail + " Wartung KM Stand : "
					+ currentTruck.kmCounterToMaintaince);  // ausgabe für logger entweder per arraylist oder toString
		}
	}

	private void truckPauseMessage(long timeStep)
	{
		if (GravelShipping.isTruckPauseMessage())
		{
			logger.log(Level.INFO, Time.stepsToTimeString(timeStep) + " "
					+ GravelLoadingEventTypes.TruckDriverPause.get() + " "
					+ currentTruck.name + " macht Pause und verbleibnde Fahrzeit =  "
					+ Time.stepsToTimeString(currentTruck.remainingTravelTime));
		}
	}

	private void truckIsDrivingMessage(long timeStep)
	{
		if (GravelShipping.isTruckDrivingMessage())
		{
			logger.log(Level.INFO, Time.stepsToTimeString(timeStep) + " " + GravelLoadingEventTypes.TruckStartPartRoute.get()
			+ " -> " + currentTruck.getName() + " Fahrzeit nach Pause: "
					+ Time.stepsToTimeString(currentTruck.remainingTravelTime));
		}
	}

	private void truckTravelingMessage(long timeStep, int travelTime)
	{
		if (GravelShipping.isTruckDrivingMessage())
		{
			logger.log(Level.INFO, Time.stepsToTimeString(timeStep) + " " + GravelLoadingEventTypes.TruckEnRoute.get()
			+ " -> " + currentTruck.getName() + " Fahrzeit: " + Time.stepsToTimeString(travelTime));
		}
	}

	private void truckWillFailDrivingMessage(long timeStep, int travelTime)
	{
		if (GravelShipping.isTruckDrivingMessage())
		{
			logger.log(Level.INFO, Time.stepsToTimeString(timeStep) + " " + GravelLoadingEventTypes.TruckWillFail.get()
			+ " -> " + currentTruck.getName() + " Fahrzeit: " + Time.stepsToTimeString(travelTime));
		}
	}

	private void increaseTruckValues(Truck truck, int travelTime)
	{
		truck.kmCounterToMaintaince += travelTimeToKm(travelTime);
		double deviation = (Math.random() * 10) / 100;
		currentTruck.averageConsumption += truck.fuelConsumption * (1.0 + deviation); //abweichung vom verbruach
		currentTruck.totalConsumption += (truck.fuelConsumption
				* (travelTimeToKm(travelTime) / 100)) * (1.0 + deviation); //verbrauch
		setOdometer(truck, travelTimeToKm(travelTime)); //km stand erhöhen
		truck.kmCounterToMaintaince += travelTimeToKm(travelTime);
	}

	private double travelTimeToKm(int travelTime)
	{
		double km = (double) travelTime / HOUR_IN_MINUTES * GravelShipping.getVmax();
		return km;
	}


	public double getAllDrivingTime()
	{
		return allDrivingTime;

	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}
	public void setFuelConsomption(TruckModels model)
	{
		this.fuelConsumption = model.getFuelConsumption();
	}
	public void setKmCounterToMaintaince(double kmCounterToMaintaince)
	{
		this.kmCounterToMaintaince = kmCounterToMaintaince;
	}
	public static String getName(Truck truck)
	{
		String truckName = truck.name;
		return truckName;
	}

	public void setDrivingTimeSinceLastPause(int drivingTimeSinceLastPause)
	{
		this.drivingTimeSinceLastPause = drivingTimeSinceLastPause;
	}

	public int getDrivingTimeSinceLastPause()
	{
		return drivingTimeSinceLastPause;
	}

	public String getTotalAccidents()
	{
		String resultString = name + " Anzahl Unfälle: " + numberAccidents;
		return resultString;
	}

	public String getFinalOdometer()
	{
		String truckName = name;
		String formattedName = String.format("%-30s", truckName);
		DecimalFormat f = new DecimalFormat("#0.00");
		String formattedKmStand = String.format("%-11s", f.format(odometer));
		String result = String.format("%s Kilometer-Stand: %s km", formattedName, formattedKmStand);
		return result;
	}

	private void setOdometer(Truck truck, double km)
	{
		truck.odometer += km;
	}

	public static double getKMStandOnly(Truck truck)
	{
		return truck.odometer;
	}

	public String getFinalConsumption()
	{
		DecimalFormat f = new DecimalFormat("#0.00");
		String result = name + " Gesamt Diesel-Verbrauch " + f.format(totalConsumption) + " Liter" + " -> Gesamtkosten: "
				+ f.format(totalConsumption * GravelShipping.getDieselPrice()) + "€\n"
				+ "\t\tAngegebner Durschnittsverbrauch: " + fuelConsumption + "l/100KM || Real Durchnischtsverbrauch:  "
				+ f.format(averageConsumption / numberOfTrips) + " l/100 KM\n";

		return result;
	}

	public static int getNumberAccidents(Truck truck)
	{
		return truck.numberAccidents;
	}

	public boolean isAccident()
	{
		return accident;
	}

	public void setProbToFail()
	{
		probToFail = PROB_TO_FAIL_RESET_VALUE;
	}

	public void setAccident(boolean accident)
	{
		this.accident = accident;
	}

	public boolean isShutDown()
	{
		return shutDown;
	}

	public void setShutDown(boolean shutDown)
	{
		this.shutDown = shutDown;
	}
}
