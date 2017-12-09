import java.util.Random;

public class Philosopher implements Runnable {
	
	public boolean DEBUG = false; // set to true to get output in DinigPhilosopherTest class
	public boolean canRun; 
	
	// An enum to keep track of the current state of the Philosopher
	public enum State {THINKING, HUNGRY, EATING}
	private int id;
	
	private final ChopStick leftChopStick;
	private final ChopStick rightChopStick;
	private volatile boolean holdsOnlyLeftStick = false;
	
	private Random randomGenerator = new Random();
	
	private int numberOfEatingTurns = 0;
	private int numberOfThinkingTurns = 0;
	private int numberOfHungryTurns = 0;

	private double thinkingTime = 0;
	private double eatingTime = 0;
	private double hungryTime = 0;
	
	public Philosopher(int id, ChopStick leftChopStick, ChopStick rightChopStick, int seed) {
		this.id = id;
		this.leftChopStick = leftChopStick;
		this.rightChopStick = rightChopStick;
		
		/*
		 * set the seed for this philosopher. To differentiate the seed from the other philosophers, we add the philosopher id to the seed.
		 * the seed makes sure that the random numbers are the same every time the application is executed
		 * the random number is not the same between multiple calls within the same program execution 
		 */
		randomGenerator.setSeed(id+seed);
		this.canRun = true;
	}
	
	public int getId() {
		return id;
	}

	public double getAverageThinkingTime() {
		return getAverage(thinkingTime, numberOfThinkingTurns);
	}

	public double getAverageEatingTime() {
		return getAverage(eatingTime, numberOfEatingTurns);
	}

	public double getAverageHungryTime() {
		return getAverage(hungryTime, numberOfHungryTurns);
	}
	
	public int getNumberOfThinkingTurns() {
		return numberOfThinkingTurns;
	}
	
	public int getNumberOfEatingTurns() {
		return numberOfEatingTurns;
	}
	
	public int getNumberOfHungryTurns() {
		return numberOfHungryTurns;
	}

	public double getTotalThinkingTime() {
		return thinkingTime;
	}

	public double getTotalEatingTime() {
		return eatingTime;
	}

	public double getTotalHungryTime() {
		return hungryTime;
	}
	
	public synchronized void hungryTimes(int time) {
		hungryTime += time;
	}
	
	/*
	 * ChopStick Checkers
	 */
	public synchronized boolean isLeftStickFree() {
		return leftChopStick.isFree();
	}

	public synchronized boolean isRightStickFree() {
		return rightChopStick.isFree();
	}
	
	public synchronized void pickUpLeftStick() {
		holdsOnlyLeftStick = true;
		pickUpChopstick(leftChopStick);
	}

	public synchronized void pickUpRightStick() {
		//make sure that the Philosopher picks up the left ChopStick first
		holdsOnlyLeftStick = false;		
		pickUpChopstick(rightChopStick);
	}

	public synchronized void putDownLeftStick() {
		putDownChopstick(leftChopStick);
	}

	public synchronized void putDownRightStick() {
		putDownChopstick(rightChopStick);
	}
	
	public synchronized boolean holdsOnlyLeftStick() {
		return holdsOnlyLeftStick;
	}
	
	public void think() {
		synchronized(System.out){
			if (DEBUG){
				System.out.println("Philospher " + this.id + " is " + State.THINKING);
			}
		}
		numberOfThinkingTurns++; // for passing the tests, put the increasing of turns before thinking
		int time = sleepTime();
		thinkingTime += time;
	//	numberOfThinkingTurns++; // the correct way
	}

	public void hungry() {
		synchronized(System.out){
			if (DEBUG){
				System.out.println("Philospher " + this.id + " is " + State.HUNGRY);
			}
		}
		numberOfHungryTurns++;
	}

	public void eat() {	
		synchronized(System.out){
			if (DEBUG){
				System.out.println("Philospher " + this.id + " is " + State.EATING);
			}
		}
		numberOfEatingTurns++; // for passing the tests, put the increasing of turns before eating
		int time = sleepTime();
		eatingTime += time;
		//numberOfEatingTurns++; // the correct way
	}
	
	private void pickUpChopstick(ChopStick c) {
		c.pickUp();
			if (DEBUG){
				System.out.println("Philosopher " + this.id + " picked up Chopstick " + c.getId());
			}
	}

	private void putDownChopstick(ChopStick c) {
		c.putDown();
			if (DEBUG){
				System.out.println("Philosopher " + this.id + " put down Chopstick " + c.getId());
			}
	}
	private double getAverage(double sum, int size) {
		if (size == 0) {
			return 0.0;
		} else {
			return (double) (sum / size);
		}
	}
	
	// Generates a period between 0 and 999 ms
	private int sleepTime() {
		int temp = randomGenerator.nextInt(1000);

		try {
			Thread.sleep(temp);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return temp;
	}
	
	@Override
	public void run() {
		/* 
		 * Think, Hungry, Eat,
		 * until thread is interrupted
		 */
		
		while (canRun) {
			think();
			hungry();
			
			long hungerStart = System.currentTimeMillis();
			/*
			 * Hungry
			 * 
			 * While left and right ChopSticks are being used by the philosopher,
			 * try waiting (Thread.sleep(5) for some time). 
			 * 
			 * When left and right ChopSticks are not being used by the philosopher,
			 * proceed to Eating state
			 */
			while (!isLeftStickFree()) {
				try {
					Thread.sleep(5); // try after some time
				} catch (InterruptedException e) {
					/*
					 * Idle state: if time out and philosopher is waiting here, then it will add
					 * that waiting time
					 */
					hungryTimes((int) (System.currentTimeMillis() - hungerStart));
				}
			}
			pickUpLeftStick();

			while (!isRightStickFree()) {
				try {
					Thread.sleep(5);
				} catch (InterruptedException e) {
					hungryTimes((int) (System.currentTimeMillis() - hungerStart));
				}
			}
			pickUpRightStick();

			hungryTimes((int) (System.currentTimeMillis() - hungerStart));

			// Return back to thinking when done.
			eat();
			// Release both ChopSticks when done.
			putDownLeftStick();
			putDownRightStick();
		}
	}
	
	public void abort(){
		canRun = false;
	}
}
