package magnitudes;

public class Time_Hour extends Time {
	
	public Time_Hour(Double time){
		super(time);
	}
	
	@Override
	public double toMin() {
		return time*_60;
	}

	@Override
	public double toHour() {
		return time;
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
