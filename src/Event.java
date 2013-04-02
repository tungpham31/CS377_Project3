/* these will be used in the Event Heap and all queues */
class Event implements Comparable<Event>{

	public enum Type { // cooresponds to "Events to be Processed" in assignment
		ARRIVAL, CPU_DONE, IO_DONE
	}

	public Type type; // constant type of event, might want to make this final

	public double time; // when this event will occur
	
	public Process associatedProcess; 

	public Event(){
	}
	
	public Event(Type type, double time, Process associatedProcess){
		this.type = type;
		this.time = time;
		this.associatedProcess = associatedProcess;
	}
	
	public int compareTo(Event other){
		// event with smaller arrival time has higher priority
		if (this.time < other.time) return -1;
		if (this.time == other.time) return 0;
		return 1;
	}
}
