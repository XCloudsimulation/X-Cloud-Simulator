package measurment;

public class LatencyMeasurement {
	public LatencyDepthIndex segment;
	public double value;
	
	public LatencyMeasurement(LatencyDepthIndex segment, double value){
		this.segment = segment;
		this.value = value;
	}
}
