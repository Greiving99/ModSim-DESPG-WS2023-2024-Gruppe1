package dev.despg.core;

public enum CoreEventTypes implements UniqueEventDescription
{
	Delay("Delay");

	String uniqueEventDescription = null;

	private CoreEventTypes(String uniqueEventDescription)
	{
		this.uniqueEventDescription = uniqueEventDescription;
	}

	@Override
	public String get()
	{
		return uniqueEventDescription;
	}
}
