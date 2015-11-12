package model;

import java.util.List;

/**
 * Basic unit of a genetic population. 
 * Each Chromo consists of a list of genes and a fitness score.
 */
public class Chromo<T> {
	private List<T> genes;
	private double fitness;
	
	/**
	 * Constructs a Chromo described by the specified genes and fitness
	 * @param genes the list of genes that will describe the Chromo
	 * @param fitness the current fitness of the Chromo
	 */
	public Chromo(List<T> genes, double fitness){
		setGenes(genes);
		setFitness(fitness);
	}
	
	//TODO: What about implementing a deep copy mechanism for the genes??
	
	public List<T> getGenes (){
		return genes;
	} 
	
	public void setGenes(List<T> genes) {
		this.genes = genes;
	}
	
	public double getFitness() {
		return fitness;
	}
	
	public void setFitness(double fitness){
		this.fitness = fitness;
	}	
}
