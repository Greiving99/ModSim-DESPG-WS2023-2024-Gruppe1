package dev.despg.examples.administration;

import java.util.ArrayList;
import dev.despg.core.Randomizer;


public class Driver extends Employee
{

	@SuppressWarnings("unused")
	private String name;

	private double quality;

	private Integer replacedBy;

	private static Randomizer drivingToWeighingStation;
	private static Randomizer drivingToCustomer;
	private static Randomizer drivingToLoadingDock;
	private static Randomizer deloadingTime;
	private static Randomizer isSick;
	private boolean isReplaced;
	private static int holidays;
	private static ArrayList<Driver> driverList = new ArrayList<>();

	public Driver(String name, double qualitaet)

	/* This is where the time it takes for a driver to load/unload,
	 * drive to the customer, and weigh the cargo is determined. Additionally,
	 * the probability of these events occurring is determined */
	{
		this.name = name;
		this.quality = qualitaet;

		Double timeCustomer1 = 200 * qualitaet;
		int timeCustomer1Int = timeCustomer1.intValue();
		Double timeCustomer2 = 300 * qualitaet;
		int timeCustomer2Int = timeCustomer2.intValue();
		Double timeCustomer3 = 400 * qualitaet;
		int timeCustomer3Int = timeCustomer3.intValue();

		Double timeLoadingDock1 = 100 * qualitaet;
		int timeLoadingDock1Int = timeLoadingDock1.intValue();
		Double timeLoadingDock2 = 120 * qualitaet;
		int timeLoadingDock2Int = timeLoadingDock2.intValue();
		Double timeLoadingDock3 = 140 * qualitaet;
		int timeLoadingDock3Int = timeLoadingDock3.intValue();

		Double timeDeloading1 = 100 * qualitaet;
		int zeitDeloading1Int = timeDeloading1.intValue();
		Double timeDeloading2 = 200 * qualitaet;
		int timeDeloading2Int = timeDeloading2.intValue();
		Double timeDeloading3 = 300 * qualitaet;
		int timeDeloading3Int = timeDeloading3.intValue();

		Double timeWeighingStation1 = 10 * qualitaet;
		int timeWeighingStation1Int = timeWeighingStation1.intValue();
		Double timeWeighingStation2 = 20 * qualitaet;
		int timeWeighingStation2Int = timeWeighingStation2.intValue();
		Double timeWeighingStation3 = 30 * qualitaet;
		int timeWeighingStation3Int = timeWeighingStation3.intValue();

		drivingToCustomer = new Randomizer();
		drivingToCustomer.addProbInt(0.5, timeCustomer1Int);
		drivingToCustomer.addProbInt(0.8, timeCustomer2Int);
		drivingToCustomer.addProbInt(1.0, timeCustomer3Int);

		drivingToLoadingDock = new Randomizer();
		drivingToLoadingDock.addProbInt(0.3, timeLoadingDock1Int);
		drivingToLoadingDock.addProbInt(0.6, timeLoadingDock2Int);
		drivingToLoadingDock.addProbInt(1.0, timeLoadingDock3Int);

		deloadingTime = new Randomizer();
		deloadingTime.addProbInt(0.3, zeitDeloading1Int);
		deloadingTime.addProbInt(0.6, timeDeloading2Int);
		deloadingTime.addProbInt(1.0, timeDeloading3Int);

		drivingToWeighingStation = new Randomizer();
		drivingToWeighingStation.addProbInt(0.3, timeWeighingStation1Int);
		drivingToWeighingStation.addProbInt(0.6, timeWeighingStation2Int);
		drivingToWeighingStation.addProbInt(1.0, timeWeighingStation3Int);

		holidays =  20;

		//This is where the scheduling for the driver exchange takes place.

		setReplaced(false);
		setReplacedBy(null);
		getDriverList().add(this);

	}

	public static boolean reportSick()
	{
		isSick = new Randomizer();
		//99% chance that an employee is never sick.
		isSick.addProbInt(0.99, 0);
		//1% chance that an employee reports sick.
		isSick.addProbInt(1, 1);
		if (isSick.nextIntOnProp() > 0)
		{
			return true;
		}
			return false;

	}

	public static int checkHolidays()
	{
		if (getHolidays() <= 0)
			return 0;
		else
		{
			int lengthHoliday = Randomizer.nextInt(getHolidays()) + 1;
					setHolidays(lengthHoliday);
			return lengthHoliday;
		}
	}

	public static int timeSick()
	{
		Randomizer krankDauer = new Randomizer();
		krankDauer.addProbInt(0.8, 1440);
		krankDauer.addProbInt(1, 1440 * 7);
		return krankDauer.nextIntOnProp();
	}
	public static int failure()
	{
		isSick = new Randomizer();
		isSick.addProbInt(0.99, 0);
		isSick.addProbInt(1, 1);

		Randomizer applyForHoliday = new Randomizer();
		applyForHoliday.addProbInt(0.8, 0);
		applyForHoliday.addProbInt(1, 1);

		if (isSick.nextIntOnProp() > 0)
		{
			return timeSick();
		}
		else if (applyForHoliday.nextIntOnProp() > 0)
		{
			return checkHolidays();
		}
		return 0;
	}
/**
 *
 * @return
 */
	public double getQuality()
	{
		return quality;
	}
/**
 *
 * @param qualitaet
 */
	public void setQuality(double qualitaet)
	{
		this.quality = qualitaet;
	}
/**
 *
 * @return
 */
	public boolean isReplaced()
	{
		return isReplaced;
	}
/**
 *
 * @param wirdErsetzt
 */
	public void setReplaced(boolean wirdErsetzt)
	{
		this.isReplaced = wirdErsetzt;
	}
/**
 *
 * @return
 */
	public Integer getReplacedBy()
	{
		return replacedBy;
	}
/**
 *
 * @param wirdErsetztVon
 */
	public void setReplacedBy(Integer wirdErsetztVon)
	{
		this.replacedBy = wirdErsetztVon;
	}

	public static ArrayList<Driver> getDriverList()
	{
		return driverList;
	}

	public static void setDriverList(ArrayList<Driver> fahrerListe)
	{
		Driver.driverList = fahrerListe;
	}
/**
 *
 * @return
 */
	public Integer getDrivingToWeighingStation()
	{
		return drivingToCustomer.nextIntOnProp();
	}
/**
 *
 * @return
 */
	public Integer getDrivingToCustomer()
	{
		return drivingToCustomer.nextIntOnProp();
	}
	/**
	 *
	 * @return
	 */
	public Integer getDrivingToLoadingDock()
	{
		return drivingToLoadingDock.nextIntOnProp();
	}
/**
 *
 * @return
 */
	public Integer getDeloadingTime()
	{
		return deloadingTime.nextIntOnProp();
	}
	public static int getHolidays()
	{
		return holidays;
	}

	public static void setHolidays(int urlaubstage)
	{
		Driver.holidays = urlaubstage;
	}

}
