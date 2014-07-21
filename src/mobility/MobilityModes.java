package mobility;

import java.util.Random;

public enum MobilityModes{
	PEDESTRIAN(0), BIKE(1), CAR(2);
	
	private final int value;

    private MobilityModes(int value) {
        this.value = value;
    }
    
    public static int GetMode(){
    	Random rnd = new Random();
    	return rnd.nextInt(MobilityModes.values().length-1);
    }
    
    public int toInt() {
        return value;
    }
    
    public static MobilityModes fromInt(int value){
    	switch(value){
    		case 0: return PEDESTRIAN;
    		case 1: return BIKE;
    		case 2: return CAR;
    		default: return PEDESTRIAN; 
    	}
    } 
}