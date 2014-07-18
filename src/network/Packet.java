package network;

import java.util.ArrayList;

import measurment.*;

public abstract class Packet {
	protected int service, user, session, session_size, packet;
	protected ArrayList<LatencyMeasurement> latencyMeasurements;

	public Packet(int service, int user, int session, int session_size, int packet){
		this.service = service;
		this.user = user;
		this.session = session;
		this.session_size = session_size;
		this.packet = packet;
		
		latencyMeasurements = new ArrayList<LatencyMeasurement>();
	}
	
	public void AddLatencyMeasurement(LatencyMeasurement meas){
		latencyMeasurements.add(meas);
	}
	
	public String DumpLatencyMeasurements(){
		String[] line = new String[6+LatencyDepthIndex.NbrDepths()];
		
		for(){}
		
		return ;
	}
}