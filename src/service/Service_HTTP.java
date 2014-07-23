package service;

import org.apache.commons.math3.distribution.ExponentialDistribution;
import org.apache.commons.math3.distribution.LogNormalDistribution;
import org.apache.commons.math3.distribution.WeibullDistribution;

import magnitudes.*;

/**
 * @author  William T?rneberg
 * @version 0.1
 * @since   2014-05-19 
 * 
 * Sources: Traffic model
 * 			Title: An HTTP Web Traffic Model Based on the Top One Million Visited Web Pages
 * 			Link: http://ieeexplore.ieee.org/stamp/stamp.jsp?arnumber=06252145
 * 
 * 			User model (On/Off process)
 * 			Title: A New Traffic Model for Current User Web Browsing Behavior
 * 			Link: http://blogs.intel.com/wp-content/mt-content/com/research/HTTP%20Traffic%20Model_v1%201%20white%20paper.pdf
 * 
 * Summary: The model is based on the traffic properties of the worlds top 1 million web pages.
 * 
 * Parameters: 		
 * 			Main object size				Weibull 	alpha = 28242.8,	beta = 0.814944
 * 			Number of main objects			Lognormal	u = 0.473844, 		a = 0.688471
 * 			Inline object size				Lognormal	u = 9.17979,		a = 1.24646
 * 			Number of inline objects		Exponential	u = 31.9291
 * 			Reading time					Lognormal	u = -0.495204, 		a = 2.7731
 */

public class Service_HTTP extends Service {

	private static final int TCP_PACKET_SIZE = 65535;
	
	private WeibullDistribution main_obje,main_object_size;
	private LogNormalDistribution nbr_main_objetcs, inline_object_size, reading_time;
	private ExponentialDistribution nbr_inline_objects;
	
	public Service_HTTP() {
		
		// Initialize distributions
		main_object_size = new WeibullDistribution(28242.8, 0.814944);
		nbr_main_objetcs = new LogNormalDistribution(0.473844, 0.688471);
		
		inline_object_size = new LogNormalDistribution(9.17979, 1.24646);
		nbr_inline_objects = new ExponentialDistribution(31.9291);
		
		reading_time = new LogNormalDistribution(-0.495204, 2.7731);
		
	}

	@Override
	public int getSessionSize() {
		double result = 0;
		
		result += nbr_main_objetcs.sample()*main_object_size.sample()/TCP_PACKET_SIZE;
		result += nbr_inline_objects.sample()*inline_object_size.sample()/TCP_PACKET_SIZE;
		
		return (int) Math.ceil(result);
	}

	@Override
	public Time getInterRequestTime() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Time getInterSessionTime() {
		return new Time_Sec(nbr_inline_objects.sample());
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

	@Override
	public int getServiceNbr() {
		// TODO Auto-generated method stub
		return 0;
	}

}
