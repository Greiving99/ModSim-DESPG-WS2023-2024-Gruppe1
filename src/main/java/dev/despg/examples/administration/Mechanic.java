package dev.despg.examples.administration;


import dev.despg.core.Randomizer;


public class Mechanic extends Employee
{

	@SuppressWarnings("unused")
	private String name;
	private double quality;
	private static int holidays;

	private static Randomizer dockFailureRepairTime;
	private static Randomizer stationFailureRepairTime;
	private static Randomizer truckFailureRepairTime;

	private static Randomizer beSick;
	private static Randomizer applyForHoliday;
	private Integer isReplacedBy;
	private boolean isReplacee;

	//Constructor of the mechanic, whose repair time is extended by the quality multiplier.

	public Mechanic(String name, double quality)
	{
		this.name = name;
		this.quality = quality;

		Double time1 = 1440 * quality;
		int time1Int = time1.intValue();
		Double time2 = 2880 * quality;
		int time2Int = time2.intValue();

		truckFailureRepairTime = new Randomizer();
		truckFailureRepairTime.addProbInt(0.97, 0);
		truckFailureRepairTime.addProbInt(0.98, time1Int);
		truckFailureRepairTime.addProbInt(1, time2Int);

		dockFailureRepairTime = new Randomizer();
		dockFailureRepairTime.addProbInt(0.97, 0);
		dockFailureRepairTime.addProbInt(0.98, time1Int);
		dockFailureRepairTime.addProbInt(1, time2Int);

		stationFailureRepairTime = new Randomizer();
		stationFailureRepairTime.addProbInt(0.97, 0);
		stationFailureRepairTime.addProbInt(0.98, time1Int);
		stationFailureRepairTime.addProbInt(1, time2Int);

		holidays = 20;
	}

	//Here, it is checked whether the employee still has vacation days.

	public static int checkHoliday()
	{
		if (getHolidays() <= 0)
			return 0;
		else
		{
			int durationHoliday = Randomizer.nextInt(getHolidays()) + 1;
					setHolidays(getHolidays() - durationHoliday);
					Administration.setCounterEmployeeSick(Administration.getCounterEmployeeSick() + 1);
			return durationHoliday;
		}
	}
	//Here, the duration of the absence due to illness is simulated.

	public static int durationSick()
	{
		Randomizer sickDuration = new Randomizer();
		sickDuration.addProbInt(0.8, 1440);
		sickDuration.addProbInt(1, 1440 * 7);

		return sickDuration.nextIntOnProp();
	}

	/*Here, it is simulated whether an employee is absent. This can be due to illness or vacation.*/

	public static int failureTime()
	{
		beSick = new Randomizer();
		beSick.addProbInt(0.99, 0);
		beSick.addProbInt(1, 1);

		applyForHoliday = new Randomizer();
		applyForHoliday.addProbInt(0.8, 0);
		applyForHoliday.addProbInt(1, 1);

		if (beSick.nextIntOnProp() > 0)
		{
			Administration.setCounterEmployeeSick(Administration.getCounterEmployeeSick() + 1);
			return durationSick();
		}
		else if (applyForHoliday.nextIntOnProp() > 0)
		{
			return checkHoliday();
		}
		return 0;
	}

	public static int getHolidays()
	{
		return holidays;
	}


	public static void setHolidays(int holidays)
	{
		Mechanic.holidays = holidays;
	}

/**
 *
 * @return
 */
	public Integer getDockFailureRepairTime()
	{
		return dockFailureRepairTime.nextIntOnProp();
	}

/**
 *
 * @return
 */
	public Integer getStationFailureRepairTime()
	{
		return stationFailureRepairTime.nextIntOnProp();
	}

/**
 *
 * @return
 */
	public Integer getTruckFailureRepairTime()
	{
		return truckFailureRepairTime.nextIntOnProp();
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
 * @param quality
 */
	public void setQuality(double quality)
	{
		this.quality = quality;
	}
/**
 *
 * @return
 */

	public Integer getIsReplacedBy()
	{
		return isReplacedBy;
	}

/**
 *
 * @param isReplacedBy
 */
	public void setIsReplacedBy(Integer isReplacedBy)
	{
		this.isReplacedBy = isReplacedBy;
	}

/**
 *
 * @return
 */
	public boolean getIsReplaced()
	{
		return isReplacee;
	}
/**
 *
 *
 * @param isReplaced
 */

	public void setIsReplaced(boolean isReplaced)
	{
		this.isReplacee = isReplaced;
	}
}
