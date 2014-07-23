package network;

import java.util.ArrayList;

import measurment.*;

public abstract class Packet {
	public int service, user, session, session_size, packet, migrated;
	protected ArrayList<LatencyMeasurement> latencyMeasurements;

	public Packet(int service, int user, int session, int session_size, int packet){
		this.service = service;
		this.user = user;
		this.session = session;
		this.session_size = session_size;
		this.packet = packet;
		migrated = 0;
		
		latencyMeasurements = new ArrayList<LatencyMeasurement>();
	}
	
	public void AddLatencyMeasurement(LatencyMeasurement meas){
		latencyMeasurements.add(meas);
	}
	
	public void Migrated(){
		migrated ++;
	}
	
	public String DumpLatencyMeasurements(){
		String[] lines = new String[PacketMeasIndex.NbrDepths()];
		
		double[] values = new double[PacketMeasIndex.NbrDepths()];
		
		for(LatencyMeasurement lm: latencyMeasurements){
			values[lm.segment.toInt()] += lm.value;
		}
		
		for(PacketMeasIndex index : PacketMeasIndex.values()){
			lines[index.toInt()] = "" + values[index.toInt()];
		}
		
		lines[PacketMeasIndex.SERVICE.toInt()] 		= "" + service;
		lines[PacketMeasIndex.USER.toInt()] 		= "" + user;
		lines[PacketMeasIndex.SESSION.toInt()] 		= "" + session;
		lines[PacketMeasIndex.SESSION_SIZE.toInt()] = "" + session_size;
		lines[PacketMeasIndex.PACKET_NBR.toInt()] 	= "" + packet;
		lines[PacketMeasIndex.MIGRATED.toInt()] 	= "" + migrated;
		
		StringBuilder sb = new StringBuilder();
		
		for (String value : lines){
			sb.append(value + ";");
		}
		
		sb.append("\r");
		
		return sb.toString();
	}
}