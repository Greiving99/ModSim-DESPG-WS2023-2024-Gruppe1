package dev.despg.examples.administration;

import java.util.Random;
import dev.despg.core.*;

public abstract class Employee
{

	private String name;
	private static final int HOLIDAYS = 20;
	private static Double quality;
	private static Randomizer beSick;
	private static Randomizer timeSick;

	private static Randomizer applyForHoliday;

	//Here, a quality multiplier is created that outputs a value ranging from 0.5 to 1.5.
	public static double randomQuality()
	{
		Random random = new Random();
		double quality = random.nextDouble() * 1.5 + 0.5;
		return quality;
	}

}
