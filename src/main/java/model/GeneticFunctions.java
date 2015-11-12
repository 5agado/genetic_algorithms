package model;

import java.util.List;
import java.util.Random;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class GeneticFunctions {
	final static int GENE_LEN = 100;
	final static int GENE_BOUND = 100;
	
	private GeneticFunctions() {};
	
	public static Function<Random, Chromo<Integer>> makeRandomIntChromo = 
			new Function<Random, Chromo<Integer>>() {
		
		@Override
		public Chromo<Integer> apply(Random rand) {
			List<Integer> genes = IntStream.range(0, GENE_LEN)
				.map(a -> rand.nextInt(GENE_BOUND))
				.boxed()
				.collect(Collectors.toList());
			
			return new Chromo<Integer>(genes, 0);
		}
	};
	public static Function<Chromo<Integer>, Double> computeIntFitness = 
			new Function<Chromo<Integer>, Double>() {
		
		@Override
		public Double apply(Chromo<Integer> chromo) {
			return (double) chromo.getGenes().stream().filter(i -> (i>50)).count();
		}
	};
	public static BiFunction<Random, Chromo<Integer>, Chromo<Integer>> mutateIntGenes = 
			new BiFunction<Random, Chromo<Integer>, Chromo<Integer>>() {
				
				@Override
				public Chromo<Integer> apply(Random rand, Chromo<Integer> chromo) {
					List<Integer> genes = chromo.getGenes().stream()
						.map(gene -> {
							if (rand.nextDouble() > 0.2)
								return gene;
							else
								return rand.nextInt(GeneticFunctions.GENE_BOUND);
						})
						.collect(Collectors.toList());
					return new Chromo<Integer>(genes, 0);
				}
	};
}
