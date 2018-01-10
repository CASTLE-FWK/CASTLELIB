package castleComponents.representations.MapGraph;

import java.util.ArrayList;
import java.util.List;

import castleComponents.objects.Vector2;

public class Link implements Comparable<Link> {
	private double weight;
	ArrayList<Node> wayPoints;

	// OSM Extra features
	boolean bicycle = false;
	String cycleWay = "";
	String roadType = "";
	boolean lit = false;
	int maxSpeed = 0;
	String name = "";
	boolean oneWay = false;
	int lanes = 0;
	long id;

	public Link() {
		init();
	}

	public void init() {
		wayPoints = new ArrayList<Node>();
	}

	public double calculateLength() {
		double length = 0.0;
		for (int i = 0; i < wayPoints.size() - 1; i++) {
			int j = i + 1;
			length += calculateCoordinateDistance(wayPoints.get(i).getCoords(), wayPoints.get(j).getCoords());
		}
		weight = length;
		return length;
	}

	public double calculateCoordinateDistance(Vector2 a, Vector2 b) {
		int earthRad = 6371; // TODO
		double lat1 = a.getX();
		double lat2 = b.getX();
		double lon1 = a.getY();
		double lon2 = b.getY();

		double dLat = Math.toRadians(lat2 - lat1);
		double dLon = Math.toRadians(lon2 - lon1);

		lat1 = Math.toRadians(lat1);
		lat2 = Math.toRadians(lat2);

		double q = Math.sin(dLat / 2.0) * Math.sin(dLat / 2.0)
				+ Math.sin(dLon / 2.0) * Math.sin(dLon / 2.0) * Math.cos(lat1) * Math.cos(lat2);

		double c = 2 * Math.atan2(Math.sqrt(q), Math.sqrt(1.0 - q));
		return c * earthRad;
	}

	public void setID(long id) {
		this.id = id;
	}

	public long getID() {
		return id;
	}

	public List<Node> getWayPoints() {
		return wayPoints;
	}

	public void addWayPoint(Node n) {
		wayPoints.add(n);
	}

	public void setWayPoints(List<Node> e) {
		wayPoints.clear();
		wayPoints.addAll(e);
	}

	public double getWeight() {
		return weight;
	}

	public boolean isBicycle() {
		return bicycle;
	}

	public void setBicycle(boolean bicycle) {
		this.bicycle = bicycle;
	}

	public String getCycleWay() {
		return cycleWay;
	}

	public void setCycleWay(String cycleWay) {
		this.cycleWay = cycleWay;
	}

	public String getRoadType() {
		return roadType;
	}

	public void setRoadType(String roadType) {
		this.roadType = roadType;
	}

	public boolean isLit() {
		return lit;
	}

	public void setLit(boolean lit) {
		this.lit = lit;
	}

	public int getMaxSpeed() {
		return maxSpeed;
	}

	public void setMaxSpeed(int maxSpeed) {
		this.maxSpeed = maxSpeed;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isOneWay() {
		return oneWay;
	}

	public void setOneWay(boolean oneWay) {
		this.oneWay = oneWay;
	}

	public int getLanes() {
		return lanes;
	}

	public void setLanes(int lanes) {
		this.lanes = lanes;
	}

	public boolean isBiDirectional() {
		return !oneWay;
	}

	@Override
	public String toString() {
		return "Edge [id=" + id + ", weight=" + weight + ", bicycle=" + bicycle + ", cycleWay=" + cycleWay
				+ ", roadType=" + roadType + ", lit=" + lit + ", maxSpeed=" + maxSpeed + ", name=" + name + ", oneWay="
				+ oneWay + ", lanes=" + lanes + ", wayPoints=" + wayPoints + "]";
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	@Override
	public int compareTo(Link o) {
		double diff = getWeight() - o.getWeight();
		if (diff < 0.0) {
			return -1;
		} else if (diff > 0.0) {
			return 1;
		}

		return 0;

	}
}
