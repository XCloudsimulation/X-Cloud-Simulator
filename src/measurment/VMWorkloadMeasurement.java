package measurment;

import vm.VMState_Description;

public class VMWorkloadMeasurement {
	public VMState_Description state;
	public double value;
	
	public VMWorkloadMeasurement(VMState_Description state, double value){
		this.state = state;
		this.value = value;
	}
}
