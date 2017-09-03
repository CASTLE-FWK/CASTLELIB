package castleComponents.objects;

public class Range2D {
	Vector2 pointA;
	Vector2 pointB;
	Vector2 pointC;
	Vector2 pointD;
	
	public Range2D(){}
	
	public Range2D(Vector2 a, Vector2 b, Vector2 c, Vector2 d){
		setPoints(a,b,c,d);
	}
	
	
	public void setPoints(Vector2 a, Vector2 b, Vector2 c, Vector2 d){
		pointA = a;
		pointB = b;
		pointC = c;
		pointD = d;	
	}
	
	public boolean containsPoint(Vector2 point){
		
	}
	
	public Vector2 getDimensions(){
		
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
		
	}
}
