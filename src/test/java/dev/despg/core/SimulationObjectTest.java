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

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.when;

import java.util.HashMap;

import org.mockito.Mockito;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SimulationObjectTest
{
	private SimulationObject simObject;

	@BeforeEach
	void init()
	{
		simObject = Mockito.spy(SimulationObject.class);
	}


	@Test
	void doesStartUtilization()
	{
		long expected = 4;

		simObject.trackerStart(TrackerType.Utilization, expected);
		assertThat(simObject.trackerStop(TrackerType.Utilization, expected + expected)).isEqualTo(expected);
	}


	/**
	 * Checks if utilStop stops the utilization correctly and if it sets utilStart
	 * to null.
	 */
	@Test // would it be overkill to make 2 unit tests out of this? doesStopUtilization
			// and doesResetUtilStart
	void doesStopUtilizationAndReset()
	{
		long trackerStart = 2L;
		long trackerStop = 8L;
		long expected = 6L;

		simObject.trackerStart(TrackerType.Utilization, trackerStart);
		assertThat(simObject.trackerStop(TrackerType.Utilization, trackerStop)).isEqualTo(expected);
	}

	/**
	 * Checks if addUtilization adds time to the total time utilized.
	 */
	@Test
	void addsToTimeUtilized()
	{
		long expected = 4;

		assertThat(simObject.addTimeStepDelta(TrackerType.Utilization, expected)).isEqualTo(expected);
	}

}
