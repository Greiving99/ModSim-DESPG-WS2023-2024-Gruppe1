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
	TruckBack("Truck zurück"),
	/** */
	TruckEnRoute("Truck ist unterwegs"),
	/** */
	TruckFailed("Truck failed"),
	/** */
	TruckRepaired("Truck repaired"),
	/** */
	TruckInRepair("Truck in repair"),
	/** */
	TruckInspection("Truck wird gewartet"),
	/** */
	TruckDiagnosis("Truck in Diagnose"),
	/** */
	TruckInspectionDone("Truck Inspektion done"),
	/** */
	TruckDriverPause("Truck Fahrer in Pause"),
	/** */
	TruckStartPartRoute("Truck start Teilstrecke"),
	/** */
	GetBestOfferForNewTruck("Bestes Angebot für neuen Truck aussuchen"),
	/** */
	NewTruckDelivered("New Truck delivered -> now inspection"),
	/** */
	TruckWillFail("Truck wird auf der Tour kaputt gehen");

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
