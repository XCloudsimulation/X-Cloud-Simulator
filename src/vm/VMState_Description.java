package vm;

public enum VMState_Description {
	VM_NAME(0), INACTIVE(1), INITIATING(2), ACTIVE(3), PROCESS(4), TRANSFERING_USER(5), MIGRATING(6), TERMINATING(7);
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