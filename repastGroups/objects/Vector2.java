package repastGroups.objects;

import java.util.ArrayList;
import java.util.Comparator;


//import repast.simphony.space.continuous.NdPoint;
//import repast.simphony.space.grid.GridPoint;


/**
 * A simple class representing a 2D vector with a whole bunch
 * of useful functions for use with Repast.
 */

public class Vector2 implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7945212763285882217L;

	private double x, y;
	
	public Vector2(){
		this.x = 0;
		this.y = 0;
	}
	
	public Vector2(double x, double y){
		this.x = x;
		this.y = y;
	}

	public Vector2(Vector2 v){
		this.x = v.x;
		this.y = v.y;
	}
	
	public Vector2(String stringVector){
		stringVector = stringVector.replaceAll("\\(", "").replaceAll("\\)","");
		String[] terms = stringVector.split(",");
		this.x = Double.parseDouble(terms[0]);
		this.y = Double.parseDouble(terms[1]);
	}
	
	public void reset(){
		this.x = 0;
		this.y = 0;
	}
	
	public double distance(Vector2 vec){
		double x = Math.pow((vec.getX() - getX()),2);
		double y = Math.pow((vec.getY() - getY()),2);
				
		return Math.sqrt(x+y); 
	}
	
	//TODO: WHAT IS THIS ONE FOR?
	public void modify(double x, double y){
		this.x += x;
		this.y += y;
	}
	
	public void modify(Vector2 vec){
		setX(vec.getX());
		setY(vec.getY());
	}
	
	public Vector2 subtract(Vector2 v){
		this.x -= v.x;
		this.y -= v.y;
		return this;
	}
	
	public Vector2 add(Vector2 v){
		this.x += v.x;
		this.y += v.y;
		return this;
	}
	
	public double dotProduct(Vector2 v){
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
	
	public int getMultipleAsInt(){
		return (int)(x * y);
	}
	
	@Override
	public String toString(){
		String out = "";
		out += "("+this.x+","+this.y+")";
		return out;
	}
	
	@Override
	public boolean equals(Object o){
		return compare((Vector2)o);
	}
	
	@Override
	public int hashCode(){
		return toString().hashCode();
	}
		
	public boolean equals(Vector2 v){
		return (v.getX() == getX() && v.getY() == getY());
	}
	
	public boolean compare(Vector2 v){
		return (v.getX() == getX() && v.getY() == getY());
	}
	
	public double compareDistance (Vector2 b){
		return Math.sqrt((Math.pow(this.x - b.getX(), 2) + Math.pow(this.y - b.getY(), 2)));
	}
	
	public ArrayList<Vector2> possibleOffsets(int offset){
		ArrayList<Vector2> offsets = new ArrayList<Vector2>();
		offsets.add(new Vector2(x-offset,y+offset)); //-1,1
		offsets.add(new Vector2(x,y+offset)); //0,1
		offsets.add(new Vector2(x+offset,y+offset)); //1,1
		offsets.add(new Vector2(x+offset,y)); //1,0
		offsets.add(new Vector2(x+offset,y-offset)); //1,-1
		offsets.add(new Vector2(x,y-offset)); //0,-1
		offsets.add(new Vector2(x-offset,y-offset)); //-1,-1
		offsets.add(new Vector2(x-offset,y)); //-1,0
		return offsets;
	}
	
	public static Comparator<Vector2> sortByX(){
		return new Comparator<Vector2>() {
			@Override
			public int compare(Vector2 o1, Vector2 o2) {
				// TODO Auto-generated method stub
				if (o1.getX() > o2.getX()){
					return 1;
				} else if (o1.getX() < o2.getX()){
					return -1;
				} else {
					return 0;
				}
			}
		};
	}
	
	public static Comparator<Vector2> sortByY(){
		return new Comparator<Vector2>() {
			@Override
			public int compare(Vector2 o1, Vector2 o2) {
				// TODO Auto-generated method stub
				if (o1.getY() > o2.getY()){
					return 1;
				} else if (o1.getY() < o2.getY()){
					return -1;
				} else {
					return 0;
				}
			}
		};
	}
}
