/**
 * 
 */
package main.java.edu.sagado.genAlg;

import java.util.ArrayList;
import java.util.Random;

/**
 * The Population class holds the array of chromos and run
 * the properly update operations on it
 *
 */
public abstract class Population <T> {
    private final double CROSSOVER_RATE;
    
    private final int NUM_ELITE;
    private final int NUM_COPIES_ELITE; 
	private Random rand; 
	
	private int popSize;
	private ArrayList<Chromo<T>> individuals;
	private double totalFitness;
	private int generation_num;	
	private int chromoLength;
	
	public double bestFitness;
	public int fittestChromo;
	public double averageFitness;
	public double worstFitness;
	
	/**
	 * Initialize a new Population
	 * 
	 * standard value:
	 * 	CROSSOVER_RATE = 0.7
	 * 	NUM_ELITE = 0
	 * 	NUM_COPIES_ELITE = 0
	 * @param size number of Chromos in the population
	 * @param chromoLen number of genes per Chromo 
	 */
	public Population (int size, int chromoLen){
		rand = new Random();
		CROSSOVER_RATE = 0.7;
		NUM_ELITE = 0;
		NUM_COPIES_ELITE = 0;
		
		popSize = size;
		generation_num = 0;
		bestFitness = 0;
		averageFitness = 0;
		worstFitness = 9999999;
		totalFitness = 0;

		individuals = new ArrayList<Chromo<T>>(popSize);
		chromoLength = chromoLen;
		
		random_populationInit();
		set_popValues();
	}
	
	/**
	 * Initialize a new Population
	 * 
	 * @param size number of Chromos in the population
	 * @param chromoLen number of genes per Chromo
	 * @param crossOverRate
	 * @param numElite how many fittest-Chromos maintain
	 * @param numCopiesPerElite how many copies for each fittest-Chromo maintein
	 */
	public Population (int size, int chromoLen, double crossOverRate, int numElite, int numCopiesPerElite){
		rand = new Random();
		CROSSOVER_RATE = crossOverRate;
		NUM_ELITE = numElite;
		NUM_COPIES_ELITE = numCopiesPerElite;
		
		popSize = size;
		generation_num = 0;
		bestFitness = 0;
		averageFitness = 0;
		worstFitness = 9999999;
		totalFitness = 0;

		individuals = new ArrayList<Chromo<T>>(popSize);
		chromoLength = chromoLen;
		
		random_populationInit();
		set_popValues();
	}
	
	/**
	 * Define the method that provides a random value (of T type) for
	 * the initialization of the population. 
	 * @return the random value
	 */
	public abstract T randomGene();
	
	/**
	 * Define how and when a mutation must occur 
	 * 
	 * @param genes the genes on which the mutation will occur
	 */
	public abstract void mutateGenes (ArrayList<T> genes);
	
	/**
	 * Define the fitness assignment method
	 * @param pop the Chromos population
	 */
	public abstract void setChromoFitness (ArrayList<Chromo<T>> pop);
	
	private void random_populationInit(){
		for (int i=0; i<popSize; i++){
			ArrayList<T> genes = new ArrayList<T>(chromoLength);
			for (int j=0; j<chromoLength; j++){
				genes.add(randomGene());
			}
			individuals.add(new Chromo<T>(genes, 0));
        }
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
	public boolean new_generation(){		
		ArrayList<Chromo<T>> freshPop = new ArrayList<Chromo<T>>(popSize);
		ArrayList<T> offspring1, offspring2;	
		int popCounter = 0;
		
		popCounter = NUM_ELITE * NUM_COPIES_ELITE;
		if (popCounter>0)
			sort(individuals);
		
		maintainBest(NUM_ELITE, NUM_COPIES_ELITE, freshPop);
		
		
		while (popCounter < popSize){		
			
			offspring1 = roulette_selection();
	    	offspring2 = roulette_selection();
	    	
	    	crossOver(offspring1, offspring2);
	    	
	    	mutateGenes(offspring1);
	    	mutateGenes(offspring2);	    	
	    	Chromo<T> off1 = new Chromo<T>(offspring1, 0);
	    	Chromo<T> off2 = new Chromo<T>(offspring2, 0);
	    	
	    	
	    	freshPop.add(off1);
	    	freshPop.add(off2);
	    	popCounter += 2;
		
		}
		
		for (int i=0; i<popSize; i++){
			individuals.set(i, freshPop.get(i));
		}
		
		set_popValues();
		generation_num++;
		return true;
	} 
	
	private void set_popValues(){
		totalFitness = 0;
		bestFitness = 0;
		averageFitness = 0;
		worstFitness = 9999999;
		
		for (int i=0; i<popSize; i++){
			if (individuals.get(i).fitness > bestFitness){
				bestFitness = individuals.get(i).fitness;
				fittestChromo = i;
			}
			
			if (individuals.get(i).fitness < worstFitness){
				worstFitness = individuals.get(i).fitness;
			}
			
			totalFitness += individuals.get(i).fitness;
        }
		
		averageFitness = totalFitness/popSize;
	}
		
	private ArrayList<T> roulette_selection(){
		double slice = rand.nextDouble() * totalFitness;
		double fitnessSoFar = 0;
		
		if (totalFitness == 0){
			int i = rand.nextInt(popSize);
			ArrayList<T> res = new ArrayList<T>(chromoLength);
			for (int j=0; j<chromoLength; j++)
				res.add(individuals.get(i).genes.get(j));
			return res;
		}

		for (int i=0; i<popSize; i++){
			fitnessSoFar += individuals.get(i).fitness;
			if (fitnessSoFar >= slice){
				ArrayList<T> res = new ArrayList<T>(chromoLength);
				for (int j=0; j<chromoLength; j++)
					res.add(individuals.get(i).genes.get(j));
				return res;
			}
		}

		System.out.println("Population.roulette_selection ERROR: no chromo selected");
		System.exit(1);
		return null;
		
	}
	
	private void crossOver(ArrayList<T> offspring1, ArrayList<T> offspring2){
	    T tmp;

	    if (rand.nextDouble() < CROSSOVER_RATE){
	    	int position = (rand.nextInt(chromoLength));
	    	
	        for (int i=position; i<chromoLength; i++){
	            tmp = offspring1.get(i);
				offspring1.set(i, offspring2.get(i));
	            offspring2.set(i, tmp);
	        }
	    }
	}
	
	private void sort(ArrayList<Chromo<T>> pop){
    	for (int i=1; i<pop.size(); i++){
    		Chromo<T> tmp = pop.get(i);
    		int j = i;
    		while (j>0 && pop.get(j-1).fitness<tmp.fitness){
    			pop.set(j, pop.get(j-1));
    			j--;
    		}
    		pop.set(j, tmp);
    	}
    }
	
	private void maintainBest(int nBest, int nCopies, ArrayList<Chromo<T>> pop){
		int popCounter = 0;
		while(nBest>0){
			for (int i=0; i<nCopies && popCounter<popSize; ++i){
				pop.add(popCounter, individuals.get(popCounter));
				popCounter++;
			}
			nBest--;
		}
	}
	
	/**
	 * 
	 * @return the ArrayList that represents the population
	 */
	public ArrayList<Chromo<T>> get_population(){
		return individuals;
	}
}
