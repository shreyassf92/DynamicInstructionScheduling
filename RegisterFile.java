import java.util.Hashtable;
import java.util.List;

public class RegisterFile {

	 Hashtable<Integer, Register> REG;
	 
	 public RegisterFile() {
		 
		 REG = new Hashtable<Integer, Register>();
		 initialize();
	 }
	
	public void updateRegister(int RegisterNumber, int Tag, Register.Status Status) {
				
		//rename destination
		REG.get(RegisterNumber).updateRegister(Status, Tag);
			
	}

	public void initialize() {
		
		for (int i = 0; i < 128; i++) {
			
			REG.put(i, new Register(Register.Status.Ready, i));
		}
	}
}
