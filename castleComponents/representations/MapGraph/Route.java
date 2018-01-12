package castleComponents.representations.MapGraph;

import castleComponents.objects.List;
import castleComponents.representations.MapGraph.Node;

public class Route {
	List<Node> nodesToVisit;
	boolean leavingSimulation = false;

	public Route() {
		nodesToVisit = new List<Node>();
	}

	public Node getNextNode() {
		return nodesToVisit.get(0);
	}

	public Node getFinalNode() {
		return nodesToVisit.getLast();
	}

	public void nodeVisted() {
		nodesToVisit.remove(0);
	}

	public void addNode(Node n) {
		nodesToVisit.add(n);
	}

	public Node getFollowingNode(Node n) {
		int currIndex = nodesToVisit.indexOf(n);
		return nodesToVisit.get(currIndex + 1);
	}

	public void addNodes(List<Node> n) {
		if (nodesToVisit == null) {
			nodesToVisit = new List<Node>();
		}
		nodesToVisit.addAll(n);
	}

	public boolean isLeavingSimulation() {
		return leavingSimulation;
	}

	public void setLeavingSimulation(boolean leavingSimulation) {
		this.leavingSimulation = leavingSimulation;
	}
	
	public String toString() {
		String str = "Route = [";
		for (Node n : nodesToVisit) {
			str += n.toString() +",";
		}
		str += " ]";
		return str;
	}
}
