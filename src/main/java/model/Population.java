package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * A genetic population and related behavior. 
 * The population is a list of @see src.main.java.model.Chromo.
 * The population behavior is defined by three functions: how a new chromo is generated, 
 * how single fitness is computed and how genes mutate.
 *
 */
public class Population <T> {
    private final double CROSSOVER_RATE;
    private final int NUM_ELITE;
	
    private Random rand = new Random(); 
	
	private int size;
	private List<Chromo<T>> individuals;
	private Chromo<T> fittestChromo;
	private int generation_num = 0;
	
	private Function<Random, Chromo<T>> makeRandomChromo;
	private Function<Chromo<T>, Double> computeFitness;
	private BiFunction<Random, Chromo<T>, Chromo<T>> mutateGenes;
	
	/**
	 * Constructs and initialize a new genetic population
	 * 
	 * @param size number of @see src.main.java.model.Chromo in the population
	 * @param crossoverRate
	 * @param numElite
	 * @param makeRandomChromo function
	 * @param computeFitness function
	 * @param mutateGenes function
	 */
	public Population (int size, double crossoverRate, int numElite,
			Function<Random, Chromo<T>> makeRandomChromo,
			Function<Chromo<T>, Double> computeFitness,
			BiFunction<Random, Chromo<T>, Chromo<T>> mutateGenes){	
		this.CROSSOVER_RATE = crossoverRate;
		this.NUM_ELITE = numElite;
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
	 * Run the genetic algorithm steps:
	 * 1.Maintain {@value #NUM_ELITE} individuals;
	 * 		define a new population by
	 * 2.Pick up two offspring with roulette selection;
	 * 3.Crossover these two offspring;
	 * 4.Mutate and add them to the new population;
	 * 5.Calculate and set the fitness values of the actual population;
	 */
	public void newGeneration(){		
		List<Chromo<T>> freshPop = new ArrayList<Chromo<T>>(size);
		
		freshPop.addAll(getElite());
		
		
		while (freshPop.size() < size){		
			Chromo<T> offspring1 = rouletteSelection();
			Chromo<T> offspring2 = rouletteSelection();
	    	
	    	crossOver(offspring1, offspring2);
	    	
	    	freshPop.add(mutateGenes.apply(rand, offspring1));
	    	if (freshPop.size() == size)
	    		break;
	    	freshPop.add(mutateGenes.apply(rand, offspring2));
		}
		
		this.individuals = freshPop;
		setFitnessValues();
		this.generation_num++;
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
				selected = new Chromo<>(individuals.get(i).getGenes(), individuals.get(i).getFitness());
				break;
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
		Chromo<T> selected = individuals.get(rand.nextInt(size));
		return new Chromo<>(selected.getGenes(), selected.getFitness());
	}	

	public List<Chromo<T>> getIndividuals() {
		return individuals.stream()
				.map(chromo -> new Chromo<>(chromo.getGenes(), chromo.getFitness()))
				.collect(Collectors.toList());
	}

	public void setIndividuals(List<Chromo<T>> individuals) {
		this.individuals = individuals.stream()
				.map(chromo -> new Chromo<>(chromo.getGenes(), chromo.getFitness()))
				.collect(Collectors.toList());
	}
	
	public int getNumberOfGenerations() {
		return this.generation_num;
	}

	public Chromo<T> getFittestChromo() {
		return new Chromo<>(fittestChromo.getGenes(), fittestChromo.getFitness());
	}
}
