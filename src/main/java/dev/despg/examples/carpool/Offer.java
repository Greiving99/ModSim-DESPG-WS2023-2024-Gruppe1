package dev.despg.examples.carpool;


import dev.despg.core.Time;

public class Offer
	{

	@SuppressWarnings("unused")
	private int offerNumber;
	private Truck offerTruck;
	private double price;
	private int deliveryTime;
	private double score;

	public Offer(int offerNumber, Truck offerTruck, double price, int deliveryTime)
	{
		this.offerNumber = offerNumber;
		this.offerTruck = offerTruck;
		this.price = price;
		this.deliveryTime = deliveryTime;
		this.score = (price * 0.1) + (deliveryTime * 0.4);

	}

	public final String toString()
	{
		String name = getOfferTruck().getName();
	    String formattedName = String.format("%-30s", name);
	    // To add spaces if the name is shorter than 30 characters
	    return String.format("%s || Preis: %.2f € || Lieferzeit: %s || Score: %.2f",
	    		formattedName, getPrice(), Time.stepsToTimeString(getDeliveryTime()), getScore());
	}

	public final double getPrice()
	{
		return price;
	}

	public final int getDeliveryTime()
	{
		return deliveryTime;
	}

	public final Truck getOfferTruck()
	{
		return offerTruck;
	}

	public final double getScore()
	{
		return score;
	}

}
