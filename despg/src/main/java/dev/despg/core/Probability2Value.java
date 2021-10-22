package dev.despg.core;

public class Probability2Value<T> implements Comparable<Probability2Value<T>>
{
	Double probabilityUpperLimit = null;
	T value;

	/**
	 * Assigns the upper limit of a probability to a value.
	 * 
	 * @param to    Upper limit of probability
	 * @param value Value of that probability
	 */
	public Probability2Value(Double to, T value)
	{
		this.probabilityUpperLimit = to;
		this.value = value;
	}

	@Override
	public int compareTo(Probability2Value<T> o)
	{
		return probabilityUpperLimit.compareTo(o.probabilityUpperLimit);
	}
}
