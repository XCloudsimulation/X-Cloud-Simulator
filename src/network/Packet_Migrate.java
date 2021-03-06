package network;

import mobility.Location;

public class Packet_Migrate extends Packet {
	private String dest;
	
	public Packet_Migrate(int service, int user, int session, int session_size, int packet, String dest, Location location) {
		super(service, user, session, session_size, packet, location);
		this.dest = dest;
	}

	public String getDest() {
		return dest;
	}

	public int getUser() {
		return user;
	}
}
