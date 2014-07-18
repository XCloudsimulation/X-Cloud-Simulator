package vm;

public enum VMState_Description {
	INACTIVE(0), INITIATING(1), ACTIVE(2), PROCESS(3), TRANSFERING_USER(4), MIGRATING(5), TERMINATING(6);
    private final int value;

    private VMState_Description(int value) {
        this.value = value;
    }
    
    public static int NbrStates(){
    	return VMState_Description.values().length;
    }
    
    public int toInt() {
        return value;
    }
}