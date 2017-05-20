package interactionGraph;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;

import observationModule.VEntity;
import stdSimLib.Agent;
import castleComponents.Entity;
import castleComponents.objects.Vector2;


public class Node {
	String name;
	String type;
	private double x;	//Because of my terribleness, these have to remain
	private double y;
	Vector2 position;
	int outgoingInteraction;
	int incomingInteraction;
	double outgoingTotalWeight;
	double incomingTotalWeight;
	ArrayList<Edge> incomingEdges;
	ArrayList<Edge> outgoingEdges;
	HashSet<Node> connectedNodes;
	
	//Store the agent information inside the Node (who cares about RAM)
	VEntity vAgent = null;
	Agent agent = null;
	
	/**
	 * Creates a new Node with name at position (x,y)
	 * Automatically determines the type from the name.
	 * @param  name Name of the Node
	 * @param  x    The x
	 * @param  y    The y
	 */
	public Node(String name, double x, double y){
		this.name = name;
		type = name.replaceAll("[0-9]", "");
		this.setX(x);
		this.setY(y);
		position = new Vector2(this.getX(),this.getY());
		outgoingInteraction = 0;
		incomingInteraction = 0;
		outgoingTotalWeight = 0;
		incomingTotalWeight = 0;
		incomingEdges = new ArrayList<Edge>();
		outgoingEdges = new ArrayList<Edge>();
		connectedNodes = new HashSet<Node>();
	}
	
	public Node(String name, Vector2 position){
		this.name = name;
		this.position = new Vector2(position);
		this.x = this.position.getX();
		this.y = this.position.getY();		
		outgoingInteraction = 0;
		incomingInteraction = 0;
		outgoingTotalWeight = 0;
		incomingTotalWeight = 0;
		incomingEdges = new ArrayList<Edge>();
		outgoingEdges = new ArrayList<Edge>();
		connectedNodes = new HashSet<Node>();
	}
	
	public Node(Agent agent){
		this.name = agent.getID();
		this.position = new Vector2(agent.getPosition());
		this.x = this.position.getX();
		this.y = this.position.getY();	
		outgoingInteraction = 0;
		incomingInteraction = 0;
		outgoingTotalWeight = 0;
		incomingTotalWeight = 0;
		incomingEdges = new ArrayList<Edge>();
		outgoingEdges = new ArrayList<Edge>();
		connectedNodes = new HashSet<Node>();
	}
	
	public Node(Entity agent){
		this.vAgent = new VEntity(agent);
		this.name = agent.getID();
		if (agent.getPosition() == null){
			this.position = new Vector2();
		} else {
			this.position = new Vector2(agent.getPosition());
		}
		this.x = this.position.getX();
		this.y = this.position.getY();	
		outgoingInteraction = 0;
		incomingInteraction = 0;
		outgoingTotalWeight = 0;
		incomingTotalWeight = 0;
		incomingEdges = new ArrayList<Edge>();
		outgoingEdges = new ArrayList<Edge>();
		connectedNodes = new HashSet<Node>();
	}

	/**
	 * Creates a copy of an existing node.
	 * @param  cloneNode The node to copy.
	 */

	public Node(Node cloneNode){
		this.name = cloneNode.getName();
		this.type = cloneNode.getType();
		this.setX(cloneNode.getX());
		this.setY(cloneNode.getY());
		this.position = new Vector2(cloneNode.getPosition());
	}

	/**
	 * Subtracts the passed in Vector2 from the 
	 * Vector2 representing this Nodes position.
	 * Used for normalising a graph, generally around (0,0)
	 * @param normVector [description]
	 */
	public void normalise(Vector2 normVector){
		position.subtract(normVector);
		setX(getX() - normVector.getX());
		setY(getY() - normVector.getY());
	}
	
	/**
	 * Returns the name of the Node.
	 * @return [description]
	 */
	public String getName(){
		return name;
	}
	
	/**
	 * Returns the type as a String of the Node.
	 * @return [description]
	 */
	public String getType(){
		return type;
	}
	
	/**
	 * Multiplies the current position coordinates
	 * by a scalar.
	 * @param multiplier A scalar.
	 */
	public void multiplyCoords(double multiplier){
		position.setX(position.getX() * multiplier);
		position.setY(position.getY() * multiplier);
		setX(getX() * multiplier);
		setY(getY() * multiplier);
	}
	
	/**
	 * Returns the current position of the Node.
	 * @return [description]
	 */
	public Vector2 getPosition(){
		return position;
	}
	
	
	/**
	 * A handy-dandy String representation of the Node.
	 * (Why tabs?)
	 * @return [description]
	 */
	@Override
	public String toString(){
		String out = name + "\t" + getX() + "\t" + getY();
		return out;
	}
	
	public void newCoords(double newX, double newY){
		this.setX(newX);
		this.setY(newY);
		position = new Vector2(this.getX(),this.getY());
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}
	
	//Lots of stuff. 10/08/16
	
	public void addIncomingEdge(Edge e){
		incomingEdges.add(e);
		connectedNodes.add(e.getStart());
		addIncomingWeight(e.getWeight());
	}
	
	public void addOutgoingEdge(Edge e){
		outgoingEdges.add(e);
		connectedNodes.add(e.getEnd());
		addOutgoingWeight(e.getWeight());
	}
	
	public void incrementOutgoingInteractions(){
		outgoingInteraction++;
	}
	
	public void incrementIncomingInteractions(){
		incomingInteraction++;
	}
	
	public void addOutgoingWeight(double weight){
		outgoingTotalWeight += weight;		
	}
	
	public double getOutgoingWeight(){
		return outgoingTotalWeight;
	}
	
	public double getIncomingWeight(){
		return incomingTotalWeight;
	}
	
	public void addIncomingWeight(double weight){		
		incomingTotalWeight += weight;
	}
	
	public double getTotalWeight(){
		return outgoingTotalWeight + incomingTotalWeight;
	}
	 
	
	public int outGoingInteractions(){
		return outgoingInteraction;
	}
	
	public int incomingInteractions(){
		return incomingInteraction;
	}
	
	public int totalInteractions(){
		return outGoingInteractions() + incomingInteractions();
	}
	
	public void merge(Node n){
		
	}
	
	public static Comparator<Node> sortByX(){
		return new Comparator<Node>() {
			@Override
			public int compare(Node n1, Node n2) {
				Vector2 o1 = n1.getPosition();
				Vector2 o2 = n2.getPosition();
				if (o1.getX() > o2.getX()){
					return 1;
				} else if (o1.getX() < o2.getX()){
					return -1;
				} else {
					return 0;
				}
			}
		};
	}
	
	public static Comparator<Node> sortByY(){
		return new Comparator<Node>() {
			@Override
			public int compare(Node n1, Node n2) {
				Vector2 o1 = n1.getPosition();
				Vector2 o2 = n2.getPosition();
				if (o1.getY() > o2.getY()){
					return 1;
				} else if (o1.getY() < o2.getY()){
					return -1;
				} else {
					return 0;
				}
			}
		};
	}
	
	
	//************Only useful if they have VAgents stored
	
	
	public VEntity getVAgent(){
		return vAgent;
	}
	
	public static Comparator<Node> sortByVAgentName(){
		return new Comparator<Node>() {
			@Override
			public int compare(Node n1, Node n2) {
				String v1 = n1.getVAgent().getName();
				String v2 = n2.getVAgent().getName();
				return v1.compareToIgnoreCase(v2);
			}
		};
	}
	
	public HashSet<Node> getConnectedNodes(){
		return connectedNodes;
	}
	
	
}