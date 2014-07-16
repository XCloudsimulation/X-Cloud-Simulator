package network;

import java.util.ArrayList;

import mobile_entities.User;

public class Hom_2D_AffiliationStrategy extends AffiliationStrategy {

	private double cell_dim;
	private int n_x_dim, n_y_dim;
	
	public Hom_2D_AffiliationStrategy(RadioBaseStation[][] rbs_enteties, double cell_dim) {
		super(rbs_enteties);
		
		this.cell_dim = cell_dim;
		
		this.n_x_dim = rbs_enteties.length;
		this.n_y_dim = rbs_enteties[0].length;
	}

	@Override
	public int AssertAffiliation(User user) {
		double u_x = user.getLocation().x;
		double u_y = user.getLocation().y;
		
		int n_x = (int) Math.floor(u_x/n_x_dim*cell_dim);
		int n_y = (int) Math.floor(u_y/n_y_dim*cell_dim);
		
		int rbsAffiliation;
		
		try{
			rbsAffiliation = rbs_enteties[n_x][n_y].getNbr();
		}catch(IndexOutOfBoundsException iobe){
			System.err.println();
			rbsAffiliation =  user.getRBSAffiliation();
		}
		
		user.setRBSAffiliation(rbsAffiliation);
		
		return rbsAffiliation;
	}

}
