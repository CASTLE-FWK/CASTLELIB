package castleComponents.representations.MapGraph;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;

import castleComponents.objects.List;
import castleComponents.objects.Vector2;

public class Node implements Comparable<Node> {
	Vector2 geoCoords;
	double outgoingTotalWeight;
	double incomingTotalWeight;
	HashMap<String, Edge> incomingEdges;
	HashMap<String, Edge> outgoingEdges;
	HashMap<String, Edge> edges;
	HashSet<Node> adjacentNodes;
	Vector2 coords;

	HashSet<Link> links;

	// OpenStreetMap Extra features
	long id = -1;
	String nodeType = "";
	boolean outOfBounds = false;
	boolean transitNode = false;
	boolean trafficLight = false;
	TrafficLight theTrafficLight = null;
	boolean carPark = false;
	Park theCarPark = null;

	// Purely for Dijkstra speed up
	double minDistance = Double.MAX_VALUE;

	public static final String FREE_STATE = "FREE";
	public String nodeState = FREE_STATE;

	public Node(long id, Vector2 coord) {
		this.id = id;
		this.geoCoords = new Vector2(coord);
		init();
	}

	public Edge findEdgeWithNode(Node n) {
		for (Edge e : edges.values()) {
			if (e.containsBothNodes(this, n)) {
				return e;
			}
		}
		return null;
	}

	@Override
	public int hashCode() {
		return (int) id;
	}

	public void removeEdgeWithID(String id) {
		incomingEdges.remove(id);
		outgoingEdges.remove(id);
		edges.remove(id);
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
		incomingEdges = new HashMap<String, Edge>();
		outgoingEdges = new HashMap<String, Edge>();
		adjacentNodes = new HashSet<Node>();
		edges = new HashMap<String, Edge>();
		links = new HashSet<Link>();
		outgoingTotalWeight = 0;
		incomingTotalWeight = 0;
	}

	public void addIncomingEdge(Edge e) {
		incomingEdges.put(e.getID(), e);
	}

	public void addOutgoingEdge(Edge e) {
		outgoingEdges.put(e.getID(), e);
	}

	public void addEdge(Edge e) {
		edges.put(e.getID(), e);
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
				+ ", number of edges=" + edges.size() + "]";
	}

	public boolean isCarPark() {
		return carPark;
	}

	public void setCarPark(boolean carPark) {
		this.carPark = carPark;
	}

	public Park getTheCarPark() {
		return theCarPark;
	}

	public void setTheCarPark(Park theCarPark) {
		this.theCarPark = theCarPark;
	}

	public void createCarPark() {
		this.theCarPark = new Park();
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

	public List<Edge> getEdges() {
		return new List<Edge>(edges.values());
	}

	// public List<Edge> getEdges(){
	// List<Edge> es = new List<Edge>();
	// for (Edge e : edges.values()) {
	// es.add(e);
	// }
	// return es;
	// }

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

	public void setTheTrafficLight(TrafficLight tl) {
		theTrafficLight = tl;
	}

	public TrafficLight getTheTrafficLight() {
		return theTrafficLight;
	}

	public double getDijkstraMinDistance() {
		return minDistance;
	}

	public void setDikstraMinDistance(double d) {
		minDistance = d;
	}

	@Override
	public int compareTo(Node o) {
		return Double.compare(minDistance, o.getDijkstraMinDistance());
	}

	public String getNodeState() {
		return nodeState;
	}

	public void setNodeState(String nodeState) {
		this.nodeState = nodeState;
	}

	public List<Edge> getIncomingEdges() {
		return new List<Edge>(incomingEdges.values());
	}

	public void setIncomingEdges(HashMap<String, Edge> incomingEdges) {
		this.incomingEdges = incomingEdges;
	}

	public List<Edge> getOutgoingEdges() {
		return new List<Edge>(outgoingEdges.values());
	}

	public void setOutgoingEdges(HashMap<String, Edge> outgoingEdges) {
		this.outgoingEdges = outgoingEdges;
	}
}