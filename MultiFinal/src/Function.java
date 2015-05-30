
public class Function {
	private double a;
	private double b;
	private double c;
	
	public Function(double[] coefficients){
		a = coefficients[0];
		b = coefficients[1];
		c = coefficients[2];
	}
	
	public double findVolume(double[] bounds){
		double x1 = bounds[0];
		double x2 = bounds[1];
		double y1 = bounds[2];
		double y2 = bounds[3];
		return (a * (Math.pow(x1, 2) - Math.pow(x2, 2)) * (y1 - y2) +
				(b* (Math.pow(y1, 2) - Math.pow(y2,2)) + 2 * c * (y1 - y2)) * (x1 - x2))/2;
	}
	
}
