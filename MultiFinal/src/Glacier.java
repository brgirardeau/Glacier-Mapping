import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import Jama.*;


public class Glacier {
	private static double numberGrids = 100;
	// x from 308.7467 to 310.17
	// y from 69.232 to 69.107
	// files are in format: elevation y x
	//private static double[] gridParams = {302.233,71.267,5.767,-.516};
	private static double[] gridParams = {300.233,72.267,8,-1};
	private static double gridHeight = gridParams[3] / numberGrids;
	private static double gridWidth = gridParams[2] / numberGrids;
	private static double volume = 0;
	
	public static void main(String[] args) throws FileNotFoundException{
		System.out.println("Grid Width: " + gridWidth + " GridHeight: " + gridHeight);
		//double[] bounds = {-gridWidth / 2, gridWidth/2, gridHeight / 2, -gridHeight/2};
		Scanner scan = new Scanner(new File("data.txt"));
		ArrayList<Point> withinParams = new ArrayList<Point>();
		while(scan.hasNextLine()){
			Scanner line = new Scanner(scan.nextLine());
			double y = line.nextDouble();
			double x = line.nextDouble();
			double elevation = line.nextDouble();
			if(x >= gridParams[0] && x <= gridParams[0] + gridParams[2] && y <= gridParams[1] && y >= gridParams[1] + gridParams[3]){
				Point pn = new Point(x,y,elevation);
				withinParams.add(pn);
				System.out.println(pn);
			}
		}
		for(int x = 0; x < numberGrids; x++){
			for(int y = 0; y < numberGrids; y++){
				ArrayList<Point> points = new ArrayList<Point>();
				double xOffset = gridParams[0] + x * gridWidth;
				double yOffset = gridParams[1] + y * gridHeight;
				points = findPointsWithinRegion(withinParams, (gridParams[0] + x * gridWidth), (gridParams[1] + y * gridHeight), gridHeight, gridWidth, xOffset + gridWidth/2, yOffset + gridHeight/2);
				//System.out.print("INFO: " + (gridParams[0] + x * gridWidth) + " " + (gridParams[1] + y * gridHeight) + " " + gridHeight + " " + gridWidth + " INFO");
//				for(Point A : points){
//					System.out.print(A + " ");
//				}
//				System.out.println();
				xOffset += gridWidth/2;
				yOffset += gridHeight/2;
				if(points.size() > 4){
					Function bestFitPlane = new Function(fitPlane(points));
					double[] bounds = {-distFrom(yOffset, xOffset, yOffset, xOffset + gridWidth/2), distFrom(yOffset, xOffset, yOffset, xOffset + gridWidth/2) , -distFrom(yOffset, xOffset, yOffset + gridWidth/2, xOffset) , distFrom(yOffset, xOffset, yOffset + gridWidth/2, xOffset)};
					volume += bestFitPlane.findVolume(bounds);
				}
			}
		}
		System.out.println(volume);
	}
	
	public static double[] fitPlane(ArrayList<Point> points){
      double n=(double) points.size();
      //or we can pass in the boundaries....
      //double maxZ=-100000;
      //double minZ=100000;
      //for (Point i:points){
      //   if (i.getZ()<minZ){
      //      minZ=i.getZ();}
      //   if (i.getZ()>maxZ){
      //      maxZ=i.getZ();}
      //}
      //we wouldn't need all of this ^^^
      double sumXX=0;
      double sumYY=0;
      double sumXY=0;
      double sumX=0;
      double sumY=0;
      double sumZ=0;
      //double d = (maxZ+minZ)/2;
      for (Point i:points){
         //centers on origin before summing...this is necessary because the plane will always intersect the origin
         i.setZ(i.getZ());
         //i.setZ(i.getZ()-d);
         sumX+=i.getX();
         sumY+=i.getY();
         sumXX+=i.getX()*i.getX();
         sumYY+=i.getY()*i.getY();
         sumXY+=i.getY()*i.getX();
         sumZ+=i.getZ();
      }
      double[][] vals = {{sumXX, sumXY,sumX},
    		  			{sumXY, sumYY, sumY},
    		  			{sumX, sumY,n}};
      Matrix A = new Matrix(vals);
      double[][] values = {{sumX*sumZ},{sumY*sumZ},{sumZ}};
      for(double[] y : vals){
    	  for(double x: y){
    		  System.out.print(x +  " ");
    	  }
    	  System.out.println();
      }
      System.out.println("MATRIX 2");
      for(double[] y : values){
    	  for(double x: y){
    		  System.out.println(x);
    	  }
      }
      
      Matrix bb = new Matrix(values);
      Matrix x = A.solve(bb);
      double a = (x.getArray())[0][0];
      double b = (x.getArray())[1][0];
      double c = (x.getArray())[2][0];
      //double[] output = {-a/c,-b/c,-d/c};
      double[] output = {a,b,c};
      System.out.print("PLANEPLANEPLANE: ");
      for(double xx: output){
    	  System.out.println(xx);
      }
      return output;

	}
	
	 public static double distFrom(double lat1, double lng1, double lat2, double lng2) {
		    double earthRadius = 6371000; //meters
		    double dLat = Math.toRadians(lat2-lat1);
		    double dLng = Math.toRadians(lng2-lng1);
		    double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
		               Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
		               Math.sin(dLng/2) * Math.sin(dLng/2);
		    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
		    double dist = (double) (earthRadius * c);

		    return dist;
		    }
	
	public static ArrayList<Point> findPointsWithinRegion(ArrayList<Point> pointsInRange, double xPos, double yPos, double Height, double Width, double xOffset, double yOffset){
		ArrayList<Point> points = new ArrayList<Point>();
		ArrayList<Point> withinParams = new ArrayList<Point>(pointsInRange);
		while(!withinParams.isEmpty()){
			double x = withinParams.get(0).getX();
			double y = withinParams.get(0).getY();
			double z = withinParams.get(0).getZ();
			if(x >= xPos && x <= xPos + Width && y <= yPos && y >= yPos + Height){
				double xPosMeters = distFrom(yOffset, xOffset, yOffset, x);
				double yPosMeters = distFrom(y, xOffset, yOffset, xOffset);
				if(x <= xOffset){
					xPosMeters *= -1;
				}
				if(y >= yOffset){
					yPosMeters *= -1;
				}
				withinParams.get(0).setX(xPosMeters);
				withinParams.get(0).setY(yPosMeters);
				//withinParams.get(0).setX(x - xOffset);
				//withinParams.get(0).setY(- (y - yOffset));
				points.add(withinParams.get(0));
				System.out.println("FINDPOINTSWITHINREGION " + withinParams.get(0) + "xBoundary: " + (xOffset - gridWidth/2) + " yBoundary: " + (yOffset - gridHeight/2));
			}
			withinParams.remove(0);
		}
		return points;
	}
	
}
