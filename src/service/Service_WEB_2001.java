package service;

import org.apache.commons.math3.distribution.LogNormalDistribution;
import org.apache.commons.math3.distribution.PoissonDistribution;

import distributions.My_IverseGaussian;
import magnitudes.*;

public class Service_WEB_2001 extends Service {

	private PoissonDistribution interSession;
	private My_IverseGaussian nbrClicks;
	private LogNormalDistribution interClick;
	private int service;
	
	public Service_WEB_2001(int service) {
		interSession = new PoissonDistribution(0.01);
		nbrClicks = new My_IverseGaussian(5,3);
		interClick = new LogNormalDistribution(3, 1.1);
		
		this.service = service;
	}

	@Override
	public int getSessionSize() {
		return (int) nbrClicks.sample();
	}

	@Override
	public Time getInterRequestTime() {
		return new Time_Sec(interClick.sample());
	}

	@Override
	public Time getInterSessionTime() {
		return new Time_Sec(interSession.sample());
	}

	@Override
	public Time getMeanInterRequestTime() {
		return null;
	}

	@Override
	public Time getMeanInterSessionTime() {
		return null;
	}

	@Override
	public Time getMeanArrivalRate() {
		double nbr_requests = nbrClicks.mean();
		double inter_packet = interClick.getNumericalMean();
		double inter_sesison = interSession.getNumericalMean();
		
		return new Time_Sec(1/(nbr_requests/(nbr_requests*inter_packet+inter_sesison)));
	}

	@Override
	public Time getMeanSessionTime() {
		return null;
	}

	@Override
	public int getServiceNbr() {
		return service;
	}

}
