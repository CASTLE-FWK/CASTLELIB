package castleComponents.representations.MapGraph;

import java.util.Comparator;
import java.util.HashSet;

import castleComponents.objects.Vector2;
import castleComponents.objects.List;

public class Node {
	Vector2 geoCoords;
	double outgoingTotalWeight;
	double incomingTotalWeight;
	List<Link> incomingEdges;
	List<Link> outgoingEdges;
	List<Edge> edges;
	HashSet<Node> adjacentNodes;
	Vector2 coords;

	HashSet<Link> links;

	// OpenStreetMap Extra features
	long id = -1;
	String nodeType = "";
	boolean outOfBounds = false;
	boolean transitNode = false;
	boolean trafficLight = false;

	public Node(long id, Vector2 coord) {
		this.id = id;
		this.geoCoords = new Vector2(coord);
		init();
	}
	
	public Edge findEdgeWithNode(Node n) {
		for (Edge e : edges) {
			if (e.containsBothNodes(this, n)) {
				return e;
			}
		}
		return null;
	}

	public Node() {
		init();
	}

	public void setOutOfBounds(boolean b) {
		this.outOfBounds = b;
	}

	public boolean isOutOfBounds() {
		return this.outOfBounds;
	}

	public void init() {
		incomingEdges = new List<Link>();
		outgoingEdges = new List<Link>();
		adjacentNodes = new HashSet<Node>();
		edges = new List<Edge>();
		links = new HashSet<Link>();
		outgoingTotalWeight = 0;
		incomingTotalWeight = 0;
	}

	public void addEdge(Edge e) {
		edges.add(e);
	}
	
	
	
	public void setID(long id) {
		this.id = id;
	}

	public long getID() {
		return this.id;
	}

	public void setNodeType(String str) {
		this.nodeType = str;
	}

	public String getNodeType() {
		return this.nodeType;
	}

	/**
	 * Creates a copy of an existing node.
	 * 
	 * @param cloneNode
	 *            The node to copy.
	 */
	// TODO
	public Node(Node cloneNode) {
		this.geoCoords = new Vector2(cloneNode.getGeoCoords());
	}

	/**
	 * Subtracts the passed in Vector2 from the Vector2 representing this Nodes
	 * position. Used for normalising a graph, generally around (0,0)
	 * 
	 * @param normVector
	 *            [description]
	 */
	public void normalise(Vector2 normVector) {
		geoCoords.subtract(normVector);
	}

	/**
	 * Multiplies the current position coordinates by a scalar.
	 * 
	 * @param multiplier
	 *            A scalar.
	 */
	public void multiplyCoords(double multiplier) {
		geoCoords.setX(geoCoords.getX() * multiplier);
		geoCoords.setY(geoCoords.getY() * multiplier);
	}

	/**
	 * Returns the current position of the Node.
	 * 
	 * @return [description]
	 */
	public Vector2 getGeoCoords() {
		return geoCoords;
	}

	public void setGeoCoords(Vector2 v) {
		geoCoords = new Vector2(v);
	}

	public void addLink(Link l) {
		links.add(l);
	}

	public HashSet<Link> getLinks() {
		return links;
	}

	@Override
	public String toString() {
		return "Node [id=" + id + ", coords= " + coords + ", geoCoords=" + geoCoords + ", nodeType=" + nodeType
				+ ", number of links=" + links.size() + "]";
	}

	public void newCoords(double newX, double newY) {
		geoCoords = new Vector2(newX, newY);
	}

	public void addOutgoingWeight(double weight) {
		outgoingTotalWeight += weight;
	}

	public double getOutgoingWeight() {
		return outgoingTotalWeight;
	}

	public double getIncomingWeight() {
		return incomingTotalWeight;
	}

	public void addIncomingWeight(double weight) {
		incomingTotalWeight += weight;
	}

	public double getTotalWeight() {
		return outgoingTotalWeight + incomingTotalWeight;
	}

	public static Comparator<Node> sortByX() {
		return new Comparator<Node>() {
			@Override
			public int compare(Node n1, Node n2) {
				Vector2 o1 = n1.getGeoCoords();
				Vector2 o2 = n2.getGeoCoords();
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

	public static Comparator<Node> sortByY() {
		return new Comparator<Node>() {
			@Override
			public int compare(Node n1, Node n2) {
				Vector2 o1 = n1.getGeoCoords();
				Vector2 o2 = n2.getGeoCoords();
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

	public List<Edge> getEdges(){
		return edges;
	}
	
	public boolean hasSameID(Node n) {
		return (n.getID() == getID());
	}
	
	public void setCoords(Vector2 v) {
		this.coords = new Vector2(v);
	}

	public Vector2 getCoords() {
		return this.coords;
	}

	public HashSet<Node> getAdjacentNodes() {
		return adjacentNodes;
	}
	
	public void addAdjacentNode(Node n) {
		adjacentNodes.add(n);
	}

	public boolean isTransitNode() {
		return transitNode;
	}

	public void setTransitNode(boolean transitNode) {
		this.transitNode = transitNode;
	}

	public void errLog(Object o) {
		System.err.println("Edge Warning: " + o.toString());
	}
	
	public void setTrafficLight(boolean b) {
		trafficLight = b;
	}
	public boolean hasTrafficLight() {
		return trafficLight;
	}
}