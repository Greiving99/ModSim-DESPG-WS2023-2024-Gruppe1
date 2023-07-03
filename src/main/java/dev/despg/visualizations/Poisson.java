package dev.despg.visualizations;

import dev.despg.core.Randomizer;
import javafx.application.Application;
import javafx.stage.Stage;

public final class Poisson extends Application
{
	private static final double LAMBDA = 3;

	public static void main(String[] args)
	{
		launch(args);
	}

	@Override
	public void start(Stage stage)
	{
		Randomizer r = new Randomizer();

		Data.draw(stage, "Poisson Density", () -> r.getPoisson(LAMBDA));
	}
}

