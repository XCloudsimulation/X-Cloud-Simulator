package magnitudes;

public class Time_Hour extends Time {
	
	public Time_Hour(Double time){
		super(time);
	}
	
	public double toMin() {
		return time*_60;
	}

	public double toHour() {
		return time;
	}

	public double toSec() {
		return toMin()*_60;
	}

	public double tomSec() {
		return toSec()*_1000;
	}
}
