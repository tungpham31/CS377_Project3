/* model for a process, you will need a collection of these */
class Process {

	public enum State { // added IO to indicate a process is on the I/O device
		NEW, READY, RUNNING, IO, WAITING, TERMINATED
	}

	public State state = State.NEW; // current state in the state machine from figure
								// 3.2

	/* Put data structures to hold size of CPU and I/O bursts here */
	public int numberOfCPUBursts;
	
	public double[] sizeOfCPUBursts;
	
	public double[] sizeOfIOBursts;

	public int currentBurst; // indicates which of the series of bursts is currently
						// being handled. state can be used to determine what
						// kind of burst

	public double remainingTime = 0; // used to calculate remaining time till
								// completion if burst is descheduled

	public Process(State state, int numberOfCPUBursts, double[] sizeOfCPUBursts, double[] sizeOfIOBursts, int currentBurst, double remainingTime){
		this.state = state;
		this.numberOfCPUBursts = numberOfCPUBursts;
		this.sizeOfCPUBursts = sizeOfCPUBursts;
		this.sizeOfIOBursts = sizeOfIOBursts;
		this.currentBurst = currentBurst;
		this.remainingTime = remainingTime;
	}
}
