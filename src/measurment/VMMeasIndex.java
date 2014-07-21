package measurment;

import vm.VMState_Description;

public class VMMeasIndex {
	public VMState_Description state;
	public double value;
	
	public VMMeasIndex(VMState_Description state, double value){
		this.state = state;
		this.value = value;
	}
}
