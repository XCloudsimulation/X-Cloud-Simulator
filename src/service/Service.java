package service;

import eduni.simjava.Sim_entity;
import magnitudes.*;

public abstract class Service{

	public abstract int getSessionSize();
	
	public abstract Time getInterRequestTime();
	
	public abstract Time getInterSessionTime();
	
	public abstract Time getMeanInterRequestTime();
	
	public abstract Time getMeanInterSessionTime();

	public abstract Time getMeanArrivalRate();
	
	public abstract Time getMeanSessionTime();
}
