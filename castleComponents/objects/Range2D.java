package castleComponents.objects;

import java.util.Arrays;
import java.util.Collections;

import stdSimLib.utilities.Utilities;

public class Range2D {
	Vector2 pointA;
	Vector2 pointB;
	Vector2 pointC;
	Vector2 pointD;
	double minX;
	double minY;
	double maxX;
	double maxY;
	
	public Range2D(){}
	
	public Range2D(Vector2 a, Vector2 b, Vector2 c, Vector2 d){
		setPoints(a,b,c,d);
	}
	public Range2D(Range2D r) {
		copy(r);
	}
	
	public void setPoints(Vector2 a, Vector2 b, Vector2 c, Vector2 d){
		pointA = new Vector2(a);
		pointB = new Vector2(b);
		pointC = new Vector2(c);
		pointD = new Vector2(d);	
		sortPoints();
	}
	
	public void copy(Range2D src) {
		Vector2[] p = src.getPoints();
		setPoints(p[0],p[1],p[2],p[3]);
	}
	
	public boolean containsPoint(Vector2 point){
		double x = point.getX();
		double y = point.getY();
		boolean isInX = (x >= minX && x <= maxX);
		boolean isInY = (y >= minY && x <= maxY);
		return (isInX && isInY);		
	}
	
	//I think this will be correct
	public Vector2 getDimensions(){
		double x = maxX - minX;
		double y = maxY - minY;
		return new Vector2(x,y);
	}
	
	public Vector2[] getPoints(){
		Vector2[] vOut = new Vector2[4];
		vOut[0] = pointA;
		vOut[1] = pointB;
		vOut[2] = pointC;
		vOut[3] = pointD;
		return vOut;
	}
	
	public Vector2[] cloneAllPoints() {
		Vector2[] vOut = new Vector2[4];
		vOut[0] = new Vector2(pointA);
		vOut[1] = new Vector2(pointB);
		vOut[2] = new Vector2(pointC);
		vOut[3] = new Vector2(pointD);
		return vOut;
	}
	
	public void sortPoints(){
		Vector2[] pts = cloneAllPoints();
		Arrays.sort(pts, Vector2.sortByX());
		pointA = pts[0];
		pointB = pts[1];
		pointC = pts[2];
		pointD = pts[3];
		
		
		
//		this.minX = Utilities.calculateMin(new double[]{pointA.getX(), pointB.getX(), pointC.getX(), pointD.getX()});
//		this.maxX = Utilities.calculateMax(new double[]{pointA.getX(), pointB.getX(), pointC.getX(), pointD.getX()});
//		
//		this.minY = Utilities.calculateMin(new double[]{pointA.getY(), pointB.getY(), pointC.getY(), pointD.getY()});		
//		this.maxY = Utilities.calculateMax(new double[]{pointA.getY(), pointB.getY(), pointC.getY(), pointD.getY()});
		
	}
	
	public static Range2D createRange(Vector2 a, Vector2 b) {
		Range2D nr = new Range2D();
		nr = new Range2D(
				new Vector2(a.getX(),a.getY()),
				new Vector2(a.getX(), b.getY()),
				new Vector2(b.getX(), a.getY()),
				new Vector2(b.getX(), b.getY()));
		return nr;
		
	}

	public List<Range2D> chunkRange(int numberOfFogs) {
		// TODO Auto-generated method stub
		
		List<Range2D> chunks = new List<Range2D>();
		this.minX = Utilities.calculateMin(new double[]{pointA.getX(), pointB.getX(), pointC.getX(), pointD.getX()});
		this.maxX = Utilities.calculateMax(new double[]{pointA.getX(), pointB.getX(), pointC.getX(), pointD.getX()});
		
		this.minY = Utilities.calculateMin(new double[]{pointA.getY(), pointB.getY(), pointC.getY(), pointD.getY()});		
		this.maxY = Utilities.calculateMax(new double[]{pointA.getY(), pointB.getY(), pointC.getY(), pointD.getY()});
		
		double xDist = maxX - minX;
		double yDist = maxY - minY;
		
		double xChunkSize = xDist / (double)numberOfFogs;
		double yChunkSize = yDist / (double)numberOfFogs;
		
		
		
		return chunks;
	}
}
