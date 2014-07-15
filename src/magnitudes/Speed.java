package magnitudes;

public class Speed{

	private Distance distane;
	private Time time;
	
	public Speed(Distance distance, Time time){
		this.distane = distance;
		this.time = time;
	}
	
	public double tomPerMin() {
		return distane.tom()/time.toMin();
	}
	
	public double tokmPerHour(){
		return distane.tokm()/time.toHour();
	}

}
