package main;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import service.FileTransfer1998;
import service.Service;
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
import data_centre.DataCentre.Scheme;
import eduni.simjava.Sim_system;
import framework.Clock;
import framework.Clock_Regular;


public class Simulation {
	
	final static String VM_MEAS_FILE_NAME = "vm_meas";
	final static String PACKET_MEAS_FILE_NAME = "packet_meas";
	final static String CELL_OCCUPANCY_MEAS_FILE_NAME = "cell_occupancy_meas";
	final static String SIMULATION_PARAMETERS_FILE_NAME = "simulation_parameters";
	final static String RBS_AFILL_FILE_NAME = "rbs_affiliation";
	
	final static String FILE_ENDING = ".csv";
	
	public static void main(String[] args){
				
		System.out.println(" ---- X-Cloud Simulator ----");
		
		// Init framework
		Sim_system.initialise();
		
		// Simulation entities
		RadioBaseStation[][] rbss;
		DataCentre[] dcs;
		UserEquipment[] users;
		Network network;
		MobilityModel mobilityModel;
		
		// Clocks
		Clock clk_mobility = new Clock_Regular("CLK_mobility", 	new Time_Sec(2), 1);
		Clock clk_network  = new Clock_Regular("CLK_network", 	new Time_Sec(100), 2);
		
		// Defaults
		int[] params = new int[Param_Index.NbrParams()];
		params[Param_Index.NBR_SIDE.toInt()] 			= 4;
		params[Param_Index.NRB_SERVICES.toInt()] 		= 1;
		params[Param_Index.CELL_DIM.toInt()] 			= 1300;
		params[Param_Index.RBS_PER_DC.toInt()] 			= 1;
		params[Param_Index.NBR_USERS.toInt()] 			= 100;
		params[Param_Index.SIMULATION_TIME.toInt()] 	= 28800*2;
		params[Param_Index.DC_VM_LIMIT.toInt()] 		= 2;//params[Param_Index.NRB_SERVICES.toInt()] ;
		double base_service_time 						= -1; // 50 users
		File dir 										= new File("Results");
		DataCentre.Scheme dc_scheme 					= Scheme.STRICT;
		Class serviceModel 								= FileTransfer1998.class;
		try {
			Constructor[] constructor = serviceModel.getConstructors();
		} catch (SecurityException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		int nbr_side, nbr_services, rbs_per_dc, nbr_rbs, nbr_dc, nbr_users, vm_limit;
		double cell_dim, dom_x, dom_y;
		Time sim_time;

		// Read parameters
		if(args.length == 0){
			System.out.println("No input parameters specified, continuing with default values.");
			Param_Index.PrintUsage();
			
		} else if(args.length != Param_Index.NbrParams()){
			System.out.println("Invalid number of arguments, continuing with default values.");
		} else{
			for(Param_Index index : Param_Index.values()){
				switch(index){
				case BASE_SERVICE_TIME: 
					base_service_time = Double.parseDouble(args[Param_Index.BASE_SERVICE_TIME.toInt()]); 
					break;
				case RESULT_DIR:
					dir = new File(args[Param_Index.RESULT_DIR.toInt()]);
					break;
				case DC_VM_ELASTICITY_SCHEME:
					dc_scheme = DataCentre.Scheme.fromString(args[Param_Index.DC_VM_ELASTICITY_SCHEME.toInt()]);
					break;
				default: 
					params[index.toInt()] = Integer.parseInt(args[index.toInt()]);
				}
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
		vm_limit = params[Param_Index.DC_VM_LIMIT.toInt()];
					
		// Simulation domain
		dom_x = cell_dim*nbr_side;
		dom_y = dom_x;
		
		// configure simulation
		if(nbr_rbs%rbs_per_dc!=0){
			System.err.println("The " + nbr_rbs + " RBSs cannot be shared equaly, with " + rbs_per_dc + " RBSs per DC.");
			System.exit(0);
		}
		
		// Initialize data centres
		dcs = new DataCentre[nbr_dc];
		DataCentre_Peer[] dcPeers = new DataCentre_Peer[nbr_dc];
		
		if(base_service_time==-1){
			Service temp_service = new Service_WEB_2001(0);
			
			double lambda = temp_service.getMeanArrivalRate().toSec();
			double result = lambda*(((double)nbr_users/((double)nbr_rbs/(double)rbs_per_dc))/(double)nbr_services);
			
			base_service_time = 1.0/result;
		}
		
		for(int i=0; i<nbr_dc; i++){
			dcPeers[i] = new DataCentre_Peer("DC"+i, new Location(i, i));
			dcs[i] = new DataCentre(dcPeers[i].name, dcPeers[i].loc, nbr_services, base_service_time, vm_limit, dc_scheme);
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
				rbss[x][y] = new RadioBaseStation("RBS_" + nbr, new Location(loc_x, loc_y), dcs[dc_index], nbr); 
				
				//System.out.println("RBS_"+nbr+", x=" + loc_x + ", y=" + loc_y + " = nbr " + nbr+" -> DC_" + dc_index);
				
				nbr ++;
			}
		}
		
		// Initilize users
		users = new UserEquipment[nbr_users];
		for(int i=0; i<users.length; i++){
			int service = (int) Math.floor(i/(nbr_users/nbr_services));
			users[i] = new UserEquipment("User"+i, rbss, new Service_WEB_2001(service>nbr_services-1?nbr_services-1:service),new ModeModel_Car(dom_x,dom_y));
		}
		
		// Initilize network
		network = new Network("Network", users, rbss, new Hom_2D_AffiliationStrategy(rbss, cell_dim));
		Sim_system.link_ports(clk_network.get_name(), clk_network.OUT_PORT_NAME, network.get_name(), network.IN_PORT_NAME);
				
		// Initilize mobility model
		mobilityModel = new MobilityModel_Smooth("Smooth", users);
		Sim_system.link_ports(clk_mobility.get_name(), clk_mobility.OUT_PORT_NAME, mobilityModel.get_name(), mobilityModel.IN_PORT_NAME);
		
		// Set termination conditions
		Sim_system.set_termination_condition(Sim_system.TIME_ELAPSED, sim_time.toSec(), false);
		
		System.out.println("\tSIMULATION_DOMAIN : X=" + dom_x + " Y=" + dom_y);
		for(Param_Index index : Param_Index.values()){
			switch(index){
			case BASE_SERVICE_TIME : 
				System.out.println("\tBASE_SERVICE_TIME : " + base_service_time);
				break;
			case RESULT_DIR : 
				System.out.println("\tRESULT_DIR : " + dir.getAbsolutePath());
				break;
			case DC_VM_ELASTICITY_SCHEME :
				System.out.println("\tDC_VM_ELASTICITY_SCHEME : " + dc_scheme);
				break;
			default: 
				System.out.println("\t"  + index + " : " + params[index.value]);
			}
			
		}

		
		System.out.println(" ---------------------------");
		
		long start = System.currentTimeMillis();
		// Run simulation
		Sim_system.run();
		long duration = System.currentTimeMillis()-start;
		
		long hours = TimeUnit.MILLISECONDS.toHours(duration);
		long min = TimeUnit.MILLISECONDS.toMinutes(duration);
		long sec = TimeUnit.MILLISECONDS.toSeconds(duration);
		long msec = TimeUnit.MILLISECONDS.toMillis(duration);
		
		String str_duration = (hours>0?hours+"hours,":"") +  (min>0?min+"minutes, ":"") + (sec>0?sec+" seconds, ":"") +  msec+" millisecond";
		
		System.out.println("\t Simulation duration: " + str_duration);
		
		// Dump data
		try {
			DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
			Date date = new Date();
			
			dir.mkdir();
			
			String desc = "_nbr_usr="+nbr_users+"_nbr_ser="+nbr_services+"_rbs_per_dc="+rbs_per_dc+"_cell_dim="+cell_dim; 
			
			FileWriter p_out 	= new FileWriter(new File(dir,PACKET_MEAS_FILE_NAME+desc+FILE_ENDING), false);
			FileWriter vm_out 	= new FileWriter(new File(dir,VM_MEAS_FILE_NAME+desc+FILE_ENDING), false);
//			FileWriter rbs_out 	= new FileWriter(new File(dir,CELL_OCCUPANCY_MEAS_FILE_NAME+desc+FILE_ENDING), false);
			FileWriter sim_out 	= new FileWriter(new File(dir,SIMULATION_PARAMETERS_FILE_NAME+desc+FILE_ENDING), false);
//			FileWriter user_out = new FileWriter(new File(dir,RBS_AFILL_FILE_NAME+desc+FILE_ENDING), false);
			
			for(PacketMeasIndex name : PacketMeasIndex.values()){
				p_out.append(name + ";");
			}
			p_out.append("\r");

			vm_out.append("VM name; State; Duration \r");
			
//			rbs_out.append("RBS;Occupancy;Time \r");
			
			System.out.print("Dumping measurement data ... ");
			for(DataCentre dc : dcs){
				dc.DumpPacketData(p_out);
				dc.DumpWorkloadData(vm_out);
			}
			
/*			for(int i=0;i<nbr_side;i++){
				for(int j=0;j<nbr_side;j++){
					rbss[i][j].DumpOccupancyData(rbs_out);
				}
			}
*/			
/*			for(UserEquipment ue: users){
				ue.DumpMeas(user_out);
			}*/
			
			sim_out.append("DATE;" +dateFormat.format(date) + "\r");
			sim_out.append("TIME_UNIT;" + "Seconds" + "\r");
			sim_out.append("SIMULATION_DUATION;" + str_duration + "\r");
			for(Param_Index index : Param_Index.values()){
				sim_out.append(index + ";" + params[index.value]+ "\r");
			}
			sim_out.append("BASE_SERVICE_TIME;" +base_service_time);
			
			
			System.out.println("DONE");
			
			p_out.close();
			vm_out.close();
//			rbs_out.close();
			sim_out.close();
//			user_out.close();
		} catch (IOException e) {
			System.out.println("Failed to dump measurements");
		}
	}
	
	private enum Param_Index {
	    NBR_SIDE(0), CELL_DIM(1), RBS_PER_DC(2), NRB_SERVICES(3), NBR_USERS(4), SIMULATION_TIME(5), BASE_SERVICE_TIME(6), RESULT_DIR(7), DC_VM_LIMIT(8), DC_VM_ELASTICITY_SCHEME(9);
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
