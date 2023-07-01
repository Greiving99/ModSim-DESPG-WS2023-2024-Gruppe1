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

import java.io.Serial;

public class SimulationException extends RuntimeException
{

	@Serial
    private static final long serialVersionUID = 1L;

	public SimulationException(String message)
	{
		super(message);
	}
}
