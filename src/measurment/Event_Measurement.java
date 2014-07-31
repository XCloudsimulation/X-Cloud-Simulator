package measurment;

public class Event_Measurement implements Measurement {

	public String event; 
	public double time;
	
	public Event_Measurement(String event, double time){
		this.event = event;
		this.time = time;
	}
	
	public String toString(){
		return event + ";" + time;
	}
}
