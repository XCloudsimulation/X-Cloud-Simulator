package measurment;

public enum LatencyDepthIndex {
	RADIO(0), NETWORK_UPLINK(1), DISPATCH(2), MIGRATE(3), PROCESS(4), NETWORK_DOWNLINK(5);
    private final int value;

    private LatencyDepthIndex(int value) {
        this.value = value;
    }
    
    public static int NbrDepths(){
    	return LatencyDepthIndex.values().length;
    }
    
    public int toInt() {
        return value;
    }
}