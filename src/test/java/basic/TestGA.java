package basic;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

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
		Properties prop = new Properties();
    	
    	String filename = "config.properties";
    	try(InputStream input = TestPopulation.class.getClassLoader().getResourceAsStream(filename);) {
    		if (input == null){
    			System.out.println("File not found");
    		    assertTrue(false);
    		}

    		prop.load(input);
    		
    		pop = new Population<>(POP_SIZE, (double)prop.get("CROSSOVER_RATE"), 
    				(int)prop.get("NUM_ELITE"),
    				GeneticFunctions.makeRandomIntChromo,
    				GeneticFunctions.computeIntFitness,
    				GeneticFunctions.mutateIntGenes);
 
    	} catch (IOException ex) {
    		ex.printStackTrace();
        }
	}
	
	@Test
	public void testGA(){
		while(pop.getNumberOfGenerations() < NUM_GEN){
			pop.newGeneration();
		}
		System.out.println(pop.getFittestChromo().getFitness());
	}
}
