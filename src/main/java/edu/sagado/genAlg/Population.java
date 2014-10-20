/**
 * 
 */
package edu.sagado.genAlg;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * The Population class holds the array of chromos and run
 * the properly update operations on it
 *
 */
public class Population <T> {
    private final double CROSSOVER_RATE = 0.5;
    private final int NUM_ELITE = 2;
	
    private Random rand = new Random(); 
	
	private int size;
	private List<Chromo<T>> individuals;
	private double totalFitness = 0;
	private int generation_num = 0;

	public double bestFitness = 0;
	public Chromo<T> fittestChromo;
	public double averageFitness = 0;
	public double worstFitness = -1;
	
	private Function<Random, Chromo<T>> makeRandomChromo;
	private Function<Chromo<T>, Double> computeFitness;
	private BiFunction<Random, Chromo<T>, Chromo<T>> mutateGenes;
	
	/**
	 * Initialize a new Population
	 * 
	 * standard value:
	 * 	CROSSOVER_RATE = 0.7
	 * 	NUM_ELITE = 1
	 * @param size number of Chromos in the population
	 * @param chromoLen number of genes per Chromo 
	 */
	public Population (int size,
			Function<Random, Chromo<T>> makeRandomChromo,
			Function<Chromo<T>, Double> computeFitness,
			BiFunction<Random, Chromo<T>, Chromo<T>> mutateGenes){		
		this.size = size;
		individuals = new ArrayList<Chromo<T>>(this.size);
		this.makeRandomChromo = makeRandomChromo;
		this.computeFitness = computeFitness;
		this.mutateGenes = mutateGenes;
		initPopulation();
	}
	
	private void initPopulation(){
		IntStream.range(0, size)
			.forEach(_i -> individuals.add(makeRandomChromo.apply(rand)));
	}

	/**
	 * Run the all genetic algorithm phases, that are:
	 * 1.Calculate and set the values of the actual population;
	 * 2.In case maintain some best individuals (so a sorting will occur);
	 * 		define a new population by
	 * 3.Pick up two offspring with roulette selection;
	 * 4.Crossover these two offspring;
	 * 5.Mutate and add them to the new population;
	 * @return if the algorithms has performed correctly 
	 */
	public boolean newGeneration(){		
		List<Chromo<T>> freshPop = new ArrayList<Chromo<T>>(size);
		
		setPopValues();
		
		freshPop.addAll(getElite());
		
		
		while (freshPop.size() < size){		
			Chromo<T> offspring1 = rouletteSelection();
			Chromo<T> offspring2 = rouletteSelection();
	    	
	    	crossOver(offspring1, offspring2);
	    	
	    	freshPop.add(mutateGenes.apply(rand, offspring1));
	    	freshPop.add(mutateGenes.apply(rand, offspring2));
		}
		
		individuals = freshPop;
		
		generation_num++;
		return true;
	} 
	
	private void setPopValues(){
		totalFitness = 0;
		bestFitness = 0;
		averageFitness = 0;
		worstFitness = 9999999;
		
		totalFitness = individuals.stream()
				.map(this::fitnessCheck)
				.mapToDouble(d -> d).sum();
		
		averageFitness = totalFitness/size;
	}
	
	private double fitnessCheck(Chromo<T> chromo){
		double fitness = computeFitness.apply(chromo);
		chromo.setFitness(fitness);
		
		if (fitness > bestFitness){
			bestFitness = fitness;
			fittestChromo = chromo;
		}
		
		if (fitness < worstFitness){
			worstFitness = fitness;
		}
		
		return fitness;	
	}
		
	private Chromo<T> rouletteSelection(){
		Chromo<T> selected = null;
		double slice = rand.nextDouble() * totalFitness;
		double fitnessSoFar = 0;
		
		if (totalFitness == 0){
			selected = new Chromo<>(individuals.get(rand.nextInt(size)).getGenes(), 0);
		}

		for (int i=0; i<size; i++){
			fitnessSoFar += individuals.get(i).getFitness();
			if (fitnessSoFar >= slice){
				selected = new Chromo<>(individuals.get(i).getGenes(), 0);
			}
		}
		
		if (selected == null){
			System.out.println("NUll Selection " + fitnessSoFar);
			selected = new Chromo<>(individuals.get(rand.nextInt(size)).getGenes(), 0);
		}

		return selected;
		
	}
	
	private void crossOver(Chromo<T> offspring1, Chromo<T> offspring2){
	    T tmp;
	    List<T> genes1 = offspring1.getGenes();
	    List<T> genes2 = offspring2.getGenes();

	    if (rand.nextDouble() < CROSSOVER_RATE){
	    	int position = (rand.nextInt(genes1.size()));
	    	
	        for (int i=position; i<genes1.size(); i++){
	            tmp = genes1.get(i);
				genes1.set(i, genes2.get(i));
	            genes2.set(i, tmp);
	        }
	    }
	    
	    offspring1.setGenes(genes1);
	    offspring2.setGenes(genes2);
	}
	
	private List<Chromo<T>> getElite(){
		return individuals.stream()
				.sorted((i1, i2) -> Double.compare(i2.getFitness(), i1.getFitness()))
				.limit(NUM_ELITE)
				.map(chromo -> new Chromo<>(chromo.getGenes(), 0))
				.collect(Collectors.toList());
	}

	/**
	 * @return the individuals
	 */
	public List<Chromo<T>> getIndividuals() {
		return individuals;
	}

	/**
	 * @param individuals the individuals to set
	 */
	public void setIndividuals(List<Chromo<T>> individuals) {
		this.individuals = individuals;
	}
	
	/**
	 * @return the generation_num
	 */
	public int getGeneration_num() {
		return generation_num;
	}

	/**
	 * @return the fittestChromo
	 */
	public Chromo<T> getFittestChromo() {
		return fittestChromo;
	}
	
	/**
	 * @return the totalFitness
	 */
	public double getTotalFitness() {
		return totalFitness;
	}

	/**
	 * @return the averageFitness
	 */
	public double getAverageFitness() {
		return averageFitness;
	}
}
