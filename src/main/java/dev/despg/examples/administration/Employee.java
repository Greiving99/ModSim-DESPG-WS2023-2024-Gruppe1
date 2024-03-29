package dev.despg.examples.administration;

import dev.despg.core.*;

public abstract class Employee
{

	@SuppressWarnings("unused")
	private String name;
	@SuppressWarnings("unused")
	private static final int HOLIDAYS = 20;
	@SuppressWarnings("unused")
	private static Double quality;
	@SuppressWarnings("unused")
	private static Randomizer beSick;
	@SuppressWarnings("unused")
	private static Randomizer timeSick;

	@SuppressWarnings("unused")
	private static Randomizer applyForHoliday;

	//Here, a quality multiplier is created that outputs a value ranging from 0.5 to 1.5.
	//TODO the value ranges from 0.5 to 2 in Code. Which is right: Comment or Code?
	public static double randomQuality()
	{
		double quality = Randomizer.nextDouble() * 1.5 + 0.5;
		return quality;
	}

}
