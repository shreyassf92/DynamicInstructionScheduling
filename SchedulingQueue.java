import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SchedulingQueue {

	
	int Size;
	List<Integer> SQ_LIST;
	int InstructionCounter;
	
	public SchedulingQueue(int S) {
		
		this.Size = S;
		this.InstructionCounter = 0;
		SQ_LIST = new ArrayList<Integer>();
	}
	
	public void addToQueue(int InstructionTag) {
		
		InstructionCounter++;
		SQ_LIST.add(InstructionTag);
	}
	
	public void removeFromQueue(int InstructionTag) {
		
		InstructionCounter--;
		SQ_LIST.remove((Object)InstructionTag);
	}
	
	public int getFreeSlots() {
		
		return this.Size - this.InstructionCounter;
	}
	
	public boolean isEmpty() {
		
		return SQ_LIST.size() > 0 ? false : true ;
		
	}

	public List<Integer> getReadyInstructions(int noOfInstToExecute) {
		
		List<Integer> readyList = new ArrayList<Integer>();
		
		for (int i = 0; i < SQ_LIST.size(); i++) {
			
			InstructionInformation instInfo = Pipeline.ROB.get(SQ_LIST.get(i));
			
			if (instInfo.Operand.Source1Register.State.equals(Register.Status.Ready) && instInfo.Operand.Source2Register.State.equals(Register.Status.Ready) && noOfInstToExecute != 0) {
				
				readyList.add(SQ_LIST.get(i));
				noOfInstToExecute--;
			}
		}
		
		//remove
		for (int integer : readyList) {
			
			removeFromQueue(integer);
		}
		
		
		Collections.sort(readyList);
		
		return readyList;
	}
	
	public List<Integer> getInstructionsInQueue( ) {
		
		return SQ_LIST;
	}
}
