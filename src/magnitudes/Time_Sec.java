package magnitudes;

public class Time_Sec extends Time {

	public Time_Sec(double time){
		super(time);
	}
	
	public double toMin() {
		return time/_60;
	}

	public double toHour() {
		return toMin()/_60;
	}

	public double toSec() {
		return time;
	}

	public double tomSec() {
		return time*_1000;
	}

}
