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

import java.util.HashMap;
import java.util.Map;

/**
 * toString should be implemented if something meaningful should be printed
 * (after simulation (step)).
 * Class (attributes / methods) should not be final because mocking can't mock them.
 */
public abstract class SimulationObject
{
	private final Map<TrackerType, Long> trackers = new HashMap<>();
	private final Map<TrackerType, Long> trackersStart = new HashMap<>();

	public abstract void simulate(long timeStep);

	/**
	 * setTracker.
	 */
	public void setTracker(TrackerType counterType, Long time)
	{
		trackers.put(counterType, time);
	}

	/**
	 * getTracker.
	 * @return value
	 */
	public Long getTracker(TrackerType counterType)
	{
		Long tracker = trackers.get(counterType);
		return (tracker == null) ? 0L : tracker;
	}

	/**
	 * start utilization.
	 */
	public void trackerStart(TrackerType counterType, long timeStep)
	{
		trackersStart.put(counterType, timeStep);
	}

	/**
	 * stop utilization.
	 */
	public long trackerStop(TrackerType counterType, long timeStep)
	{
		Long trackerValue = trackers.get(counterType);
		if (trackerValue == null)
			trackerValue = 0L;

		long timeDelta = timeStep - trackersStart.get(counterType);

		trackers.put(counterType, trackerValue + timeDelta);
		trackersStart.remove(counterType);

		return timeDelta;
	}

	/**
	 * increase utilization.
	 * @return increased utilization
	 */
	public long addTimeStepDelta(TrackerType counterType, long timeStepDelta)
	{
		Long trackerValue = trackers.get(counterType);
		if (trackerValue == null)
			trackerValue = 0L;

		trackers.put(counterType, trackerValue + timeStepDelta);
		// ToDO: trackerValue+ (removed)
		return timeStepDelta;
	}
}
