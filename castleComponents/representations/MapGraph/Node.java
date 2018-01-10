package castleComponents.representations.MapGraph;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;

import castleComponents.objects.Vector2;

public class Node {
	String name;
	String type;
	private double x;	//Because of my terribleness, these have to remain
	private double y;
	Vector2 coords;
	int outgoingInteraction;
	int incomingInteraction;
	double outgoingTotalWeight;
	double incomingTotalWeight;
	ArrayList<Link> incomingEdges;
	ArrayList<Link> outgoingEdges;
	HashSet<Node> connectedNodes;
	
	//OSM Extra features
	long id = 0;
	String nodeType = "";
	boolean outOfBounds = false;
	
	
	public Node(String name, Vector2 position){
		this.name = name;
		this.coords = new Vector2(position);
		this.x = this.coords.getX();
		this.y = this.coords.getY();		
		outgoingInteraction = 0;
		incomingInteraction = 0;
		outgoingTotalWeight = 0;
		incomingTotalWeight = 0;
		init();
	}
	
	public Node() {
		init();
		
	}
	public void setOutOfBounds(boolean b) {
		this.outOfBounds = b;
	}
	
	public boolean isOutOfBounds() {
		return this.outOfBounds;
	}
	
	public void init() {
		incomingEdges = new ArrayList<Link>();
		outgoingEdges = new ArrayList<Link>();
		connectedNodes = new HashSet<Node>();
	}
	
	public void setID(long id) {
		this.id = id;
	}
	
	public long getID() {
		return this.id;
	}
	
	public void setNodeType(String str) {
		this.nodeType = str;
	}
	
	public String getNodeType() {
		return this.nodeType;
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
		this.coords = new Vector2(cloneNode.getCoords());
	}

	/**
	 * Subtracts the passed in Vector2 from the 
	 * Vector2 representing this Nodes position.
	 * Used for normalising a graph, generally around (0,0)
	 * @param normVector [description]
	 */
	public void normalise(Vector2 normVector){
		coords.subtract(normVector);
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
		coords.setX(coords.getX() * multiplier);
		coords.setY(coords.getY() * multiplier);
		setX(getX() * multiplier);
		setY(getY() * multiplier);
	}
	
	/**
	 * Returns the current position of the Node.
	 * @return [description]
	 */
	public Vector2 getCoords(){
		return coords;
	}
	
	public void setCoords(Vector2 v) {
		coords = new Vector2(v);
		this.x = coords.getX();
		this.y = coords.getY();
	}

	
	@Override
	public String toString() {
		return "Node [id=" + id + ", coords=" + coords + ", nodeType=" + nodeType + "]";
	}

	public void newCoords(double newX, double newY){
		this.setX(newX);
		this.setY(newY);
		coords = new Vector2(this.getX(),this.getY());
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
	
	public static Comparator<Node> sortByX(){
		return new Comparator<Node>() {
			@Override
			public int compare(Node n1, Node n2) {
				Vector2 o1 = n1.getCoords();
				Vector2 o2 = n2.getCoords();
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
				Vector2 o1 = n1.getCoords();
				Vector2 o2 = n2.getCoords();
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
	public HashSet<Node> getConnectedNodes(){
		return connectedNodes;
	}
	
	
}
