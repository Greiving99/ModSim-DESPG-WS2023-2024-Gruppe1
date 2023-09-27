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


import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;


import static org.assertj.core.api.Assertions.*;


class RandomizerTest
{

	@Mock
	private Randomizer r;

	@BeforeEach
	void init()
	{
		r = new Randomizer();

	}

	/**
	 * Checks if addProb2Int adds new Probability2Value objects correctly. Creates
	 * an expected Randomizer that sets its private class member "prob2Int" through
	 * reflection.
	 */
	@Test
	void addProb2IntShouldAddProbability()
	{
		List<Probability2Value<Integer>> p = new ArrayList<Probability2Value<Integer>>();
		p.add(new Probability2Value<Integer>(0.5, 2));

		//ReflectionTestUtils.setField(rExpected, "prob2Int", p);
		r.addProbInt(0.5, 2);
		Probability2Value<Integer> actual = r.getProb2Int().get(0);
		Probability2Value<Integer> expected = r.getProb2Int().get(0);
		assertThat(actual.value()).isEqualTo(expected.value());
		assertThat(actual.probabilityUpperLimit()).isEqualTo(expected.probabilityUpperLimit());
	}

	/**
	 * Checks if addProbInt throws the correct SimulationException when the
	 * probability is out of the defined bounds.
	 */
	@Test
	void addProbIntShouldThrowBecauseOutOfBounds()
	{
		assertThatThrownBy(() ->
		{
			r.addProbInt(Randomizer.getMinProbability() - 0.1, 5);
		}).isInstanceOf(AssertionError.class);
		assertThatThrownBy(() ->
		{
			r.addProbInt(Randomizer.getMaxProbability() + 0.1, 5);
		}).isInstanceOf(AssertionError.class);

	}

	/**
	 * Checks if addProbInt throws the correct SimulationException if the
	 * parameterized probability already exists.
	 */
	@Test
	void addProbIntShouldThrowBecauseDuplicate()
	{
		assertThatThrownBy(() ->
		{
			r.addProbInt(1, 5);
			r.addProbInt(1, 7);
		}).isInstanceOf(SimulationException.class).hasMessageContaining("already exists");
	}

	/**
	 * Checks if nextInt picks the correct value.
	 */
	@Test
	void nextIntPickedCorrect()
	{
		int expected = 2;
		List<Probability2Value<Integer>> p = new ArrayList<Probability2Value<Integer>>();
		p.add(new Probability2Value<Integer>(1.0, 2));

		//ReflectionTestUtils.setField(r, "prob2Int", p);
		r.addProbInt(1.0, 2);

		assertThat(r.nextInt()).isEqualTo(expected);

	}

	/**
	 * Checks if nextInt throws the correct SimulationException when the probability
	 * is out of bounds.
	 */
	@Test
	void addProbIntShouldThrowAssertionErrorBecauseOutOfBounds()
	{
		List<Probability2Value<Integer>> p = new ArrayList<Probability2Value<Integer>>();
		p.add(new Probability2Value<Integer>(Randomizer.getMaxProbability() + 0.1, 2));
		//ReflectionTestUtils.setField(r, "prob2Int", p);


		assertThatThrownBy(() ->
		{
			r.addProbInt(Randomizer.getMaxProbability() + 0.1, 2);
		}).isInstanceOf(AssertionError.class);


	}

	/**
	 * Checks if nextInt throws the correct SimulationException when the randomized
	 * probability couldn't find a matching upper limit in the Randomizer. Every
	 * Randomizer should have a value assigned to the probability of MAX_PROBABILITY
	 */
	@Test
	void nextIntShouldThrowBecauseProbabilityNotCovered()
	{
		List<Probability2Value<Integer>> p = new ArrayList<Probability2Value<Integer>>();
		p.add(new Probability2Value<Integer>(0.0, 2));
		//ReflectionTestUtils.setField(r, "prob2Int", p);
		r.addProbInt(0.0, 2);

		assertThatThrownBy(() ->
		{
			r.nextInt();
		}).isInstanceOf(SimulationException.class).hasMessageContaining("Probability not covered");


	}

	/**
	 * Checks if nextInt throws the correct SimulationException when the probability
	 * list is empty.
	 */
	@Test
	void nextIntShouldThrowBecauseEmptyArrayList()
	{
		assertThatThrownBy(() ->
		{
			r.nextInt();
		}).isInstanceOf(SimulationException.class).hasMessageContaining("No probabilities");
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
	    void testNextIntOutOfBoundsProbability()
	    {
	        r.addProbInt(Randomizer.getMaxProbability() + 0.1, 5);
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

	        Random fixedSeedRandom = new Random(12345);
	        double mockRandomValue = fixedSeedRandom.nextDouble();


	        double result = r.getTriangular(min, max, mode);
	        assertThat(result).isBetween(min, max);
	    }
	    @Test
	    void testRandomnessProperties()
	    {

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

	        // Call nextInt and expect an exception
	        assertThatThrownBy(() ->
	        {
	            r.nextInt();
	        }).isInstanceOf(SimulationException.class)
	          .hasMessageContaining("is out of bounds");
	    }

	    private static class RandomProvider
	    {
	        double getRandom()
	        {
	            return Math.random();
	        }
	    }

	    @Test
	    void testGetTriangularRandLessThanF()
	    {
	        double min = 1.0;
	        double max = 10.0;
	        double mode = 5.0;

	        RandomProvider randomProvider = new RandomProvider()
	        {
	            @Override
	            double getRandom()
	            {
	                return 0.1;
	            }
	        };

	        double f = (mode - min) / (max - min);
	        double rand = randomProvider.getRandom();

	        double expected = min + Math.sqrt(rand * (max - min) * (mode - min));

	        double result = r.getTriangular(min, max, mode);
	        assertThat(result).isEqualTo(expected);
	    }
}
