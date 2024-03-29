/**
 * Copyright (C) 2021 despg.dev, Ralf Buschermöhle
 * <p>
 * DESPG is made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * <p>
 * see LICENSE
 *
 */
package dev.despg.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class Randomizer
{
	private static final double MIN_PROBABILITY = 0.0;
	private static final double MAX_PROBABILITY = 1.0;
	private static final Random RANDOM = new Random();

	public static double getMinProbability()
	{
		return MIN_PROBABILITY;
	}

	public static double getMaxProbability()
	{
		return MAX_PROBABILITY;
	}

	private final List<Probability2Value<Integer>> prob2Int = new ArrayList<>();

	/**
	 *
	 * @return defined probability intervals -> integer values
	 */
	public List<Probability2Value<Integer>> getProb2Int()
	{
		return prob2Int;
	}

	/**
	 * This method is used to assign Integer values to a certain probability.
	 *
	 * @param to    Upper limit for that probability to occur
	 * @param value Assigned value of that probability
	 */
	public void addProbInt(double to, int value) throws SimulationException
	{
		assert (to >= MIN_PROBABILITY);
		assert (to <= MAX_PROBABILITY);

		for (Probability2Value<Integer> prob2value : prob2Int)
		{
			if (prob2value.probabilityUpperLimit() == to)
				throw new SimulationException("Probability " + to + " already exists");
		}

		prob2Int.add(new Probability2Value<>(to, value));
	}

	/**
	 * This method iterates through an ArrayList of Probability2Value objects and
	 * compares its probabilityUpperLimit against a random Double.
	 *
	 * @return This returns the Integer value that had been assigned to the occurred
	 *         probability
	 */
	public int nextIntOnProp() throws SimulationException
	{
		if (prob2Int.isEmpty())
			throw new SimulationException("No probabilities in ArrayList");

		double r = RANDOM.nextDouble();

		for (Probability2Value<Integer> pI : prob2Int)
		{
			if (pI.probabilityUpperLimit() < MIN_PROBABILITY || pI.probabilityUpperLimit() > MAX_PROBABILITY)
				throw new SimulationException("Probability " + pI.probabilityUpperLimit() + " is out of bounds ("
						+ MIN_PROBABILITY + "-" + MAX_PROBABILITY + ")");
			else if (r <= pI.probabilityUpperLimit())
				return pI.value();
		}

		throw new SimulationException("Probability not covered");
	}

	/**
	 *
	 * @param bound
	 * @return the random int Number
	 */
	public static int nextInt(int bound) throws IllegalArgumentException
	{
		return RANDOM.nextInt(bound);
	}

	/**
	 *
	 * @return the random double Number
	 */
	public static double nextDouble()
	{
		return RANDOM.nextDouble();
	}

	/**
	 * returns a (uniform) number between min and max.
	 */
	public double getUniform(double min, double max)
	{
		assert (max > min);

	    return RANDOM.nextDouble() * max + min;
	}

	/**
	 * returns a (uniform) number between min and max with a certain precision.
	 */
	public double getUniformWithPrecision(double min, double max, double precision)
	{
		assert (precision > 0);

	    return Math.round(getUniform(min, max) / precision) * precision;
	}

	/**
	 * returns a (triangular) number between min and max with mode.
	 * @param mode - highest probability
	 * @return random value between min and max
	 */
	public double getTriangular(double min, double max, double mode)
	{
		assert (max > mode);
		assert (mode > min);

	    double f = (mode - min) / (max - min);
	    double rand = Math.random();

	    return rand < f
	   		 ? min + Math.sqrt(rand * (max - min) * (mode - min))
	   		 : max - Math.sqrt((1 - rand) * (max - min) * (max - mode));
	}

	/**
	 * returns a (triangular) number between min and max with mode and a certain precision.
	 */
	public double getTriangularWithPrecision(double min, double max, double mode, double precision)
	{
		assert (precision > 0);

	    return Math.round(getTriangular(min, max, mode) / precision) * precision;
	}

	/**
	 * Computes a uniformly-distributed random double with parameterized rate.
	 */
	public Double getExponential(double rate)
	{
		assert (rate > 0);

	    double u;
	    do
	    {
	        u = RANDOM.nextDouble();
	    } while (u == 0d); // Reject zero, u must be positive for this to work.

	    return -(Math.log(u) / (rate));
	}

	/**
	 * Computes a value of a normal distribution with parameterized mean and deviation.
	 */
	public double getNormal(double mean, double deviation)
	{
		assert (mean > 0);
		assert (deviation > 0);

		return RANDOM.nextGaussian() * deviation + mean;
	}

	/**
	 * Computes a value of a discrete Poisson distribution with parameterized lambda.
	 */
	public int getPoisson(double lambda)
	{
		assert (lambda > 0);

		double l = Math.exp(-lambda);
		double p = 1.0;
		int k = 0;

		do
		{
			k++;
			p *= RANDOM.nextDouble();
		} while (p > l);

		return k - 1;
	}

}
