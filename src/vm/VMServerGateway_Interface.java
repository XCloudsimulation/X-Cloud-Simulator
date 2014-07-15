package vm;

import eduni.simjava.Sim_event;

public interface VMServerGateway_Interface {
	public void UpdateState(String id, VMState_Description state);
	public void Migrate(Sim_event e, String dest);
}
