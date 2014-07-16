package vm;

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
	
	private VMState state;
	
	private long baseServieTime;
	
	private DataCentre hostDC;
	
	public VM(String name, DataCentre hostDC) {
		super(name);
		
		this.hostDC = hostDC;
		
		add_port(new Sim_port(IN_PORT_NAME));
		
		System.out.println(get_name() + " - Starting");
		
		state = new VMState_Inactive();
	}
	
	@Override
	public void body(){
		while(Sim_system.running()){
			state.Execute();
		}
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
				state = new VMState_Migrating(((Packet_Migrate) packet).getDest(), e.scheduled_by());
				hostDC.UpdateState(getName(), VMState_Description.MIGRATING);
			}
			else if (packet instanceof Packet_Data){
				System.out.println(get_name() + " - Processing request " + ((Packet_Data) packet).number + " of session " + ((Packet_Data) packet).session + " from user " + ((Packet_Data) packet).user);
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
				hostDC.Migrate(e, dest);
			} else{
				hostDC.UpdateState(get_name(), VMState_Description.TERMINATING);
				state = new VMState_Terminating();
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
			
			state = new VMState_Initiating();
			
			hostDC.UpdateState(get_name(), VMState_Description.INITIATING);
			sim_putback(e);
		}
		
	}
	
	private class VMState_Initiating extends VMState{
		public VMState_Initiating() {
			state_desc = VMState_Description.INITIATING;
		}

		@Override
		public void Execute() {
			
			sim_pause(10);
			hostDC.UpdateState(get_name(), VMState_Description.ACTIVE);
			state = new VMState_Active();
		}
	}
	
	private class VMState_Terminating extends VMState{
		public VMState_Terminating() {
			state_desc = VMState_Description.TERMINATING;
		}

		@Override
		public void Execute() {
			sim_pause(0);
			
			hostDC.UpdateState(get_name(), VMState_Description.INACTIVE);
			state = new VMState_Inactive();
		}
	}
	
	private class VMState_TransferingUser extends VMState{
		public VMState_TransferingUser() {
			state_desc = VMState_Description.TERMINATING;
		}

		@Override
		public void Execute() {
			sim_pause(0);
			
			hostDC.UpdateState(get_name(), VMState_Description.INACTIVE);
			state = new VMState_Inactive();
		}
	}
}