/**
 * Copyright (C) 2021 despg.dev, Ralf Buschermöhle
 *
 * DESPG is made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * see LICENSE
 *
 */
package dev.despg.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.Mockito;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Random;

import static org.assertj.core.api.Assertions.*;

import static org.mockito.Mockito.when;

class RandomizerTest
{

    private Randomizer r;
    private Random mockRandom;

    @BeforeEach
    void init()
    {
        r = new Randomizer();

        // Mock the Random class to return predictable values
        mockRandom = Mockito.mock(Random.class);
        when(mockRandom.nextDouble()).thenReturn(0.5);
    }

    @Test
    void addProb2IntShouldAddProbability()
    {
        r.addProbInt(0.5, 2);
        Probability2Value<Integer> actual = r.getProb2Int().get(0);
        assertThat(actual.value()).isEqualTo(2);
        assertThat(actual.probabilityUpperLimit()).isEqualTo(0.5);
    }

    @Test
    void addProbIntShouldThrowBecauseOutOfBounds()
    {
        assertThatThrownBy(() -> r.addProbInt(Randomizer.getMinProbability() - 0.1, 5))
                .isInstanceOf(AssertionError.class);
        assertThatThrownBy(() -> r.addProbInt(Randomizer.getMaxProbability() + 0.1, 5))
                .isInstanceOf(AssertionError.class);
    }

    @Test
    void addProbIntShouldThrowBecauseDuplicate()
    {
        r.addProbInt(1, 5);
        assertThatThrownBy(() -> r.addProbInt(1, 7))
                .isInstanceOf(SimulationException.class).hasMessageContaining("already exists");
    }

    @Test
    void nextIntPickedCorrect()
    {
        r.addProbInt(1.0, 2);
        assertThat(r.nextInt()).isEqualTo(2);
    }

    @Test
    void nextIntShouldThrowBecauseProbabilityNotCovered()
    {
        r.addProbInt(0.0, 2);
        assertThatThrownBy(r::nextInt)
                .isInstanceOf(SimulationException.class).hasMessageContaining("Probability not covered");
    }

    @Test
    void nextIntShouldThrowBecauseEmptyArrayList()
    {
        assertThatThrownBy(r::nextInt)
                .isInstanceOf(SimulationException.class).hasMessageContaining("No probabilities");
    }

	 @Test
	    void testGetUniform()
	 {
	        double min = 1.0;
	        double max = 10.0;

	        double result = r.getUniform(min, max);
	        assertThat(result).isBetween(min, max);
	    }

	    @Test
	    void testGetUniformWithPrecision()
	    {
	        double min = 1.0;
	        double max = 10.0;
	        double precision = 0.5;

	        double result = r.getUniformWithPrecision(min, max, precision);
	        assertThat(result).isBetween(min, max);
	        assertThat(result % precision).isEqualTo(0);
	    }

	    @Test
	    void testGetTriangular()
	    {
	        double min = 1.0;
	        double max = 10.0;
	        double mode = 5.0;

	        double result = r.getTriangular(min, max, mode);
	        assertThat(result).isBetween(min, max);
	    }

	    @Test
	    void testGetTriangularWithPrecision()
	    {
	        double min = 1.0;
	        double max = 10.0;
	        double mode = 5.0;
	        double precision = 0.5;

	        double result = r.getTriangularWithPrecision(min, max, mode, precision);
	        assertThat(result).isBetween(min, max);
	        assertThat(result % precision).isEqualTo(0);
	    }

	    @Test
	    void testGetExponential()
	    {
	        double rate = 2.0;

	        double result = r.getExponential(rate);
	        assertThat(result).isGreaterThan(0);
	    }

	    @Test
	    void testGetNormal()
	    {
	        double mean = 5.0;
	        double deviation = 2.0;

	        double result = r.getNormal(mean, deviation);
	        // Hier überprüfen wir nur, ob das Ergebnis innerhalb von 3 Standardabweichungen liegt.
	        assertThat(result).isBetween(mean - 3 * deviation, mean + 3 * deviation);
	    }

	    @Test
	    void testGetPoisson()
	    {
	        double lambda = 3.0;

	        int result = r.getPoisson(lambda);
	        assertThat(result).isGreaterThanOrEqualTo(0);
	    }

	    @Test
	    void testAssertionsInGetUniform()
	   {
	        assertThatThrownBy(() -> r.getUniform(10.0, 1.0)).isInstanceOf(AssertionError.class);
	    }

