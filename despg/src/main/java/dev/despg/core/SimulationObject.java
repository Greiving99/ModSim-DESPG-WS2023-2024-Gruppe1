package dev.despg.core;

public abstract class SimulationObject
{
	private Integer timeUtilized = 0;
	private Integer utilStart = null;
	

	public void setTimeUtilized(Integer timeUtilized) {
		this.timeUtilized = timeUtilized;
	}

	public void setUtilStart(Integer utilStart) {
		this.utilStart = utilStart;
	}

	public Integer getUtilStart() 
	{
		return utilStart;
	}
	
	public Integer getTimeUtilized()
	{
		return timeUtilized;
	}

	public abstract boolean simulate(int timeStep);
	// toString should be implemented if something meaningful should be printed (after simulation (step))
	
	public void utilStart(int timeStep)
	{
		utilStart = timeStep;
	}
	
	public void utilStop(int timeStep)
	{
		timeUtilized += timeStep - utilStart;
		utilStart = null;
	}
	
	
	public int addUtilization(int timeUtilizedDelta)
	{
		timeUtilized += timeUtilizedDelta;
		return timeUtilizedDelta;
	}
}
