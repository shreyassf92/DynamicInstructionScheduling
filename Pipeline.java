import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.Stack;

public class Pipeline {
	
	public enum Stage{
		
		IF,
		ID,
		IS,
		EX,
		WB
	}

	Stack<String> InstructionStack;
	int S;
	int N;
	
	int Tag;
	
	public int Cycle;
	
	RegisterFile registerFile;
	DispatchQueue dispatchQueue;
	SchedulingQueue schedulingQueue;
	ExecuteQueue executeQueue;
	
    static Hashtable<Integer, InstructionInformation> ROB;
	
	//Constructor
	public Pipeline(int S, int N, Stack<String> InstructionStack) {
		
		this.S = S;
		this.N = N;		
		
		this.InstructionStack = InstructionStack;
		
		this.Tag = -1;
		
		Cycle = 0;
		
		this.registerFile = new RegisterFile();
		this.dispatchQueue = new DispatchQueue(N);
		this.schedulingQueue = new SchedulingQueue(S);
		this.executeQueue = new ExecuteQueue();
		
		ROB = new Hashtable<Integer, InstructionInformation>();
	}
	
	public void Process() {
		
		
		do {
			
			//5. Retire
			FakeRetire();
			
			//4. Execute
			Execute();
			
			//3. Issue
			Issue();
			
			//2. Dispatch
			Dispatch();
			
			//1. Fetch
			Fetch();
			
			//debug info
			_debug();
			
		}while(AdvanceCycle());
	}
	
	private void FakeRetire() {
		
		if (!executeQueue.isEmpty()) {
		
			//remove executed instructions
			List<Integer> readyToWB = executeQueue.getReadyInstructions();
			
			for (Integer sequenceNumber : readyToWB) {
				
				InstructionInformation insInfo = ROB.get(sequenceNumber);
				
				//update state to WB 
				insInfo.updateInstructionState(Pipeline.Stage.WB, Cycle);
				
				// Update the register file state (e.g., ready flag) and wake up dependent instructions (set their operand ready flags).
				//set the register to Ready in register file
				if (insInfo.Operand.Destination != -1 && this.registerFile.REG.get(insInfo.Operand.Destination).State.equals(Register.Status.NotReady) && 
														this.registerFile.REG.get(insInfo.Operand.Destination).Tag == sequenceNumber) {
					
					registerFile.updateRegister(insInfo.Operand.Destination, sequenceNumber, Register.Status.Ready);
				}
				
				//set the source operands of instructions that were dependent on this instruction to Ready
				List<Integer> instrucsinSchedulerAndDispatcher = dispatchQueue.getInstrucInIDState();
				instrucsinSchedulerAndDispatcher.addAll(schedulingQueue.getInstructionsInQueue());
				
				for (Integer tagNumber : instrucsinSchedulerAndDispatcher) {
					
					InstructionInformation schedulerInst = ROB.get(tagNumber);
					
					if (schedulerInst.Operand.Source1 != -1 && schedulerInst.Operand.Source1Register.State.equals(Register.Status.NotReady) && schedulerInst.Operand.Source1Register.Tag == sequenceNumber) {
						schedulerInst.Operand.Source1Register.State = Register.Status.Ready;
						schedulerInst.Operand.Source1Register.Tag = sequenceNumber;
					}
					
					if (schedulerInst.Operand.Source2 != -1 && schedulerInst.Operand.Source2Register.State.equals(Register.Status.NotReady) && schedulerInst.Operand.Source2Register.Tag == sequenceNumber) {
						schedulerInst.Operand.Source2Register.State = Register.Status.Ready;
						schedulerInst.Operand.Source2Register.Tag = sequenceNumber;
					}
				}
			}
		}
	}
	
	private void Execute() {
		
		//logic
		//update execution latency
		//remove instruction, from execute list, which has completed its execution latency, and update state to WB
		
		//implementation
		//decrement execution latency by 1 for all instructions
		
		if(!schedulingQueue.isEmpty()) {
			
			int noOfInstToExecute = (S > N) ? N+1 : S;
			List<Integer> readyToExecute = new ArrayList<Integer>();
			
			//this function removes the returned instructions from the issue queue
			readyToExecute = schedulingQueue.getReadyInstructions(noOfInstToExecute);
			int executionLatency = 0;
			
			for (int sequenceNumber : readyToExecute) {
					
				switch (ROB.get(sequenceNumber).OperationType) {
					case 0: {
						
						executionLatency = 1;
						break;
					}
					case 1: {
						
						executionLatency = 2;
						break;
					}
					case 2: {
						
						executionLatency = 5;
						break;
					}
				} 
				
				//add instruction's sequence number to execute list with its execution latency
				executeQueue.addToQueue(sequenceNumber, executionLatency);
				
				//update state to Execute 
				ROB.get(sequenceNumber).updateInstructionState(Pipeline.Stage.EX, Cycle);
			}
		}
		
		if (!executeQueue.isEmpty()) {
			
			executeQueue.updateExecutionLatency();
		}
	}
	