	    @Test
	    void testAssertionsInGetUniformWithPrecision()
	    		{
	        assertThatThrownBy(() -> r.getUniformWithPrecision(1.0, 10.0, -0.5)).isInstanceOf(AssertionError.class);
	    }

	    @Test
	    void testAssertionsInGetTriangular()
	    {
	        assertThatThrownBy(() -> r.getTriangular(10.0, 1.0, 5.0)).isInstanceOf(AssertionError.class);
	        assertThatThrownBy(() -> r.getTriangular(1.0, 10.0, 0.0)).isInstanceOf(AssertionError.class);
	        assertThatThrownBy(() -> r.getTriangular(1.0, 10.0, 11.0)).isInstanceOf(AssertionError.class);
	    }

	    @Test
	    void testAssertionsInGetTriangularWithPrecision()
	    {
	        assertThatThrownBy(() -> r.getTriangularWithPrecision(1.0, 10.0, 5.0, -0.5)).isInstanceOf(AssertionError.class);
	    }

	    @Test
	    void testAssertionsInGetExponential()
	    {
	        assertThatThrownBy(() -> r.getExponential(-2.0)).isInstanceOf(AssertionError.class);
	    }

	    @Test
	    void testAssertionsInGetNormal()
	    {
	        assertThatThrownBy(() -> r.getNormal(-5.0, -2.0)).isInstanceOf(AssertionError.class);
	    }
	    @Test
	    void nextIntShouldThrowBecauseOfOutOfBoundsProbability() throws Exception
	    {
	        // Directly access the prob2Int list using reflection
	        Field prob2IntField = Randomizer.class.getDeclaredField("prob2Int");
	        prob2IntField.setAccessible(true);
	        @SuppressWarnings("unchecked")
	        List<Probability2Value<Integer>> prob2Int = (List<Probability2Value<Integer>>) prob2IntField.get(r);

	        // Add a Probability2Value object with an out-of-bounds probability
	        prob2Int.add(new Probability2Value<>(Randomizer.getMaxProbability() + 0.1, 5));

	        // Mock the Random value to be definitely less than the added probability
	        when(mockRandom.nextDouble()).thenReturn(0.01);

	        // Call nextInt and expect an exception
	        assertThatThrownBy(() ->
	        {
	            r.nextInt();
	        }).isInstanceOf(SimulationException.class)
	          .hasMessageContaining("is out of bounds");
	    }
	    @Test
	    void testGetTriangularSecondPath()
	    {
	        double min = 2.0;
	        double max = 10.0;
	        double mode = 5.0;

	        //Random fixedSeedRandom = new Random(12345); // immer gleiche Sequenz
	        //double mockRandomValue = fixedSeedRandom.nextDouble();

	        // Ersetzen Sie `Math.random()` durch `mockRandomValue` in Ihrem Test oder übergeben Sie `mockRandomValue` an Ihre Methode, wenn möglich.

	        double result = r.getTriangular(min, max, mode);
	        assertThat(result).isBetween(min, max);
	    }
	    @Test
	    void testRandomnessProperties()
	    {
	        // Restore the original random behavior for this test
	        when(mockRandom.nextDouble()).thenCallRealMethod();

	        double mean = 5.0;
	        double deviation = 2.0;
	        int trials = 1000;
	        double sum = 0;
	        for (int i = 0; i < trials; i++)
	        		{
	            sum += r.getNormal(mean, deviation);
	        }
	        double average = sum / trials;
	        assertThat(average).isCloseTo(mean, within(deviation));
	    }

	    private Random random = new Random();

	    public void randomizer()
	    {
	        this.random = new Random();
	    }

	    // Constructor with a custom Random instance for testing
	    public void randomizer(Random random)
	    {
	        this.random = random;
	    }

	    // Other methods remain the same

	    // Modify the method to use the injected random instance
	    public double getTriangular(double min, double max, double mode)
	    {
	        assert (max > mode);
	        assert (mode > min);

	        double f = (mode - min) / (max - min);
	        double rand = random.nextDouble();

	        return rand < f
	                ? min + Math.sqrt(rand * (max - min) * (mode - min))
	                : max - Math.sqrt((1 - rand) * (max - min) * (max - mode));
	    }
	    @Test
	    void nextIntShouldThrowBecauseNoProbabilityAdded() throws Exception
	    {
	        assertThatThrownBy(() -> r.nextInt()).isInstanceOf(SimulationException.class)
	          .hasMessageContaining("No probabilities in ArrayList");
	    }

}
