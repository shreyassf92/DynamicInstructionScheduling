import java.util.List;

public class Operand {

	public enum OperandType{
		 Destination,
		 Source1,
		 Source2
	}
	
	int Destination;
	int Source1;
	int Source2;
	
	Register DestinationRegister;
	Register Source1Register;
	Register Source2Register;
	
	public Operand(int Destination, int Source1, int Source2) {
		
		this.Destination = Destination;
		this.Source1 = Source1;
		this.Source2 = Source2;
		
		this.DestinationRegister = new Register(Register.Status.Ready, 0);
		this.Source1Register = new Register(Register.Status.Ready, 0);
		this.Source2Register = new Register(Register.Status.Ready, 0);
		
	}

	public void updateOperandState(OperandType Type, Register.Status Status, int Tag) {
		
		switch (Type) {
		
			case Destination : {
				this.DestinationRegister.updateRegister(Status, Tag);
				break;
			}
			
			case Source1 : {
				this.Source1Register.updateRegister(Status, Tag);
				break;
			}	
			
			case Source2 : {
				this.Source2Register.updateRegister(Status, Tag);
				break;
			}
		}
	}
}
