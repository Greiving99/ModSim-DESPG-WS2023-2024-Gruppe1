package dev.despg.examples.administration;

import dev.despg.core.Event;
import dev.despg.core.EventQueue;
import dev.despg.core.Randomizer;
import dev.despg.core.SimulationObject;
import dev.despg.core.SimulationObjects;
import dev.despg.core.TrackerType;



public class Customer extends SimulationObject
{

	private static final double DELOADINGCOST = 1.27;

	private String name;

	private static Integer gravelCustomer;
	private static Integer customerOrder;
	private static double fixCost;

	private static EventQueue eventQueue;

	private Truck truckatCustomer;

	private static Randomizer orderedGravelPrice;
	private static Randomizer orderedGravel;


	public Customer(String name)
	{
		this.name = name;

		Double profitMultiplier1 = 0.8;
		int profitMultiplier1Int = profitMultiplier1.intValue();
		Double profitMultiplier2 = 1.1;
		int profitMultiplier2Int = profitMultiplier2.intValue();
		Double profitMultiplier3 = 1.3;
		int profitMultiplier3Int = profitMultiplier3.intValue();

		eventQueue = EventQueue.getInstance();

		orderedGravel = new Randomizer();
		orderedGravel.addProbInt(0.3, 200);
		orderedGravel.addProbInt(0.7, 300);
		orderedGravel.addProbInt(1.0, 400);

		orderedGravelPrice = new Randomizer();
		orderedGravelPrice.addProbInt(0.6, profitMultiplier1Int);
		orderedGravelPrice.addProbInt(0.7, profitMultiplier2Int);
		orderedGravelPrice.addProbInt(1.0, profitMultiplier3Int);
		//Assigns a random number of gravel to the customer object.
		Customer.gravelCustomer = orderedGravel.nextIntOnProp();
		setCustomerOrder(getGravelCustomer());
		SimulationObjects.getInstance().add(this);

	}
	public final String toString()
	{
		String toString = "Kunde: " + name;
		if (truckatCustomer != null)
			toString += " " + "deloading:" + truckatCustomer;
		return toString;
	}
	/**
	 * Checks events from the event queue that either are assigned to this class or
	 * to an object of this class. If it is assigned to this class, the object of
	 * which the simulate function got called, checks if it is currently occupied
	 * and if the attached object is indeed a truck. In that case, the event gets
	 * removed from the queue, gets executed and a new event gets added to the queue
	 * which gets triggered when the Deloading is done. Deloading costs will be calculated
	 * based on the time the Driver needs to unload and a factor of 1.27 euros per minute.
	 *
	 * A "Deloading is done" event gets pulled from the queue if the receiving
	 * object is the object on which the simulate function got called on. In that
	 * case the event gets removed from the queue and handled by checking if trucks
	 * load is above the ordered amount of Gravel of the customer object of which this
	 * method is called.
	 * If it is above, it will unload the amount ordered and add the difference back to
	 * to GravelToShip. Additionally, a customer order is completed, requiring a new customer
	 * order to transport gravel again.
	 * Otherwise it will unload the entire amount loaded.
	 * In either case there will be a new event added to the event queue with no
	 * difference in parameters. Costs and revenues will be calculated and processed.
	 * Finally, the trucks are added to the EventQueue with the 'Loading' event, so they
	 * return to the LoadingDock.
	 */
	public void simulate(long timeStep)
	{

		/* The truck arrives at the customer, and if there is no truck at the customer, the 'Deloading' event is retrieved from the EventQueue.
		 * The unloading costs are calculated based on the time the driver takes to unload and a factor of 1.27 euros per minute.
		 * Then, the 'DeloadingDone' event is added to the truck and placed in the EventQueue.*/

		if (truckatCustomer == null)
		{
			Event event = eventQueue.getNextEvent(timeStep, true, GravelLoadingEventTypes.Deloading, this.getClass(), null);
			if (truckatCustomer == null && event != null && event.objectAttached() != null
					&& event.objectAttached().getClass() == Truck.class)
			{
				eventQueue.remove(event);
				truckatCustomer = (Truck) event.objectAttached();
				int deloadingTime =  truckatCustomer.getDriver().getDeloadingTime();
				eventQueue.add(new Event(timeStep
						+ truckatCustomer.addTimeStepDelta(TrackerType.Utilization, deloadingTime),
						 GravelLoadingEventTypes.DeloadingDone, truckatCustomer, null, this));
				trackerStart(TrackerType.Utilization, timeStep);
				fixCost += DELOADINGCOST * deloadingTime;
			}
		}
		else

			/*Here, the 'DeloadingDone' event is obtained. The truck is unloaded at the customer's location.
			 * If the cargo in the truck is greater than the gravel the customer still needs, the remaining gravel is unloaded,
			 * and the difference is added back to 'GravelToShip.' Additionally, a customer order is completed,
			 * requiring a new customer order to transport gravel again.*/

		{
			Event event = eventQueue.getNextEvent(timeStep, true, GravelLoadingEventTypes.DeloadingDone, null, this);
			if (event != null && event.objectAttached() != null && event.objectAttached().getClass() == Truck.class)
			{
				eventQueue.remove(event);
				Integer loadInTruck = truckatCustomer.getLoad();
				long driveToLoadingDock = truckatCustomer.getDriver().getDrivingToLoadingDock();
				int gravelLeft = this.getGravelCustomer();

				if (loadInTruck != null && loadInTruck >= gravelLeft)
				{
					loadInTruck -= gravelLeft;
					GravelShipping.increaseGravelShipped(gravelLeft);
					GravelShipping.increaseSuccessfulLoadingSizes(gravelLeft);
					double geld = getCustomerOrder() * (orderedGravelPrice.nextIntOnProp() * Administration.totalCost());
					Administration.setRevenue(Administration.getRevenue() + geld);
					Businessaccount.setBankBalance(Businessaccount.getBankBalance() + geld);

					//Here, the new order is created.
					this.setGravelCustomer(orderedGravel.nextIntOnProp());
					setCustomerOrder(getGravelCustomer());

					GravelShipping.setGravelToShip(GravelShipping.getGravelToShip() + loadInTruck);
					GravelShipping.increaseSuccessfulLoadings();
					truckatCustomer.addTimeStepDelta(TrackerType.Utilization, truckatCustomer.getDriver().getDrivingToLoadingDock());

					GravelShipping.setsuccessfulOrder(GravelShipping.getsuccessfulOrder() + 1);
					LoadingDock.setFixedCost(LoadingDock.getFixedCost()
							+ (Truck.getFuelCost() * driveToLoadingDock)); //Return trip costs.
					//Create applicants and hire them if one is better.
					Businessaccount.solvent(Businessaccount.getBankBalance(), Administration.totalCost());
					Administration.evaluate();
				}
				/* If the order is not yet completed, the cargo of the truck is subtracted from the gravel to be transported in the order.
				 * The costs are recalculated, and finally, the trucks are added to the EventQueue with the 'Loading' event,
				 * so they return to the LoadingDock.*/

				else if (loadInTruck  != null && loadInTruck < gravelLeft)
				{
					gravelLeft -= loadInTruck;
					this.setGravelCustomer(gravelLeft);
					truckatCustomer.addTimeStepDelta(TrackerType.Utilization, truckatCustomer.getDriver().getDrivingToLoadingDock());
					GravelShipping.increaseGravelShipped(loadInTruck);
					GravelShipping.increaseSuccessfulLoadingSizes(loadInTruck);
					GravelShipping.increaseSuccessfulLoadings();

					LoadingDock.setFixedCost(LoadingDock.getFixedCost()
					+ (Truck.getFuelCost() * driveToLoadingDock)); //Return trip costs.
				}

				eventQueue.add(new Event(timeStep + driveToLoadingDock, GravelLoadingEventTypes.Loading,
						truckatCustomer, LoadingDock.class, null));

				truckatCustomer.unload();
				// Should the chance for a truck Failing also be simulated after Truck unloads at the customer?
				// truckatCustomer.truckFailed(timeStep);
				truckatCustomer = null;
				trackerStop(TrackerType.Utilization, timeStep);
			}

		}
	}
	/**
	 *
	 * @return
	 */
	public Integer getGravelCustomer()
	{
		return gravelCustomer;
	}
	/**
	 *
	 * @return
	 */
	public void setGravelCustomer(int gravelLeft)
	{
		gravelCustomer = gravelLeft;
	}
	/**
	 *
	 * @return
	 */
	public static double getFixCost()
	{
		return fixCost;
	}
	/**
	 *
	 * @return
	 */
	public static void setFixCost(double fixCost)
	{
		Customer.fixCost = fixCost;
	}
	/**
	 *
	 * @return
	 */
	public static Integer getCustomerOrder()
	{
		return customerOrder;
	}
	/**
	 *
	 * @return
	 */
	public static void setCustomerOrder(Integer customerOrder)
	{
		Customer.customerOrder = customerOrder;
	}
	/**
	 *
	 * @return
	 */
	public static Integer getOrderedGravelPrice()
	{
		return orderedGravelPrice.nextIntOnProp();
	}

}
