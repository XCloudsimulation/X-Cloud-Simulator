package mobile_entities;
import java.util.Random;

import service.Service;
import mobility.Location;
import mobility.MobilityState;
import network.Packet_Data;
import network.Packet_Migrate;
import network.RadioBaseStation;
import eduni.simjava.Sim_entity;
import eduni.simjava.Sim_port;
import eduni.simjava.Sim_system;

public class UserEquipment extends Sim_entity{
	public static final String OUT_PORT_NAME = "OUT_PORT_NAME";
	
/*	private Sim_port[] rbs_ports;*/
	private Sim_port out_port;
	
	private MobilityState mobilityState;
	
	private Location location;
	
	private Random rnd;
	
	private int rbsAffiliation;
	
	private Service service;
	
	public UserEquipment(String name, RadioBaseStation[][] rbss, Service service) {
		super(name);
		
		rnd = new Random();
		
		setLocation(location);
		
		out_port = new Sim_port(OUT_PORT_NAME);
		add_port(out_port);
		
		this.service = service;
		
/*		rbs_ports = new Sim_port[rbss.length*rbss[0].length];
		for(int i=0; i<rbss.length; i++){
			for(int j=0; j<rbss[0].length; j++){
				Sim_port temp = new Sim_port(rbss[i][j].get_name());
				rbs_ports[i*j+j] = temp;
				add_port(temp);
				Sim_system.link_ports(get_name(), rbss[i][j].get_name(), rbss[i][j].get_name(), rbss[i][j].IN_PORT_NAME);
			}
		}*/
	}

	@Override
	public void body(){
		int cnt = 0;
		int service = 0;
		
		while(Sim_system.running()){
			
			// Dummy, just to debug.
			sim_pause(rnd.nextDouble()*5);
			
			System.out.println(get_name() + " - sending packet to rbs " + getRBSAffiliation() + " on port " + out_port.get_pname());
			
			if (cnt==100){
				sim_schedule(out_port,0.0,service,new Packet_Migrate(service, get_id(), 1, 1,cnt, "DC1"));
			} else {
				sim_schedule(out_port,0.0,service,new Packet_Data(service, get_id(), 1, 20,cnt));
				service = rnd.nextInt(2);
			}
			cnt ++;
		}
	}
	
	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public int getRBSAffiliation() {
		return rbsAffiliation;
	}

	public void setRBSAffiliation(int rbsAffiliation) {
		this.rbsAffiliation = rbsAffiliation;
	}

	public MobilityState getMobilityState() {
		return mobilityState;
	}

	public void setMobilityState(MobilityState mobilityState) {
		this.mobilityState = mobilityState;
	}

	public void updateLocation() {
		// TODO Auto-generated method stub
		
	}
}
