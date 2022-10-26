/**
 * Copyright (C) 2021 despg.dev, Ralf Buschermöhle
 *
 * DESPG is made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * see LICENSE
 *
 */
package dev.despg.core;

/**
 * toString should be implemented if something meaningful should be printed
 * (after simulation (step)).
 */
public abstract class SimulationObject
{
	private Integer timeUtilized = 0;
	private Integer utilStart;

	public abstract boolean simulate(int timeStep);

	/**
	 *
	 * @param timeUtilized
	 */
	public void setTimeUtilized(Integer timeUtilized)
	{
		this.timeUtilized = timeUtilized;
	}

	/**
	 *
	 * @param utilStart
	 */
	public void setUtilStart(Integer utilStart)
	{
		this.utilStart = utilStart;
	}

	/**
	 *
	 * @return
	 */
	public Integer getUtilStart()
	{
		return utilStart;
	}

	/**
	 *
	 * @return
	 */
	public Integer getTimeUtilized()
	{
		return timeUtilized;
	}

	/**
	 *
	 * @param timeStep
	 */
	public void utilStart(int timeStep)
	{
		utilStart = timeStep;
	}

	/**
	 *
	 * @param timeStep
	 */
	public void utilStop(int timeStep)
	{
		timeUtilized += timeStep - utilStart;
		utilStart = null;
	}

	/**
	 *
	 * @param timeUtilizedDelta
	 * @return
	 */
	public int addUtilization(int timeUtilizedDelta)
	{
		timeUtilized += timeUtilizedDelta;
		return timeUtilizedDelta;
	}
}
