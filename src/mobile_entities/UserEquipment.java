package mobile_entities;
import java.util.Random;

import service.Service;
import mobility.*;
import network.Packet_Data;
import network.Packet_Description;
import network.Packet_Migrate;
import network.RadioBaseStation;
import eduni.simjava.Sim_entity;
import eduni.simjava.Sim_port;
import eduni.simjava.Sim_system;

public class UserEquipment extends Sim_entity{
	public static final String OUT_PORT_NAME = "OUT_PORT_NAME";
	public static final String PREV_OUT_PORT_NAME = "PREV_OUT_PORT_NAME";
	
	private Sim_port[] rbs_ports;
	//private Sim_port out_port, prev_out_port;
	
	private ModeModel mobility;
	
	private Random rnd;
	
	private int rbsAffiliation, prev_rbsAffiliation;
	private int[] rbsPos;
	private String dc, prev_dc;
	
	private Service service_model;
	
	private boolean migrate;
	
	public UserEquipment(String name, RadioBaseStation[][] rbss, Service service, ModeModel mobility) {
		super(name);
		
		rnd = new Random();
		
		/*out_port = new Sim_port(OUT_PORT_NAME);
		add_port(out_port);

		prev_out_port = new Sim_port(PREV_OUT_PORT_NAME);
		add_port(prev_out_port);*/
		
		this.service_model = service;
		this.mobility = mobility;
		
		rbs_ports = new Sim_port[rbss.length*rbss[0].length];
		for(int i=0; i<rbss.length; i++){
			for(int j=0; j<rbss[0].length; j++){
				Sim_port temp = new Sim_port(rbss[i][j].get_name());
				rbs_ports[rbss[i][j].getNbr()] = temp;
				add_port(temp);
				Sim_system.link_ports(get_name(), rbss[i][j].get_name(), rbss[i][j].get_name(), rbss[i][j].IN_PORT_NAME);
			}
		}
		
		System.out.println(get_name() + " - Initial location x=" + mobility.getLocation().x + ", y=" + mobility.getLocation().y);
		
		migrate = false;
	}

	@Override
	public void body(){
		int clicks;
		int session=0;
		
		while(Sim_system.running()){
			sim_pause(service_model.getInterSessionTime().toSec());
			
			clicks = service_model.getSessionSize();
			
			for(int i=0; i<clicks; i++){
				if(migrate){
					sim_schedule(rbs_ports[prev_rbsAffiliation],0.0,Packet_Description.MIGRATE.toInt(),new Packet_Migrate(service_model.getServiceNbr(), get_id(), session, clicks, i, dc));
					migrate = false;
				}
				
				sim_pause(service_model.getInterRequestTime().toSec());
				
				try{
				sim_schedule(rbs_ports[rbsAffiliation],0.0,Packet_Description.DATA.toInt(),new Packet_Data(service_model.getServiceNbr(), get_id(), session, clicks,i));
				}catch(Exception e){
					System.err.println(rbsAffiliation + " - " + rbs_ports[rbsAffiliation]);
				}
			}
			
			session ++;
		}
	}
	
	public synchronized Location getLocation() {
		return mobility.getLocation();
	}

	public synchronized void setLocation(Location location) {
		mobility.setLocation(location);
	}

	public synchronized int getRBSAffiliation() {
		return rbsAffiliation;
	}

	public synchronized void setRBSAffiliation(int rbsAffiliation) {
		prev_rbsAffiliation = this.rbsAffiliation;
		this.rbsAffiliation = rbsAffiliation;
	}

	public synchronized void updateLocation(double time) {
		mobility.update(time);
	}

	public int[] getRbsPos() {
		return rbsPos;
	}

	public void setRbsPos(int[] rbsPos) {
		this.rbsPos = rbsPos;
	}

	public void setDC(String dc_name) {
		prev_dc = dc;
		dc = dc_name;
	}
	
	public void updateDC(String dc_name) {
		prev_dc = dc;
		dc = dc_name;
		migrate = true;
	}
}
