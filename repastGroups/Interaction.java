package repastGroups;

public class Interaction implements java.io.Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -262768393273140446L;
	
	Entity agentFrom, agentTo;
	String type;	//Types are defined somewhere...
	int occurrence = 0;
	String agentFromAsString, agentToAsString;
	
	private static final String COMMA = ",";
	
	public Interaction(Agent agentFrom, Agent agentTo, String type){
		this.agentFrom = agentFrom;
		this.agentTo = agentTo;
		this.type = type;
		occurrence = 1;
	}
	
	public Interaction(Agent agentFrom, Agent agentTo, String type, int occurrence){
		this.agentFrom = agentFrom;
		this.agentTo = agentTo;
		this.type = type;
		this.occurrence = occurrence;
	}
	
	public Interaction(String agentFrom, String agentTo, String type){
		agentFromAsString = agentFrom;
		agentToAsString = agentTo;
		this.type = type;
		occurrence = 1;
	}
	
	
	public Interaction(Entity entityFrom, Entity entityTo, String interactionType) {
		this.agentFrom = entityFrom;
		this.agentTo = entityTo;
		this.type = interactionType;
		occurrence = 1;
	}

	@Override
	public String toString(){
		return agentFrom.getID() + COMMA + agentTo.getID() + COMMA + type + COMMA + occurrence;
	}


	/**
	 * @return the agentFrom
	 */
	public Entity getAgentFrom() {
		return agentFrom;
	}


	/**
	 * @return the agentTo
	 */
	public Entity getAgentTo() {
		return agentTo;
	}
	
	public boolean checkAgentPresence(Entity ent){
		return (agentFrom.compareEntity(ent));
	}


	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}


	/**
	 * @return the occurrence
	 */
	public int getOccurrence() {
		return occurrence;
	}
	
	public void setOccurrence(int set){
		occurrence = set;
	}
	
	public void incrementOccurrence(){
		occurrence++;
	}
	
	public String publishInteraction(String open, String close){
		String out = "";
		out += open + "interaction from=\"" + agentFrom.getID() + "\"" + " " + "to=\"" + agentTo.getID() + "\"" + " " + "type=\"" + getType() + "\"" + " " + "count=\"" + getOccurrence() + "\"" + close;   				
		return out;
	}
	
	//TODO: There has to be a faster way to do this.
	public boolean checkForSimilarity(String from, String to, String interactionType){
		return ( (agentFrom.getID().compareToIgnoreCase(from) == 0) && (agentTo.getID().compareToIgnoreCase(to) == 0) && (type.compareToIgnoreCase(interactionType) == 0));  
	}
}

