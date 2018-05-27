package castleComponents.objects;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import stdSimLib.utilities.Utilities;

//import repast.simphony.space.continuous.NdPoint;
//import repast.simphony.space.grid.GridPoint;

/**
 * A simple class representing a 2D vector with a whole bunch of useful
 * functions for use with Repast.
 */

public class Vector2 implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7945212763285882217L;

	private double x, y;

	private boolean nullPoint = true;

	public static final Vector2 NULL = new Vector2(true);

	public Vector2() {
		this.x = 0;
		this.y = 0;
		nullPoint = false;
	}

	public Vector2(double x, double y) {
		this.x = x;
		this.y = y;
		nullPoint = false;
	}

	public Vector2(Vector2 v) {
		this.x = 0;
		this.y = 0;
		this.x = v.x;
		this.y = v.y;
		this.nullPoint = v.isANullPoint();
		// nullPoint = false;
	}

	public Vector2(String stringVector) {
		stringVector = stringVector.replaceAll("\\(", "").replaceAll("\\)", "");
		String[] terms = stringVector.split(",");
		this.x = Double.parseDouble(terms[0]);
		this.y = Double.parseDouble(terms[1]);
		nullPoint = false;
	}

	public static Vector2 parseFromString(String stringVector) {
		return new Vector2(stringVector);
	}

	public Vector2(boolean n) {
		if (n) {
			nullPoint = true;
		} else {
			nullPoint = false;
		}
		this.x = 0;
		this.y = 0;
	}

	public void reset() {
		this.x = 0;
		this.y = 0;
	}

	public Vector2 copy(Vector2 v) {
		setX(v.getX());
		setY(v.getY());
		return this;
	}

	public double length() {
		return Math.abs(x - y);
	}

	public double distance(Vector2 vec) {
		double x = Math.pow((vec.getX() - getX()), 2);
		double y = Math.pow((vec.getY() - getY()), 2);

		return Math.sqrt(x + y);
	}

	// TODO: WHAT IS THIS ONE FOR?
	public void modify(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public void modify(Vector2 vec) {
		setX(vec.getX());
		setY(vec.getY());
	}

	public Vector2 subtract(Vector2 v) {
		// double xm = this.x - v.getX();
		// double xy = this.y - v.getY();
		this.x -= v.getX();
		this.y -= v.getY();
		return this;
	}

	public Vector2 add(Vector2 v) {
		this.x += v.x;
		this.y += v.y;
		return this;
	}

	public Vector2 add(double x, double y) {
		this.x += x;
		this.y += y;
		return this;
	}
	
	public void clampX(Vector2 range) {
		if (x < range.getX()) {
			x = range.getX();
		} else if (x > range.getY()) {
			x = range.getY();
		}
	}
	public void clampY(Vector2 range) {
		if (y < range.getX()) {
			y = range.getX();
		} else if (y > range.getY()) {
			y = range.getY();
		}
	}
	public void clamp(Vector2 xR, Vector2 yR) {
		clampX(xR);
		clampY(yR);
	}
	
	public void floor() {
		x = Math.floor(x);
		y = Math.floor(y);
	}
	
	public void ceil() {
		x = Math.ceil(x);
		y = Math.ceil(y);
	}

	public double dotProduct(Vector2 v) {
		return (this.x * v.x + this.y * v.y);
	}

	public Vector2 multiply(double mult) {
		this.x = this.x * mult;
		this.y = this.y * mult;
		return this;
	}

	public Vector2 divide(double divide) {
		this.x = this.x / divide;
		this.y = this.y / divide;
		return this;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public void setX(double X) {
		x = X;
	}

	public void setY(double Y) {
		y = Y;
	}

	public Vector2 getUnitVector() {
		double x = getX() / Math.abs(getX());
		if (Double.isNaN(x)) {
			x = 0;
		}
		double y = getY() / Math.abs(getY());
		if (Double.isNaN(y)) {
			y = 0;
		}
		return new Vector2(x, y);
	}

	public int getMultipleAsInt() {
		return (int) (x * y);
	}

	public void setPair(double X, double Y) {
		this.x = X;
		this.y = Y;
	}

	@Override
	public String toString() {
		String out = "";
		out += "(" + this.x + "," + this.y + ")";
		if (nullPoint) {
			out += "(NULLVEC)";
		}
		return out;
	}

	public Vector2 round(int dp) {
		this.x = Utilities.roundDoubleToXDP(this.x, dp);
		this.y = Utilities.roundDoubleToXDP(this.y, dp);
		return this;
	}

	@Override
	public boolean equals(Object o) {
		return compare((Vector2) o);
	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	public boolean equals(Vector2 v) {
		return (v.getX() == getX() && v.getY() == getY());
	}

	public Vector2 getDifference(Vector2 v) {
		return new Vector2(getX() - v.getX(), getY() - v.getY());
	}

	public Vector2 negate() {
		return new Vector2(-getX(), -getY());
	}

	public boolean compare(Vector2 v) {
		return (v.getX() == getX() && v.getY() == getY());
	}

	public double calculateDistance(Vector2 b) {
		return Math.sqrt((Math.pow(this.x - b.getX(), 2) + Math.pow(this.y - b.getY(), 2)));
	}

	public double calculateSlope(Vector2 b) {
		return (b.getY() - getY()) / (b.getX() - getX());
	}

	public boolean sameX(Vector2 v) {
		return (v.getX() == x);
	}

	public boolean sameY(Vector2 v) {
		return (v.getY() == y);
	}

	// TODO: Seriously this is simple, why cant you think?
	// public static List<Vector2> getAllPointsBetweenTwoVectors(Vector2 v1, Vector2
	// v2){
	// double minX = Utilities.calculateMin(new double[]{v1.getX(), v2.getX()});
	// double maxX = Utilities.calculateMax(new double[]{v1.getX(), v2.getX()});
	// double minY = Utilities.calculateMin(new double[]{v1.getY(), v2.getY()});
	// double maxY = Utilities.calculateMax(new double[]{v1.getY(), v2.getY()});
	// }

	public ArrayList<Vector2> possibleOffsets(double offset) {
		ArrayList<Vector2> offsets = new ArrayList<Vector2>();
		offsets.add(new Vector2(x - offset, y + offset)); // -1,1
		offsets.add(new Vector2(x, y + offset)); // 0,1
		offsets.add(new Vector2(x + offset, y + offset)); // 1,1
		offsets.add(new Vector2(x + offset, y)); // 1,0
		offsets.add(new Vector2(x + offset, y - offset)); // 1,-1
		offsets.add(new Vector2(x, y - offset)); // 0,-1
		offsets.add(new Vector2(x - offset, y - offset)); // -1,-1
		offsets.add(new Vector2(x - offset, y)); // -1,0
		return offsets;
	}

	public static Comparator<Vector2> sortByX() {
		return new Comparator<Vector2>() {
			@Override
			public int compare(Vector2 o1, Vector2 o2) {
				// TODO Auto-generated method stub
				if (o1.getX() > o2.getX()) {
					return 1;
				} else if (o1.getX() < o2.getX()) {
					return -1;
				} else {
					return 0;
				}
			}
		};
	}

	public static Comparator<Vector2> sort() {
		return new Comparator<Vector2>() {
			@Override
			public int compare(Vector2 o1, Vector2 o2) {
				// TODO Auto-generated method stub
				int byX = sortByX().compare(o1, o2);
				if (byX == 0) {
					return sortByY().compare(o1, o2);
				} else {
					return byX;
				}
			}
		};
	}

	public static Comparator<Vector2> sortByY() {
		return new Comparator<Vector2>() {
			@Override
			public int compare(Vector2 o1, Vector2 o2) {
				// TODO Auto-generated method stub
				if (o1.getY() > o2.getY()) {
					return 1;
				} else if (o1.getY() < o2.getY()) {
					return -1;
				} else {
					return 0;
				}
			}
		};
	}

	public boolean isANullPoint() {
		return nullPoint;
	}

	public void setNullPoint(boolean b) {
		nullPoint = b;
	}

	public Vector2 getCenter() {
		return new Vector2(getX() / 2.0, getY() / 2.0);
	}

	public double getProduct() {
		return getX() * getY();
	}
}
