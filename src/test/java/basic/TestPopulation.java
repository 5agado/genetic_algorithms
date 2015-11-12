package basic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Random;

import model.GeneticFunctions;
import model.Population;

import org.junit.BeforeClass;
import org.junit.Test;

public class TestPopulation {
	final static int POP_SIZE = new Random().nextInt(100);
	final int NUM_GEN = 100;
	static Population<Integer> pop;
	
	@BeforeClass
	public static void initPop(){
		Properties prop = new Properties();
    	
    	String filename = "config.properties";
    	try(InputStream input = TestPopulation.class.getClassLoader().getResourceAsStream(filename);) {
    		if (input == null){
    			System.out.println("File not found");
    		    assertTrue(false);
    		}

    		prop.load(input);
    		
    		pop = new Population<>(POP_SIZE, Double.parseDouble(prop.getProperty("CROSSOVER_RATE")), 
    				Integer.parseInt(prop.getProperty("NUM_ELITE")),
    				GeneticFunctions.makeRandomIntChromo,
    				GeneticFunctions.computeIntFitness,
    				GeneticFunctions.mutateIntGenes);
 
    	} catch (IOException ex) {
    		ex.printStackTrace();
        }
	}
	
	@Test
	public void populationRightInitSize(){
		assertEquals("population size", POP_SIZE, pop.getIndividuals().size());
	}
	
	
	@Test
	public void populationRightSize(){
		System.out.println(pop.getIndividuals().size());
		while(pop.getNumberOfGenerations() < NUM_GEN){
			pop.newGeneration();
			assertEquals("population size", POP_SIZE, pop.getIndividuals().size());
		}
	}
}
