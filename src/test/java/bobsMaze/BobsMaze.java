package bobsMaze;

import java.awt.Point;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
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

public class BobsMaze extends Application {
	public static final int SQUARE_SIZE = 20;
	
	private static final int POPULATION_SIZE = 1700;
	private static final int NUM_GENES = 70;
	private static final int GENE_BOUND = 4;
	private static final double CROSSOVER_RATE = 0.5;
	private static final double MUTATION_RATE = 0.05;
	private static final int NUM_ELITE = 2;
	private static final int MAX_GENERATIONS = 1000;
	private static Population<Integer> pop;
	private static Maze maze;
	
	public static void main(String[] args) {
		maze = new Maze(BobsMaze.class.getClassLoader().getResource("bobsMaze/maze_01_15x10.txt").getPath());
		pop = new Population<>(POPULATION_SIZE, CROSSOVER_RATE, NUM_ELITE,
				makeRandomIntChromo, computeIntFitness, mutateIntGenes);
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		final int PANEL_HEIGHT = maze.height*SQUARE_SIZE;
		final int PANEL_WIDTH = maze.width*SQUARE_SIZE;
		
		Group root = new Group();
		primaryStage.setTitle("Bob's Maze");
	    primaryStage.setScene(new Scene(root));
	         
	    //Draw the maze 
	    Canvas canvas = new Canvas(PANEL_WIDTH, PANEL_HEIGHT);
	    root.getChildren().add(canvas);   
	    GraphicsContext gc = canvas.getGraphicsContext2D();
	    for (int i=0; i<maze.map.size(); i++){
	    	List<Integer> row = maze.map.get(i);
	    	for (int j=0; j<row.size(); j++){
	    		switch (row.get(j).intValue()) {
				case 1:
					gc.setFill(Color.BLACK);
					gc.fillRect(j*SQUARE_SIZE, i*SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE);
					break;
				case 5:
					gc.setFill(Color.YELLOW);
					gc.fillRect(j*SQUARE_SIZE, i*SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE);
					break;
				case 8:
					gc.setFill(Color.BLUE);
					gc.fillRect(j*SQUARE_SIZE, i*SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE);
					break;
				default:
					break;
				}
	    	}
		}
	    
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
    	    	Point current = new Point(maze.startP.x, maze.startP.y);
    			for (Integer i : best.getGenes()){
    				move(current, i.intValue());
    				if (!maze.isValidPosition(current))
    					break;
    				gc2.fillRect(current.x*SQUARE_SIZE, current.y*SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE);
    			}
    			if (pop.getNumberOfGenerations() >= MAX_GENERATIONS || maze.distToExit(current)==0)
    				this.stop();
	        }
	    }.start();
	    
	    primaryStage.show();
	}
	
	public static void move(Point p, int i){
		switch (i) {
		case 0:         		//up
			p.y++;
			break;	
		case 1:              	//down
			p.y--;	
			break;
		case 2:					//right
			p.x++;
			break;	
		case 3:					//left
			p.x--;
			break;
		default:
			break;
		}
	}
	
	public static Function<Random, Chromo<Integer>> makeRandomIntChromo = 
			new Function<Random, Chromo<Integer>>() {
		
		@Override
		public Chromo<Integer> apply(Random rand) {
			List<Integer> genes = IntStream.range(0, NUM_GENES)
					.map(a -> rand.nextInt(GENE_BOUND))
					.boxed().collect(Collectors.toList());
			return new Chromo<Integer>(genes, 0);
		}
	};
	public static Function<Chromo<Integer>, Double> computeIntFitness = 
			new Function<Chromo<Integer>, Double>() {
		
		@Override
		public Double apply(Chromo<Integer> chromo) {
			Point tmp = new Point(maze.startP.x, maze.startP.y);
			Point current = new Point(maze.startP.x, maze.startP.y);
			for (Integer i : chromo.getGenes()){
				move(current, i.intValue());
				if (!maze.isValidPosition(current))
					break;
				tmp.setLocation(current);
			}
			return (double) 1/maze.distToExit(tmp);
		}
	};
	public static BiFunction<Random, Chromo<Integer>, Chromo<Integer>> mutateIntGenes = 
			new BiFunction<Random, Chromo<Integer>, Chromo<Integer>>() {
				
				@Override
				public Chromo<Integer> apply(Random rand, Chromo<Integer> chromo) {
					List<Integer> genes = IntStream.range(0, NUM_GENES)
							.map(a -> rand.nextInt(GENE_BOUND))
							.boxed().collect(Collectors.toList());
					for (int i=0; i< chromo.getGenes().size(); i++){
						if (rand.nextDouble() > MUTATION_RATE){
							genes.set(i, chromo.getGenes().get(i));
						}
					}
					
					return new Chromo<Integer>(genes, 0);
				}
	};
	
	static class Maze {
		private static final int START = 5;
		private static final int BLOCK = 1;
		private static final int EXIT = 8;
		private List<List<Integer>> map;
		private int height, width;
		Point startP;
		Point exitP;
		
		public Maze(String filePath){
			map = new ArrayList<List<Integer>>();
			readMaze(filePath);
		}
		
		private void readMaze (String filePath){
			try (Scanner scan = new Scanner(new File(filePath));){
				this.width = scan.nextInt();
				this.height = scan.nextInt();
				scan.nextLine();
				for (int i=0; i<height; i++){
		            String[] myList = scan.nextLine().split(" ");
		            List<Integer> row = Arrays.asList(myList).stream().
		            		mapToInt(Integer::parseInt).boxed().collect(Collectors.toList());
		            for (int j=0; j<width; j++){
						if (row.get(j).intValue() == START)
							startP = new Point(j, i);
						if (row.get(j).intValue() == EXIT)
							exitP =  new Point(j, i);
					}
					map.add(row);
				}
			} catch (FileNotFoundException e) {
				System.out.println("No such file " + filePath);
				System.exit(1);
			}
		}
		
		public boolean isValidPosition(Point p){
			if (p.x < 0 || p.x >= width || p.y < 0 || p.y >= height)
				return false;
			if (map.get(p.y).get(p.x) == BLOCK || map.get(p.y).get(p.x) == START || map.get(p.y).get(p.x) == EXIT)
				return false;
			
			return true;
		}
		
		public double distToExit(Point pos){
			return (Math.abs(pos.x - exitP.x) + Math.abs(pos.y - exitP.y));
		}
	}

}
