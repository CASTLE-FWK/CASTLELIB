package stdSimLib.utilities;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import castleComponents.objects.List;
import castleComponents.representations.MapGraph.Edge;
import castleComponents.representations.MapGraph.Node;

public class SlowDijkstra {

	private final List<Node> nodes;
	private final List<Edge> edges;
	private Set<Node> settledNodes;
	private Set<Node> unSettledNodes;
	private Map<Node, Node> predecessors;
	private Map<Node, Double> distance;

	public SlowDijkstra(List<Node> n, List<Edge> e) {
		this.nodes = new List<Node>(n);
		this.edges = new List<Edge>(e);
	}

	public void execute(Node source) {
		settledNodes = new HashSet<Node>();
		unSettledNodes = new HashSet<Node>();
		distance = new HashMap<Node, Double>();
		predecessors = new HashMap<Node, Node>();
		distance.put(source, 0.0);
		unSettledNodes.add(source);
		while (unSettledNodes.size() > 0) {
			Node node = getMinimum(unSettledNodes);
			if (node == null) {
				errLog("node is null A");
			}
			settledNodes.add(node);
			unSettledNodes.remove(node);
			findMinimalDistances(node);
		}
	}

	private void findMinimalDistances(Node node) {
		if (node == null) {
			errLog("node is null B");
			System.exit(0);
		}
		HashSet<Node> adjacentNodes = node.getAdjacentNodes();

		for (Node target : adjacentNodes) {
			if (getShortestDistance(target) > getShortestDistance(node) + getDistance(node, target)) {
				distance.put(target, getShortestDistance(node) + getDistance(node, target));
				predecessors.put(target, node);
				unSettledNodes.add(target);
			}
		}
	}

	private double getDistance(Node node, Node target) {
		for (Edge edge : edges) {
			if (edge.containsBothNodes(node, target)) {
				return edge.getDistanceInKM();
			}
		}
		throw new RuntimeException("Should not happen");
	}

	private Node getMinimum(Set<Node> vertexes) {
		Node minimum = null;
		for (Node vertex : vertexes) {
			if (minimum == null) {
				minimum = vertex;
			} else {
				if (getShortestDistance(vertex) < getShortestDistance(minimum)) {
					minimum = vertex;
				}
			}
		}
		return minimum;
	}

	private boolean isSettled(Node vertex) {
		return settledNodes.contains(vertex);
	}

	private double getShortestDistance(Node destination) {
		Double d = distance.get(destination);
		if (d == null) {
			return Double.MAX_VALUE;
		} else {
			return d;
		}
	}

	public List<Node> getPath(Node target) {
		List<Node> path = new List<Node>();
		Node step = target;
		// check if a path exists
		if (predecessors.get(step) == null) {
			return null;
		}
		
		path.add(step);
		while (predecessors.get(step) != null) {
			step = predecessors.get(step);
			path.add(step);
			
		}
		// Put it into the correct order
		Collections.reverse(path);
		return path;
	}

	public void errLog(Object o) {
		System.err.println("Dijkstra Warning: " + o.toString());
	}

}