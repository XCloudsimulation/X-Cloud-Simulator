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
		String[] line = new String[PacketMeasIndex.NbrDepths()];
		
		for(LatencyMeasurement lm: latencyMeasurements){
			line[lm.segment.toInt()] = "" + lm.value;
		}
		
		line[PacketMeasIndex.SERVICE.toInt()] 		= "" + service;
		line[PacketMeasIndex.USER.toInt()] 			= "" + user;
		line[PacketMeasIndex.SESSION.toInt()] 		= "" + session;
		line[PacketMeasIndex.SESSION_SIZE.toInt()] 	= "" + session_size;
		line[PacketMeasIndex.PACKET_NBR.toInt()] 	= "" + packet;
		line[PacketMeasIndex.MIGRATED.toInt()] 		= "" + migrated;
		
		StringBuilder sb = new StringBuilder();
		
		for (String value : line){
			sb.append(value + ";");
		}
		
		sb.append("\r");
		
		return sb.toString();
	}
}