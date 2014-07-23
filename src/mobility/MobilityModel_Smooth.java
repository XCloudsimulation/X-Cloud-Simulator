package mobility;

import java.util.ArrayList;
import java.util.Random;

import org.apache.commons.math3.distribution.ExponentialDistribution;

import eduni.simjava.Sim_event;
import eduni.simjava.Sim_system;
import mobile_entities.UserEquipment;

public class MobilityModel_Smooth extends MobilityModel {
	
	public MobilityModel_Smooth(String name,
			UserEquipment[] mobileEnteties) {
		super(name, mobileEnteties);
		
		int type = 0;
		int count = 0;
		int mode_factor = mobileEnteties.length/MobilityModes.values().length;
	}

	@Override
	public void body(){
		Sim_event e; 
		
		while(Sim_system.running()){
			e = new Sim_event();
			sim_get_next(e);
			
			for(UserEquipment ue: mobileEnteties){
				ue.updateLocation(Sim_system.sim_clock());
			}
			
			sim_completed(e);
		}
	}

	@Override
	protected void UpdateLocation() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void ResetModel() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void UpdateProgress() {
		// TODO Auto-generated method stub
		
	}
}