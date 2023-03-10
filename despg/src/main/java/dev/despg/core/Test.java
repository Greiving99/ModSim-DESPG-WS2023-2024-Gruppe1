package dev.despg.core;

import java.time.DayOfWeek;

public final class Test
{
	private Test()
	{
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args)
	{
		Randomizer r = new Randomizer();

		for (int i = 0; i < 100; i++)
			System.out.println(Time.stepsToDateString(Time.minutesToSteps(r.getNormal(50, 10))));

		System.out.println(10L % 3L);

		long test = Time.stepsToDay(500, DayOfWeek.SATURDAY);
		System.out.println(test);
		System.out.println(Time.stepsToTimeString(test));
	}

}
