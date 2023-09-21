package dev.despg.examples.carpool;

public class TruckModels
{
	private String brand;
	private String modelName;
	private double fuelConsumption;

	public TruckModels(String brand, String modelName, double fuelConsumption)
	{
		this.brand = brand;
		this.modelName = modelName;
		this.fuelConsumption = fuelConsumption;
	}

	public final String getTruckBrand()
	{
		return brand;
	}
	public final String getModelName()
	{
		return modelName;
	}
	public final double getFuelConsumption()
	{
		return fuelConsumption;
	}
}
