package stdSimLib.utilities;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

import castleComponents.objects.List;
import castleComponents.representations.MapGraph.Edge;
import castleComponents.representations.MapGraph.Node;

public class Dijkstra {

	private Map<Node, Node> predecessors;
	private PriorityQueue<Node> nodeQueue;
	private Map<Node, Double> distance;

	public Dijkstra() {
	}

	public void execute(Node source) {
		predecessors = new HashMap<Node, Node>();
		distance = new HashMap<Node, Double>();
		nodeQueue = new PriorityQueue<Node>(new Comparator<Node>() {

			@Override
			public int compare(Node o1, Node o2) {
				Double d1 = distance.get(o1);
				Double d2 = distance.get(o2);
				return Double.compare(d1, d2);
			}
		});

		// Optimisation
		source.setDijkstraMinDistance(0.0);
		distance.put(source, 0.0);
		nodeQueue.add(source);
		while (!nodeQueue.isEmpty()) {
			Node u = nodeQueue.poll();
			for (Edge e : u.getEdges()) {
				Node v = e.getOtherEnd(u);
//				if (v.getNodeType().compareToIgnoreCase("NOGO") == 0) {
//					continue;
//				}
				double weight = e.getDistanceInKM();
				double distThroughU = getDistance(u) + weight;
				if (distThroughU < getDistance(v)) {
					nodeQueue.remove(v);
					distance.put(v,distThroughU);
					predecessors.put(v, u);
					nodeQueue.add(v);
				}
			}
		}
	}
	
	public double getDistance(Node dest) {
		Double d = distance.get(dest);
		if (d == null) {
			return Double.POSITIVE_INFINITY;
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
class NodeComparator implements Comparator<Node>{

	@Override
	public int compare(Node o1, Node o2) {
		return (int)(o1.getDijkstraMinDistance() - o2.getDijkstraMinDistance());
	}
	
}