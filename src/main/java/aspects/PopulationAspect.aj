package aspects;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import model.Chromo;
import model.Population;


public aspect PopulationAspect {
	private static Logger logger = Logger.getLogger("MyLogger");
	
	public PopulationAspect() throws SecurityException, IOException {
		logger.setLevel(Level.FINEST);
		FileHandler fh = new FileHandler("src/test/resources/pop.log");
		fh.setFormatter(new SimpleFormatter());
		fh.setLevel(Level.FINEST);
		logger.addHandler(fh);
	}
	
	pointcut rouletteSelection() : 
		execution(* Population.rouletteSelection());
	
	pointcut newGeneration() : 
		execution(* Population.newGeneration());
	
	@SuppressWarnings("rawtypes")
	Chromo around(Population p) : rouletteSelection() && target(p){
		Chromo c = proceed(p);
		if (c == null){
			logger.warning("Roulette selection returned null");
			c = p.getRandomChromoFromPopulation();
		}
		return c;
	}
	
	@SuppressWarnings("rawtypes")
	boolean around(Population p) : newGeneration() && target(p){
		Double beforeBestFitness = 0.0;
		if (p.getFittestChromo() != null){
			beforeBestFitness = p.getFittestChromo().getFitness();
		}
		if (proceed(p)){
			Double afterBestFitness = p.getFittestChromo().getFitness();
			Double improvement = afterBestFitness - beforeBestFitness;
			if (improvement > 0){
				logger.info("New generation " + p.getGeneration_num() + 
						", bestFitness improvement of " + improvement);
			}
			else {
				logger.fine("New generation " + p.getGeneration_num() + 
						" no improvment");
			}
			return true;
		}
		else {
			logger.warning("Failed to generate new generation");
			return false;
		}
	}
	
}
