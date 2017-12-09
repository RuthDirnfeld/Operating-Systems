import java.util.ArrayList;

public class DeadlockDetector extends Thread {
	// creating the 5 Philosophers
	private Philosopher p0;
	private Philosopher p1;
	private Philosopher p2;
	private Philosopher p3;
	private Philosopher p4;
	private volatile boolean isDeadlock = false;

	public DeadlockDetector(ArrayList<Philosopher> threads) {
		p0 = threads.get(0);
		p1 = threads.get(1);
		p2 = threads.get(2);
		p3 = threads.get(3);
		p4 = threads.get(4);
	}

	/*
	 * Run while Deadlock is not detected
	 * Check constantly for Deadlock
	 */
	@Override
	public void run() {
		while (!isDeadlock) {
			checkDeadlock();
		}
	}

	/*
	 * Determine whether all the philosophers are in a Deadlock. This will happen when all 
	 * the philosophers had grabbed their left ChopSticks but are waiting for their right 
	 * ones to become available.
	 */
	private void checkDeadlock() {
		if (p0.holdsOnlyLeftStick() && p1.holdsOnlyLeftStick() && p2.holdsOnlyLeftStick()
				&& p3.holdsOnlyLeftStick() && p4.holdsOnlyLeftStick()) {
			isDeadlock = true;
		}
	}

	public boolean isDeadlock() {
		return isDeadlock;
	}
}
