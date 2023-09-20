package dev.despg.examples.administration;


public class Businessaccount extends Account
{

private static double totalCost = Administration.totalCost();;
private static double bankBalance = 10000;
private static double difference;
 private static double sumCredit;

public Businessaccount(Businessaccount geschaeftskonto, String iban, String companyName, Double bankBalance,
			Double amount)
{
		super(geschaeftskonto, iban, companyName, bankBalance, amount);
}

/* We are financially sound when our account balance minus total costs is above 0. In that case, this value becomes our new account balance.
 * We are not financially sound when our account balance minus total costs is below 0. In that case, we store the difference as a deficit.*/

public static boolean solvent(double bankBalance, double totalCost)
{
	Administration.setTotalCost(Administration.getTotalCost() + totalCost);
	if (bankBalance - totalCost > 0)
	{
		setBankBalance(getBankBalance() - totalCost);
	}
	else if (bankBalance - totalCost < 0)
	{
		difference =  totalCost - bankBalance;
			setBankBalance(0);
		takeCredit(difference);

	}
	Customer.setFixCost(0);
	LoadingDock.setFixedCost(0);
	WeighingStation.setFixCost(0);
	Administration.setRepairCost(0);

	return true;
}
/*
 * get Kredit
 * @param The difference is the gap between the total costs and the account balance.
 * @return Records the actual borrowed loan amount.
 */

//Here, we take out a loan. Our loan amount increases by the sum of the difference from being financially sound.

public static double takeCredit(double difference)
{
	setSumCredit(getSumCredit() + difference);
	return difference;
}

public static double getDifference()
{
	return difference;
}
public static void setDifference(double difference)
{
	Businessaccount.difference = difference;
}
public static double getBankBalance()
{
	return bankBalance;
}
public static void setBankBalance(double bankBalance)
{
	Businessaccount.bankBalance = bankBalance;
}

public static double getSumCredit()
{
	return sumCredit;
}
public static void setSumCredit(double sumCredit)
{
	Businessaccount.sumCredit = sumCredit;
}
public static double getTotalCost()
{
	return totalCost;
}
public static void setTotalCost(double totalCost)
{
	Businessaccount.totalCost = totalCost;
}

}
