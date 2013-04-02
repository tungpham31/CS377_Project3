import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

public class SchedSim {
	private static String fileName; // file name of input file
	private static int maxProcesses; // cap on total processes for simulation
	private static int maxCPUBursts; // cap on total CPU bursts per process
	private static double currentTime = 0; // current time in simulation, starting at
	// zero
	public static double nextProcessTime;
	public static int numberOfCPUBursts;
	public static double[] sizeOfCPUBursts;
	public static double[] sizeOfIOBursts;

	private enum Algorithm { // algorithm to use for entire run of simulation
		FCFS, SJF, SRTF;
	}

	private static Algorithm algorithm;

	// a heap containing all the events waiting
	private static Queue<Event> eventHeap;

	// a priority queue for processes which are ready to run
	private static Queue<Process> readyQueue;

	// a first in first out queue to handle IO events
	private static Queue<Process> ioQueue;

	// input stream to read bytes from input file
	private static FileInputStream inputStream;

	// process table
	private static List<Process> processTable;
	
	//Devices;
	public static Device CPU, IO;

	public static void main(String[] args) {
		// parse arguments
		parseArguments(args);

		// you might want to open the binary input file here
		File file = new File(fileName);
		try {
			inputStream = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// initialize data structures
		initializeDataStructures();

		/* DES loop */
		// see psudeocode in the assignment
		// all of your input reading occurs when processing the Arrival event
		while (!eventHeap.isEmpty()) {
			Event currentEvent = eventHeap.poll(); // extract an event from the
													// heap
			currentTime = currentEvent.time; // update time to match
												// the event
			switch (currentEvent.type) {
			case ARRIVAL:
				// read data for this event and the next one
				readDataFromInput();
				Process process = new Process(Process.State.NEW,
						numberOfCPUBursts, sizeOfCPUBursts, sizeOfIOBursts, 0,
						sizeOfCPUBursts[0]); // create new process for this
												// event
				processTable.add(process); // add the process to table
				if (CPU.isIdle()) {
					process.state = Process.State.RUNNING;// set its state to
															// RUNNING
					CPU.currentProcess = process; // put process into CPU
					// create a new CPU Burst Completion Event
					// its time will be set to when the first CPU burst is done
					Event CBCEvent = new Event(Event.Type.CPU_DONE, currentTime
							+ sizeOfCPUBursts[0], process);
					eventHeap.add(CBCEvent); // add new event to the heap
				} else {
					// if the CPU is not idle
					process.state = Process.State.READY; // set its state to
															// READY
					readyQueue.add(process); // add the process to ready queue
				}

				// add next event to queue
				Event nextEvent = new Event(Event.Type.ARRIVAL,
						nextProcessTime, null);
				eventHeap.add(nextEvent);

				break;

			case CPU_DONE:
				// get the associated process of this event
				Process processInEvent = currentEvent.associatedProcess;
				// if this was the last of the CPU burst
				if (processInEvent.numberOfCPUBursts - 1 == processInEvent.currentBurst) {
					processInEvent.state = Process.State.TERMINATED; // set its
																		// state
																		// to
																		// TERMINATED;
				} else {
					// otherwise if the IO devide is idle
					if (IO.isIdle()) {
						processInEvent.state = Process.State.IO; // set its
																	// state to
																	// IO
						IO.currentProcess = processInEvent; // put this process
															// into IO
						// create I/O Completetion event
						Event IOCEvent = new Event(
								Event.Type.ARRIVAL,
								currentTime
										+ processInEvent.sizeOfIOBursts[processInEvent.currentBurst],
								processInEvent);
						eventHeap.add(IOCEvent); // add this event to eventHeap
					}
					else{
						// otherwise,
						processInEvent.state = Process.State.WAITING;
						ioQueue.add(processInEvent);
					}
				}
				
				CPU.currentProcess = null;
				
				if (!readyQueue.isEmpty()){
					process = readyQueue.poll();
					CPU.currentProcess = process;
					Event CBCEvent = new Event(Event.Type.CPU_DONE, currentTime + process.sizeOfCPUBursts[process.currentBurst + 1], process);
					eventHeap.add(CBCEvent);
				}
			}
		}

		// output statistics
	}

	/*
	 * parse the arguments given from main
	 */
	private static void parseArguments(String[] args) {
		fileName = args[0]; // get fileName
		maxProcesses = Integer.parseInt(args[1]); // get maxProcesses
		maxCPUBursts = Integer.parseInt(args[2]); // get maxCPUBursts
		// get the algorithm using in this scheduling simulator
		String algorithmName = args[3];
		if (algorithmName == "FCFS")
			algorithm = Algorithm.FCFS;
		if (algorithmName == "SJF")
			algorithm = Algorithm.SJF;
		if (algorithmName == "SRTF")
			algorithm = Algorithm.SRTF;
	}

	/*
	 * initialize global data structures
	 */
	private static void initializeDataStructures() {
		currentTime = 0;
		eventHeap = new PriorityQueue<Event>();
		// add an inital event for event heap which will arrive at time = value
		// of the first byte in the input
		eventHeap
				.add(new Event(Event.Type.ARRIVAL, readAByteFromInput(), null));
		readyQueue = new PriorityQueue<Process>();
		ioQueue = new LinkedList<Process>();
		processTable = new LinkedList<Process>();
		CPU = new Device();
		IO = new Device();
	}

	private static void readDataFromInput() {
		nextProcessTime = readAByteFromInput() / 10.0;
		numberOfCPUBursts = readAByteFromInput() % maxCPUBursts + 1;
		int n = numberOfCPUBursts;
		sizeOfCPUBursts = new double[n];
		sizeOfIOBursts = new double[n];
		for (int i = 0; i <= n - 1; i++)
			sizeOfCPUBursts[i] = readAByteFromInput() / 25.6;
		for (int i = 0; i <= n - 2; i++)
			sizeOfIOBursts[i] = readAByteFromInput() / 25.6;
	}

	private static int readAByteFromInput() {
		int val = -1;
		try {
			val = (int) inputStream.read() & 0xff;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return val;
	}
}
