package mobility;

import java.util.Random;

public enum MobilityMode_Smooth{
		PEDESTRIAN(0), BIKE(1), CAR(2);
		
		private final int value;

	    private MobilityMode_Smooth(int value) {
	        this.value = value;
	    }
	    
	    public static int GetMode(){
	    	Random rnd = new Random();
	    	return rnd.nextInt(MobilityMode_Smooth.values().length-1);
	    }
	    
	    public int toInt() {
	        return value;
	    }
	    
	    public static MobilityMode_Smooth fromInt(int value){
	    	switch(value){
	    		case 0: return PEDESTRIAN;
	    		case 1: return BIKE;
	    		case 2: return CAR;
	    		default: return PEDESTRIAN; 
	    	}
	    } 
}