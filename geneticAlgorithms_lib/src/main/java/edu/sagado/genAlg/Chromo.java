package main.java.edu.sagado.genAlg;

import java.util.ArrayList;

/**
 * A Chromo is the basic unit of the genetic population. 
 * Each Chromo consists of a Generic ArrayList (genes) and a fitness score.
 */
public class Chromo<T> {
	ArrayList<T> genes;
	double fitness;
	
	/**
	 * Create a new Chromo with an empty ArrayList<T> and 0 fitness
	 * @param size initial ArrayList capacity;
	 */
	public Chromo(int size){
		genes = new ArrayList<T>(size); 
		fitness = 0;
	}
	
	/**
	 * Create a new Chromo described by genes, with the specific fitness
	 * @param genes the ArrayList to be copied as the new Chromo
	 * @param fitness the fitness of the new Chromo
	 */
	public Chromo(ArrayList<T> genes, double fitness){
		this.genes = genes; 
		this.fitness = fitness;
	}
	
	/**
	 * 
	 * @return the genes of this Chromo
	 */
	public ArrayList<T> get_genes (){
		return genes;
	} 
	
	/**
	 * Set the fitness of this Chromo to the new value
	 * @param fit new value for the fitness
	 */
	public void set_fitness(double fit){
		this.fitness = fit;
	}
}
