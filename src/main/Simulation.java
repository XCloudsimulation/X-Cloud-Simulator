package main;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import service.Service_WEB_2001;
import vm.VMState_Description;
import magnitudes.*;
import measurment.PacketMeasIndex;
import mobile_entities.UserEquipment;
import mobility.*;
import network.Hom_2D_AffiliationStrategy;
import network.Network;
import network.RadioBaseStation;
import data_centre.*;
import eduni.simjava.Sim_system;
import framework.Clock;
import framework.Clock_Regular;

public class Simulation {
	
	final static String VM_MEAS_FILE_NAME = "vm_meas";
	final static String PACKET_MEAS_FILE_NAME = "packet_meas";
	final static String CELL_OCCUPANCY_MEAS_FILE_NAME = "cell_occupancy_meas";
	final static String SIMULATION_PARAMETERS = "simulation_parameters";
	
	final static String FILE_ENDING = ".csv";
	
	public static void main(String[] args){
				
		System.out.println(" ---- X-Cloud Simulator ---- ");
		
		// Init framework
		Sim_system.initialise();
		
		// Simulation entities
		RadioBaseStation[][] rbss;
		DataCentre[] dcs;
		UserEquipment[] users;
		Network network;
		MobilityModel mobilityModel;
		
		// Clocks
		Clock clk_mobility = new Clock_Regular("CLK_mobility", 	new Time_Sec(1), 1);
		Clock clk_network  = new Clock_Regular("CLK_network", 	new Time_Sec(100), 2);
		
		// Defaults
		int[] params = new int[Param_Index.NbrParams()];
		params[Param_Index.NBR_SIDE.toInt()] 			= 4;
		params[Param_Index.NRB_SERVICES.toInt()] 		= 1;
		params[Param_Index.CELL_DIM.toInt()] 			= 800;
		params[Param_Index.RBS_PER_DC.toInt()] 			= 1;
		params[Param_Index.NBR_USERS.toInt()] 			= 100;
		params[Param_Index.SIMULATION_TIME.toInt()] 	= 100000;
		double base_service_time = -1;
		
		int nbr_side, nbr_services, rbs_per_dc, nbr_rbs, nbr_dc, nbr_users ;
		double cell_dim, dom_x, dom_y;
		Time sim_time;
		
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
		sim_time = new Time_Sec(params[Param_Index.SIMULATION_TIME.toInt()]);
					
		// Simulation domain
		dom_x = cell_dim*nbr_side;
		dom_y = dom_x;
		
		System.out.println("\t Simulation domain x=" + dom_x + ", y=" + dom_y);
		System.out.println("\t Number of RBS=" + nbr_rbs + ", dimensions= " + cell_dim);
		System.out.println("\t Number of users=" + nbr_users);
		
		// configure simulation
		if(nbr_rbs%rbs_per_dc!=0){
			System.err.println("The " + nbr_rbs + " RBSs cannot be shared equaly, with " + rbs_per_dc + " RBSs per DC.");
			System.exit(0);
		}
		
		// Initialize data centres
		dcs = new DataCentre[nbr_dc];
		DataCentre_Peer[] dcPeers = new DataCentre_Peer[nbr_dc];
		
		if(base_service_time==-1){
			Service_WEB_2001 temp_service = new Service_WEB_2001(0);
			base_service_time = temp_service.getMeanArrivalRate().toSec()*(nbr_rbs/(nbr_users/nbr_services));
		}
		
		for(int i=0; i<nbr_dc; i++){
			dcPeers[i] = new DataCentre_Peer("DC"+i, new Location(i, i));
			dcs[i] = new DataCentre(dcPeers[i].name, dcPeers[i].loc, nbr_services, base_service_time);
		}
		for(DataCentre dc : dcs){
			dc.registerPeers(dcPeers);
		}
		
		// Initilize RBSs
		rbss = new RadioBaseStation[nbr_side][nbr_side];
		
		int nbr, dc_index;
		double loc_x, loc_y;
		
		nbr=0;
		for(int x=0; x<nbr_side; x++){
			for(int y=0; y<nbr_side; y++){
				//nbr = y*nbr_side+x;
				loc_x = (x*2+1)*cell_dim/2;
				loc_y = (y*2+1)*cell_dim/2;
				
				double dc_dim = (nbr_side/Math.sqrt(rbs_per_dc));
				double dc_x = (x/Math.sqrt(rbs_per_dc));
				double dc_y = (y/Math.sqrt(rbs_per_dc));
				
				dc_index = (int) (Math.floor(dc_x)*dc_dim + Math.floor(dc_y));
				rbss[x][y] = new RadioBaseStation("RBS_" + nbr, new Location(loc_x, loc_y),dcs[dc_index], nbr); 
				
				System.out.println("RBS_"+nbr+", x=" + loc_x + ", y=" + loc_y + " = nbr " + nbr+" -> DC_" + dc_index);
				
				nbr ++;
			}
		}
		
		// Initilize users
		users = new UserEquipment[nbr_users];
		for(int i=0; i<users.length; i++){
			users[i] = new UserEquipment("User"+i, rbss, new Service_WEB_2001(0),new ModeModel_Car(dom_x,dom_y));
		}
		
		// Initilize network
		network = new Network("Network", users, rbss, new Hom_2D_AffiliationStrategy(rbss, cell_dim));
		Sim_system.link_ports(clk_network.get_name(), clk_network.OUT_PORT_NAME, network.get_name(), network.IN_PORT_NAME);
				
		// Initilize mobility model
		mobilityModel = new MobilityModel_Smooth("Smooth", users);
		Sim_system.link_ports(clk_mobility.get_name(), clk_mobility.OUT_PORT_NAME, mobilityModel.get_name(), mobilityModel.IN_PORT_NAME);
		
		// Set termination conditions
		Sim_system.set_termination_condition(Sim_system.TIME_ELAPSED, sim_time.toSec(), false);
		
		System.out.println(" --------------------------- ");
		
		// Run simulation
		Sim_system.run();
		
		// Dump data
		try {
			DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
			Date date = new Date();
			
			FileWriter p_out = new FileWriter(PACKET_MEAS_FILE_NAME+"_"+dateFormat.format(date) + FILE_ENDING, true);
			FileWriter vm_out = new FileWriter(VM_MEAS_FILE_NAME+"_"+dateFormat.format(date) + FILE_ENDING, true);
			FileWriter rbs_out = new FileWriter(CELL_OCCUPANCY_MEAS_FILE_NAME+"_"+dateFormat.format(date) + FILE_ENDING, true);
			FileWriter sim_out = new FileWriter(SIMULATION_PARAMETERS+"_"+dateFormat.format(date) + FILE_ENDING, true);
			
			for(PacketMeasIndex name : PacketMeasIndex.values()){
				p_out.append(name + ";");
			}
			p_out.append("\r");

			vm_out.append("VM name; State; Duration \r");
			
			rbs_out.append("RBS;Occupancy;Time \r");
			
			System.out.print("Dumping measurement data ... ");
			for(DataCentre dc : dcs){
				dc.DumpPacketData(p_out);
				dc.DumpWorkloadData(vm_out);
			}
			
			for(int i=0;i<nbr_side;i++){
				for(int j=0;j<nbr_side;j++){
					rbss[i][j].DumpOccupancyData(rbs_out);
				}
			}
			
			sim_out.append("DATE;" +dateFormat.format(date));
			for(Param_Index index : Param_Index.values()){
				sim_out.append(index + ";" + params[index.value]);
			}
			sim_out.append("BASE_SERVICE_TIME;" +base_service_time);
			
			System.out.println("DONE");
			
			p_out.close();
			vm_out.close();
			rbs_out.close();
			sim_out.close();
		} catch (IOException e) {
			System.out.println("Failed to dump measurements");
		}
	}
	
	private enum Param_Index {
	    NBR_SIDE(0), CELL_DIM(1), RBS_PER_DC(2), NRB_SERVICES(3), NBR_USERS(4), SIMULATION_TIME(5);
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
