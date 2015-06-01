
public class Point {
	private double x;
	private double y;
	private double z;
	
	public Point(double xx, double yy, double zz){
		x = xx;
		y = yy;
		z = zz;
	}
	
	public double getX(){
		return x;
	}
	
	public double getY(){
		return y;
	}
	
	public double getZ(){
		return z;
	}
	
	public void setX(double xx){
		x = xx;
	}
	
	public void setY(double yy){
		y = yy;
	}
	
	public void setZ(double zz){
		z = zz;
	}
	
	public String toString(){
		return "x: " + x + " y: " + y + " z: " + z;
	}
}
