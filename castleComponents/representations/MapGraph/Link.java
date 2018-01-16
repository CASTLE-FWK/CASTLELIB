package castleComponents.representations.MapGraph;

import castleComponents.objects.Vector2;

import java.util.HashSet;

import castleComponents.Entity;
import castleComponents.objects.List;

public class Link implements Comparable<Link> {
	private double weight;
	List<Node> wayPoints;
	List<Edge> edges;

	// OpenStreetMap Extra features
	boolean bicycle = false;
	String cycleWay = "";
	String roadType = "";
	boolean lit = false;
	int maxSpeed = 0;
	String name = "";
	boolean oneWay = false;
	int lanes = 0;
	long id;
	boolean humanAccessible = false;
	boolean carParkArea = false;

	public boolean isCarParkArea() {
		return carParkArea;
	}

	public void setCarParkArea(boolean carParkArea) {
		this.carParkArea = carParkArea;
	}

	HashSet<Entity> currentEntities;

	public Link() {
		init();
	}

	public Node[] findAdjacentNodes(Node n) {
		Node prev = null;
		Node aft = null;
		for (int i = 0; i < wayPoints.size() - 1; i++) {
			if (wayPoints.get(i) == n) {
				if (i == 0) {
					prev = null;
				} else {
					prev = wayPoints.get(i - 1);
				}
				if (i > wayPoints.size() - 2) {
					aft = null;
				} else {
					aft = wayPoints.get(i + 1);
				}
			}
		}
		return new Node[] { prev, aft };
	}

	public void init() {
		wayPoints = new List<Node>();
		currentEntities = new HashSet<Entity>();
		edges = new List<Edge>();
	}

	public boolean isNodeInLink(Node n) {
		for (Node ns : wayPoints) {
			if (ns.getID() == n.getID()) {
				return true;
			}
		}
		return false;
	}

	public Node[] findClosestNodePair(Vector2 pos) {
		Node[] pair = new Node[2];
		for (int i = 0; i < wayPoints.size() - 1; i++) {
			Vector2 coordA = wayPoints.get(i).getGeoCoords();
			Vector2 coordB = wayPoints.get(i + 1).getGeoCoords();

			// Check if point lies on line
			if (pointIsOnLine(pos, coordA, coordB)) {
				pair[0] = wayPoints.get(i);
				pair[1] = wayPoints.get(i + 1);
				return pair;
			}

		}
		return null;
	}

	// Pinched from:
	// https://stackoverflow.com/questions/11907947/how-to-check-if-a-point-lies-on-a-line-between-2-other-points
	public boolean pointIsOnLine(Vector2 currPoint, Vector2 point1, Vector2 point2) {
		double dxc = currPoint.getX() - point1.getX();
		double dyc = currPoint.getY() - point1.getY();

		double dxl = point2.getX() - point1.getX();
		double dyl = point2.getY() - point1.getY();

		double cross = (dxc * dyl) - (dyc * dxl);

		if (Math.abs(dxl) >= Math.abs(dyl))
			return dxl > 0 ? point1.getX() <= currPoint.getX() && currPoint.getX() <= point2.getX()
					: point2.getX() <= currPoint.getX() && currPoint.getX() <= point1.getX();
		else
			return dyl > 0 ? point1.getY() <= currPoint.getY() && currPoint.getY() <= point2.getY()
					: point2.getY() <= currPoint.getY() && currPoint.getY() <= point1.getY();

	}

	// **Getters and other helper things **//
	public void addEntity(Entity e) {
		currentEntities.add(e);
	}

	public void removeEntity(Entity e) {
		currentEntities.remove(e);
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

	public void setID(long id) {
		this.id = id;
	}

	public long getID() {
		return id;
	}

	public boolean isHumanAccessible() {
		return humanAccessible;
	}

	public void setHumanAccessible(boolean humanAccessible) {
		this.humanAccessible = humanAccessible;
	}

	public List<Edge> getEdges() {
		return edges;
	}

	public double setup() {
		double length = 0.0;
		for (int i = 0; i < wayPoints.size() - 1; i++) {
			int j = i + 1;
			length += calculateCoordinateDistance(wayPoints.get(i).getGeoCoords(), wayPoints.get(j).getGeoCoords());

			// Build edges at same time
			Edge newEdge = new Edge(wayPoints.get(i), wayPoints.get(j));
			newEdge.takeStatsFromLink(bicycle, cycleWay, roadType, lit, maxSpeed, name, oneWay, lanes);
			edges.add(newEdge);
		}
		weight = length;
		return length;
	}

	public static double calculateCoordinateDistance(Vector2 a, Vector2 b) {
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

	// TODO
	public void moveEntity_BAD(Entity e, Vector2 pos, float moveDist, Vector2 nextNode) {
		// Determine which pair of nodes Entity is sitting between
		Node[] nodes = findClosestNodePair(pos);
		if (nodes == null) {
			System.err.println("LINK: null nodes");
		}
		Node a = nodes[0];
		Vector2 coordsA = a.getGeoCoords();
		Node b = nodes[1];
		Vector2 coordsB = b.getGeoCoords();
		double distanceInKM = calculateCoordinateDistance(coordsA, coordsB);

		// Calculate the length and slope of the Link segment
		double length = distanceInKM;
		double percentageOfMove = moveDist / length;
		double slope = coordsA.calculateSlope(coordsB);

		// Calculate current movement percentage
		double entityDistFromNode = calculateCoordinateDistance(pos, nextNode);
		double percentage = entityDistFromNode / length;

		// Calculate link percentage of move
		double proposedPercentage = percentage + percentageOfMove;

		// If less that 100%, do a simple shift
		if (proposedPercentage < 100) {
			// TODO Should return a VALID or something nice
		}
		// Else if geq 100%, move to next node and add percentage from there
		// unless Node is a traffic-light or other
		else if (proposedPercentage > 100) {
			// TODO Need to tell the next node about it
		}
	}

	public void errLog(Object o) {
		System.err.println("Link Warning: " + o.toString());
	}
}
