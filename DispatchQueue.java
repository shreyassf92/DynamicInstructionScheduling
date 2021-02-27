import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DispatchQueue {

	int Size;
	List<Integer> DQ_LIST;
	int InstructionCounter;
	
	public DispatchQueue(int N) {
	
		this.Size = 2 * N;
		this.InstructionCounter = 0;
		DQ_LIST = new ArrayList<Integer>();
	}
	
	public void addToQueue(int InstructionTag) {
		
		InstructionCounter++;
		DQ_LIST.add(InstructionTag);
	}
	
	public void removeFromQueue(int InstructionTag) {
		
		InstructionCounter--;
		DQ_LIST.remove((Object)InstructionTag);
	}
	
	public int getFreeSlots() {
		
		return this.Size - this.InstructionCounter;
	}

	public List<Integer> getReadyInstructions(int Count){
		
		List<Integer> readyList = new ArrayList<Integer>();
		
		for (int i = 0; i < Count; i++) {
			
			int seqNumber = DQ_LIST.get(i);
			
			if(Pipeline.ROB.get(seqNumber).InstructionState != Pipeline.Stage.IF) {
				
				readyList.add(seqNumber);	
			}		
		}
		
		//remove
		for (int integer : readyList) {
			
			removeFromQueue(integer);
		}
		
		Collections.sort(readyList);
				
		return readyList;
	}
	
	public List<Integer> getInstrForUnconditionalTrans(){
		
		List<Integer> uncondTransList = new ArrayList<Integer>();
		
		for (int sequenceNumber : DQ_LIST) {
			
			if(Pipeline.ROB.get(sequenceNumber).InstructionState.equals(Pipeline.Stage.IF)) {
				
				uncondTransList.add(sequenceNumber);
			}
		}
		
		Collections.sort(uncondTransList);
		
		return uncondTransList;
	}
	
	public boolean isEmpty() {
	
		return DQ_LIST.size() > 0 ? false : true ;
		
	}
	
	public List<Integer> getInstrucInIDState(){
		
		List<Integer> instIDState = new ArrayList<Integer>();
		
		for (int sequenceNumber : DQ_LIST) {
			
			if (Pipeline.ROB.get(sequenceNumber).InstructionState.equals(Pipeline.Stage.ID)) {
				
				instIDState.add(sequenceNumber);
			}
		}
		
		return instIDState;
	}
	
}
