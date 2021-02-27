
public class Register {
	
	public enum Status{
		
		Ready,
		NotReady
	}

	Status State;
	int Tag;
	
	public Register(Status State, int Tag) {
		
		this.State = State;
		this.Tag = Tag;
	}

	public void updateRegister(Status State, int Tag) {
		
		this.State = State;
		this.Tag = Tag;
		
	}
}
