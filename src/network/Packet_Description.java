package network;

import vm.VMState_Description;

public enum Packet_Description {
	DATA(0), MIGRATE(1);
	
	private final int value;

    private Packet_Description(int value) {
        this.value = value;
    }
    
    public static int NbrStates(){
    	return Packet_Description.values().length;
    }
    
    public int toInt() {
        return value;
    }
}
