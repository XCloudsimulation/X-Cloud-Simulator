package mobile_entities;
import java.util.Random;

import mobility.Location;
import network.Packet_Data;
import network.Packet_Migrate;
import eduni.simjava.Sim_entity;
import eduni.simjava.Sim_port;
import eduni.simjava.Sim_system;

public class User extends Sim_entity{
	public static final String OUT_PORT_NAME = "OUT_PORT_NAME";
	
	private Sim_port out_port;
	
	private Location location;
	
	private Random rnd;
	
	private int rbsAffiliation;
	
	public User(String name) {
		super(name);
		
		out_port = new Sim_port(OUT_PORT_NAME);
		add_port(out_port);
		
		rnd = new Random();
		
		setLocation(new Location(0, 0));
	}

	@Override
	public void body(){
		int cnt = 0;
		int service = 0;
		
		while(Sim_system.running()){
			
			// Dummy, just to debug.
			sim_pause(rnd.nextDouble()*5);
			
			if (cnt==10){
				sim_schedule(out_port,0.0,service,new Packet_Migrate(service,get_id(),"DC1"));
			} else {
				sim_schedule(out_port,0.0,service,new Packet_Data(service,get_id(),0,cnt));
				service = rnd.nextInt(3);
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
}
