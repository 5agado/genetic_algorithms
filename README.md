#Genetic Algorithms
Template for genetic algorithms using a population of chromosomes, with related evolutionary behaviors (e.g. generation, selection, crossover, mutation, elitism).  

##Basic Definition
A genetic algorithm is a specific type of evolutionary algorithm, and it is about searching the solution of a problem using concepts of natural selection and evolution, starting from an initial set of randomly generated hypothesis. Such set is most commonly referred as *population*. 

A population is composed of a list of chromosomes of generic type (representation of the genes).
A chromosome is an entity characterized by a fitness score and a series of genes. Each chromosome encodes a possible solution for the addressed problem.

At each step of the algorithm a population is evolved via a set of different operations. First we select a couple of individuals (*selection*), then we combine them in order to generate new chromosomes (*crossover*) inserting possible random modifications (*mutation*). The old populations is then replaced by the newly generated offspring.

An additional technique is called *elitism*, and is about keeping the current best individuals in the new population.

##Usage
A new population can be instantiated and run by providing the genetic parameters and functions.

Genetic parameters are size, crossover-rate and elite-size.

There are three genetic functions required: one for the generation of a new chromosomes, one for the fitness computation and one for the gene mutation.

## License

Released under version 2.0 of the [Apache License].

[Apache license]: http://www.apache.org/licenses/LICENSE-2.0
