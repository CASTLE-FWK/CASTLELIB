package castleComponents.representations.MapGraph;

import java.util.HashSet;

import castleComponents.Entity;

public class Edge {
	Node nodeA;
	Node nodeB;
	double weight;
	double distanceInKM;
	String id;
	HashSet<Entity> entities;
	
	//Map Graph specifics
	boolean bicycle = false;
	String cycleWay = "";
	String roadType = "";
	boolean lit = false;
	int maxSpeed = 0;
	String name = "";
	boolean oneWay = false;
	int lanes = 0;
	boolean humanAccessible = false;
	
	public void takeStatsFromLink(boolean b, String c, String r, boolean l, int m, String n, boolean o, int la, boolean hu) {
		setBicycle(b);
		setCycleWay(c);
		setRoadType(r);
		setLit(l);
		if (m == 0) {
			setMaxSpeed(50);
		} else {
			setMaxSpeed(m);
		}
		
		setName(n);
		setOneWay(o);
		setLanes(la);
		setHumanAccessible(hu);
	}

	public Edge(Node a, Node b) {
		this.nodeA = a;
		this.nodeB = b;
		id = a.getID() < b.getID() ? "" + a.getID() + b.getID() : "" + b.getID() + a.getID();
		weight = this.nodeA.getGeoCoords().calculateDistance(this.nodeB.getGeoCoords());
		distanceInKM = Link.calculateCoordinateDistance(this.nodeA.getGeoCoords(), this.nodeB.getGeoCoords());
		entities = new HashSet<Entity>();
	}

	public boolean isNodeConnected(Node n) {
		return n.hasSameID(nodeA) || n.hasSameID(nodeB);
	}

	public boolean containsBothNodes(Node a, Node b) {
		return (a.hasSameID(nodeA) && b.hasSameID(nodeB)) || (b.hasSameID(nodeA) && a.hasSameID(nodeB));
	}
	
	public boolean containsNodeID(long id) {
		return (nodeA.getID() == id || nodeB.getID() == id);
	}
	
	public Node getOtherEnd(Node n) {
		if (n == nodeA) {
			return nodeB;
		} else if (n == nodeB) {
			return nodeA;
		}
		return null;
	}

	public Node getNodeA() {
		return nodeA;
	}

	public void setNodeA(Node nodeA) {
		this.nodeA = nodeA;
	}

	public Node getNodeB() {
		return nodeB;
	}

	public void setNodeB(Node nodeB) {
		this.nodeB = nodeB;
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	public double getDistanceInKM() {
		return distanceInKM;
	}

	public void setDistanceInKM(double distanceInKM) {
		this.distanceInKM = distanceInKM;
	}

	public String getID() {
		return id;
	}

	public void errLog(Object o) {
		System.err.println("Edge Warning: " + o.toString());
	}
	
	public HashSet<Entity> getEntities(){
		return entities;
	}
	public void addEntity(Entity e) {
		entities.add(e);
	}
	
	public void removeEntity(Entity e) {
		entities.remove(e);
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

	public boolean isHumanAccessible() {
		return humanAccessible;
	}

	public void setHumanAccessible(boolean humanAccessible) {
		this.humanAccessible = humanAccessible;
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

}
