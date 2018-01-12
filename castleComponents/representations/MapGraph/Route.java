package castleComponents.representations.MapGraph;

import castleComponents.objects.List;
import castleComponents.representations.MapGraph.Node;

public class Route {
	List<Node> nodesToVisit;
	Node prevNode;
	boolean leavingSimulation = false;

	public Route() {
		nodesToVisit = new List<Node>();
		prevNode = null;
	}

	public Node getNextNode() {
		if (isEmpty()) {
			return null;
		}
		return nodesToVisit.get(0);
	}
	
	public boolean isEmpty() {
		return nodesToVisit.size() <= 0;
	}

	public Node getFinalNode() {
		return nodesToVisit.getLast();
	}

	public void nodeVisted() {
		prevNode = nodesToVisit.remove(0);
	}

	public void addNode(Node n) {
		nodesToVisit.add(n);
	}

	public Node getFollowingNode(Node n) {
		int currIndex = nodesToVisit.indexOf(n);
		if (currIndex == nodesToVisit.size()) {
			return nodesToVisit.get(currIndex + 1);
		} else {
			return nodesToVisit.get(currIndex);
		}
		
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
			str += n.toString() + ",";
		}
		str += " ]";
		return str;
	}

	public Node getPrevNode() {
		return prevNode;
	}

	public void setPrevNode(Node prevNode) {
		this.prevNode = prevNode;
	}
}
