package service;

import org.apache.commons.math3.analysis.function.Gaussian;
import org.apache.commons.math3.analysis.function.Inverse;
import org.apache.commons.math3.distribution.ExponentialDistribution;
import org.apache.commons.math3.distribution.LogNormalDistribution;
import org.apache.commons.math3.distribution.PoissonDistribution;

import distributions.My_IverseGaussian;
import magnitudes.*;

public class Service_WEB_2001 extends Service {

	private PoissonDistribution interSession;
	private My_IverseGaussian nbrClicks;
	private LogNormalDistribution interClick;
	
	public Service_WEB_2001() {
		interSession = new PoissonDistribution(0.01);
		nbrClicks = new My_IverseGaussian(5,3);
		interClick = new LogNormalDistribution(3, 1.1);
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Time getMeanInterSessionTime() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Time getMeanArrivalRate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Time getMeanSessionTime() {
		// TODO Auto-generated method stub
		return null;
	}

}
