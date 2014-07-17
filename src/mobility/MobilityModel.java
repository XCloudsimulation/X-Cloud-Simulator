package mobility;

import java.util.ArrayList;

import mobile_entities.UserEquipment;

import org.w3c.dom.UserDataHandler;

import eduni.simjava.Sim_entity;
import eduni.simjava.Sim_event;
import eduni.simjava.Sim_port;
import eduni.simjava.Sim_system;
import framework.*;

public abstract class MobilityModel extends Sim_entity{

	public static final String IN_PORT_NAME = "IN";

	protected UserEquipment[] mobileEnteties;

	protected double t_prev;
	protected double t_now;

	private Sim_port in_port;

	public MobilityModel(String name, UserEquipment[] mobileEnteties) {
		super(name);
		this.mobileEnteties = mobileEnteties;
		in_port = new Sim_port(IN_PORT_NAME);
		add_port(in_port);

		sim_trace(1, get_name() + " - Created - With " + mobileEnteties.length + " mobile entities of type " + mobileEnteties.getClass() + "\t Type: " + this.getClass());
	}

	@Override
	public void body(){
		ResetModel();

		while(Sim_system.running()){
			Sim_event e = new Sim_event();
			sim_get_next(e);	// Get the next event
			
			switch(e.get_tag()){
				case Clock.CLOCK_EVENT:
					t_now= e.event_time();
						
					//double t_1 = Sim_system.sim_clock();
					UpdateLocation();
					//System.out.println("Updating location took: " + (Sim_system.sim_clock()-t_1) + " " + sim_waiting());
					
					sim_completed(e);
					t_prev = t_now;
					break;
				case Clock.CLOCK_EVENT_2:
					UpdateProgress();
					break;
				default:
					System.err.println(get_name() + " - Undefined clock pulse ID: " + e.get_tag() + "\r");
					break;
			}
		}
	}

	protected abstract void UpdateLocation();

	protected abstract void ResetModel();

	protected abstract void UpdateProgress();

}
