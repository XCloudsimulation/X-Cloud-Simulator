package mobility;
public class Location {

	public int x, y;
	
	public Location(int x, int y){
		this.x = x;
		this.y = y;
	}
	
	public void Update(int x, int y){
		this.x = x;
		this.y = y;
	}
	
	public double getDistance(Location target){
		return Math.sqrt(Math.pow(this.x-target.x, 2) + Math.pow(this.y-target.y, 2));
	}
	
	public String toString(){
		return "x="+x+",y="+y;
	}
}
