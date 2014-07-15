package magnitudes;

public class Distance_m extends Distance {
	
	private double distance;
	
	public Distance_m(double distance){
		this.distance = distance;
	}
	
	public double tom() {
		return distance;
	}

	public double tokm() {
		return distance/1000.0;
	}
}
