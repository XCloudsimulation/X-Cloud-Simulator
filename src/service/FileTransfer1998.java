package service;

import magnitudes.*;

import org.apache.commons.math3.distribution.WeibullDistribution;

import service.Service;
import jsc.distributions.Pareto;

public class FileTransfer1998 extends Service {
	
	private Pareto requestSize,fileSize, offTime_pareto; // Shape = alpha, Location = k
	private WeibullDistribution offTime;
	
	private int service_nbr;

	public FileTransfer1998(int service_nbr) {
		requestSize = new Pareto(1000.0, 1.0);
		fileSize = new Pareto(133000.0, 1.1); // Tail
		//fileSize = new LogNormalDistribution(9.357, 1.318);
		offTime = new WeibullDistribution(1.46, 0.382);
		offTime_pareto = new Pareto(1, 1.5); 
		
		this.service_nbr = service_nbr;
	}

	public synchronized int getSessionSize() {
		return (int) (fileSize.random()/requestSize.random());
	}

	public synchronized Time getInterRequestTime() {
		return new Time_Sec(offTime.sample());
	}

	public synchronized Time getInterSessionTime() {
		return new Time_Sec(offTime_pareto.random());
	}
	
	@Override
	public synchronized Time getMeanArrivalRate(){
		int p = (int)getMeanSessionSize();
		double irr = p*getMeanInterRequestTime().toSec();
		double isr = getMeanInterSessionTime().toSec();
		
		return new Time_Sec(p/(irr+isr));
	}

	public synchronized Time getMeanInterRequestTime() {
		return new Time_Sec(offTime.getNumericalMean());
	}

	public synchronized Time getMeanInterSessionTime() {
		return new Time_Sec(offTime_pareto.mean());
	}
	
	private synchronized double getMeanFileSize(){
		return fileSize.mean();
	}
	
	private synchronized double getMeanRequetSize(){
		double result = 0;
		for(int i=0; i < 50000; i++)
		{
			result += requestSize.random()/50000.0;
		}
		return result;
	}
	
	private synchronized double getMeanSessionSize(){
		return getMeanFileSize()/getMeanRequetSize();
	}

	public synchronized Time getMeanSessionTime() {
		return new Time_Sec(0.7);
	}

	@Override
	public int getServiceNbr() {
		// TODO Auto-generated method stub
		return 0;
	}
}
