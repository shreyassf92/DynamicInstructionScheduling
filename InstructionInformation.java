import java.util.Hashtable;
import java.util.TreeMap;

public class InstructionInformation {

	String InstructionLine;
	int SequenceNumber;
	
	String PC;
	int OperationType;
	String OperandState;
	
	Operand Operand;
	
	Pipeline.Stage InstructionState;
	
	public TreeMap<Pipeline.Stage, InstructionTiming> Timing;
	
	
	public InstructionInformation(String InstructionLine, int Tag) {
		
		this.InstructionLine = InstructionLine;
		this.SequenceNumber = Tag;

		this.Timing = new TreeMap<Pipeline.Stage, InstructionTiming>();
		
		processLine();
	}
	
	
	public void processLine() {
		
		String[] Instruction = this.InstructionLine.split(" ");
		
		PC = Instruction[0];
		OperationType = Integer.parseInt(Instruction[1]);
		
		this.Operand = new Operand(Integer.parseInt(Instruction[2]),  Integer.parseInt(Instruction[3]), Integer.parseInt(Instruction[4]));
	}
	
	public void updateInstructionState(Pipeline.Stage Stage, int Cycle) {
		
		InstructionState = Stage;
		
		InstructionTiming instTiming = new InstructionTiming();
		instTiming.PipelineCycle = Cycle;
		
		//add new
		Timing.put(InstructionState, instTiming);
				
		//get the last state and update cycle count
		switch (InstructionState) {
			case IF: {	
				break;
			}
			case ID: {	
				Timing.get(Pipeline.Stage.IF).CycleCount = Cycle - Timing.get(Pipeline.Stage.IF).PipelineCycle;
				break;
			}
			case IS: {	
				Timing.get(Pipeline.Stage.ID).CycleCount = Cycle - Timing.get(Pipeline.Stage.ID).PipelineCycle;
				break;
			}
			case EX: {	
				Timing.get(Pipeline.Stage.IS).CycleCount = Cycle - Timing.get(Pipeline.Stage.IS).PipelineCycle;
				break;
			}
			case WB: {	
				Timing.get(Pipeline.Stage.EX).CycleCount = Cycle - Timing.get(Pipeline.Stage.EX).PipelineCycle;
				Timing.get(Pipeline.Stage.WB).CycleCount = 1;
				break;
			}
		
		}
		

		
		
		//if new state (should be new state, as this method is called only when there is change in state)
		//if (Timing.isEmpty() || (Timing.size() != 0 && Timing.lastKey() != InstructionState) ) {
			
		
	}
	
	public void updateOperandState() {
	
		//OperandState = 
		
	}
	
	
	

}
