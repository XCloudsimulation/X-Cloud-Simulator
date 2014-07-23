package network;

public class OccupancyMeasurement {
	private int nbr_occupants;
	private double time;
	
	public OccupancyMeasurement(int nbr_occupants, double time){
		this.setTime(time);
		this.setNbr_occupants(nbr_occupants);
	}

	public int getNbr_occupants() {
		return nbr_occupants;
	}

	public void setNbr_occupants(int nbr_occupants) {
		this.nbr_occupants = nbr_occupants;
	}

	public double getTime() {
		return time;
	}

	public void setTime(double time) {
		this.time = time;
	}
}
