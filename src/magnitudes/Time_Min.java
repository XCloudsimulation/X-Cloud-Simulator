package magnitudes;

public class Time_Min extends Time {
	
	public Time_Min(double time){
		super(time);
	}
	
	public double toMin() {
		return time;
	}

	public double toHour() {
		return time/_60;
	}

	public double toSec() {
		return toMin()*_60;
	}

	public double tomSec() {
		return toSec()*_1000;
	}
}
