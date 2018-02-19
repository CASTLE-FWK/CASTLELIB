package interactionGraph;

import stdSimLib.Interaction;

public class Edge implements Comparable<Edge>{
	Node start, end;
	private double weight;
	String type;
	
	public Edge(Node start, Node end, String type, double weight){
		this.start = start;
		this.end = end;
		this.setWeight(weight);
		this.type = type;
		this.start.addIncomingEdge(this);
		this.end.addOutgoingEdge(this);
		this.start.incrementOutgoingInteractions();
		this.end.incrementOutgoingInteractions();
		this.start.addIncomingWeight(weight);
		this.end.addOutgoingWeight(weight);
	}
	
	public Edge(Interaction interaction){
		if (interaction.getAgentFrom() == null){
			System.out.println("INTERACTNULL");
		}
		start = new Node(interaction.getAgentFrom().getID(),interaction.getAgentFrom().getPosition());
		end = new Node(interaction.getAgentTo().getID(), interaction.getAgentTo().getPosition());
		weight = interaction.getOccurrence();
		type = interaction.getType();
	}
	
	public Node getStart(){
		return start;
		
	}
	
	public Node getEnd(){
		return end;
	}
	public double getWeight(){
		return weight;
	}
	
	@Override
	public String toString(){
		String out = "Node-From: "+start.getName()+",\tNode-To: "+end.getName()+",\tType: "+type+",\tWeight: "+getWeight();
		return out;
	}
	
	public String toMedusaString(){
		String out = start.getName()+"\t"+end.getName()+"\ti "+type+"\tc "+getWeight();
		return out;
	}
	
	public boolean containsNode(Node n){
		return (start.getName().equals(n.getName()) || end.getName().equals(n.getName()));
	}
	
	public boolean containsNodes(Node n1, Node n2){
		return (containsNode(n1) && containsNode(n2));
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	@Override
	public int compareTo(Edge o) {
		double diff = getWeight() - o.getWeight();
		if(diff < 0.0){
			return -1;
		} else if (diff > 0.0){
			return 1;
		}
		
		return 0;
		
	}
}