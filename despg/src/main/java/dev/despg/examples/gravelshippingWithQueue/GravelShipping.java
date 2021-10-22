package dev.despg.examples.gravelshippingWithQueue;

import java.util.logging.Level;
import java.util.logging.Logger;

import dev.despg.core.EventQueue;
import dev.despg.core.Queue;
import dev.despg.core.Simulation;
import dev.despg.core.Time;

public class GravelShipping extends Simulation
{
	private static Logger logger = Logger.getLogger("GravelShipping");

	public static Integer gravelToShip = 2000;
	public static Integer gravelShipped = 0;
	private final int gravelToShippedFinal = gravelToShip;

	public static Integer successfulLoadings = 0;
	public static Integer successfulLoadingSizes = 0;

	public static Integer unsuccessfulLoadings = 0;
	public static Integer unsuccessfulLoadingSizes = 0;

	private static final int NUM_TRUCKS = 5;
	private static final int NUM_LOADING_DOCKS = 3;
	private static final int NUM_WEIGHING_STATIONS = 2;

	private static Queue<Truck> roadToWeighingStations = new Queue<>("roadToWeighingStations");
	private static Queue<Truck> roadToLoadingDocks = new Queue<>("roadToLoadingDocks");

	/**
	 * Defines the setup of simulation objects and starting events before executing
	 * the simulation. Prints utilization statistics afterwards
	 */
	public static void main(String[] args)
	{
		EventQueue eventqueue = EventQueue.getInstance();

		for (int i = 0; i < NUM_LOADING_DOCKS; i++)
			new LoadingDock("LD" + i, roadToWeighingStations, roadToLoadingDocks);

		for (int i = 0; i < NUM_WEIGHING_STATIONS; i++)
			new WeighingStation("WS" + i, roadToWeighingStations, roadToLoadingDocks);

		for (int i = 0; i < NUM_TRUCKS; i++)
			roadToLoadingDocks.add(new Truck("T" + i));

		GravelShipping gs = new GravelShipping();
		int timeStep = gs.simulate();

		// output some statistics after simulation run
		logger.log(Level.INFO, "Gravel shipped\t\t = " + gravelShipped + " tons");
		logger.log(Level.INFO, "Mean Time / Gravel Unit\t = " + ((double) timeStep / gravelShipped) + " minutes");

		logger.log(Level.INFO,
				String.format("Successfull loadings\t = %d(%.2f%%), mean size %.2ft", successfulLoadings,
						(double) successfulLoadings / (successfulLoadings + unsuccessfulLoadings) * 100,
						(double) successfulLoadingSizes / successfulLoadings));

		logger.log(Level.INFO,
				String.format("Unsuccessfull loadings\t = %d(%.2f%%), mean size %.2ft", unsuccessfulLoadings,
						(double) unsuccessfulLoadings / (successfulLoadings + unsuccessfulLoadings) * 100,
						(double) unsuccessfulLoadingSizes / unsuccessfulLoadings));
	}

	/**
	 * Prints information after every timeStep in which an event got triggered.
	 */
	@Override
	protected void printEveryStep(int numberOfSteps, int timeStep)
	{
		String time = numberOfSteps + ". " + Time.stepsToString(timeStep);
		String eventQueue = "EventQueue: " + EventQueue.getInstance().toString();
		String roadA = roadToLoadingDocks.toString();
		String roadB = roadToWeighingStations.toString();
		String shipped = String.format("shipped/toShip : %dt(%.2f%%) / %dt", gravelShipped,
				(double) gravelShipped / gravelToShippedFinal * 100, gravelToShip);

		logger.log(Level.INFO, time + " " + shipped + "\n " + eventQueue + "\n " + roadA + "\n " + roadB + "\n");
	}
}