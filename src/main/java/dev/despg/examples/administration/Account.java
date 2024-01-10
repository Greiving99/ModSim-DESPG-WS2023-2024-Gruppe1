package dev.despg.examples.administration;

public abstract class Account
{

	/* This class is intended to serve as a connection to the other projects.
	 * Here, employees from purchasing and sales,
	 * for example, could deposit money they have earned into our account.*/

	@SuppressWarnings("unused")
	private static String iban;
	private static double bankBalance;
	@SuppressWarnings("unused")
	private static double amount;
	@SuppressWarnings("unused")
	private Businessaccount businessaccount;
	public Account(Businessaccount businessaccount, String iban, String companyName, Double bankBalance, Double amount)
	{
		this.businessaccount = businessaccount;
		Account.iban = iban;
		Account.bankBalance = bankBalance;
		Account.amount = amount;
	}

	//Here, a deposit is made.

	public static void deposit(double amount)
	{
	bankBalance += amount;
	}

	//Here, a deposit is made.

	public static void payOff(double amount)
	{
	bankBalance -= amount;
	}

	//Here, it is checked whether the amount is greater than or equal to 0.
/**
 *
 * @param amount
 * @return
 */
	public boolean check(double amount)
	{
		if (amount <= 0)
		{
		return true;
		}
		return false;
	}
	public static double getBankBalance()
	{
		return bankBalance;
	}
	public static void setBankBalance(double bankBalance)
	{
		Account.bankBalance = bankBalance;
	}
}
