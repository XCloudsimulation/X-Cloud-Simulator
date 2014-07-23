package distributions;

import java.util.Random;

public class My_IverseGaussian{
	private double mu, lambda;
	
	public My_IverseGaussian(double mu, double lambda){
		this.lambda = lambda;
		this.mu = mu;
	}
	
	public double sample() {
	       Random rand = new Random();
	       double v = rand.nextGaussian();   // sample from a normal distribution with a mean of 0 and 1 standard deviation
	       double y = v*v;
	       double x = mu + (mu*mu*y)/(2*lambda) - (mu/(2*lambda)) * Math.sqrt(4*mu*lambda*y + mu*mu*y*y);
	       double test = rand.nextDouble();  // sample from a uniform distribution between 0 and 1
	       if (test <= (mu)/(mu + x))
	              return x;
	       else
	              return (mu*mu)/x;
	}

	public double mean(){
		return mu;
	}
	
}