	private void Issue() {
		
		//logic
		//add up to N+1 ready instructions to execute Queue with instructions execution latency and update state to EX
		
		//implementation
		int freeSlots = 0;
		int noOfInstToIssue = 0;
		List<Integer> readyToIssue = new ArrayList<Integer>();
		
		if(!dispatchQueue.isEmpty()) {
			
			freeSlots = schedulingQueue.getFreeSlots();
			
			if(freeSlots != 0) {
				
				noOfInstToIssue	= freeSlots >= N ? N : freeSlots;  
	
				//this function removes the returned instructions from the dispatch queue
				readyToIssue = dispatchQueue.getReadyInstructions(noOfInstToIssue);
				
				for (int sequenceNumber : readyToIssue) {
					
					//add instruction's sequence number to issue list 
					schedulingQueue.addToQueue(sequenceNumber);
					
					//update state to Issue 
					ROB.get(sequenceNumber).updateInstructionState(Pipeline.Stage.IS, Cycle);
				}
			}
		}
	}
	
	private void Dispatch() {
		
		//logic
		//create ready-to-be-issued list from dispatch queue (include only instruction that are in ID state)
		//sort ready-to-be-dispatched list in ascending order 
		//check if scheduling queue is free
			//if free, add instruction to scheduling queue and update state to IS; Also remove it from dispatch queue
		//Rename registers, and update state of all instructions in dispatch queue to ID
			//renaming logic
			//destination operand's register is renamed to the sequence number of the instruction

		//implementation
		List<Integer> uncondTransList = new ArrayList<Integer>();
		
		//rename and update state to Dispatch 
		uncondTransList = dispatchQueue.getInstrForUnconditionalTrans();
		
		for (int sequenceNumber : uncondTransList) {
			
			InstructionInformation inst = ROB.get(sequenceNumber);
			
			//rename source1
			if (inst.Operand.Source1 == -1) {
				inst.Operand.Source1Register.updateRegister(Register.Status.Ready, -1);
			}
			else {
				
				Register S1 = registerFile.REG.get(inst.Operand.Source1);
				
				if (S1.State.equals(Register.Status.Ready)) {
					
					inst.Operand.updateOperandState(Operand.OperandType.Source1, Register.Status.Ready, S1.Tag);
				}
				else if (S1.State.equals(Register.Status.NotReady)) {
					
					inst.Operand.updateOperandState(Operand.OperandType.Source1, Register.Status.NotReady, S1.Tag);
				}
			}
			
			//rename source2
			if (inst.Operand.Source2 == -1) {
				inst.Operand.Source2Register.updateRegister(Register.Status.Ready, -1);
			}
			else {
				
				Register S2 = registerFile.REG.get(inst.Operand.Source2);
				
				if (S2.State.equals(Register.Status.Ready)) {
					
					inst.Operand.updateOperandState(Operand.OperandType.Source2, Register.Status.Ready, S2.Tag);			
				}
				else if (S2.State.equals(Register.Status.NotReady)) {
					
					inst.Operand.updateOperandState(Operand.OperandType.Source2, Register.Status.NotReady, S2.Tag);		
				}
			}
			
			//rename destination
			if (inst.Operand.Destination == -1) {
				
				inst.Operand.DestinationRegister.updateRegister(Register.Status.Ready, -1);
			}
			else {
						
				registerFile.updateRegister(inst.Operand.Destination, sequenceNumber, Register.Status.NotReady);
			}
			
			//update state to Dispatch 
			inst.updateInstructionState(Pipeline.Stage.ID, Cycle);
		}	
	}
	
