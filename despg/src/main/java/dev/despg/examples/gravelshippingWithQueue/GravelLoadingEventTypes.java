/**
 * Copyright (C) 2021 yamm.dev, Ralf Buschermöhle
 * 	
 * DESPG is made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * see LICENSE
 * 
 */
package dev.despg.examples.gravelshippingWithQueue;

import dev.despg.core.UniqueEventDescription;

/**
 * Implementation of {@link dev.despg.core.UniqueEventDescription}. Defines the
 * event types that can happen during the GravelShipping simulation
 */
public enum GravelLoadingEventTypes implements UniqueEventDescription
{
	LoadingDone("Loading Truck done"), WeighingDone("Weighing Truck done"), UnloadingDone("Unloading Truck done");

	String eventTypeUniqueDescription = null;

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
