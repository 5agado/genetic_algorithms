package basic;

import org.junit.Before;
import org.junit.Test;

import model.GeneticFunctions;
import model.Population;

public class TestGA {
	final int POP_SIZE = 80;
	final int NUM_GEN = 500;
	Population<Integer> pop;
	
	@Before
	public void initPop(){
		pop = new Population<>(POP_SIZE,
				GeneticFunctions.makeRandomIntChromo,
				GeneticFunctions.computeIntFitness,
				GeneticFunctions.mutateIntGenes);
	}
	
	@Test
	public void testGA(){
		while(pop.getGeneration_num() < NUM_GEN){
			pop.newGeneration();
		}
		System.out.println(pop.getFittestChromo().getFitness());
	}
}
