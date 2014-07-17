package magnitudes;

public class Distance_km extends Distance {

	private double distance;
	
	public Distance_km(double distance){
		this.distance = distance;
	}
	
	@Override
	public double tom() {
		return distance*1000.0;
	}

	@Override
	public double tokm() {
		return distance;
	}

}
