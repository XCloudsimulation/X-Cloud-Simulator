package magnitudes;

public class Time_mSec extends Time {

	public Time_mSec(double time) {
		super(time);
	}

	@Override
	public double toMin() {
		return toSec()/_60;
	}

	@Override
	public double toHour() {
		return (toSec()/_60)/_60;
	}

	@Override
	public double toSec() {
		return time/_1000;
	}

	@Override
	public double tomSec() {
		return time;
	}
}
