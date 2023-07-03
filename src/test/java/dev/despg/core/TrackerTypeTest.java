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
import org.junit.jupiter.api.Test;


class TrackerTypeTest
{
	/**
	 * Checks description Utilization.
	 */
	@Test
	void testUtilizationTrackerType()
	{
		assertThat("Utilization").isEqualTo(TrackerType.Utilization.get());
	}

	/**
	 * Checks description Failure.
	 */
	@Test
	void testFailureTrackerType()
	{
		assertThat("Failure").isEqualTo(TrackerType.Failure.get());
	}
}
