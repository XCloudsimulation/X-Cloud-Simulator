package magnitudes;

public class Time_Min extends Time {
	
	public Time_Min(double time){
		super(time);
	}
	
	@Override
	public double toMin() {
		return time;
	}

	@Override
	public double toHour() {
		return time/_60;
	}

	@Override
	public double toSec() {
		return toMin()*_60;
	}

	@Override
	public double tomSec() {
		return toSec()*_1000;
	}
}
