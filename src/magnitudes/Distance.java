package magnitudes;

import java.util.ArrayList;

public abstract class Distance {
	
	public abstract double tom();
	
	public abstract double tokm();
	
	public static Distance calculateTraveledDistance(Time to, Speed init, ArrayList<AccelerationVsTimeEvent> acc_vs_time){
		double distance = 0;
		double speed = init.tomPerMin();
		double acceleration = 0;
		double t_now = 0;
		double t_prev = 0;
		int event_index = 0;
		
		AccelerationVsTimeEvent current = acc_vs_time.get(event_index);
		
		while(t_now <= to.toMin() && event_index+1 < acc_vs_time.size()){
			
			if(acc_vs_time.get(event_index+1).getTime().toMin() > to.toMin()){
				t_now = to.toMin();
			} else {
				t_now = acc_vs_time.get(event_index+1).getTime().toMin();
			}
			t_prev = acc_vs_time.get(event_index).getTime().toMin();
			
			acceleration = current.getAcceleration().tomPerMin2();
		
			distance += (speed + (acceleration*(t_now-t_prev))/2) * (t_now-t_prev); 
			speed += acceleration*(t_now-t_prev);
			
			event_index ++;
		}
		
		if(t_now <= to.toMin()) {
			t_prev = t_now;
			t_now = to.toMin();
			
			distance += (speed + (acceleration*(t_now-t_prev))/2) * (t_now-t_prev); 
			speed += acceleration*(t_now-t_prev);
		}
		
		return new Distance_m(distance);
	}
}
