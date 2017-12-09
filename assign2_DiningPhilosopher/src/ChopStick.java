
public class ChopStick {
	private int id;
	
	private volatile boolean isFree;

	public ChopStick(int id) {
		this.id = id;
		isFree = true;
	}

	/* 
	 * Implementing the pickup and put down ChopStick logic
	 * The same ChopStick can not be picked up by more than one philosopher at a time.
	 */
	
	public void pickUp() {
		isFree = false;
	}

	public void putDown() {
		isFree = true;
	}

	public int getId() {
		return id;
	}

	public boolean isFree() {
		return isFree;
	}
}
	

