package dev.despg.examples.administration;

public final class Administration
{
	private static double qualityMechanic;
	private static double repairCost;
	private static double failureCost;
	private static double revenue;
	private static double totalCost;

	private static int counterEmployeeSick;

	private static Driver driverToReplace;
	private static Mechanic mechanicToReplace;
	private static LoadingDockWorker loadingDockWorkerToReplace;

	private static Double worstQuality;

	private static int driverHired;
	private static int loadingDockWorkerHired;
	private static int mechanicHired;

	private static int numApplicantDriver;
	private static int numApplicantMechanic;
	private static int numApplicantLoadingDockWorker;
public void administraion()
{
}

	public static double totalCost()
	{
		double deliveryCost = Customer.getFixCost();
		double storageCost = LoadingDock.getFixedCost();
		double weighingCost = WeighingStation.getFixCost();
		double repairCost = Administration.getRepairCost();
		double totalCost = deliveryCost + storageCost + weighingCost + repairCost;
		return totalCost;
	}

	@SuppressWarnings("static-access")
	public static void evaluate()
	{
		//Saves an employee from the job market.
		int employee = LabourMarket.getRandomEmployee();
		//Obtains a random quality for the applicant.
		@SuppressWarnings("static-access")
		double qualityEmployee = LabourMarket.getDriver().randomQuality();
		worstQuality = 0.0;

		/*In 35% of the cases, it is a driver. Then, we iterate through our drivers to identify the one with the worst quality.
		 * The worst driver is stored and compared with the applicant.
		 * If the applicant is better, the truck list is checked and replaced with this driver.*/
		if (employee <= 35)
		{
			for (Driver driver : Driver.getDriverList())
			{
				if (driver.getQuality() > worstQuality)
				{
					setWorstQuality(driver.getQuality());
					setDriverToReplace(driver);
				}
			}
			if (getDriverToReplace().getQuality() > qualityEmployee)
			{
				for (Truck truck : Truck.getTruckList())
				{
					boolean isEqual = truck.getDriver() == getDriverToReplace();
					if (isEqual)
					{
						truck.getDriver().setReplacedBy(GravelShipping.getNumDriver() - 1);
						truck.getDriver().setReplaced(true);
						Administration.setDriverHired(Administration.getDriverHired() + 1);
					}
				}
			}
		}

		/*We go through each mechanic and check if the applicant is better than the current mechanics.
		 * If yes, the respective mechanic is set to true.*/

		//At a rate of 35%, mechanics.
		else if (employee <= 70)
		{

			setQualityMechanic(LabourMarket.randomQuality());
			if (LoadingDock.getTruckMechanic().getQuality() > qualityMechanic)
			{
				LoadingDock.setNewTruckMechanic(true);
			}
			else if (LoadingDock.getMechanic().getQuality() > qualityMechanic)
			{
				LoadingDock.setNewMechanic(true);
			}
			else if (WeighingStation.getMechanic().getQuality() > qualityMechanic)
			{
				WeighingStation.setNewMechanic(true);
			}

		}
		/* At a rate of 35%, it's a warehouse worker. Then, we iterate through our warehouse workers to identify the one with the worst quality.
		 * The worst warehouse worker is stored and compared with the applicant.
		 * If the applicant is better, we go through the loading docks and replace this warehouse worker.*/

		else
		{
			qualityEmployee = LabourMarket.getLoadingDockWorker().randomQuality();
			for (LoadingDockWorker loadingDockWorker : LoadingDockWorker.getLoadingDockWorkerList())
			{
				if (loadingDockWorker.getQuality() > worstQuality)
				{
					setWorstQuality(loadingDockWorker.getQuality());
					setLoadingDockWorkerToReplace(loadingDockWorker);
				}
			}

			if (getLoadingDockWorkerToReplace().getQuality() > qualityEmployee)
			{
				loadingDockWorkerToReplace.setIsReplacedBy(GravelShipping.getNumberLagerarbeiter() -  1);
				loadingDockWorkerToReplace.setIsReplaced(true);
				Administration.setLoadingDockWorkerHired(Administration.getLoadingDockWorkerHired() + 1);

			}

		}
	}

