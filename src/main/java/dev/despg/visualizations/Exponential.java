package dev.despg.visualizations;

import dev.despg.core.Randomizer;
import javafx.application.Application;
import javafx.stage.Stage;

public final class Exponential extends Application
{
	private static final double RATE = 1;

	public static void main(String[] args)
	{
		launch(args);
	}

	@Override
	public void start(Stage stage)
	{
		Randomizer r = new Randomizer();

		Scatterplot.drawPlotWithRandoms(stage, "Exponential Density", () -> r.getExponential(RATE));
	}
}

