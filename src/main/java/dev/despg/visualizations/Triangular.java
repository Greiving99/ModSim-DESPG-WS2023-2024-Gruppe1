package dev.despg.visualizations;

import dev.despg.core.Randomizer;
import javafx.application.Application;
import javafx.stage.Stage;

public final class Triangular extends Application
{
	private static final double MODE = 8;

	public static void main(String[] args)
	{
		launch(args);
	}

	@Override
	public void start(Stage stage)
	{
		Randomizer r = new Randomizer();

		Scatterplot.drawPlotWithRandoms(stage, "Triangular Density", () -> r.getTriangular(Scatterplot.MIN, Scatterplot.MAX, MODE));
	}
}

