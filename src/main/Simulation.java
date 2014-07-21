package main;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

import vm.VMState_Description;
import magnitudes.*;
import measurment.PacketMeasIndex;
import mobile_entities.UserEquipment;
import mobility.Location;
import mobility.MobilityModel;
import network.Hom_2D_AffiliationStrategy;
import network.Network;
import network.RadioBaseStation;
import data_centre.*;
import eduni.simjava.Sim_system;
import framework.Clock;
import framework.Clock_Regular;

public class Simulation {
	
	final static String VM_MEAS_FILE_NAME = "vm_meas.csv";
	final static String PACKET_MEAS_FILE_NAME = "packet_meas.csv";
	
	public static void main(String[] args){
				
		// Init framework
		Sim_system.initialise();
		
		// Simulation entities
		RadioBaseStation[][] rbss;
		DataCentre[] dcs;
		UserEquipment[] users;
		Network network;
		MobilityModel mobilityModel;
		
		Random rnd = new Random();
		
		// Clocks
		//Clock clk_mobility = new Clock_Regular("CLK_mobility", new Time_Sec(1), 1);
		//Clock clk_network = new Clock_Regular("CLK_network", new Time_Sec(1), 2);
		
		// Defaults
		int[] params = new int[Param_Index.NbrParams()];
		params[Param_Index.NBR_SIDE.toInt()] = 4;
		params[Param_Index.NRB_SERVICES.toInt()] = 2;
		params[Param_Index.CELL_DIM.toInt()] = 800;
		params[Param_Index.RBS_PER_DC.toInt()] = 4;
		params[Param_Index.NBR_USERS.toInt()] = 10;
		
		int nbr_side, nbr_services, rbs_per_dc, nbr_rbs, nbr_dc, nbr_users ;
		double cell_dim;
		
		// Read parameters
		if(args.length == 0){
			System.out.println("No input parameters specified.");
			Param_Index.PrintUsage();
		} else if(args.length != Param_Index.NbrParams()){
			System.out.println("Invalid number of arguments");
		} else{
			for(Param_Index index : Param_Index.values()){
				params[index.toInt()] = Integer.parseInt(args[index.toInt()]);
			}
		}
			
		// Save parameters
		nbr_side = params[Param_Index.NBR_SIDE.toInt()];
		nbr_rbs = (int) Math.pow(nbr_side,2);
		rbs_per_dc = params[Param_Index.RBS_PER_DC.toInt()];
		nbr_dc 	= nbr_rbs/rbs_per_dc;
		nbr_services = params[Param_Index.NRB_SERVICES.toInt()];
		cell_dim = params[Param_Index.CELL_DIM.toInt()];
		nbr_users = params[Param_Index.NBR_USERS.toInt()];
					
		// configure simulation
		if(nbr_rbs%rbs_per_dc!=0){
			System.err.println("The " + nbr_rbs + " RBSs cannot be shared equaly, with " + rbs_per_dc + " RBSs per DC.");
			System.exit(0);
		}
		
		// Initialize data centres
		dcs = new DataCentre[nbr_dc];
		DataCentre_Peer[] dcPeers = new DataCentre_Peer[nbr_dc];
		
		for(int i=0; i<nbr_dc; i++){
			dcPeers[i] =  new DataCentre_Peer("DC"+i, new Location(i, i));
			dcs[i] = new DataCentre(dcPeers[i].name, dcPeers[i].loc, nbr_services);
		}
		for(DataCentre dc : dcs){
			dc.registerPeers(dcPeers);
		}
		
		// Initilize RBSs
		rbss = new RadioBaseStation[nbr_side][nbr_side];
		
		int nbr, dc_index;
		double loc_x, loc_y;
		
		for(int x=0; x<nbr_side; x++){
			for(int y=0; y<nbr_side; y++){
				nbr = y*nbr_side+x;
				loc_x = (x*2+1)*cell_dim/2;
				loc_y = (y*2+1)*cell_dim/2;
				
				double dc_dim = (nbr_side/Math.sqrt(rbs_per_dc));
				double dc_x = (x/Math.sqrt(rbs_per_dc));
				double dc_y = (y/Math.sqrt(rbs_per_dc));
				
				dc_index = (int) (Math.floor(dc_x) + Math.floor(dc_y)*dc_dim);
				rbss[x][y] = new RadioBaseStation("RBS_" + nbr, new Location(loc_x, loc_y),dcs[dc_index], nbr); 
				
				//System.out.println("RBS_x=" + loc_x + ",y=" + loc_y + " = nbr " + nbr+" -> DC_" + dc_index);
			}
		}
		
		// Initilize users
		users = new UserEquipment[nbr_users];
		for(int i=0; i<users.length; i++){
			users[i] = new UserEquipment("User"+i, new Location(
					(nbr_side*cell_dim)*rnd.nextDouble()
					,(nbr_side*cell_dim)*rnd.nextDouble()
					), rbss);
		}
		
		// Initilize network
		network = new Network("Network", users, rbss, new Hom_2D_AffiliationStrategy(rbss, cell_dim));
				
		// Set termination conditions
		Sim_system.set_termination_condition(Sim_system.TIME_ELAPSED, 10 , false);
		
		// Run simulation
		Sim_system.run();
		
		// Dump data
		try {
			FileWriter p_out = new FileWriter(PACKET_MEAS_FILE_NAME, true);
			FileWriter vm_out = new FileWriter(VM_MEAS_FILE_NAME, true);
			
			for(PacketMeasIndex name : PacketMeasIndex.values()){
				p_out.append(name + ";");
			}
			p_out.append("\r");

			vm_out.append("VM name; State; Duration \r");
			
			System.out.print("Dumping measurement data ... ");
			for(DataCentre dc : dcs){
				dc.DumpPacketData(p_out);
				dc.DumpWorkloadData(vm_out);
			}
			System.out.println("DONE");
			
			p_out.close();
			vm_out.close();
		} catch (IOException e) {
			System.out.println("Failed to dump measurements");
		}
	}
	
	private enum Param_Index {
	    NBR_SIDE(0), CELL_DIM(1), RBS_PER_DC(2), NRB_SERVICES(3), NBR_USERS(4);
	    private final int value;

	    private Param_Index(int value) {
	        this.value = value;
	    }

	    public static void PrintUsage(){
	    	System.out.print("Input parameters :");
	    	for(Param_Index target: Param_Index.values()){
	    		System.out.print(" ["+target+"] ");
	    	}
	    	System.out.print("\r");
	    }
	    
	    public static int NbrParams(){
	    	return Param_Index.values().length;
	    }
	    
	    public int toInt() {
	        return value;
	    }
	}
	
}
