package stdSimLib.utilities;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

import castleComponents.objects.List;
import castleComponents.representations.MapGraph.Edge;
import castleComponents.representations.MapGraph.Node;

public class Dijkstra {

	private Map<Node, Node> predecessors;
	private PriorityQueue<Node> nodeQueue;

	public Dijkstra() {
	}

	public void execute(Node source) {
		predecessors = new HashMap<Node, Node>();
		nodeQueue = new PriorityQueue<Node>();

		// Optimisation
		source.setDikstraMinDistance(0.0);
		nodeQueue.add(source);
		while (!nodeQueue.isEmpty()) {
			Node n = nodeQueue.poll();
			for (Edge e : n.getEdges()) {
				Node v = e.getOtherEnd(n);
				if (v.getNodeType().compareToIgnoreCase("NOGO") == 0) {
					continue;
				}
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