package dev.despg.examples.administration;

import dev.despg.core.Randomizer;

public class LabourMarket
{

	private String name;
	private double quality;
	private static Driver driver;

	private static Mechanic mechanic;
	private static LoadingDockWorker loadingDockWorker;

	public LabourMarket(String name, double quality)
	{
		this.name = name;
		this.quality = quality;
	}

	//Here, a quality multiplier is created that outputs a value ranging from 0.9 to 1.1.

	public static double randomQuality()
	{
		double quality = Randomizer.nextDouble() * 0.2 + 0.9;
		return quality;

	}

	//Here, it is determined with what probability an applicant practices one of these professions.

	public static int getRandomEmployee()
	{
		int probability = Randomizer.nextInt(100) + 1;

		/* There is a 35% probability that it is a driver.
		 * The number of applicants is increased by 1, and a driver with a random quality is created.
		 * Additionally, the number of drivers is increased by 1 */

		if (probability <= 35)
		{
			Administration.setNumApplicantDriver(Administration.getNumApplicantDriver() + 1);
			driver = new Driver("Fahrer" + GravelShipping.getNumDriver(), randomQuality());
			GravelShipping.setNumberDriver(GravelShipping.getNumDriver() + 1);
		}
		//There is a 35% probability that it is a mechanic, and the number of applicants is increased by 1.
		else if (probability <= 70)
		{
			Administration.setNumApplicantMechanic(Administration.getNumApplicantMechanic() + 1);
		}
		else
			/* There is a 35% probability that it is a warehouse worker. The number of applicants is increased by 1,
			 * and a warehouse worker with a random quality is created. Additionally, the number of warehouse workers is increased by 1.*/
		{
			Administration.setNumApplicantLoadingDockWorker(Administration.getNumApplicantLoadingDockWorker() + 1);
			loadingDockWorker = new LoadingDockWorker("Lagerarbeiter" + GravelShipping.getNumberLoadingDocks(), randomQuality());
			GravelShipping.setNumberLoadingDocks(GravelShipping.getNumberLoadingDocks() + 1);
		}
		return probability;
	}


	public static Driver getDriver()
	{
		return driver;
	}

	public static void setDriver(Driver driver)
	{
		LabourMarket.driver = driver;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public double getQuality()
	{
		return quality;
	}

	public void setQuality(double quality)
	{
		this.quality = quality;
	}

	public static Mechanic getMechanic()
	{
		return mechanic;
	}

	public static void setMechanic(Mechanic mechanic)
	{
		LabourMarket.mechanic = mechanic;
	}

	public static LoadingDockWorker getLoadingDockWorker()
	{
		return loadingDockWorker;
	}

	public static void setLoadingDockWorker(LoadingDockWorker lagerarbeiter)
	{
		LabourMarket.loadingDockWorker = lagerarbeiter;
	}
}
