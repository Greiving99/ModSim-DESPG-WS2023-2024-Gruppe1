package dev.despg.examples.gravelshipping;
import dev.despg.core.Event;
import dev.despg.core.EventQueue;
import dev.despg.core.Simulation;
import dev.despg.core.Time;


public class GravelShipping extends Simulation
{
	public static Integer gravelToShip = 200;
	public static Integer gravelShipped = 0;
	private final int gravelToShippedFinal = gravelToShip;
	
	public static Integer successfulLoadings = 0;
	public static Integer successfulLoadingSizes = 0;
	
	public static Integer unsuccessfulLoadings = 0;
	public static Integer unsuccessfulLoadingSizes = 0;
	
	
	private static final int NUM_TRUCKS = 1;
	private static final int NUM_LOADING_DOCKS = 2;
	private static final int NUM_WEIGHING_STATIONS = 1;

	
	/**
	 * Defines the setup of simulation objects and starting events before executing the simulation. Prints utilization statistics afterwards
	 */
	public static void main(String[] args)
	{
		EventQueue eventqueue = EventQueue.getInstance();
		
		for (int i = 0; i < NUM_TRUCKS; i++)
			eventqueue.add(new Event(0, GravelLoadingEventTypes.Loading, new Truck("T" + i), LoadingDock.class, null));
		
		for (int i = 0; i < NUM_LOADING_DOCKS; i++)
			new LoadingDock("LD" + i);

		for (int i = 0; i < NUM_WEIGHING_STATIONS; i++)
			new WeighingStation("WS" + i);

		GravelShipping gs = new GravelShipping();
		int timeStep = gs.simulate();
		
		// output some statistics after simulation run
		System.out.println("Gravel shipped\t\t = " + gravelShipped + " tons");
		System.out.println("Mean Time / Gravel Unit\t = " 
					+ ((double) timeStep / gravelShipped) + " minutes");
		
		System.out.println(String.format("Successfull loadings\t = %d(%.2f%%), mean size %.2ft", successfulLoadings, 
						(double) successfulLoadings / (successfulLoadings + unsuccessfulLoadings) * 100, 
						(double) successfulLoadingSizes / successfulLoadings));
		
		System.out.println(String.format("Unsuccessfull loadings\t = %d(%.2f%%), mean size %.2ft", unsuccessfulLoadings, 
				(double) unsuccessfulLoadings / (successfulLoadings + unsuccessfulLoadings) * 100,
				(double) unsuccessfulLoadingSizes / unsuccessfulLoadings));
	}


	/**
	 * Prints information after every timeStep in which an event got triggered.
	 */
	@Override
	protected int printEveryStep(int numberOfSteps, int timeStep)
	{
		System.out.print(numberOfSteps++ + ". " + Time.stepsToString(timeStep) + " " + EventQueue.getInstance());
		System.out.println(String.format(" shipped/toShip : %dt(%.2f%%) / %dt", 
				gravelShipped, (double) gravelShipped / gravelToShippedFinal * 100, gravelToShip)); 
		return numberOfSteps;
	}
}
