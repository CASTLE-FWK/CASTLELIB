package interactionGraph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;

import interLib.Agent;
import interLib.Interaction;
import interLib.Snapshot;
import castleComponents.Entity;
import castleComponents.objects.Vector2;

public class InteractionGraph {
	HashSet<Node> nodes; //TODO: Replace with HashMap
	HashMap<String,Node> nodesMap;
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
		nodes = new HashSet<Node>();
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
		nodes = new HashSet<Node>();
		edges = new ArrayList<Edge>();
		nodesMap = new HashMap<String,Node>();
	}
	
	public InteractionGraph(ArrayList<Entity> entities, ArrayList<Interaction> interactions){
		nodes = new HashSet<Node>();
		for (Entity e : entities){
			addNode(new Node(e));
		}
		for (Interaction inter : interactions){
			Node start = findNode(inter.getAgentFromAsString());
			Node end = findNode(inter.getAgentToAsString());
			if (start != null && end != null){
				edges.add(new Edge(start, end, inter.getType(), inter.getOccurrence()));
			}
		}
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
	
	public ArrayList<Node> sortNodesOnEdgeCount(){
		ArrayList<Node> nodes = new ArrayList<Node>(this.nodes);
		Collections.sort(nodes, new Comparator<Node>(){
			@Override
			public int compare(Node n1, Node n2){
				if (n1.getTotalWeight() < n2.getTotalWeight()){
					return -1;
				} else if (n1.getTotalWeight() < n2.getTotalWeight()){
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
	 * @param nodes the nodes to set
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
	
	public void addNode(Node n){
		nodes.add(n);
		nodesMap.put(n.getName(), n);
	}
	
	public Node findNode(String name){
//		for (Node n : nodes){
//			if (n.getName().compareToIgnoreCase(name) == 0){
//				return n;
//			}
//		}
//		return null;
		return nodesMap.get(name);
	}
	
	public void addEdge(Edge e){
		edges.add(e);
		e.getStart().addOutgoingEdge(e);
		e.getEnd().addIncomingEdge(e);
	}
}
