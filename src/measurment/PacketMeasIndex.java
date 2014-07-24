package measurment;

public enum PacketMeasIndex {
	PROCESSED_BY(0),SERVICE(1), USER(2), SESSION(3), SESSION_SIZE(4), MIGRATED(5), PACKET_NBR(6), RADIO(7), NETWORK_UPLINK(8), DISPATCH(9), MIGRATE(10), PROCESS(11), NETWORK_DOWNLINK(12), QUEUE(13);
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