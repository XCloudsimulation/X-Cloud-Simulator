package measurment;

public enum PacketMeasIndex {
	REJECTED_BY(0),PROCESSED_BY(1),SERVICE(2), USER(3), SESSION(4), SESSION_SIZE(5), MIGRATED(6), PACKET_NBR(7), RADIO(8), NETWORK_UPLINK(9), DISPATCH(10), MIGRATE(11), PROCESS(12), NETWORK_DOWNLINK(13), QUEUE(14);
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