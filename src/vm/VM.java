package vm;

import java.util.ArrayList;
import java.util.HashMap;

import magnitudes.Time;
import magnitudes.Time_Sec;
import magnitudes.Time_mSec;
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
import eduni.simjava.Sim_type_p;


public class VM extends Sim_entity {
	public final static String IN_PORT_NAME = "IN_PORT_NAME";
	
	private ArrayList<VMMeasIndex> vm_meas;
	
	private VMState state;
	private VMState_Description currentState;
	
	private double baseServieTime;
	
	private double inStateSince;
	
	private DataCentre hostDC;
	
	private HashMap<String, Integer> sessions;
	
	private Sim_event deffered_event;
	
	public VM(String name, DataCentre hostDC) {
		super(name);
		
		this.hostDC = hostDC;
		
		add_port(new Sim_port(IN_PORT_NAME));
		
		state = new VMState_Inactive();
		
		vm_meas = new ArrayList<VMMeasIndex>();
		
		sessions = new HashMap<String, Integer>();
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

	public synchronized void UpdateBaseServiceTime(double base_service_time2) {
		this.baseServieTime = base_service_time2;
	}

	private abstract class VMState{
		protected VMState_Description state_desc;
		protected Sim_event e;
		
		public void Execute(){
			e = new Sim_event();
			sim_get_next(e);
		}
		
		protected void TransitionToState(VMState new_state){
			state = new_state;
			hostDC.UpdateState(get_name(), state.state_desc);
			
			AddStateMeas(state.state_desc);
		}
		
		protected void AddStateMeas(VMState_Description desc){
			double t_now = Sim_system.sim_clock();
			vm_meas.add(new VMMeasIndex(currentState, t_now-inStateSince));
			
			currentState = desc;
			inStateSince = t_now;
		}
	}
	
	private class VMState_Active extends VMState{

		public VMState_Active() {
			state_desc = VMState_Description.ACTIVE;
		}

		@Override
		public void Execute() {
			e = new Sim_event();
			
			sim_select(new Sim_type_p(Packet_Description.MIGRATE.toInt()), e);
			
			//if(packet instanceof Packet_Migrate && sim_waiting(new Sim_from_p(e.scheduled_by())) > 2){
			if(e!=null && e.get_tag()==Packet_Description.MIGRATE.toInt() && sim_waiting(new Sim_from_p(e.scheduled_by())) > 0){
				Object packet = e.get_data();
				//System.err.println(get_name() + " - Migrating to DC: " + ((Packet_Migrate) packet).getDest());
				TransitionToState(new VMState_TransferingUser(((Packet_Migrate) packet).getDest(), e.scheduled_by()));
				return;
			} 
			
			sim_get_next(e);
			Object packet = e.get_data();
			
			if (packet instanceof Packet_Data){
				int user 	= ((Packet) packet).user;
				int seq_num = ((Packet) packet).packet;
				int ses_num = ((Packet) packet).session;
				int ses_siz = ((Packet) packet).session_size;
				
				//System.out.println("\t\t\t " + get_name() + " - Processing request " + seq_num + "/" + ses_siz + " of session " + ses_num + " from user " + user + ", remaining deffered events: " + sim_waiting());
				((Packet) packet).AddLatencyMeasurement(new LatencyMeasurement(PacketMeasIndex.PROCESS, baseServieTime));
				
				hostDC.StorePacket((Packet) packet);
				
				AddStateMeas(VMState_Description.PROCESS);
				sim_pause(baseServieTime);
				AddStateMeas(VMState_Description.ACTIVE);
				
				if(!sessions.containsKey(user+":"+ses_num)){
					sessions.put(user+":"+ses_num, ses_siz);
				}
				if(seq_num >= ses_siz-1){
					sessions.remove(user+":"+ses_num);
					//System.err.println("\t\t\t " + get_name() + " - Completed session: " + (user+":"+ses_num) + ", remaining sessions: " +sessions.size());
				}
				
				if(sessions.size() == 0 && sim_waiting() == 0){
					TransitionToState(new VMState_Terminating());
				}
					
			} else {
				//System.err.println("Invalid packet type");
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
				//System.out.println(e.get_src());
				((Packet)e.get_data()).Migrated();
				hostDC.Migrate(e, dest);
			} else{
				TransitionToState(new VMState_Terminating());
			}
			
		}
	}
	
	private class VMState_TransferingUser extends VMState{
		private String dest;
		private int user;
		
		public VMState_TransferingUser(String dest, int user) {
			state_desc = VMState_Description.TRANSFERING_USER;
			this.dest = dest;
			this.user = user;
		}

		@Override
		public void Execute() {
			//System.out.println(get_name() + " - Transefering " + sim_waiting(new Sim_from_p(user)) + " of " + user + " requests to " + dest);
			
			//super.Execute();
			Time migate_time = new Time_mSec(10);
			
			e = new Sim_event();
			//System.out.println(get_name() + " - Deffered event = " + sim_waiting());
			sim_select(new Sim_from_p(user),e);
			
			while(e.get_tag() >= 0 && e.scheduled_by()==user){
				
				if(e.get_data() instanceof Packet_Data){
					
					int session = ((Packet) e.get_data()).session;
					int user 	= ((Packet) e.get_data()).user;
					int seq_num = ((Packet) e.get_data()).packet;
					int ses_siz = ((Packet) e.get_data()).session_size;
					
					sessions.remove(user+":"+session);
					
					//System.err.println("\t\t\t " + get_name() + " - Migrating " + seq_num + "/" + ses_siz + " of session " + session + " from user " + user + ", to: " + dest);
					//System.err.println("\t\t\t " + get_name() + " - Remainig sessions: " + sessions.size()); 
					sim_pause(migate_time.toSec());
					
					((Packet)e.get_data()).Migrated();
					((Packet)e.get_data()).AddLatencyMeasurement(new LatencyMeasurement(PacketMeasIndex.MIGRATE, migate_time.toSec()));
					
					hostDC.Migrate(e, dest);
				}
				e = new Sim_event();
				sim_select(new Sim_from_p(user),e);
			} 
			
			//System.out.println(get_name() + " - Transefering done - Remainig packets: " + sim_waiting());

			
			if(sessions.size() == 0 && sim_waiting() == 0){
				TransitionToState(new VMState_Terminating());
			} else {
				TransitionToState(new VMState_Active());
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
			} else if(e.get_tag()==Packet_Description.DATA.toInt()){
				Object packet = e.get_data();
				((Packet) packet).AddLatencyMeasurement(new LatencyMeasurement(PacketMeasIndex.QUEUE, e.end_waiting_time()-e.event_time()));
				
				TransitionToState(new VMState_Initiating());
				deffered_event = e;
			}
			
		}
		
	}
	
	private class VMState_Initiating extends VMState{
		public VMState_Initiating() {
			state_desc = VMState_Description.INITIATING;
		}

		@Override
		public void Execute() {
			Time init_time = new Time_Sec(82);
			
			sim_pause(init_time.toSec());
			sim_completed(deffered_event);
			
			Object packet = deffered_event.get_data();
			((Packet) packet).AddLatencyMeasurement(new LatencyMeasurement(PacketMeasIndex.QUEUE, init_time.toSec()));
			((Packet) packet).AddLatencyMeasurement(new LatencyMeasurement(PacketMeasIndex.PROCESS, 2));
				
			hostDC.StorePacket((Packet) packet);
			
			deffered_event = null;
			
			TransitionToState(new VMState_Active());
		}
	}
	
	private class VMState_Terminating extends VMState{
		public VMState_Terminating() {
			state_desc = VMState_Description.TERMINATING;
		}

		@Override
		public void Execute() {
			Time init_time = new Time_Sec(21);
			
			sim_pause(init_time.toSec());
			
			TransitionToState(new VMState_Inactive());
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