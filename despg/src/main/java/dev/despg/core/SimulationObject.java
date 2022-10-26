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

	public final void setTimeUtilized(Integer timeUtilized)
	{
		this.timeUtilized = timeUtilized;
	}

	public final void setUtilStart(Integer utilStart)
	{
		this.utilStart = utilStart;
	}

	public final Integer getUtilStart()
	{
		return utilStart;
	}

	public final Integer getTimeUtilized()
	{
		return timeUtilized;
	}

	public final void utilStart(int timeStep)
	{
		utilStart = timeStep;
	}

	public final void utilStop(int timeStep)
	{
		timeUtilized += timeStep - utilStart;
		utilStart = null;
	}

	public final int addUtilization(int timeUtilizedDelta)
	{
		timeUtilized += timeUtilizedDelta;
		return timeUtilizedDelta;
	}
}
