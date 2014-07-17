package network;

public class Packet_Data extends Packet {
	public int session, number;
	
	public Packet_Data(int service, int user, int session, int number) {
		super(service, user);
		
		this.session = session;
		this.number = number;
	}

}
