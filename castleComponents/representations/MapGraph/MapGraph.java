package castleComponents.representations.MapGraph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;

import castleComponents.objects.Vector2;
import interactionGraph.Edge;
import interactionGraph.Node;

public class MapGraph {
	HashSet<Node> nodes; // TODO: Replace with HashMap
	HashMap<String, Node> nodesMap;
	ArrayList<Edge> edges;
	int numberOfNodes = 0;
	int numberOfEdges = 0;
	int snapshotInterval;
	int currentTimeStep = 0;

	public MapGraph() {
		nodes = new HashSet<Node>();
		edges = new ArrayList<Edge>();
		nodesMap = new HashMap<String, Node>();
	}

	public void normalise() {
		Vector2 agg = new Vector2();
		for (Node n : nodes) {
			agg.add(n.getPosition());
		}
		agg.divide(nodes.size());
		for (Node n : nodes) {
			n.normalise(agg);
		}
	}

	public void sortEdges() {
		Collections.sort(edges);
	}

	// todo change
	public ArrayList<Node> sortNodesOnEdgeCount() {
		ArrayList<Node> nodes = new ArrayList<Node>(this.nodes);
		Collections.sort(nodes, new Comparator<Node>() {
			@Override
			public int compare(Node n1, Node n2) {
				if (n1.getTotalWeight() < n2.getTotalWeight()) {
					return -1;
				} else if (n1.getTotalWeight() < n2.getTotalWeight()) {
					return 1;
				} else {
					return 0;
				}
			}
		});
		return nodes;
	}

	/**
	 * @return the nodes
	 */
	public ArrayList<Node> getNodes() {
		return new ArrayList<Node>(this.nodes);
	}

	/**
	 * @param nodes
	 *            the nodes to set
	 */
	public void setNodes(ArrayList<Node> nodes) {
		this.nodes = new HashSet<Node>(nodes);
	}

	/**
	 * @return the edges
	 */
	public ArrayList<Edge> getEdges() {
		return edges;
	}

	/**
	 * @param edges
	 *            the edges to set
	 */
	public void setEdges(ArrayList<Edge> edges) {
		this.edges = edges;
	}

	/**
	 * @return the numberOfNodes
	 */
	public int getNumberOfNodes() {
		return numberOfNodes;
	}

	/**
	 * @param numberOfNodes
	 *            the numberOfNodes to set
	 */
	public void setNumberOfNodes(int numberOfNodes) {
		this.numberOfNodes = numberOfNodes;
	}

	/**
	 * @return the numberOfEdges
	 */
	public int getNumberOfEdges() {
		return numberOfEdges;
	}

	/**
	 * @param numberOfEdges
	 *            the numberOfEdges to set
	 */
	public void setNumberOfEdges(int numberOfEdges) {
		this.numberOfEdges = numberOfEdges;
	}

	public void addNode(Node n) {
		nodes.add(n);
		nodesMap.put(n.getName(), n);
	}

	public Node findNode(String name) {
		return nodesMap.get(name);
	}

	public void addEdge(Edge e) {
		edges.add(e);
		e.getStart().addOutgoingEdge(e);
		e.getEnd().addIncomingEdge(e);
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

}