	private void Fetch() {
		
		//logic 
		//check if not end of file 
			//if Dispatch Queue is free
				//if free check how many instructions can be fetched
					//pop instructions from stack and add it to ROB
					//also add it to Dispatch Queue and update the state to IF
		
		//implementation
		int freeSlots = 0;
		int noOfInstToFetch = 0;
		String instructionLine = "";
		int sequenceNumber = 0;
		
		if(!this.InstructionStack.empty()) {
			
			freeSlots = dispatchQueue.getFreeSlots();
			
			if(freeSlots != 0) {
				
				noOfInstToFetch	= freeSlots >= N ? N : freeSlots;  
				
				noOfInstToFetch = this.InstructionStack.size() < noOfInstToFetch ? this.InstructionStack.size() : noOfInstToFetch;
				
				
				for (int i = 1; i <= noOfInstToFetch; i++) {
					
					instructionLine = this.InstructionStack.pop();
					
					sequenceNumber = addToROB(instructionLine);
					
					//add instruction's sequence number to dispatch list 
					dispatchQueue.addToQueue(sequenceNumber);
					
					//update state to Fetch 
					ROB.get(sequenceNumber).updateInstructionState(Pipeline.Stage.IF, Cycle);
					
				}			
			}
		}
	}
	
	private boolean AdvanceCycle() {
		
		boolean continueCycle = false;
		
		//check if another cycle is needed
		//ROB.size() != 0 || 
		continueCycle = (InstructionStack.size() != 0 || !dispatchQueue.isEmpty() || !schedulingQueue.isEmpty()|| !executeQueue.isEmpty()) ? true : false;
		
		if (continueCycle) {
			
			Cycle++;
		}
		
		return continueCycle;
	}
	
	private int addToROB(String InstructionLine) {
		
		Tag++;
		
		InstructionInformation instInfo = new InstructionInformation(InstructionLine, Tag);
		
		ROB.put(Tag, instInfo);
		
		return Tag;
	}
	
	private void removeFromROB(String InstructionLine) {

	}
	
	public void printOutput(){
		
		for (int i = 0; i < ROB.size(); i++) {
			
			InstructionInformation instInfo = ROB.get(i);

			System.out.print(i + "\t" + "fu{" + instInfo.OperationType+"}\t" + "src{" + instInfo.Operand.Source1 + "," + instInfo.Operand.Source2 + "}\t" + "dst{" + instInfo.Operand.Destination + "}\t");
			System.out.print("IF{" + instInfo.Timing.get(Pipeline.Stage.IF).PipelineCycle + "," + instInfo.Timing.get(Pipeline.Stage.IF).CycleCount + "}\t");
			System.out.print("ID{" + instInfo.Timing.get(Pipeline.Stage.ID).PipelineCycle + "," + instInfo.Timing.get(Pipeline.Stage.ID).CycleCount + "}\t");
			System.out.print("IS{" + instInfo.Timing.get(Pipeline.Stage.IS).PipelineCycle + "," + instInfo.Timing.get(Pipeline.Stage.IS).CycleCount + "}\t");
			System.out.print("EX{" + instInfo.Timing.get(Pipeline.Stage.EX).PipelineCycle + "," + instInfo.Timing.get(Pipeline.Stage.EX).CycleCount + "}\t");
			System.out.print("WB{" + instInfo.Timing.get(Pipeline.Stage.WB).PipelineCycle + "," + instInfo.Timing.get(Pipeline.Stage.WB).CycleCount + "}\t");
			System.out.println("");
		}
		
		Cycle++; //since we are starting cycle from 0
		
		System.out.println("number of instructions"+"\t=\t"+ ROB.size());
		System.out.println("number of cycles"+"\t=\t"+ Cycle);
		
		DecimalFormat decimalFormat = new DecimalFormat("#.#####");
		double ipc = ((double)ROB.size()/(double)Cycle);
		
		System.out.print("IPC"+"\t=\t"+ decimalFormat.format(ipc));
	}

	public void _debug() {
		
		System.out.println("cycle : "+Cycle);
		
		for (Integer key : dispatchQueue.DQ_LIST) {
			
			System.out.print(key + "\t");
		}

		System.out.println("");
		
		for (Integer key : schedulingQueue.SQ_LIST) {
			
			System.out.print(key + "\t");
		}
		
		System.out.println("");
		
		for (Integer tag : executeQueue.EX_LIST.keySet()) {
				
			System.out.print(tag + "\t");
		}
		
		System.out.println("");
		
	}
}
