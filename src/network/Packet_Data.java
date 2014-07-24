package network;

import mobility.Location;

public class Packet_Data extends Packet {

	public Packet_Data(int service, int user, int session, int session_size, int packet, Location location) {
		super(service, user, session, session_size, packet, location);
	}

}
