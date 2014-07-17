package magnitudes;

public class Time_Sec extends Time {

	public Time_Sec(double time){
		super(time);
	}
	
	@Override
	public double toMin() {
		return time/_60;
	}

	@Override
	public double toHour() {
		return toMin()/_60;
	}

	@Override
	public double toSec() {
		return time;
	}

	@Override
	public double tomSec() {
		return time*_1000;
	}

}
