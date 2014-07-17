package network;

public class Packet_Migrate extends Packet {
	private String dest;
	
	public Packet_Migrate(int service, int user, String dest) {
		super(service, user);
		this.dest = dest;
	}

	public String getDest() {
		return dest;
	}

	public int getUser() {
		return user;
	}
}
