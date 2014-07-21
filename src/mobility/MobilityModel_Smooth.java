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
		for(UserEquipment ue: mobileEnteties){
			type = (int) Math.floor(count/mode_factor);
			//ue.setMobilityState(new MobilityState(MobilityModes.fromInt(type)));
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
			ue.updateLocation();
		}
	}

	@Override
	protected void ResetModel() {
		// TODO Auto-generated method stub
	}

	@Override
	protected void UpdateProgress() {
		
	}
	
	private abstract class ModeModel{
		protected Random uniform;
		protected ExponentialDistribution direction_dist, speed_dist;
		
		protected int v_max, a_min, a_max;
		protected MobilityState state;
		protected double current_speed, target_speed, current_direction, target_direction, acceleration, dom_x, dom_y, remaining_turn_time, next_speed_event, next_direction_event;
		
		
		private Location location;
		
		public ModeModel(double dom_x, double dom_y){
			uniform = new Random();
			state = MobilityState.MOBILE;
			
			this.dom_x = dom_x;
			this.dom_y = dom_y;
			
			location = new Location(uniform.nextDouble()*dom_x, uniform.nextDouble()*dom_y);
		}
		
		public void update(double delta_time){
			
		}
		
		private void getNewDirection() {
			target_direction = direction_dist.sample();
			remaining_turn_time = 2.0 + uniform.nextDouble()*8.0;
		}

		private void getNewSpeed() {
			target_speed = speed_dist.sample();
			acceleration = target_speed<current_speed ? uniform.nextDouble()*a_min : uniform.nextDouble()*a_max;
		}

		private void handleEdge() {
			
		}

		private void move(double delta_time) {
			double new_x, new_y;
			turn();
			
			current_speed += acceleration*delta_time;
			if(current_speed == target_speed){
				acceleration = 0;
			}
			
		}

		private void turn() {
			
		}
		
		protected void assignMovement(){
			
		}
	}
	
	private class ModeModel_Car extends ModeModel{

		public ModeModel_Car(double dom_x, double dom_y){
			super(dom_x, dom_y);
			
			direction_dist 	= new ExponentialDistribution(25);
			speed_dist 		= new ExponentialDistribution(0);
			
			assignMovement();
		}
	}
}