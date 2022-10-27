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
	private Long timeUtilized = 0L;
	private Long utilStart;

	public abstract boolean simulate(long timeStep);

	/**
	 *
	 * @param timeUtilized
	 */
	public void setTimeUtilized(Long timeUtilized)
	{
		this.timeUtilized = timeUtilized;
	}

	/**
	 *
	 * @param utilStart
	 */
	public void setUtilStart(Long utilStart)
	{
		this.utilStart = utilStart;
	}

	/**
	 *
	 * @return
	 */
	public Long getUtilStart()
	{
		return utilStart;
	}

	/**
	 *
	 * @return
	 */
	public Long getTimeUtilized()
	{
		return timeUtilized;
	}

	/**
	 *
	 * @param timeStep
	 */
	public void utilStart(long timeStep)
	{
		utilStart = timeStep;
	}

	/**
	 *
	 * @param timeStep
	 */
	public void utilStop(long timeStep)
	{
		timeUtilized += timeStep - utilStart;
		utilStart = null;
	}

	/**
	 *
	 * @param timeUtilizedDelta
	 * @return
	 */
	public long addUtilization(long timeUtilizedDelta)
	{
		timeUtilized += timeUtilizedDelta;
		return timeUtilizedDelta;
	}
}
