package dev.despg.core;

public enum TrackerType
{
	/** */
	Utilization("Utilization"),
	/** */
	Failure("Failure");


	private final String trackerTypeString;

	TrackerType(String value)
	{
		this.trackerTypeString = value;
	}

	public String get()
	{
		return trackerTypeString;
	}
}
