package dev.despg.visualizations;

import java.util.HashMap;
import java.util.Map;
import javafx.scene.Scene;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public abstract class Scatterplot
{
	private static final int PIXEL_Y 			= 800;
	private static final int PIXEL_X 			= 1000;

	public static final double PRECISION 		= 0.1;
	public static final int NUMBERS_COMPUTED 	= 100000;

	public static final double MIN = 0;
	public static final double MAX = 10;


	public static void drawPlotWithRandoms(Stage stage, String plotTitle, RandomValue generate)
	{
		stage.setTitle(plotTitle);

		Map<Double, Double> densityFunction = new HashMap<>();
		for (int i = 0; i < Scatterplot.NUMBERS_COMPUTED; i++)
		{
			double value = Math.round(generate.getDouble() / Scatterplot.PRECISION) * Scatterplot.PRECISION;
			Double existingValue = densityFunction.get(value);

			densityFunction.put(value, existingValue == null ? 1.0 : ++existingValue);
		}
		densityFunction.replaceAll((k, v) -> densityFunction.get(k) / Scatterplot.NUMBERS_COMPUTED);

		Series<Number, Number> xy = new Series<>();

		for (Double key : densityFunction.keySet())
			xy.getData().add(new XYChart.Data<>(key, densityFunction.get(key)));

		NumberAxis xAxis = new NumberAxis(MIN, MAX, 0.01);
		NumberAxis yAxis = new NumberAxis();
		ScatterChart<Number, Number> sc = new ScatterChart<>(xAxis, yAxis);
		xAxis.setLabel("random value");
		yAxis.setLabel("probability");

		sc.getData().add(xy);
		sc.setPrefSize(PIXEL_X, PIXEL_Y);
		sc.setTitle(plotTitle);

		VBox vb = new VBox();
		vb.getChildren().add(sc);
		stage.setScene(new Scene(vb, PIXEL_X, PIXEL_Y));
		stage.show();
	}
}
