package castleComponents.representations.MapGraph;

import castleComponents.objects.Vector2;

public class Edge {
	Node nodeA;
	Node nodeB;
	double weight;
	double distanceInKM;
	String id;

	public Edge(Node a, Node b) {
		this.nodeA = a;
		this.nodeB = b;
		id = a.getID() < b.getID() ? "" + a.getID() + b.getID() : "" + b.getID() + a.getID();
		weight = this.nodeA.getGeoCoords().calculateDistance(this.nodeB.getGeoCoords());
		distanceInKM = Link.calculateCoordinateDistance(this.nodeA.getGeoCoords(), this.nodeB.getGeoCoords());
	}

	public boolean isNodeConnected(Node n) {
		return n.hasSameID(nodeA) || n.hasSameID(nodeB);
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

}
