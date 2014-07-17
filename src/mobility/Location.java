package mobility;
public class Location {

	public double x, y;
	
	public Location(double loc_x, double loc_y){
		this.x = loc_x;
		this.y = loc_y;
	}
	
	public void Update(int x, int y){
		this.x = x;
		this.y = y;
	}
	
	public double getDistance(Location target){
		return Math.sqrt(Math.pow(this.x-target.x, 2) + Math.pow(this.y-target.y, 2));
	}
	
	@Override
	public String toString(){
		return "x="+x+",y="+y;
	}
}
