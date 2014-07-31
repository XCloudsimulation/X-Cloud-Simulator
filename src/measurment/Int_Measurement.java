package measurment;

public class Int_Measurement implements Measurement {

	public double time;
	public int value;
	
	public Int_Measurement(int value, double time){
		this.value = value;
		this.time = time;
	}
	
	public String toString(){
		return value + ";" + time;
	}
}
