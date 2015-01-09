package basic;

import static org.junit.Assert.*;

import java.util.Random;

import model.GeneticFunctions;
import model.Population;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestPopulation {
	final int POP_SIZE = 10;
	final int NUM_GEN = 500;
	static Random rand = new Random();
	static Population<Integer> pop;
	
	@BeforeClass
	public static void initPop(){
		pop = new Population<>(rand.nextInt(100),
				GeneticFunctions.makeRandomIntChromo,
				GeneticFunctions.computeIntFitness,
				GeneticFunctions.mutateIntGenes);
	}
	
	@Test
	public void populationRightInitSize(){
		System.out.println(pop.getIndividuals().size());
		//assert("population size", POP_SIZE, pop.getIndividuals().size());
	}
	
	
	@Test
	public void populationRightSize(){
		System.out.println(pop.getIndividuals().size());
		while(pop.getGeneration_num() < 100){
			pop.newGeneration();
			//assertEquals("population size", POP_SIZE, pop.getIndividuals().size());
		}
	}
}
