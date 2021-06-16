package dev.despg.core;


import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.test.util.ReflectionTestUtils;
import static org.assertj.core.api.Assertions.*;

import dev.despg.core.exception.SimulationException;


class RandomizerTest {
	
	@Mock Randomizer r;
	
	@BeforeEach
	void init() {
		r = new Randomizer();
		
	}
	
	@Test
	void addProb2Int_shouldAddProbability() {
		Randomizer rExpected = new Randomizer();
		ArrayList<Probability2Value<Integer>> p = new ArrayList<Probability2Value<Integer>>();
		p.add(new Probability2Value<Integer>(0.5,2));
		
		ReflectionTestUtils.setField(rExpected, "prob2Int", p );
		r.addProbInt(0.5, 2);
		Probability2Value<Integer> actual = r.getProb2Int().get(0);
		Probability2Value<Integer> expected = r.getProb2Int().get(0);
		assertThat(actual.value).isEqualTo(expected.value);
		assertThat(actual.probabilityUpperLimit).isEqualTo(expected.probabilityUpperLimit);
	}
	
	@Test
	void addProbInt_shouldThrowBecauseOutOfBounds() {
		   assertThatThrownBy(() -> { r.addProbInt(Randomizer.getMinProbability()-0.1,5); }).isInstanceOf(SimulationException.class)
           .hasMessageContaining("is out of bounds");
		   assertThatThrownBy(() -> { r.addProbInt(Randomizer.getMaxProbability()+0.1,5); }).isInstanceOf(SimulationException.class)
           .hasMessageContaining("is out of bounds");
		   
	}
	
	@Test
	void addProbInt_shouldThrowBecauseDuplicate() {
		assertThatThrownBy(() -> { r.addProbInt(1,5); r.addProbInt(1, 7); }).isInstanceOf(SimulationException.class)
        .hasMessageContaining("already exists");
	}
	
	@Test
	void nextIntPickedCorrect() {
		int expected = 2;
		ArrayList<Probability2Value<Integer>> p = new ArrayList<Probability2Value<Integer>>();
		//p.add(new Probability2Value<Integer>(0.5,1));
		p.add(new Probability2Value<Integer>(1.0,2));
		
		ReflectionTestUtils.setField(r, "prob2Int", p );
		
		assertThat(r.nextInt()).isEqualTo(expected);
		
	}
	
	@Test
	void nextInt_shouldThrowBecauseOutOfBounds() {
		ArrayList<Probability2Value<Integer>> p = new ArrayList<Probability2Value<Integer>>();
		p.add(new Probability2Value<Integer>(Randomizer.getMaxProbability()+0.1,2));
		ReflectionTestUtils.setField(r, "prob2Int", p );
		
		assertThatThrownBy(() -> { r.nextInt(); }).isInstanceOf(SimulationException.class)
        .hasMessageContaining("is out of bounds");

		
	}
	
	@Test
	//can Random.nextDouble roll 0.0?
	void nextInt_shouldThrowBecauseProbabilityNotCovered() {
		ArrayList<Probability2Value<Integer>> p = new ArrayList<Probability2Value<Integer>>();
		p.add(new Probability2Value<Integer>(0.0,2));
		ReflectionTestUtils.setField(r, "prob2Int", p );
		
		assertThatThrownBy(() -> { r.nextInt(); }).isInstanceOf(SimulationException.class)
        .hasMessageContaining("Probability not covered");

		
	}
	
	@Test
	void nextInt_shouldThrowBecauseEmptyArrayList() {
		ArrayList<Probability2Value<Integer>> p = new ArrayList<Probability2Value<Integer>>();
		ReflectionTestUtils.setField(r, "prob2Int", p );
	
		assertThatThrownBy(() -> { r.nextInt(); }).isInstanceOf(SimulationException.class)
        .hasMessageContaining("No probabilities");	
	}
	

}
