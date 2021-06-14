package dev.despg.core;
import java.util.ArrayList;
import java.util.Random;


public class Randomizer
{	
	private static final double MIN_PROBABILITY = 0.0;
	private static final double MAX_PROBABILITY = 1.0;
	
	private static Random random = new Random();
	private ArrayList<Probability2Value<Integer>> prob2Int = new ArrayList<Probability2Value<Integer>>();

	/**
	 * This method is used to assign Integer values to a certain probability.
	 * @param to Upper limit for that probability to occur
	 * @param value Assigned value of that probability
	 */
	public void addProbInt(double to, int value)
	{
		if (to >= MIN_PROBABILITY && to <= MAX_PROBABILITY)
		{
			prob2Int.add(new Probability2Value<Integer>(to, value));
		}
	}
	
	/**
	 * This method iterates through an ArrayList of Probability2Value objects and compares its probabilityUpperLimit against a random Double.
	 * @return This returns the Integer value that had been assigned to the occurred probability
	 */
	public int nextInt()
	{
		double r = random.nextDouble();		
		
		for (Probability2Value<Integer> pI : prob2Int)
		{
			if (r <= pI.probabilityUpperLimit)
				return pI.value;
		}
		
		return 0;
	}
	
	public static int getPoisson(double lambda) 
	{
		double L = Math.exp(-lambda);
		double p = 1.0;
		int k = 0;

		do {
			k++;
			p *= Math.random();
		} while (p > L);

		return k - 1;
	}
}
