package edu.sagado.genAlg;

import java.util.ArrayList;
import java.util.List;

/**
 * A Chromo is the basic unit of the genetic population. 
 * Each Chromo consists of a list of genes and a fitness score.
 */
public class Chromo<T> {
	private List<T> genes;
	private double fitness;
	
	/**
	 * Create a new Chromo described by genes, with the specific fitness
	 * @param genes the ArrayList to be copied as the new Chromo
	 * @param fitness the fitness of the new Chromo
	 */
	public Chromo(List<T> genes, double fitness){
		setGenes(genes);
		setFitness(fitness);
	}
	
	/**
	 * 
	 * @return the genes of this Chromo
	 */
	public List<T> getGenes (){
		return (List<T>) ((ArrayList) genes).clone();
	} 
	
	/**
	 * @param genes the genes to set
	 */
	public void setGenes(List<T> genes) {
		this.genes = (List<T>) ((ArrayList) genes).clone();
	}
	
	/**
	 * @return the fitness
	 */
	public double getFitness() {
		return fitness;
	}
	
	/**
	 * 
	 * @param fitness
	 */
	public void setFitness(double fitness){
		this.fitness = fitness;
	}	
}
