/**
 * Copyright (C) 2021 despg.dev, Ralf Buschermöhle
 * <p>
 * DESPG is made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * <p>
 * see LICENSE
 *
 */
package dev.despg.examples.purchase_storage_sale;

import dev.despg.core.UniqueEventDescription;

/**
 * Implementation of {@link dev.despg.core.UniqueEventDescription}. Defines the
 * event types that can happen during the GravelShipping simulation
 */
public enum GravelLoadingEventTypes implements UniqueEventDescription
{
	Loading("Loading Truck"),
	LoadingDone("Loading Truck done"),
	Weighing("Weighing Truck"),
	WeighingDone("Weighing Truck done");

	private final String eventTypeUniqueDescription;

	GravelLoadingEventTypes(String value)
	{
		this.eventTypeUniqueDescription = value;
	}

	@Override
	public String get()
	{
		return eventTypeUniqueDescription;
	}
}
