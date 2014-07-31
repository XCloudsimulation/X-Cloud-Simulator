package mobility;

import org.apache.commons.math3.distribution.ExponentialDistribution;
import org.apache.commons.math3.distribution.PoissonDistribution;

public class ModeModel_Pedestrian extends ModeModel{

	public ModeModel_Pedestrian(double dom_x, double dom_y){
		super(dom_x, dom_y);
		
		direction_event = new ExponentialDistribution(25);
		speed_event 	= new PoissonDistribution(25);
		
		v_max = 1.8;
		a_min = -1.38/3;
		a_max = 1.38/4;
		turn_time_min = 0.1;
		turn_time_max = 3;
		
		speeds = new double[3];
		speeds[0] = 0;
		speeds[1] = 1.11;
		speeds[2] = 1.38;
		
		assignMovement();
	}
}
