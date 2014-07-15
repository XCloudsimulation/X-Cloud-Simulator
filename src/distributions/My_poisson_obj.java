/* My_poisson_obj.java */

package distributions;

import eduni.simjava.distributions.DiscreteGenerator;
import eduni.simjava.distributions.Sim_parameter_exception;
import eduni.simjava.distributions.Sim_random_obj;

/**
 * A random number generator based on the Geometric distribution.
 * @version     1.0, 26 April 2014
 * @author      William T??rneberg
 */
public class My_poisson_obj implements DiscreteGenerator {
	private Sim_random_obj source;
	private double lambda;
	private String name;

	/**
	 * Constructor with which <code>Sim_system</code> is allowed to set the random number
	 * generator's seed
	 * @param name The name to be associated with this instance
	 * @param lambda The mean of the distribution
	 */
	public My_poisson_obj(String name, double lambda) {
		if (lambda <= 0.0) {
			throw new Sim_parameter_exception("Sim_poisson_obj: The mean must be greater than 0.");
		}
		source = new Sim_random_obj("Internal PRNG");
		this.lambda = lambda;
		this.name = name;
	}

	/**
	 * The constructor with which a specific seed is set for the random
	 * number generator
	 * @param name The name to be associated with this instance
	 * @param mean The mean of the distribution
	 * @param seed The initial seed for the generator, two instances with
	 *             the same seed will generate the same sequence of numbers
	 */
	public My_poisson_obj(String name, double lambda, long seed) {
		if (lambda <= 0.0) {
			throw new Sim_parameter_exception("Sim_poisson_obj: The mean must be greater than 0.");
		}
		source = new Sim_random_obj("Internal PRNG", seed);
		this.lambda = lambda;
		this.name = name;
	}

	/**
	 * Generate a new random number.
	 * @return The next random number in the sequence
	 */
	@Override
	public long sample() {
		double L = Math.exp(-lambda);
		double p = 1.0;
		int k = 0;

		do {
			k++;
			p *= Math.random();
		} while (p > L);

		return k - 1;
	}

	// Used by other distributions that rely on the Poisson distribution
	static long sample(Sim_random_obj source, double mean) {
		long x = -1L;
		double m = Math.exp(mean);
		do {
			x++;
			m *= source.sample();
		} while(m > 1.0);
		return x;
	}

	/**
	 * Set the random number generator's seed.
	 * @param seed The new seed for the generator
	 */
	@Override
	public void set_seed(long seed) {
		source.set_seed(seed);
	}

	/**
	 * Get the random number generator's seed.
	 * @return The generator's seed
	 */
	@Override
	public long get_seed() {
		return source.get_seed();
	}

	/**
	 * Get the random number generator's name.
	 * @return The generator's name
	 */
	@Override
	public String get_name() {
		return name;
	}

}
