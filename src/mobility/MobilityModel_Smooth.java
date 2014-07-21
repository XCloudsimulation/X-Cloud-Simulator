package mobility;

import java.util.ArrayList;
import java.util.Random;

import org.apache.commons.math3.distribution.ExponentialDistribution;

import eduni.simjava.Sim_event;
import eduni.simjava.Sim_system;
import mobile_entities.UserEquipment;

public class MobilityModel_Smooth extends MobilityModel {

	// Global
	private Random uni_dist;
	
	// Car - Urban
	private ExponentialDistribution exp_dist_car;
	
	
	public MobilityModel_Smooth(String name,
			UserEquipment[] mobileEnteties) {
		super(name, mobileEnteties);

		uni_dist = new Random();
		
		// Distributions
		//exp_dist = new ExponentialDistribution();
		
		int type = 0;
		int count = 0;
		int mode_factor = mobileEnteties.length/MobilityMode_Smooth.values().length;
		for(UserEquipment ue: mobileEnteties){
			type = (int) Math.floor(count/mode_factor);
			ue.setMobilityState(new MobilityState_Smooth(MobilityMode_Smooth.fromInt(type)));
		}
		UpdateLocation();
	}

	@Override
	public void body(){
		Sim_event e; 
		
		while(Sim_system.running()){
			e = new Sim_event();
			sim_get_next(e);
			
			sim_completed(e);
		}
	}
	
	@Override
	protected void UpdateLocation() {
		for(UserEquipment ue: mobileEnteties){
			switch(((MobilityState_Smooth)ue.getMobilityState()).getMode()){
				case BIKE: 
					
					break;
				case CAR: break;
				case PEDESTRIAN: break;
				default: System.err.println("Uknown mode");
			}
		}
	}

	@Override
	protected void ResetModel() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void UpdateProgress() {
		
	}
	
}
