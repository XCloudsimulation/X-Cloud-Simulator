package magnitudes;

import java.util.ArrayList;

public abstract class Time {

	protected static final double _60 = 60.0;
	protected static final double _1000 = 1000.0;
	
	protected double time;
	
	public Time(double time){
		this.time = time;
	}
	
	public abstract double toMin();

	public abstract double toHour();

	public abstract double toSec();
	
	public abstract double tomSec();
	
	
	public static Time calculateTimeTo(Distance to, Speed init, ArrayList<AccelerationVsTimeEvent> acc_vs_time){
		double s = 0;
		double v = init.tomPerMin();
		double a = 0;
		double t_now = 0;
		double t_prev = 0;
		int event_index = 0;
		
		AccelerationVsTimeEvent current = acc_vs_time.get(event_index);
		
		while(s <= to.tom() && event_index+1 < acc_vs_time.size()){
			t_now = acc_vs_time.get(event_index+1).getTime().toMin();
			t_prev = acc_vs_time.get(event_index).getTime().toMin();
			
			a = current.getAcceleration().tomPerMin2();
		
			s += (v + (a*(t_now-t_prev))/2) * (t_now-t_prev); 
			v += a*(t_now-t_prev);
			
			event_index ++;
		}

		if(s <= to.tom()) {
			if(a!=0)
				t_now += (Math.sqrt(2.0*a*(to.tom()-s)+Math.pow(v, 2))-v)/a; 
			else 
				t_now += s/v;
		}
		return new Time_Min(t_now);
	}
	
}
