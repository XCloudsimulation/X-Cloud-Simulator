package network;

import java.util.ArrayList;
import java.util.HashMap;

import eduni.simjava.Sim_entity;
import eduni.simjava.Sim_event;
import eduni.simjava.Sim_port;
import eduni.simjava.Sim_system;
import measurment.Numerical_Measurement;
import mobile_entities.*;

public class Network extends Sim_entity{

	public static final String IN_PORT_NAME = "IN";

	private UserEquipment[] hosted_entities;
	private AffiliationStrategy affiliation_strategy;
	private RadioBaseStation[][] rbs_enteties;
	
	private Sim_port in_port;
	
	private static HashMap<Integer, ArrayList<Numerical_Measurement>> node_allocations;
	
	public Network(String name, UserEquipment[] hosted_entities, RadioBaseStation[][] rbs_enteties, AffiliationStrategy affiliation_strategy) {
		super(name);
		
		this.hosted_entities = hosted_entities;
		this.rbs_enteties = rbs_enteties;
		this.affiliation_strategy = affiliation_strategy;
		
		//sim_trace(1, "Initilizing Network");
		
		in_port = new Sim_port(IN_PORT_NAME);
		add_port(in_port);
		
		node_allocations = new HashMap<Integer, ArrayList<Numerical_Measurement>>();
		
		InitNodeAssociations();
	}

	@Override
	public void body(){
		while(Sim_system.running()){
			Sim_event e = new Sim_event();
			sim_get_next(e);	// Get the next event
			
			UpdateNodeAssociations(); // Updates node affiliation based on every clock pulse.
			
			sim_completed(e);	// The event has completed service
		}
	}
	
	public synchronized void InitNodeAssociations(){
		for(UserEquipment user: hosted_entities){
			int[] node = affiliation_strategy.AssertAffiliation(user);
				int new_rbs = rbs_enteties[node[0]][node[1]].getNbr();
				
				user.setRBSAffiliation(new_rbs);
				user.setRbsPos(node);
				user.setDC(rbs_enteties[node[0]][node[1]].getDc_name());
				
				rbs_enteties[node[0]][node[1]].regUser();
				//System.out.println("Network - " + user.get_name() + " is now associated with " + rbs_enteties[node[0]][node[1]].get_name());

				//Sim_system.link_ports(user.get_name(), user.OUT_PORT_NAME, rbs_enteties[node[0]][node[1]].get_name(), rbs_enteties[node[0]][node[1]].IN_PORT_NAME);
			}
	}
	
	public synchronized void UpdateNodeAssociations(){
		//HashMap<Integer, Integer> updated_node_allocations = new HashMap<Integer, Integer>();
		
		for(UserEquipment user: hosted_entities){
			int[] node = affiliation_strategy.AssertAffiliation(user);
			
			int prev_rbs = user.getRBSAffiliation();
			int new_rbs = rbs_enteties[node[0]][node[1]].getNbr();
					
			if(prev_rbs != new_rbs){
				int[] prev_rbs_pos = user.getRbsPos();

				rbs_enteties[node[0]][node[1]].regUser();
				rbs_enteties[prev_rbs_pos[0]][prev_rbs_pos[1]].unregUser();
				
				user.setRBSAffiliation(new_rbs);
				user.setRbsPos(node);
				user.updateDC(rbs_enteties[node[0]][node[1]].getDc_name());
				
				//System.out.println("Network - " + user.get_name() + " is now associated with " + rbs_enteties[node[0]][node[1]].get_name());
			}
			
			/*	
			if(!updated_node_allocations.containsKey(node)){
				updated_node_allocations.put(node, 0);
			}
			updated_node_allocations.put(node,updated_node_allocations.get(node) + 1);*/
		}
		
		/*for(Integer node: updated_node_allocations.keySet()){
			if(!node_allocations.containsKey(node)){
				node_allocations.put(node, new ArrayList<Measurement>());
			}
			node_allocations.get(node).add(new Measurement(updated_node_allocations.get(node),Sim_system.sim_clock()));
		}*/
	}

	public String getMeasureData(int rn){
		StringBuffer sb = new StringBuffer();

		for(Numerical_Measurement target: node_allocations.get(rn))
			sb.append(target.value + ";" + target.time + "\r");
		
		return sb.toString();
	} 
}

