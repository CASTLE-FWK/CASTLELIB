package stdSimLib;

import java.util.ArrayList;


//import repast.simphony.space.continuous.NdPoint;
//import repast.simphony.space.grid.GridPoint;


/**
 * A simple class representing a 2D vector with a whole bunch
 * of useful functions for use with Repast.
 */

public class Vector2_Deprecated implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7945212763285882217L;

	private double x, y;
	
	public Vector2_Deprecated(){
		this.x = 0;
		this.y = 0;
	}
	
	public Vector2_Deprecated(double x, double y){
		this.x = x;
		this.y = y;
	}

	public Vector2_Deprecated(Vector2_Deprecated v){
		this.x = v.x;
		this.y = v.y;
	}
	
	public Vector2_Deprecated(String stringVector){
		stringVector = stringVector.replaceAll("\\(", "").replaceAll("\\)","");
		String[] terms = stringVector.split(",");
		this.x = Double.parseDouble(terms[0]);
		this.y = Double.parseDouble(terms[1]);
	}
	
	public void reset(){
		this.x = 0;
		this.y = 0;
	}
	
	public double distance(Vector2_Deprecated vec){
		double x = Math.pow((vec.getX() - getX()),2);
		double y = Math.pow((vec.getY() - getY()),2);
				
		return Math.sqrt(x+y); 
	}
	
	//TODO: WHAT IS THIS ONE FOR?
	public void modify(double x, double y){
		this.x += x;
		this.y += y;
	}
	
	public void modify(Vector2_Deprecated vec){
		setX(vec.getX());
		setY(vec.getY());
	}
	
	public Vector2_Deprecated subtract(Vector2_Deprecated v){
		this.x -= v.x;
		this.y -= v.y;
		return this;
	}
	
	public Vector2_Deprecated add(Vector2_Deprecated v){
		this.x += v.x;
		this.y += v.y;
		return this;
	}
	
	public double dotProduct(Vector2_Deprecated v){
		return (this.x * v.x + this.y * v.y);
	}
	
	public void multiply(double mult){
		this.x = this.x * mult;
		this.y = this.y * mult;
	}
	
	public void divide(double divide){
		this.x = this.x / divide;
		this.y = this.y / divide;
	}
	
	public double getX(){
		return x;
	}	
	
	public double getY(){
		return y;
	}
	
	public void setX(double X){
		x = X;
	}
	
	public void setY(double Y){
		y = Y;
	}
	
	@Override
	public String toString(){
		String out = "";
		out += "("+this.x+","+this.y+")";
		return out;
	}
	
	@Override
	public boolean equals(Object o){
		return compare((Vector2_Deprecated)o);
	}
	
	@Override
	public int hashCode(){
		return toString().hashCode();
	}
		
	public boolean equals(Vector2_Deprecated v){
		return (v.getX() == getX() && v.getY() == getY());
	}
	
	public boolean compare(Vector2_Deprecated v){
		return (v.getX() == getX() && v.getY() == getY());
	}
	
	public double compareDistance (Vector2_Deprecated b){
		return Math.sqrt((Math.pow(this.x - b.getX(), 2) + Math.pow(this.y - b.getY(), 2)));
	}
	
	public ArrayList<Vector2_Deprecated> possibleOffsets(int offset){
		ArrayList<Vector2_Deprecated> offsets = new ArrayList<Vector2_Deprecated>();
		offsets.add(new Vector2_Deprecated(x-offset,y+offset)); //-1,1
		offsets.add(new Vector2_Deprecated(x,y+offset)); //0,1
		offsets.add(new Vector2_Deprecated(x+offset,y+offset)); //1,1
		offsets.add(new Vector2_Deprecated(x+offset,y)); //1,0
		offsets.add(new Vector2_Deprecated(x+offset,y-offset)); //1,-1
		offsets.add(new Vector2_Deprecated(x,y-offset)); //0,-1
		offsets.add(new Vector2_Deprecated(x-offset,y-offset)); //-1,-1
		offsets.add(new Vector2_Deprecated(x-offset,y)); //-1,0
		return offsets;
	}
}
