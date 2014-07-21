package vm;

import java.util.ArrayList;

import measurment.PacketMeasIndex;
import measurment.LatencyMeasurement;
import measurment.VMMeasIndex;
import network.*;
import data_centre.DataCentre;
import eduni.simjava.Sim_entity;
import eduni.simjava.Sim_event;
import eduni.simjava.Sim_from_p;
import eduni.simjava.Sim_port;
import eduni.simjava.Sim_system;


public class VM extends Sim_entity {
	public final static String IN_PORT_NAME = "IN_PORT_NAME";
	
	private final static long STARTUP_TIME = 10;
	private final static long SHUTDOWN_TIME = 10;
	
	private ArrayList<VMMeasIndex> vm_meas;
	
	private VMState state;
	private VMState_Description currentState;
	
	private long baseServieTime;
	
	private double inStateSince;
	
	private DataCentre hostDC;
	
	public VM(String name, DataCentre hostDC) {
		super(name);
		
		this.hostDC = hostDC;
		
		add_port(new Sim_port(IN_PORT_NAME));
		
		state = new VMState_Inactive();
		
		vm_meas = new ArrayList<VMMeasIndex>();
	}
	
	@Override
	public void body(){
		currentState = VMState_Description.INACTIVE;
		inStateSince = Sim_system.sim_clock();
		
		while(Sim_system.running()){
			state.Execute();
		}
		
		// Close state watch
		double t_now = Sim_system.sim_clock();
		vm_meas.add(new VMMeasIndex(currentState, t_now-inStateSince));
	}

	public synchronized void UpdateBaseServiceTime(long baseServiceTime) {
		this.baseServieTime = baseServiceTime;
	}

	private abstract class VMState{
		protected VMState_Description state_desc;
		protected Sim_event e;
		
		public void Execute(){
			e = new Sim_event();
			sim_get_next(e);
		}
		
		public void TransitionToState(VMState new_state){
			state = new_state;
			hostDC.UpdateState(get_name(), state.state_desc);
			
			double t_now = Sim_system.sim_clock();
			vm_meas.add(new VMMeasIndex(currentState, t_now-inStateSince));
			
			currentState = state.state_desc;
			inStateSince = t_now;
		}
	}
	
	private class VMState_Active extends VMState{

		public VMState_Active() {
			state_desc = VMState_Description.ACTIVE;
		}

		@Override
		public void Execute() {
			super.Execute();
			
			Object packet = e.get_data();
			
			if(packet instanceof Packet_Migrate){
				//System.out.println(get_name() + " - Migrating to DC: " + ((Packet_Migrate) packet).getDest());
				TransitionToState(new VMState_Migrating(((Packet_Migrate) packet).getDest(), e.scheduled_by()));
			}
			else if (packet instanceof Packet_Data){
				System.out.println("\t\t\t " + get_name() + " - Processing request " + ((Packet_Data) packet).packet + " of session " + ((Packet_Data) packet).session + " from user " + ((Packet_Data) packet).user);
				((Packet) packet).AddLatencyMeasurement(new LatencyMeasurement(PacketMeasIndex.PROCESS, 2));
				hostDC.StorePacket((Packet) packet);
				sim_process(2);
			} else {
				System.err.println("Invalid packet type");
			}
		}
	}
	
	private class VMState_Migrating extends VMState{
		private String dest;
		private int user;
		
		public VMState_Migrating(String dest, int user) {
			state_desc = VMState_Description.MIGRATING;
			this.dest = dest;
			this.user = user;
		}

		@Override
		public void Execute() {
			//super.Execute();
			
			e = new Sim_event();
			sim_get_next(new Sim_from_p(user),e);
			
			if(e!=null && e.get_tag()>=0){
				((Packet)e.get_data()).Migrated();
				hostDC.Migrate(e, dest);
			} else{
				TransitionToState(new VMState_Terminating());
			}
			
		}
	}
	
	private class VMState_Inactive extends VMState{
		public VMState_Inactive() {
			state_desc = VMState_Description.INACTIVE;
		}

		@Override
		public void Execute() {
			super.Execute();
			
			if(e.get_tag()<0){
				return;
			}
			
			TransitionToState(new VMState_Initiating());
			
			sim_putback(e);
		}
		
	}
	
	private class VMState_Initiating extends VMState{
		public VMState_Initiating() {
			state_desc = VMState_Description.INITIATING;
		}

		@Override
		public void Execute() {
			sim_pause(0);
			
			TransitionToState(new VMState_Active());
		}
	}
	
	private class VMState_Terminating extends VMState{
		public VMState_Terminating() {
			state_desc = VMState_Description.TERMINATING;
		}

		@Override
		public void Execute() {
			sim_pause(0);
			
			TransitionToState(new VMState_Inactive());
		}
	}
	
	private class VMState_TransferingUser extends VMState{
		public VMState_TransferingUser() {
			state_desc = VMState_Description.TERMINATING;
		}

		@Override
		public void Execute() {
			sim_pause(0);

			TransitionToState( new VMState_Active());
		}
	}

	public String DumpWorkloadMeasurements() {
		StringBuilder sb = new StringBuilder();
		
		for(VMMeasIndex vmm: vm_meas){
			sb.append(get_name() + ";" + vmm.state + ";" + vmm.value + "\r");
		}
		
		return sb.toString();
	}
}