package basic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import edu.sagado.genAlg.GeneticFunctions;
import edu.sagado.genAlg.Population;

public class TestGA {
	final static int POP_SIZE = 100;
	final static int NUM_GEN = 5000;
	
//	private void initPop(){
//		pop = new Population<>(POP_SIZE);
//		pop.setComputeFitness(GeneticFunctions.computeIntFitness);
//		pop.setMakeRandomChromo(GeneticFunctions.makeRandomIntChromo);
//		pop.setMutateGenes(GeneticFunctions.mutateIntGenes);
//	}
	
	public static void main(String[] args) {		
		Population<Integer> pop = new Population<>(POP_SIZE,
			GeneticFunctions.makeRandomIntChromo,
			GeneticFunctions.computeIntFitness,
			GeneticFunctions.mutateIntGenes);
		
		while(pop.getGeneration_num() < NUM_GEN){
			pop.newGeneration();
			System.out.println(pop.getFittestChromo().getFitness());
		}
	}
}
