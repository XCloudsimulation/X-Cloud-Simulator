import java.util.Random;

import network.Packet_Data;
import network.Packet_Migrate;
import eduni.simjava.Sim_entity;
import eduni.simjava.Sim_port;
import eduni.simjava.Sim_system;

public class User extends Sim_entity{
	public static final String OUT_PORT_NAME = "OUT_PORT_NAME";
	
	private Sim_port out_port;

	private Random rnd;
	
	public User(String name) {
		super(name);
		
		out_port = new Sim_port(OUT_PORT_NAME);
		add_port(out_port);
		
		rnd = new Random();
	}

	@Override
	public void body(){
		int cnt = 0;
		int service = 0;
		
		while(Sim_system.running()){
			sim_pause(rnd.nextDouble()*5);
			
			if (cnt==10){
				sim_schedule(out_port,0.0,service,new Packet_Migrate(service,get_id(),"DC1"));
			} else {
				sim_schedule(out_port,0.0,service,new Packet_Data(service,get_id(),0,cnt));
				service = rnd.nextInt(3);
			}
			cnt ++;
		}
	}
}
