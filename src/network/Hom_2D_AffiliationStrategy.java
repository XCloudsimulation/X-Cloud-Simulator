package network;

import mobile_entities.UserEquipment;

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
	public int[] AssertAffiliation(UserEquipment user) {
		double u_x = user.getLocation().x;
		double u_y = user.getLocation().y;
		
		int n_x = (int) Math.floor(u_x/cell_dim);
		int n_y = (int) Math.floor(u_y/cell_dim);
		
		int rbsAffiliation;
		
		try{
			rbsAffiliation = rbs_enteties[n_x][n_y].getNbr();
		}catch(IndexOutOfBoundsException iobe){
			System.err.println(this.getClass() + " - " + user.get_name() + " out of simulation domain bounds");
			rbsAffiliation =  user.getRBSAffiliation();
		}
		
		return new int[]{n_x, n_y};
	}

}
