package dev.despg.visualizations;

import dev.despg.core.Randomizer;
import javafx.application.Application;
import javafx.stage.Stage;

public final class Normal extends Application
{
	private static final double MEAN = 2;
	private static final double DEVIATION = 0.5;


	public static void main(String[] args)
	{
		launch(args);
	}

	@Override
	public void start(Stage stage)
	{
		Randomizer r = new Randomizer();

		Data.draw(stage, "Normal Density", () -> r.getNormal(MEAN, DEVIATION));
	}
}

