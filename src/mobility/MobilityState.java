package mobility;

public enum MobilityState {

	MOBILE(0), TURNING(1), NEW_DIRECTION(2), NEW_SPEED(3), ACCELERATING(4), EDGE(5);
	
	private final int value;

    private MobilityState(int value) {
        this.value = value;
    }
	
}