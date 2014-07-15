package framework;

import magnitudes.Time;
import eduni.simjava.Sim_entity;
import eduni.simjava.Sim_port;

/*
 * Produces qlock pulses with an interval of "period"
 */
public abstract class Clock extends Sim_entity{
	
	public static final String OUT_PORT_NAME = "OUT";
	
	public static final int CLOCK_EVENT = 1;
	public static final int CLOCK_EVENT_2 = 2;
	
	protected Sim_port out_port;
	protected double period;
	protected int pulse_ID;
	
	public Clock(String name, Time time, int pulse_ID) {
		super(name);
		
		out_port = new Sim_port(OUT_PORT_NAME);
		add_port(out_port);
		
		this.period = time.toMin();
		
		this.pulse_ID = pulse_ID;
		
		System.out.println(get_name() + ": Frequency set to " + 1.0/time.toSec() + " Hz, once per " + time.tomSec() + " milliseconds.");
	}
	
	@Override
	public abstract void body();
}
