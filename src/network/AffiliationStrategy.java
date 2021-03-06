package network;

import mobile_entities.*;

public abstract class AffiliationStrategy {

	protected RadioBaseStation[][] rbs_enteties; 
	
	public AffiliationStrategy(RadioBaseStation[][] rbs_enteties){
		this.rbs_enteties = rbs_enteties;
	}
	
	public abstract int[] AssertAffiliation(UserEquipment user);

}
