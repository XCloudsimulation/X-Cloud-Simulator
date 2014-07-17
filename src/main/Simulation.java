package main;
import mobile_entities.User;
import mobility.Location;
import network.RadioBaseStation;
import data_centre.*;
import eduni.simjava.Sim_system;

public class Simulation {
	
	public static void main(String[] args){
		RadioBaseStation[][] rbss;
		DataCentre[] dcs;
		
		int num_side, nbr_services, rbs_per_dc ;
		double cell_dim;
		
		if(args.length == 0){
			System.out.println("No input parameters specified. \r Usage:");
			return;
		}
		
		Sim_system.initialise();
		
		int nbr_rbs = num_side^2;
		int nbr_dc 	= nbr_rbs/rbs_per_dc;
		
		if(nbr_rbs%rbs_per_dc!=0){
			System.err.println("The " + nbr_rbs + " RBSs cannot be shared equaly, with " + rbs_per_dc + " RBSs per DC.");
			System.exit(0);
		}
		
		// Initialize data centres
		dcs = new DataCentre[nbr_dc];
		DataCentre_Peer[] dcPeers = new DataCentre_Peer[nbr_rbs];
		
		for(int i=0; i<nbr_rbs; i++){
			dcPeers[i] =  new DataCentre_Peer("DC"+i, new Location(i, i));
			dcs[i] = new DataCentre(dcPeers[i].name, dcPeers[i].loc, nbr_services);
		}
		for(DataCentre dc : dcs){
			dc.registerPeers(dcPeers);
		}
		
		// Initialize connections
		//Sim_system.link_ports(user.get_name(), user.OUT_PORT_NAME, dcs[0].get_name(), dcs[0].IN_PORT_NAME);
		
		// Initilize RBSs
		rbss = new RadioBaseStation[num_side][num_side];
		
		int nbr, dc_index;
		double loc_x, loc_y;
		
		for(int x=0; x<num_side; x++){
			for(int y=0; y<num_side; y++){
				nbr = num_side*y+x;
				loc_x = (x*2+1)*cell_dim/2;
				loc_y = (y*2+1)*cell_dim/2;
				
				dc_index = (int) (Math.floor(loc_x/(Math.sqrt(rbs_per_dc)*cell_dim)) + Math.floor(loc_y/(Math.sqrt(rbs_per_dc)*cell_dim))*Math.sqrt(rbs_per_dc));

				rbss[x][y] = new RadioBaseStation("RBS_" + nbr, new Location(loc_x, loc_y), dcs[dc_index], nbr); 
			}
		}
		
		// Enteties
		User user = new User("UE");
		
		// Set termination conditions
		Sim_system.set_termination_condition(Sim_system.TIME_ELAPSED, 100 , false);
		
		
		// Rin simulation
		Sim_system.run();
	}
	
	public enum Variable_Index{
		NUM_SIDE, CELL_DIM, NBR_SERVICES, RBS_PER_DC;
		
		public void PrintUsage(){
			System.out.println("Usage:");
			for (Variable_Index target: Variable_Index.values()){
				System.out.println(target);
			}
		}
		
		public int NbrVariable(){
			return Variable_Index.values().length;
		}
	}
	
}
