package interactionGraph;

import java.util.ArrayList;
import java.util.Collections;

import castleComponents.objects.Vector2;
import interLib.Agent;
import interLib.Interaction;
import interLib.Snapshot;

public class InteractionGraph {
	ArrayList<Node> nodes;
	ArrayList<Edge> edges;
	int numberOfNodes = 0;
	int numberOfEdges = 0;
	int snapshotInterval;
	int currentTimeStep = 0;
	String experimentName = "";
	String runName = "";
	

	
	public InteractionGraph(Snapshot snapshot){
		ArrayList<Agent> agents = snapshot.getAgents();
		ArrayList<Interaction> interactions = snapshot.getInteractions();
		nodes = new ArrayList<Node>();
		edges = new ArrayList<Edge>();
		
		snapshotInterval = snapshot.getSnapshotInterval();
		currentTimeStep = snapshot.getCurrentTime();
		experimentName = snapshot.getExperimentName();
		runName = snapshot.getRunName();
		
		for (Agent agent : agents){
			nodes.add(new Node(agent));
		}
		numberOfNodes = nodes.size();
		
		for (Interaction interaction : interactions){
			edges.add(new Edge(interaction));
		}
		numberOfEdges = edges.size();	
	}
	
	public InteractionGraph() {
		// TODO Auto-generated constructor stub
	}
	
	/*Used for HDA*/
	public void updateNodesDistance(Node nodeA, Node nodeB, double weight, double HDAStrength){		
		double newX, newY, newXModified, newYModified;
		newX = Math.abs(nodeA.getX() - nodeB.getX());
		newY = Math.abs(nodeA.getY() - nodeB.getY());
		
		newXModified = newX / (HDAStrength * weight);
		newYModified = newY / (HDAStrength * weight);
		
		if (nodeA.getX() < nodeB.getX()){
			nodeA.setX(nodeA.getX() + ((newX/HDAStrength) - newXModified));
			nodeB.setX(nodeB.getX() - ((newX/HDAStrength) + newXModified));
		} else if (nodeA.getX() > nodeB.getX()){
			nodeB.setX(nodeB.getX() + ((newX/HDAStrength) - newXModified));
			nodeA.setX(nodeA.getX() - ((newX/HDAStrength) + newXModified));
		}
		
		if (nodeA.getY() < nodeB.getY()){
			nodeA.setY(nodeA.getY() + ((newY/HDAStrength) - newYModified));
			nodeB.setY(nodeB.getY() - ((newY/HDAStrength) + newYModified));
		} else if (nodeA.getY() > nodeB.getY()){
			nodeB.setY(nodeB.getY() + ((newY/HDAStrength) - newYModified));
			nodeA.setY(nodeA.getY() - ((newY/HDAStrength) + newYModified));
		}			
	}
	
	public void normalise(){
		Vector2 agg = new Vector2();
		for (Node n : nodes){
			agg.add(n.getPosition());
		}
		agg.divide(nodes.size());
		for (Node n : nodes){
			n.normalise(agg);
		}
	}

	public void sortEdges(){
		Collections.sort(edges);
	}


	/**
	 * @return the nodes
	 */
	public ArrayList<Node> getNodes() {
		return nodes;
	}


	/**
	 * @param nodes the nodes to set
	 */
	public void setNodes(ArrayList<Node> nodes) {
		this.nodes = nodes;
	}


	/**
	 * @return the edges
	 */
	public ArrayList<Edge> getEdges() {
		return edges;
	}


	/**
	 * @param edges the edges to set
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
	 * @param numberOfNodes the numberOfNodes to set
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
	 * @param numberOfEdges the numberOfEdges to set
	 */
	public void setNumberOfEdges(int numberOfEdges) {
		this.numberOfEdges = numberOfEdges;
	}

	/**
	 * @return the snapshotInterval
	 */
	public int getSnapshotInterval() {
		return snapshotInterval;
	}

	/**
	 * @param snapshotInterval the snapshotInterval to set
	 */
	public void setSnapshotInterval(int snapshotInterval) {
		this.snapshotInterval = snapshotInterval;
	}

	/**
	 * @return the currentTimeStep
	 */
	public int getCurrentTimeStep() {
		return currentTimeStep;
	}

	/**
	 * @param currentTimeStep the currentTimeStep to set
	 */
	public void setCurrentTimeStep(int currentTimeStep) {
		this.currentTimeStep = currentTimeStep;
	}

	/**
	 * @return the experimentName
	 */
	public String getExperimentName() {
		return experimentName;
	}

	/**
	 * @return the runName
	 */
	public String getRunName() {
		return runName;
	}
}
