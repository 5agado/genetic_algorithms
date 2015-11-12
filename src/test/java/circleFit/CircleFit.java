package circleFit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.IntStream;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import model.Chromo;
import model.Population;

public class CircleFit extends Application {
	public static final int PANEL_HEIGHT = 400;
	public static final int PANEL_WIDTH = 400;
	
	private static final int NUM_CIRCLES = 30;
	private static final int MAX_CIRCLE_RADIUS = 50;
	private static final int MIN_CIRCLE_RADIUS = 5;
	
	private static final int POPULATION_SIZE = 500;
	private static final double CROSSOVER_RATE = 0.5;
	private static final double MUTATION_RATE = 0.05;
	private static final int NUM_ELITE = 2;
	private static final int MAX_GENERATIONS = 2000;
	private static Population<Integer> pop;
	private static List<Circle> circles;
	
	public static void main(String[] args) {
		circles = new ArrayList<Circle>();
		IntStream.range(0, NUM_CIRCLES).forEach(_i -> circles.add(getNewRandomCircle()));
		
		pop = new Population<>(POPULATION_SIZE, CROSSOVER_RATE, NUM_ELITE,
				makeRandomIntChromo, computeIntFitness, mutateIntGenes);
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		Group root = new Group();
		primaryStage.setTitle("Circle Fit");
		primaryStage.setScene(new Scene(root));
		
		//Draw the random generate circles
	    Canvas canvas = new Canvas(PANEL_WIDTH, PANEL_HEIGHT);
	    root.getChildren().add(canvas);  
	    GraphicsContext gc = canvas.getGraphicsContext2D();
	    gc.setStroke(Color.BLACK);
	    circles.stream().forEach(c -> gc.strokeOval(c.center_x-c.radius, c.center_y-c.radius, c.radius*2, c.radius*2));
	    
	    //Update population and draw best solution
	    Canvas canvas2 = new Canvas(PANEL_WIDTH, PANEL_HEIGHT);
	    root.getChildren().add(canvas2);
	    GraphicsContext gc2 = canvas2.getGraphicsContext2D();
	    gc2.setFill(Color.RED);
	    new AnimationTimer(){
	        public void handle(long currentNanoTime){
	        	gc2.clearRect(0, 0, PANEL_WIDTH, PANEL_HEIGHT);
	        	pop.newGeneration();
    	    	Chromo<Integer> best = pop.getFittestChromo();
    			Circle c = new Circle(best.getGenes().get(0), best.getGenes().get(1), best.getGenes().get(2));
    			gc2.fillOval(c.center_x-c.radius, c.center_y-c.radius, c.radius*2, c.radius*2);
    			if (pop.getNumberOfGenerations() >= MAX_GENERATIONS)
    				this.stop();
	        }
	    }.start();
	    
	    primaryStage.show();
	}
	
	public static Function<Random, Chromo<Integer>> makeRandomIntChromo = 
			new Function<Random, Chromo<Integer>>() {
		
		@Override
		public Chromo<Integer> apply(Random rand) {
			Circle c = getNewRandomCircle();
			List<Integer> genes = Arrays.asList(c.center_x, c.center_y, c.radius);
			return new Chromo<Integer>(genes, 0);
		}
	};
	public static Function<Chromo<Integer>, Double> computeIntFitness = 
			new Function<Chromo<Integer>, Double>() {
		
		@Override
		public Double apply(Chromo<Integer> chromo) {
			Circle c = new Circle(chromo.getGenes().get(0), chromo.getGenes().get(1), chromo.getGenes().get(2));
			return (double) (is_valid(c)?c.radius:0);
		}
	};
	public static BiFunction<Random, Chromo<Integer>, Chromo<Integer>> mutateIntGenes = 
			new BiFunction<Random, Chromo<Integer>, Chromo<Integer>>() {
				
				@Override
				public Chromo<Integer> apply(Random rand, Chromo<Integer> chromo) {
					Circle c = getNewRandomCircle();
					List<Integer> genes = Arrays.asList(c.center_x, c.center_y, c.radius);
					for (int i=0; i< chromo.getGenes().size(); i++){
						if (rand.nextDouble() > MUTATION_RATE){
							genes.set(i, chromo.getGenes().get(i));
						}
					}
					
					return new Chromo<Integer>(genes, 0);
				}
	};
	
	static class Circle {
		public int center_x;
		public int center_y;
		public int radius;
		
		public Circle(int center_x, int center_y, int radius){
			this.center_x = center_x;
			this.center_y = center_y;
			this.radius = radius;
		}
	}
	
	public static boolean is_valid (Circle c1){
		boolean valid = true;
		for (Circle c2 : circles){
			if (intersect(c1, c2)){
				valid = false;
				break;
			}
		}
		
		if (c1.center_x >  c1.radius && c1.center_x < (PANEL_WIDTH - c1.radius));
		else
			valid = false;
		if (c1.center_y >  c1.radius && c1.center_y < (PANEL_HEIGHT - c1.radius));
		else
			valid = false;
		return valid;
	}
	
	private static boolean intersect (Circle c1, Circle c2){
		double x=Math.pow((c1.center_x - c2.center_x),2);
	    double y=Math.pow((c1.center_y - c2.center_y),2);
	    double res=Math.sqrt(x+y);
		return (res<(c1.radius+c2.radius) || res<Math.abs(c1.radius-c2.radius));
	}
	
	public static Circle getNewRandomCircle(){
		Random rand = new Random();
		int radius = rand.nextInt(MAX_CIRCLE_RADIUS) + MIN_CIRCLE_RADIUS;

		int x = rand.nextInt(PANEL_WIDTH-(2*radius))+radius;
		int y = rand.nextInt(PANEL_HEIGHT-(2*radius))+radius;
		
		Circle c = new Circle(x, y, radius);
		return c;
	}
}