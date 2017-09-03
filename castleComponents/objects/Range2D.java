package castleComponents.objects;

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
	
	public void setPoints(Vector2 a, Vector2 b, Vector2 c, Vector2 d){
		pointA = a;
		pointB = b;
		pointC = c;
		pointD = d;	
		sortPoints();
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
	
	public void sortPoints(){
		this.minX = Utilities.calculateMin(new double[]{pointA.getX(), pointB.getX(), pointC.getX(), pointD.getX()});
		this.maxX = Utilities.calculateMax(new double[]{pointA.getX(), pointB.getX(), pointC.getX(), pointD.getX()});
		
		this.minY = Utilities.calculateMin(new double[]{pointA.getY(), pointB.getY(), pointC.getY(), pointD.getY()});		
		this.maxY = Utilities.calculateMax(new double[]{pointA.getY(), pointB.getY(), pointC.getY(), pointD.getY()});
		
	}
}
