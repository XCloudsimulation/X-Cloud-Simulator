package main;

public enum Variable_Index{
	NUM_SIDE, CELL_DIM, NBR_SERVICES, RBS_PER_DC;
	
	public void PrintUsage(){
		System.out.println("Usage:");
		for (Variable_Index target: Variable_Index.values()){
			System.out.println(target);
		}
	}
	
	public int NbrVariable(){
		return Variable_Index.values().length;
	}
}