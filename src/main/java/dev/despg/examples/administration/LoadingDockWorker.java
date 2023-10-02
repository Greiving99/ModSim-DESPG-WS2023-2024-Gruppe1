package dev.despg.examples.administration;

import java.util.ArrayList;
import java.util.Random;
import dev.despg.core.Randomizer;


public class LoadingDockWorker extends Employee
{

	@SuppressWarnings("unused")
	private String name;
	private double quality;
	private static int holidays;

	private static Randomizer loadingWeight;
	private static Randomizer loadingTime;
	private static Randomizer beSick;

	private Integer isReplacedBy;
	private boolean isReplaced;

	private static ArrayList<LoadingDockWorker> loadingDockWorkerList = new ArrayList<>();

	/* This is where it is determined how long the employee takes to load a shipment and how much they load each time.
	 * This is then adjusted up or down by the quality multiplier.*/

	public LoadingDockWorker(String name, double quality)
	{

		this.name = name;
		this.quality = quality;

		Double timeLoading1 = 30 * quality;
		int timeLoading1Int = timeLoading1.intValue();
		Double timeLoading2 = 40 * quality;
		int timeLoading2Int = timeLoading2.intValue();
		Double timeLoading3 = 50 * quality;
		int timeLoading3Int = timeLoading3.intValue();

		loadingWeight = new Randomizer();
		loadingWeight.addProbInt(0.3, 35);
		loadingWeight.addProbInt(0.6, 39);
		loadingWeight.addProbInt(1.0, 41);

		loadingTime = new Randomizer();
		loadingTime.addProbInt(0.3, timeLoading1Int);
		loadingTime.addProbInt(0.8, timeLoading2Int);
		loadingTime.addProbInt(1.0, timeLoading3Int);

		holidays = 20;

		setIsReplaced(false);						// Wird für einen möglichen Austausch vorgemerkt
		setIsReplacedBy(null);

		loadingDockWorkerList.add(this);

	}

	public static boolean reportSick()
	{
		beSick = new Randomizer();
		beSick.addProbInt(0.99, 0);
		beSick.addProbInt(1, 1);

		if (beSick.nextInt() > 0)
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
			Random random = new Random();
			int dauerUrlaub = random.nextInt(getHolidays()) + 1;
					setHolidays(dauerUrlaub);
			return dauerUrlaub;
		}
	}

	public static int durationSick()
	{
		Randomizer durationSick = new Randomizer();
		durationSick.addProbInt(0.8, 1440);
		durationSick.addProbInt(1, 1440 * 7);

		return durationSick.nextInt();
	}

	public static int duration()
	{
		beSick = new Randomizer();
		beSick.addProbInt(0.99, 0);
		beSick.addProbInt(1, 1);

		Randomizer applyForHoliday = new Randomizer();
		applyForHoliday.addProbInt(0.8, 0);
		applyForHoliday.addProbInt(1, 1);

		if (beSick.nextInt() > 0)
		{
			return durationSick();
		}
		else if (applyForHoliday.nextInt() > 0)
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
	public boolean isReplaced()
	{
		return isReplaced;
	}
/**
 *
 * @param isReplaced
 */
	public void setIsReplaced(boolean isReplaced)
	{
		this.isReplaced = isReplaced;
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

	public static ArrayList<LoadingDockWorker> getLoadingDockWorkerList()
	{
		return loadingDockWorkerList;
	}

	public static void setLoadingDockWorkerList(ArrayList<LoadingDockWorker> loadingDockWorkerList)
	{
		LoadingDockWorker.loadingDockWorkerList = loadingDockWorkerList;
	}
/**
 *
 * @return
 */
	public Integer getWeightLoaded()
	{
		return loadingWeight.nextInt();
	}
/**
 *
 * @return
 */
	public Integer getTimeLoad()
	{
		return loadingTime.nextInt();
	}

	public static int getHolidays()
	{
		return holidays;
	}


	public static void setHolidays(int holidays)
	{
		LoadingDockWorker.holidays = holidays;
	}

}

