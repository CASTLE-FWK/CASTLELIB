package castleComponents.representations.MapGraph;

import castleComponents.objects.List;
import castleComponents.objects.Vector2;
import castleComponents.representations.MapGraph.Node;

public class Route {
	List<Node> nodesToVisit;
	List<Edge> edgesToTraverse;
	Node prevNode;
	Edge prevEdge;
	boolean leavingSimulation = false;
	int edgePointer = 0;
	int nodePointer = 0;

	public double totalDistance = 0.0;

	// Route status
	double distanceAlongEdge = 0.0;
	Edge currentEdge;
	Heading heading = Heading.NONE;

	public Route() {
		nodesToVisit = new List<Node>();
		edgesToTraverse = new List<Edge>();
		prevNode = null;
		distanceAlongEdge = 0.0;
		leavingSimulation = false;
		currentEdge = null;
		heading = Heading.NONE;
		prevEdge = null;
		edgePointer = 0;
		nodePointer = 0;
	}

	public Route(Route r) {
		nodesToVisit = new List<Node>();
		prevNode = null;
		edgesToTraverse = new List<Edge>();
		clone(r);
	}

	public void clone(Route r) {
		nodesToVisit.addAll(r.getNodesToVisit());
		edgesToTraverse.addAll(r.getEdgesToTraverse());
		prevNode = r.getPrevNode();
		prevEdge = r.getPrevEdge();
		distanceAlongEdge = r.getDistanceAlongEdge();
		currentEdge = r.getCurrentEdge();
		heading = r.getHeading();
		edgePointer = r.getEdgePointer();
		nodePointer = r.getNodePointer();
	}

	public Node getNextNode() {
		if (noMoreNodes()) {
			return null;
		}
		return nodesToVisit.get(nodePointer);
	}

	public Edge getNextEdge() {
		if (noMoreEdges()) {
			return null;
		}
		return edgesToTraverse.get(edgePointer);
	}

	// public Edge getFollowingEdge(Edge te) {
	// for (int i = 0; i < edgesToTraverse.size(); i++) {
	// if (edgesToTraverse.get(i).getID().compareToIgnoreCase(te.getID()) == 0) {
	// if (i + 1 < edgesToTraverse.size()) {
	// return edgesToTraverse.get(i + 1);
	// }
	// }
	// }
	// return te;
	// }

	public boolean noMoreNodes() {
		return nodesToVisit.size() <= nodePointer;
	}

	public boolean noMoreEdges() {
		return edgesToTraverse.size() <= edgePointer;
	}

	public Node getFinalNode() {
		return nodesToVisit.getLast();
	}

	public Edge getFinalEdge() {
		return edgesToTraverse.getLast();
	}

	public void nodeVisted() {
		if (!noMoreNodes()) {
			prevNode = nodesToVisit.get(nodePointer);
			nodePointer++;
		}
		if (!noMoreEdges()) {
			prevEdge = edgesToTraverse.get(edgePointer);
			edgePointer++;
		}
	}

	public void addNode(Node n) {
		nodesToVisit.add(n);
	}

	public Node getFollowingNode(Node n) {
		int currIndex = nodesToVisit.indexOf(n);
		if (currIndex < nodesToVisit.size()) {
			return nodesToVisit.get(currIndex + 1);
		} else {
			return nodesToVisit.get(currIndex);
		}
	}

	public Edge getFollowingEdge(Edge e) {
		int currIndex = edgesToTraverse.indexOf(e);
		if (currIndex < edgesToTraverse.size()) {
			return edgesToTraverse.get(currIndex + 1);
		} else {
			return edgesToTraverse.get(currIndex);
		}
	}

	public void addNodes(List<Node> n) {
		if (nodesToVisit == null) {
			nodesToVisit = new List<Node>();
		}
		nodesToVisit.addAll(n);
		edgesToTraverse = new List<Edge>();
		// Build the edge path
		for (int i = 0; i < nodesToVisit.size() - 1; i++) {
			Edge e = nodesToVisit.get(i).findEdgeWithNode(nodesToVisit.get(i + 1));
			edgesToTraverse.add(e);
			totalDistance += e.getDistanceInKM();
		}
		heading = getHeading();

	}

	public boolean isLeavingSimulation() {
		return leavingSimulation;
	}

	public void setLeavingSimulation(boolean leavingSimulation) {
		this.leavingSimulation = leavingSimulation;
	}

	public String stats() {
		String str = "Route Stats = [";
		str += "number of nodes: " + nodesToVisit.size();
		str += ", distance (km): " + totalDistance;
		str += ", current node #: " + nodePointer;
		str += ", current edge #: " + edgePointer;
		str += " ]";
		return str;
	}

	public double getTotalDistance() {
		return totalDistance;
	}

	public String toShortString() {
		String str = "Route = [ ";
		str += "number of nodes: " + nodesToVisit.size();
		str += ", path: (";
		for (Node n : nodesToVisit) {
			str += "<" + n.getID() + n.getCoords() + ">";
		}
		str += " ]";
		return str;
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

	public double getDistanceAlongEdge() {
		return distanceAlongEdge;
	}

	public void setDistanceAlongEdge(double distanceAlongEdge) {
		this.distanceAlongEdge = distanceAlongEdge;
	}

	public Edge getCurrentEdge() {
		return currentEdge;
	}

	public void setCurrentEdge(Edge currentEdge) {
		this.currentEdge = currentEdge;
	}

	public Heading getHeading() {
		if (nodesToVisit.size() == 0) {
			//You are already at your destination
			this.heading = Heading.NONE;
			return this.heading;
		}
		if (nodePointer >= nodesToVisit.size()) {
			nodePointer = nodesToVisit.size() - 2; //Dodgy, 
		}
		Node a = nodesToVisit.get(nodePointer);
		Node b = nodesToVisit.get(nodePointer + 1);
		Vector2 diffs = new Vector2(a.getCoords()).subtract(b.getCoords());
		this.heading = Heading.getHeadingFromDoubles(diffs.getX(), diffs.getY());
		return this.heading;
	}

	public void setHeading(Heading heading) {
		this.heading = heading;
	}

	public List<Node> getNodesToVisit() {
		return nodesToVisit;
	}

	public void setNodesToVisit(List<Node> nodesToVisit) {
		this.nodesToVisit = nodesToVisit;
	}

	public List<Edge> getEdgesToTraverse() {
		return edgesToTraverse;
	}

	public void setEdgesToTraverse(List<Edge> edgesToTraverse) {
		this.edgesToTraverse = edgesToTraverse;
	}

	public Edge getPrevEdge() {
		return prevEdge;
	}

	public int getEdgePointer() {
		return edgePointer;
	}

	public void setEdgePointer(int edgePointer) {
		this.edgePointer = edgePointer;
	}

	public int getNodePointer() {
		return nodePointer;
	}

	public void setNodePointer(int nodePointer) {
		this.nodePointer = nodePointer;
	}

	public void setPrevEdge(Edge prevEdge) {
		this.prevEdge = prevEdge;
	}

}
