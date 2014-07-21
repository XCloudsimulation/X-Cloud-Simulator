package data_centre;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import network.Packet;
import vm.VM;
import vm.VMServerGateway_Interface;
import vm.VMState_Description;
import measurment.PacketMeasIndex;
import measurment.LatencyMeasurement;
import mobility.*;
import eduni.simjava.Sim_entity;
import eduni.simjava.Sim_event;
import eduni.simjava.Sim_port;
import eduni.simjava.Sim_system;

public class DataCentre extends Sim_entity implements VMServerGateway_Interface{
	public static final String IN_PORT_NAME = "IN_PORT_NAME";
	public static final String OUT_PORT_NAME = "OUT_PORT_NAME";
	
	private HashMap<Integer, VM_association> vms;
	private HashMap<String, DataCentre_association> peer_refs;
	private ArrayList<Packet> packets;
	
	private Location loc;
	
	private int nbr_vms;
	
	private int active_vms;
	
	public DataCentre(String name, Location loc, int nbr_vms) {
		super(name);
		
		System.out.print(name +  "\t Initializing ... ");
		
		add_port(new Sim_port(IN_PORT_NAME));
		add_port(new Sim_port(OUT_PORT_NAME));
		
		this.loc = loc;
		
		this.nbr_vms = nbr_vms;
		
		// Initiliaze VMs
		vms = new HashMap<Integer, VM_association>();
		
		for (int i=0; i<nbr_vms; i++){
			CreateVMPlaceholder(i);
		}
		
		active_vms = 0;
		
		peer_refs = new HashMap<String, DataCentre.DataCentre_association>();
		
		packets = new ArrayList<Packet>();
		
		System.out.println(" DONE");
	}
	
	public void registerPeers(DataCentre_Peer[] peers){	
		for (DataCentre_Peer peer : peers){
			Sim_port port = new Sim_port(peer.name);
			add_port(port);			
			Sim_system.link_ports(get_name(), port.get_pname(), peer.name, IN_PORT_NAME);
			
			peer_refs.put(peer.name, new DataCentre_association(port, this.loc.getDistance(peer.loc)));
		}
	}

	private void CreateVMPlaceholder(int service){
		String vm_id = (get_name() + "_VM" + service);
		
		VM_association vm_ass = new VM_association(new VM(vm_id, this), new Sim_port(vm_id));
		
		vms.put(service, vm_ass);
		
		add_port(vm_ass.port);
		
		Sim_system.link_ports(get_name(), vm_ass.port.get_pname(), vm_ass.vm.get_name(), VM.IN_PORT_NAME);
	}	

	@Override
	public void UpdateState(String id, VMState_Description state) {
		System.out.println("\t\t" + get_name() + " - WM: " + id + " transitioning to state " + state);
		
		switch (state) {
			case ACTIVE : active_vms++; break;
			case INACTIVE : active_vms--; break;
			case INITIATING : break;
			case TERMINATING : break;
			case MIGRATING : break;
			default : System.err.println(state + " is an invalid state."); break;
		}	
	}

	@Override
	public void Migrate(Sim_event e, String dest) {
		System.out.println("\t\t" + get_name() + " - Migrating packet from " + e.scheduled_by() +  " to " + dest);
		send_on_intact(e,peer_refs.get(dest).port);
	}
	
	private void UpdateBaseServiceTime(long serviceTime){
		for (VM_association target : vms.values()) {
		    target.vm.UpdateBaseServiceTime(serviceTime);
		}
	}
	
	public synchronized void StorePacket(Packet p){
		packets.add(p);
	}
	
	public void DumpPacketData(FileWriter wr){
		try {
			for(Packet packet: packets){
				wr.append(packet.DumpLatencyMeasurements());
			}
		} catch (IOException e) {
			System.err.println(get_name() + " - Unable to dump packet.");
		}
	}
	
	public void DumpWorkloadData(FileWriter wr){
		for(VM_association vm: vms.values()){
			try {
				wr.append(vm.vm.DumpWorkloadMeasurements());
			} catch (IOException e) {
				System.err.println(get_name() + " - Unable to dump packet.");
			}
		}
	}
	
	@Override
	public void body(){
		Sim_event e;
		
		while(Sim_system.running()){
			e = new Sim_event();
			sim_get_next(e);

			if(e.get_tag()<0){
				return;
			}
			
			System.out.println("\t\t" + get_name() + " - Received packet of service type " + e.get_tag() + ", from " + e.scheduled_by());
			
			((Packet)e.get_data()).AddLatencyMeasurement(new LatencyMeasurement(PacketMeasIndex.DISPATCH, 0.12));
			
			send_on_intact(e, vms.get(e.get_tag()).port); 
		}
	}
	
	private class VM_association{
		public Sim_port port;
		public VM vm;
		
		public VM_association(VM vm, Sim_port port){
			this.vm = vm;
			this.port = port;
		}
	}
	
	private class DataCentre_association{
		public Sim_port port;
		public double dist;
		
		public DataCentre_association(Sim_port port, double dist){
			this.port = port;
			this.dist = dist;
		}	
	}
	
}
