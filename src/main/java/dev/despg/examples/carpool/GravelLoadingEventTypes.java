/**
 * Copyright (C) 2021 despg.dev, Ralf Buschermöhle
 *
 * DESPG is made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * see LICENSE
 *
 */
package dev.despg.examples.carpool;

import dev.despg.core.UniqueEventDescription;

/**
 * Implementation of {@link dev.despg.core.UniqueEventDescription}. Defines the
 * event types that can happen during the GravelShipping simulation
 */
public enum GravelLoadingEventTypes implements UniqueEventDescription
{
	/** */
	Loading("Loading Truck"),
	/** */
	LoadingDone("Loading Truck done"),
	/** */
	Weighing("Weighing Truck"),
	/** */
	WeighingDone("Weighing Truck done"),
	/** */
	DockRepaired("Loading Dock repaired"),
	/** */
	TruckStart("Truck starts driving"),
	/** */
	TruckBack("Truck is back"),
	/** */
	TruckEnRoute("Truck is on the road"),
	/** */
	TruckFailed("Truck failed"),
	/** */
	TruckRepaired("Truck repaired"),
	/** */
	TruckInRepair("Truck in repair"),
	/** */
	TruckInspection("Truck in maintance"),
	/** */
	TruckDiagnosis("Truck in diagnose"),
	/** */
	TruckInspectionDone("Truck inspection done"),
	/** */
	TruckDriverPause("Truck driver on a break"),
	/** */
	TruckStartPartRoute("Truck starts a partial leg of the journey"),
	/** */
	GetBestOfferForNewTruck("Selecting the best offer for a new truck"),
	/** */
	NewTruckDelivered("New Truck delivered -> now inspection"),
	/** */
	TruckWillFail("The truck will break down during the journey.");

	private String eventTypeUniqueDescription;

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
