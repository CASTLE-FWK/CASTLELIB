package castleComponents.representations.MapGraph;

public class Outcome{
	OutcomeResult result;
	double distanceAlongEdge;
	Node destNode;
	MapGraph mapGraph;
	Edge edge;
	public Outcome(OutcomeResult r, double distAlongEdge, Node destNode, MapGraph mapGraph, Edge edge) {
		this.result = r;
		this.distanceAlongEdge = distAlongEdge;
		this.destNode = destNode;
		this.mapGraph = mapGraph;
		this.edge = edge;
	}
	public OutcomeResult getResult() {
		return result;
	}
	public void setResult(OutcomeResult result) {
		this.result = result;
	}
	public double getDistanceAlongEdge() {
		return distanceAlongEdge;
	}
	public void setDistanceAlongEdge(double distanceAlongEdge) {
		this.distanceAlongEdge = distanceAlongEdge;
	}
	public Node getDestNode() {
		return destNode;
	}
	public void setDestNode(Node destNode) {
		this.destNode = destNode;
	}
	public MapGraph getMapGraph() {
		return mapGraph;
	}
	public void setMapGraph(MapGraph mapGraph) {
		this.mapGraph = mapGraph;
	}
	public Edge getEdge() {
		return edge;
	}
	public void setEdge(Edge edge) {
		this.edge = edge;
	}
	

	
//	public Outcome(Rest)
}


