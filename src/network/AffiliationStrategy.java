package network;

import java.util.ArrayList;

import mobile_entities.*;

public abstract class AffiliationStrategy {

	protected ArrayList<RadioNode> rbs_enteties; 
	
	public AffiliationStrategy(ArrayList<RadioNode> rbs_enteties){
		this.rbs_enteties = rbs_enteties;
	}
	
	public abstract int AssertAffiliation(User user);

}
