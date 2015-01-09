package model;

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
	private Chromo<T> fittestChromo;
	private int generation_num = 0;
	
	private Function<Random, Chromo<T>> makeRandomChromo;
	private Function<Chromo<T>, Double> computeFitness;
	private BiFunction<Random, Chromo<T>, Chromo<T>> mutateGenes;
	
	/**
	 * Initialize a new Population
	 * 
	 * standard values:
	 * 	CROSSOVER_RATE = {@value #CROSSOVER_RATE}
	 * 	NUM_ELITE = {@value #NUM_ELITE}
	 * @param size number of Chromos in the population
	 * @param makeRandomChromo function
	 * @param computeFitness function
	 * @param mutateGenes function
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
		setFitnessValues();
	}
	
	private void initPopulation(){
		IntStream.range(0, size)
			.forEach(_i -> individuals.add(makeRandomChromo.apply(rand)));
	}

	/**
	 * Run the all genetic algorithm phases, that are:
	 * 1.Maintain {@value #NUM_ELITE} individuals;
	 * 		define a new population by
	 * 2.Pick up two offspring with roulette selection;
	 * 3.Crossover these two offspring;
	 * 4.Mutate and add them to the new population;
	 * 5.Calculate and set the fitness values of the actual population;
	 * @return if the algorithms has performed correctly 
	 */
	public boolean newGeneration(){		
		List<Chromo<T>> freshPop = new ArrayList<Chromo<T>>(size);
		
		freshPop.addAll(getElite());
		
		
		while (freshPop.size() < size){		
			Chromo<T> offspring1 = rouletteSelection();
			Chromo<T> offspring2 = rouletteSelection();
	    	
	    	crossOver(offspring1, offspring2);
	    	
	    	freshPop.add(mutateGenes.apply(rand, offspring1));
	    	freshPop.add(mutateGenes.apply(rand, offspring2));
		}
		
		individuals = freshPop;
		setFitnessValues();
		generation_num++;
		return true;
	} 
	
	private void setFitnessValues() {
		individuals.stream()
			.forEach(chromo -> chromo.setFitness(computeFitness.apply(chromo)));
		fittestChromo = individuals.stream()
			.sorted((i1, i2) -> Double.compare(i2.getFitness(), i1.getFitness()))
			.findFirst().get();
	}
		
	private Chromo<T> rouletteSelection(){
		Chromo<T> selected = null;
		double totalFitness = individuals.stream()
				.mapToDouble(chromo -> chromo.getFitness()).sum();
		double slice = rand.nextDouble() * totalFitness;
		double fitnessSoFar = 0;
		
		if (totalFitness == 0){
			selected = getRandomChromoFromPopulation();
		}

		for (int i=0; i<size; i++){
			fitnessSoFar += individuals.get(i).getFitness();
			if (fitnessSoFar >= slice){
				selected = new Chromo<>(individuals.get(i).getGenes(), 0);
			}
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
				.collect(Collectors.toList());
	}
	
	public Chromo<T> getRandomChromoFromPopulation(){
		return new Chromo<>(individuals.get(rand.nextInt(size)).getGenes(), 0);
	}	

	//What about implementing a deep copy mechanism for the genes??
	
	public List<Chromo<T>> getIndividuals() {
		return individuals.stream()
				.map(chromo -> new Chromo<>(chromo.getGenes(), 0))
				.collect(Collectors.toList());
	}

	public void setIndividuals(List<Chromo<T>> individuals) {
		this.individuals = individuals.stream()
				.map(chromo -> new Chromo<>(chromo.getGenes(), 0))
				.collect(Collectors.toList());
	}
	
	public int getGeneration_num() {
		return generation_num;
	}

	public Chromo<T> getFittestChromo() {
		return new Chromo<>(fittestChromo.getGenes(), fittestChromo.getFitness());
	}
}
