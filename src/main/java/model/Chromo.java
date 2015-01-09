package model;

import java.util.List;

/**
 * A Chromo is the basic unit of the genetic population. 
 * Each Chromo consists of a list of genes and a fitness score.
 */
public class Chromo<T> {
	private List<T> genes;
	private double fitness;
	
	/**
	 * Create a new Chromo described by genes and with the specified fitness
	 * @param genes the list of genes that will describe the new Chromo
	 * @param fitness the current fitness of the new Chromo
	 */
	public Chromo(List<T> genes, double fitness){
		setGenes(genes);
		setFitness(fitness);
	}
	
	//List<T>) ((ArrayList<T>) genes).clone()
	//What about implementing a deep copy mechanism for the genes??
	
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
