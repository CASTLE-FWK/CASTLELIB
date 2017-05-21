package castleComponents.representations;

import stdSimLib.Interaction;

public class NetworkEdge implements Comparable<NetworkEdge>{
	NetworkNode start, end;
	private double weight;
	String type;
	
	public NetworkEdge(NetworkNode start, NetworkNode end, String type, double weight){
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
	public NetworkNode getStart(){
		return start;
		
	}
	
	public NetworkNode getEnd(){
		return end;
	}
	public double getWeight(){
		return weight;
	}
	
	@Override
	public String toString(){
		String out = start.getName()+"\t"+end.getName()+"\tType: "+type+"\tWeight: "+getWeight();
		return out;
	}
	
	public String toMedusaString(){
		String out = start.getName()+"\t"+end.getName()+"\ti "+type+"\tc "+getWeight();
		return out;
	}
	
	public boolean containsNetworkNode(NetworkNode n){
		return (start.getName().equals(n.getName()) || end.getName().equals(n.getName()));
	}
	
	public boolean containsNetworkNodes(NetworkNode n1, NetworkNode n2){
		return (containsNetworkNode(n1) && containsNetworkNode(n2));
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	@Override
	public int compareTo(NetworkEdge o) {
		double diff = getWeight() - o.getWeight();
		if(diff < 0.0){
			return -1;
		} else if (diff > 0.0){
			return 1;
		}
		
		return 0;
		
	}
}
