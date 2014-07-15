package network;

import java.util.ArrayList;

import measurment.*;

public abstract class Packet {
	protected int service;
	protected ArrayList<LatencyMeasurement> latencyMeasurements;
	public int user;
	
	public Packet(int service, int user){
		this.service = service;
		this.user = user;
		latencyMeasurements = new ArrayList<LatencyMeasurement>();
	}
	
	public void AddLatencyMeasurement(LatencyMeasurement meas){
		latencyMeasurements.add(meas);
	}
	
	public String DumpLatencyMeasurements(){
		return null;
	}
}