package main;
import mobile_entities.User;
import mobility.Location;

import org.w3c.dom.CDATASection;

import data_centre.*;
import eduni.simjava.Sim_system;

public class Simulation {

	private static final int NBR_VMS = 4;
	private static final int NBR_DCS = 2;
	
	public static void main(String[] args){
		Sim_system.initialise();
		
		// Enteties
		User user = new User("UE");
		DataCentre[] dcs = new DataCentre[NBR_DCS];
		
		
		// Initialize data centres
		DataCentre_Peer[] dcPeers = new DataCentre_Peer[NBR_DCS];
		
		for(int i=0; i<NBR_DCS; i++){
			dcPeers[i] =  new DataCentre_Peer("DC"+i, new Location(i, i));
			dcs[i] = new DataCentre(dcPeers[i].name, dcPeers[i].loc, NBR_VMS);
		}
		for(DataCentre dc : dcs){
			dc.registerPeers(dcPeers);
		}
		
		
		
		// Initialize connections
		Sim_system.link_ports(user.get_name(), user.OUT_PORT_NAME, dcs[0].get_name(), dcs[0].IN_PORT_NAME);
		
		
		// Set termination conditions
		Sim_system.set_termination_condition(Sim_system.TIME_ELAPSED, 100 , false);
		
		
		// Rin simulation
		Sim_system.run();
	}
	
}
