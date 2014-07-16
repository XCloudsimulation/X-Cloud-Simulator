package network;

import data_centre.DataCentre;
import eduni.simjava.Sim_entity;
import eduni.simjava.Sim_event;
import eduni.simjava.Sim_port;
import eduni.simjava.Sim_system;
import mobility.*;

public class RadioBaseStation extends Sim_entity {
	
	public static final String IN_PORT_NAME = "IN";
	public static final String OUT_PORT_NAME = "OUT";
	
	
	private Location location;
	private Sim_port out_port, in_port;
	
	private int nbr;
	
	public RadioBaseStation(String name, Location location, DataCentre dc, int nbr) {
		super(name);
		this.location = location;
		this.setNbr(nbr);
		
		out_port = new Sim_port(OUT_PORT_NAME);
		add_port(out_port);
		
		in_port = new Sim_port(IN_PORT_NAME);
		add_port(in_port);
		
		Sim_system.link_ports(getName(), OUT_PORT_NAME, dc.get_name(), dc.IN_PORT_NAME);
	}

	@Override
	public void body(){
		while(Sim_system.running()){
			Sim_event e = new Sim_event();
			sim_get_next(e);	// Get the next event
			
			send_on_intact(e, out_port);
			
			sim_trace(1, get_name() + " Forwarded packet to " + e.get_data());
		}
	}

	public synchronized Location getLocation() {
		return location;
	}

	public synchronized void setLocation(Location location) {
		this.location = location;
	}

	public int getNbr() {
		return nbr;
	}

	public void setNbr(int nbr) {
		this.nbr = nbr;
	}
}
