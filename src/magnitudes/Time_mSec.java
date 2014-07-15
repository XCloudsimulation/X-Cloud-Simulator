package magnitudes;

public class Time_mSec extends Time {

	public Time_mSec(double time) {
		super(time);
	}

	public double toMin() {
		return toSec()/_60;
	}

	public double toHour() {
		return (toSec()/_60)/_60;
	}

	public double toSec() {
		return time/_1000;
	}

	public double tomSec() {
		return time;
	}
}
