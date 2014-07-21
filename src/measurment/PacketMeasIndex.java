package measurment;

public enum PacketMeasIndex {
	SERVICE(0), USER(1), SESSION(2), SESSION_SIZE(3), MIGRATED(4), PACKET_NBR(5), RADIO(6), NETWORK_UPLINK(7), DISPATCH(8), MIGRATE(9), PROCESS(10), NETWORK_DOWNLINK(11), QUEUE(12);
    private final int value;

    private PacketMeasIndex(int value) {
        this.value = value;
    }
    
    public static int NbrDepths(){
    	return PacketMeasIndex.values().length;
    }
    
    public int toInt() {
        return value;
    }
}