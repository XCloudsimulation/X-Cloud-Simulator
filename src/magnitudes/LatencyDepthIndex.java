package magnitudes;

public enum LatencyDepthIndex {
	RADIO(0), NETWORK(1), DISPATCH(2), MIGRATE(3), PROCESS(4);
    private final int value;

    private LatencyDepthIndex(int value) {
        this.value = value;
    }

    public static void PrintUsage(){
    	System.out.print("Input parameters :");
    	for(LatencyDepthIndex target: LatencyDepthIndex.values()){
    		System.out.print(" ["+target+"] ");
    	}
    	System.out.print("\r");
    }
    
    public static int NbrParams(){
    	return LatencyDepthIndex.values().length;
    }
    
    public int toInt() {
        return value;
    }
}
