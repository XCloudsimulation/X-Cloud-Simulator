package mobility;

import java.util.Random;

import org.apache.commons.math3.distribution.ExponentialDistribution;
import org.apache.commons.math3.distribution.PoissonDistribution;

public abstract class ModeModel{
		protected Random uniform;
		protected ExponentialDistribution direction_event;
		protected PoissonDistribution speed_event;
		
		protected double[] speeds;
		
		protected double turn_time_min, turn_time_max, v_max, a_min, a_max, current_speed, target_speed, current_direction, target_direction, acceleration, dom_x, dom_y, turn_time, turn_delta, next_speed_event, next_direction_event;
		
		double prev_time;
		
		private Location location;
		
		public ModeModel(double dom_x, double dom_y){
			uniform = new Random();
			
			this.dom_x = dom_x;
			this.dom_y = dom_y;
			
			location = new Location(uniform.nextDouble()*dom_x, uniform.nextDouble()*dom_y);
			prev_time = 0;
		}
		
		public Location getLocation(){
			return location;
		}
		
		public void setLocation(Location location){
			this.location = location;
		}
		
		public void update(double time){
			
			if (next_speed_event <= time){
				getNewSpeed(time);
			}
			
			if (next_direction_event <= time){
				getNewDirection(time);
			}
			
			move(time-prev_time);
			
			prev_time = time;
			
			//System.out.println("Moved to: x=" + location.x + ", y=" + location.y + ". Speed="+current_speed+"/"+target_speed+", direction="+current_direction+"/"+target_direction);
		}
		
		private void getNewDirection(double time) {
			target_direction = current_direction + uniform.nextDouble()*Math.PI;
			turn_time = turn_time_min + uniform.nextDouble()*(turn_time_max - turn_time_min);
			next_direction_event = time + direction_event.sample();
		}

		private void getNewSpeed(double time) {
			target_speed = speeds[uniform.nextInt(speeds.length-1)];
			acceleration = target_speed<current_speed ? uniform.nextDouble()*a_min : uniform.nextDouble()*a_max;
			next_speed_event = time + speed_event.sample();
		}

		private void handleEdge() {
			//System.out.println("BUMP - Hit the edge, turning around. x="+location.x+", y="+location.y+", direction="+current_direction);
			
			getNewDirection(prev_time);
			move(10);
			
			//System.out.println("\t New paratemers: x="+location.x+", y="+location.y+", direction="+current_direction);
			
		}

		private void move(double delta_time) {
			double new_x, new_y;
			turn(delta_time);
			
			current_speed += acceleration*delta_time;
			if((acceleration > 0 && current_speed >= target_speed) || (acceleration < 0 && current_speed <= target_speed)){
				acceleration = 0;
			}
			
			if(current_speed <0){
				current_speed =0;
			}
			
			new_x = location.x + Math.cos(current_direction)*current_speed;
			new_y = location.y + Math.sin(current_direction)*current_speed;
			
			if((new_x >= dom_x || new_x <= 0) || (new_y >= dom_y || new_y <= 0)){
				handleEdge();
				return;
			}
			
			location.x = new_x;
			location.y = new_y;
		}

		private void turn(double delta_time) {
			turn_delta = target_direction-current_direction;
			
			current_direction += (delta_time/turn_time)*turn_delta;
		}
		
		private void turnAround(){
			current_direction += Math.PI*(1+uniform.nextDouble()*0.5);
		}
		
		protected void assignMovement(){
			location.y = uniform.nextDouble()*dom_y;
			location.x = uniform.nextDouble()*dom_x;

			
			current_speed = speeds[uniform.nextInt(speeds.length-1)];
			current_direction = uniform.nextDouble()*2*Math.PI;
			
			getNewSpeed(0);
			getNewDirection(0);
		}
	}