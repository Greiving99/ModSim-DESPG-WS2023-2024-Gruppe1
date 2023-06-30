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

public record Probability2Value<T>(
		Double probabilityUpperLimit,
		T value)
		implements Comparable<Probability2Value<T>>
{
	@Override
	public int compareTo(Probability2Value<T> o)
	{
		return probabilityUpperLimit.compareTo(o.probabilityUpperLimit);
	}

}
