package network;

import data_centre.DataCentre;
import eduni.simjava.Sim_entity;
import eduni.simjava.Sim_event;
import eduni.simjava.Sim_port;
import eduni.simjava.Sim_system;
import measurment.PacketMeasIndex;
import measurment.LatencyMeasurement;
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
		
		//System.out.println(name + " -> " + OUT_PORT_NAME + " -> " + dc.IN_PORT_NAME + " -> " + dc.get_name());
		Sim_system.link_ports(name, OUT_PORT_NAME, dc.get_name(), dc.get_name() );
	}

	@Override
	public void body(){
		while(Sim_system.running()){
			Sim_event e = new Sim_event();
			sim_get_next(e);	// Get the next event
			
			System.out.println("\t" + get_name() + " - Received packet from " + e.scheduled_by() + " forwardig it to " + out_port.get_dest_ename());
			
			if((Packet) e.get_data() != null){
				((Packet) e.get_data()).AddLatencyMeasurement(new LatencyMeasurement(PacketMeasIndex.RADIO, 0));
				((Packet) e.get_data()).AddLatencyMeasurement(new LatencyMeasurement(PacketMeasIndex.NETWORK_UPLINK, 0));
				((Packet) e.get_data()).AddLatencyMeasurement(new LatencyMeasurement(PacketMeasIndex.NETWORK_DOWNLINK, 0));
			}
			send_on_intact(e, out_port);
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