	public static double getRepairCost()
	{
		return repairCost;
	}

	public static void setRepairCost(double repaircost)
	{
		Administration.repairCost = repaircost;
	}

	public static double getFailureCost()
	{
		return failureCost;
	}

	public static void setFailureCost(double failureCost)
	{
		Administration.failureCost = failureCost;
	}

	public static double getRevenue()
	{
		return revenue;
	}

	public static void setRevenue(double revenue)
	{
		Administration.revenue = revenue;
	}


	public static double getTotalCost()
	{
		return totalCost;
	}


	public static void setTotalCost(double totalCost)
	{
		Administration.totalCost = totalCost;
	}

	public static int getCounterEmployeeSick()
	{
		return counterEmployeeSick;
	}


	public static void setCounterEmployeeSick(int counterEmployeeSick)
	{
		Administration.counterEmployeeSick = counterEmployeeSick;
	}

	public static int getNumApplicantDriver()
	{
		return numApplicantDriver;
	}

	public static void setNumApplicantDriver(int numApplicanDriver)
	{
		Administration.numApplicantDriver = numApplicanDriver;
	}

	public static int getNumApplicantMechanic()
	{
		return numApplicantMechanic;
	}

	public static void setNumApplicantMechanic(int numApplicantMechanic)
	{
		Administration.numApplicantMechanic = numApplicantMechanic;
	}

	public static int getNumApplicantLoadingDockWorker()
	{
		return numApplicantLoadingDockWorker;
	}

	public static void setNumApplicantLoadingDockWorker(int numApplicantLoadingDockWorker)
	{
		Administration.numApplicantLoadingDockWorker = numApplicantLoadingDockWorker;
	}

	public static double getQualityMechanic()
	{
		return qualityMechanic;
	}

	public static void setQualityMechanic(double qualitaetMechanic)

	{
		Administration.qualityMechanic = qualitaetMechanic;
	}
	public static Driver getDriverToReplace()
	{
		return driverToReplace;
	}

	public static void setDriverToReplace(Driver driverReplace)
	{
		Administration.driverToReplace = driverReplace;
	}

	public static int getDriverHired()
	{
		return driverHired;
	}

	public static void setDriverHired(int fahrerEingestellt)
	{
		Administration.driverHired = fahrerEingestellt;
	}

	public static Mechanic getMechanicToReplace()
	{
		return mechanicToReplace;
	}

	public static void setMechanicToReplace(Mechanic mechanikerErsetzen)
	{
		Administration.mechanicToReplace = mechanikerErsetzen;
	}

	public static LoadingDockWorker getLoadingDockWorkerToReplace()
	{
		return loadingDockWorkerToReplace;
	}

	public static void setLoadingDockWorkerToReplace(LoadingDockWorker lagerarbeiterErsetzen)
	{
		Administration.loadingDockWorkerToReplace = lagerarbeiterErsetzen;
	}

	public static int getLoadingDockWorkerHired()
	{
		return loadingDockWorkerHired;
	}

	public static void setLoadingDockWorkerHired(int lagerarbeiterEingestellt)
	{
		Administration.loadingDockWorkerHired = lagerarbeiterEingestellt;
	}

	public static int getMechanicHired()
	{
		return mechanicHired;
	}

	public static void setMechanicHired(int mechanikerEingestellt)
	{
		Administration.mechanicHired = mechanikerEingestellt;
	}

	public static Double getWorstQuality()
	{
		return worstQuality;
	}

	public static void setWorstQuality(Double schlechtesteQualitaet)
	{
		Administration.worstQuality = schlechtesteQualitaet;
	}

}
