package stdSimLib.utilities;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

import castleComponents.objects.List;
import castleComponents.representations.MapGraph.Edge;
import castleComponents.representations.MapGraph.Node;

public class Dijkstra {

	private Set<Node> unSettledNodes;
	private Map<Node, Node> predecessors;
	private Map<Node, Double> distance;
	private PriorityQueue<Node> nodeQueue;

	public Dijkstra() {

	}

	public void execute(Node source) {
		unSettledNodes = new HashSet<Node>();
		distance = new HashMap<Node, Double>();
		predecessors = new HashMap<Node, Node>();
		nodeQueue = new PriorityQueue<Node>();
		distance.put(source, 0.0);
		unSettledNodes.add(source);

		// Optimisation
		source.setDikstraMinDistance(0.0);
		nodeQueue.add(source);
		while (!nodeQueue.isEmpty()) {
			Node n = nodeQueue.poll();
			for (Edge e : n.getEdges()) {
				Node v = e.getOtherEnd(n);
				double weight = e.getDistanceInKM();
				double distThroughN = n.getDijkstraMinDistance() + weight;
				if (distThroughN < v.getDijkstraMinDistance()) {
					nodeQueue.remove(n);
					v.setDikstraMinDistance(distThroughN);
					predecessors.put(v, n);
					nodeQueue.add(v);
				}
			}
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