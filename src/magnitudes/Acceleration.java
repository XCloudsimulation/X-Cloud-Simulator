package magnitudes;

public class Acceleration  {

	private Speed speed;
	private Time time;
	
	public Acceleration(Speed speed, Time time){
		this.speed = speed;
		this.time = time;
	}
	
	public double tomPerMin2() {
		return speed.tomPerMin()/time.toMin();
	}

}
