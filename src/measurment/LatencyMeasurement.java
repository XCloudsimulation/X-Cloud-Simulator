package measurment;

public class LatencyMeasurement {
	public PacketMeasIndex segment;
	public double value;
	
	public LatencyMeasurement(PacketMeasIndex segment, double value){
		this.segment = segment;
		this.value = value;
	}
}
