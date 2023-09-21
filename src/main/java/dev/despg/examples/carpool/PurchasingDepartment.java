package dev.despg.examples.carpool;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import dev.despg.core.Event;
import dev.despg.core.EventQueue;
import dev.despg.core.SimulationObject;
import dev.despg.core.SimulationObjects;
import dev.despg.core.Time;


public final class PurchasingDepartment extends SimulationObject
{
	private static final int DRIVE_TIME_TO_REPAIR_SHOP = 60;
	private static Logger logger = Logger.getLogger("GravelShipping");
	private static final int MAX_OFFERS = 15;
	private static EventQueue eventQueue;
	@SuppressWarnings("unused")
	private String name;
	private static List<Offer> truckOffers = new ArrayList<>();
	private static List<Offer> truckOfferCopy = new ArrayList<>();
	private Random rand = new Random();
	private Truck currentTruck;



	public PurchasingDepartment(String name)
	{
		this.name = name;


		for (int i = 0; i <= MAX_OFFERS; i++)
		{
			truckOffers.add(new Offer(i, new Truck("Truck neu " + i), rand.nextInt(150001)
					+ 100000, rand.nextInt(41761) + 1440));
			//min 1440 (1 day) max 43200 (1 month) // price between 100000 and 250000 €

		}
		//Create a copy of the list of offers so that all available offers can be displayed at the end.
		for (Offer offer : truckOffers)
		{
			truckOfferCopy.add(offer);
		}

		eventQueue = EventQueue.getInstance();

		SimulationObjects.getInstance().add(this);
	}


	public String printNewTruck(Truck newTruck, int best)
	{
		String toString = "Neuer Truck gekauft: " + Truck.getName(newTruck) + " | Der neue LKW hat "
				+ truckOffers.get(best).getPrice() + "€ gekostet und eine Lieferzeit von "
				+ Time.stepsToTimeString(truckOffers.get(best).getDeliveryTime());
		return toString;
	}

	@Override
	public boolean simulate(long timeStep)
	{

		Event event = eventQueue.getNextEvent(timeStep, true, GravelLoadingEventTypes.GetBestOfferForNewTruck, this.getClass(), null);

		if (event != null)
		{
			eventQueue.remove(event);

			// Select the best from the list of truck offers Time.stepsToTimeString(timeStep)
			int bestOfferIndex = bestOffer();
			if (truckOffers.size() == 0)
			{
				logger.log(Level.INFO, Time.stepsToTimeString(timeStep) + " Keine neuen Angebote Verfügbar");
				return false;
			}
			int deliveryTime = truckOffers.get(bestOfferIndex).getDeliveryTime();

			// A new truck is selected from the list of truck offers, taking into account delivery time and price.
			currentTruck = truckOffers.get(bestOfferIndex).getOfferTruck();

			GravelShipping.setAllInstances(currentTruck);

			if (GravelShipping.isNewTruckMessage())
				{
					logger.log(Level.INFO, printNewTruck(currentTruck, bestOfferIndex));
				}

			eventQueue.add(new Event(timeStep + deliveryTime, GravelLoadingEventTypes.NewTruckDelivered,
			currentTruck, PurchasingDepartment.class, this));

			truckOffers.remove(bestOfferIndex);
			return true;
		}
		event = eventQueue.getNextEvent(timeStep, true, GravelLoadingEventTypes.NewTruckDelivered, this.getClass(), null);

		if (event != null)
		{
			eventQueue.remove(event);
			currentTruck = (Truck) event.objectAttached();
			eventQueue.add(new Event(timeStep + DRIVE_TIME_TO_REPAIR_SHOP, GravelLoadingEventTypes.TruckInspection,
					currentTruck, TruckRepairShop.class, this)); // nach dem truck geliefert wurde wird eine Inspektion durchgeführt
			// Message
			logger.log(Level.INFO, Time.stepsToTimeString(timeStep) + " "
					+ GravelLoadingEventTypes.NewTruckDelivered.get() + " " + Truck.getName(currentTruck));
			return true;
		}

		return false;
	}

	private int bestOffer()
	{

		int index = 0;
		double smallestScore = 100000;

		for (int i = 0; i < truckOffers.size(); i++)
		{

			if (truckOffers.get(i).getScore() < smallestScore)
			{
				index = i;
				smallestScore = truckOffers.get(i).getScore();
			}
		}

		return index;

	}

	public static List<Offer> getTruckOfferCopy()
	{
		return truckOfferCopy;
	}

}

