import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


public class DiningPhilosopher {
	
	/*
	 * Controls whether logs should be shown on the console or not. 
	 * Logs should print events such as: state of the philosopher, and state of the chopstick
	 * 		for example: philosopher # is eating; 
	 * 		philosopher # picked up his left chopstick (chopstick #) 
	 */
	public boolean DEBUG = false;
	private final int NUMBER_OF_PHILOSOPHERS = 5;
	private int SIMULATION_TIME = 10000;
	private int SEED = 0;
	
//	private ExecutorService deadlockDetectorThread = Executors.newFixedThreadPool(1);
	
	ExecutorService executorService = null;
	ArrayList<Philosopher> philosophers = null;
	ArrayList<ChopStick> chopSticks = null;
	ExecutorService deadlockDetectorThread = null; // = Executors.newFixedThreadPool(1);
	
	DeadlockDetector deadlockDetector = null;
	
	public void start() throws InterruptedException {		
		try {
				/*
				 * First we start two non-adjacent threads, which are T1 and T3
				 */
				for (int i = 1; i < NUMBER_OF_PHILOSOPHERS; i+=2) {
					executorService.execute(philosophers.get(i));
					Thread.sleep(50); //makes sure that this thread kicks in before the next one
				}
			
				/*
				 * Now we start the rest of the threads, which are T0, T2, and T4
				 */
				for (int i = 0; i < NUMBER_OF_PHILOSOPHERS; i+=2) {
					executorService.execute(philosophers.get(i));
					Thread.sleep(50); //makes sure that this thread kicks in before the next one
				}
				executorService.execute(deadlockDetector);
			
				// Main thread sleeps till time of simulation
				Thread.sleep(SIMULATION_TIME);		
			
				// Stop all philosophers.
				for (Philosopher p : philosophers) {
					p.abort();
				}
		
		} finally {
			executorService.shutdown();
			executorService.awaitTermination(10, TimeUnit.MILLISECONDS);
		}
	}

	public void initialize(int simulationTime, int randomSeed) {
		SIMULATION_TIME = simulationTime;
		SEED = randomSeed;
		
		philosophers = new ArrayList<Philosopher>(NUMBER_OF_PHILOSOPHERS);
		chopSticks = new ArrayList<ChopStick>(NUMBER_OF_PHILOSOPHERS);
		
		//create the executor service
		executorService = Executors.newFixedThreadPool(NUMBER_OF_PHILOSOPHERS);
		deadlockDetectorThread = Executors.newFixedThreadPool(1);
		
		// Add ChopSticks
		for (int i = 1; i <= NUMBER_OF_PHILOSOPHERS; i++) {
			chopSticks.add(new ChopStick(i));
		}
		
		// Add Philosophers and assign the corresponding ChopSticks to them
		for (int i = 1; i <= NUMBER_OF_PHILOSOPHERS; i++) {
			Philosopher p;
			if (i == NUMBER_OF_PHILOSOPHERS) {
				p = new Philosopher(i-1, chopSticks.get(0), chopSticks.get(i - 1), randomSeed);
			} else {
				p = new Philosopher(i-1, chopSticks.get(i), chopSticks.get(i - 1), randomSeed);
			}
			
			philosophers.add(p);
		}
		deadlockDetector = new DeadlockDetector(philosophers);
		deadlockDetectorThread.execute(deadlockDetector);
	}
	
	public ArrayList<Philosopher> getPhilosophers() {
		return philosophers;
	}
	
	/*
	 * The following code prints a table where each row corresponds to one of the Philosophers, 
	 * Columns correspond to the Philosopher ID (PID), average think time (ATT), average eating time (AET), average hungry time (AHT), number of thinking turns(#TT), number of eating turns(#ET), and number of hungry turns(#HT).
	 * This table should be printed regardless of the DEBUG value
	 */
	public void printTable() {
		DecimalFormat df2 = new DecimalFormat(".##");
		System.out.println("\n---------------------------------------------------");
		System.out.println("PID \tATT \tAET \tAHT \t#TT \t#ET \t#HT");
		
		for (Philosopher p : philosophers) {
			System.out.println(p.getId() + "\t"
					+ df2.format(p.getAverageThinkingTime()) + "\t"
					+ df2.format(p.getAverageEatingTime()) + "\t"
					+ df2.format(p.getAverageHungryTime()) + "\t"
					+ p.getNumberOfThinkingTurns() + "\t"
					+ p.getNumberOfEatingTurns() + "\t"
					+ p.getNumberOfHungryTurns() + "\t");
		}
		
		System.out.println("---------------------------------------------------\n");
	}

}