public class Device {
	public Process currentProcess = null;
	
	public Device(){
		
	}
	
	public boolean isIdle() {
		return currentProcess == null;
	}
}
