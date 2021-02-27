import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class ExecuteQueue {

	int Size;
    Hashtable<Integer, Integer> EX_LIST;
	int InstructionCounter;
	
	public ExecuteQueue() {
		
		this.Size = 0;
		this.InstructionCounter = 0;
		this.EX_LIST = new Hashtable<Integer, Integer>();
	}

	public void addToQueue(int InstructionTag, int ExecutionLatency) {
		
		InstructionCounter++;
		EX_LIST.put(InstructionTag, ExecutionLatency);
	}
	
	public void removeFromQueue(int InstructionTag) {
		
		InstructionCounter--;
		EX_LIST.remove((Object)InstructionTag);
	}
	
	public boolean isEmpty() {
		
		return EX_LIST.size() > 0 ? false : true ;
		
	}
	
	public void updateExecutionLatency() {
		
		for (Integer tag : EX_LIST.keySet()) {
			
			int value = EX_LIST.get(tag);
			
			value--;
			
			EX_LIST.put(tag, value);
		}
		
	}
	
	public List<Integer> getReadyInstructions() {
		
		List<Integer> readyList = new ArrayList<Integer>();
		
		for (Integer tag : EX_LIST.keySet()) {
			
			int value = EX_LIST.get(tag);
			
			if (value == 0) {
				
				readyList.add(tag);
			}
		}
		
		//remove
		for (Integer tag : readyList) {
			
			removeFromQueue(tag);
		}
		
		return readyList;
	}
	
}
