package mobility;

import org.apache.commons.math3.distribution.ExponentialDistribution;
import org.apache.commons.math3.distribution.PoissonDistribution;

public class ModeModel_Car extends ModeModel{

	public ModeModel_Car(double dom_x, double dom_y){
		super(dom_x, dom_y);
		
		direction_event = new ExponentialDistribution(25);
		speed_event 	= new PoissonDistribution(25);
		
		v_max = 13.9;
		a_min = -4;
		a_max = 2.5;
		turn_time_min = 2;
		turn_time_max = 10;
		
		speeds = new double[3];
		speeds[0] = 0;
		speeds[1] = 3*v_max/5;
		speeds[2] = v_max;
		
		assignMovement();
	}
}
