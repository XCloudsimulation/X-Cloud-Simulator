package mobility;

public class MobilityState_Smooth extends MobilityState {

	private MobilityMode_Smooth mode;
	
	public MobilityState_Smooth(MobilityMode_Smooth mode) {
		setMode(mode);
	}

	public MobilityMode_Smooth getMode() {
		return mode;
	}

	public void setMode(MobilityMode_Smooth mode) {
		this.mode = mode;
	}

}