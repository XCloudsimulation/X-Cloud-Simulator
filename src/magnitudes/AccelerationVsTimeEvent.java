package magnitudes;

public class AccelerationVsTimeEvent  {

	private Acceleration acceleration;
	private Time time;
	
	public AccelerationVsTimeEvent(Acceleration acceleration, Time time){
		this.acceleration = acceleration;
		this.time = time;
	}
	
	public AccelerationVsTimeEvent(double acceleration, double time){
		this.acceleration = new Acceleration(new Speed(new Distance_m(0), new Time_Min(1.0)), new Time_Min(1.0));
		this.time = new Time_Min(time);
	}
	
	public AccelerationVsTimeEvent(double acceleration, Time time){
		this.acceleration = new Acceleration(new Speed(new Distance_m(0), new Time_Min(1.0)), new Time_Min(1.0));
		this.time = time;
	}
	
	public AccelerationVsTimeEvent(Acceleration acceleration, double time){
		this.acceleration = acceleration;
		this.time = new Time_Min(time);
	}
	
	public Acceleration getAcceleration(){
		return acceleration;
	}

	public Time getTime() {
		return time;
	}
}
