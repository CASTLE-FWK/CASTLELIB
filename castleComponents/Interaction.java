package castleComponents;

public class Interaction implements java.io.Serializable {

	private static final long serialVersionUID = -262768393273140446L;

	E entityFrom, entityTo;
	InteractionType type; // Types are defined somewhere...
	String name;
	int occurrence = 0;
	String agentFromAsString, agentToAsString;

	private static final String COMMA = ",";

	public static enum InteractionType {
		QUERY, COMMUNICATION, INDIRECT
	};

	public Interaction(E entityFrom, E entityTo, InteractionType type, String name) {
		this.entityFrom = entityFrom;
		this.entityTo = entityTo;
		this.type = type;
		this.name = name;
		occurrence = 1;
	}

	public Interaction(Agent agentFrom, Agent agentTo, String type, int occurrence) {
		this.entityFrom = agentFrom;
		this.entityTo = agentTo;
		this.name = type;
		this.occurrence = occurrence;
	}

	public Interaction(String agentFrom, String agentTo, String type) {
		agentFromAsString = agentFrom;
		agentToAsString = agentTo;
		this.name = type;
		occurrence = 1;
	}

	public Interaction(E entityFrom, E entityTo, String interactionType) {
		this.entityFrom = entityFrom;
		this.entityTo = entityTo;
		this.name = interactionType;
		occurrence = 1;
	}

	@Override
	public String toString() {
		return entityFrom.getID() + COMMA + entityTo.getID() + COMMA + type + COMMA + occurrence;
	}

	/**
	 * @return the agentFrom
	 */
	public E getEntityFrom() {
		return entityFrom;
	}

	/**
	 * @return the agentTo
	 */
	public E getEntityTo() {
		return entityTo;
	}

	public boolean checkAgentPresence(E ent) {
		return (entityFrom.compareEntity(ent));
	}

	/**
	 * @return the type
	 */
	public String getInteractionName() {
		return name;
	}

	public InteractionType getType() {
		return type;
	}

	/**
	 * @return the occurrence
	 */
	public int getOccurrence() {
		return occurrence;
	}

	public void setOccurrence(int set) {
		occurrence = set;
	}

	public void incrementOccurrence() {
		occurrence++;
	}

	public String publishInteraction(String open, String close) {
		String out = "";
		out += open + "interaction from=\"" + entityFrom.getID() + "\"" + " " + "to=\"" + entityTo.getID() + "\"" + " "
				+ "name=\"" + getInteractionName() + "\"" + " " + "count=\"" + getOccurrence() + "\"" + close;
		return out;
	}

	public boolean checkForSimilarity(String from, String to, String interactionType) {
		return ((entityFrom.getID().compareToIgnoreCase(from) == 0) && (entityTo.getID().compareToIgnoreCase(to) == 0)
				&& (name.compareToIgnoreCase(interactionType) == 0));
	}

	public boolean equals(Interaction inter) {
		if (entityFrom.getEntityID().equals(inter.getEntityFrom().getEntityID())
				&& name.equalsIgnoreCase(inter.getInteractionName()) && type == inter.getType()) {
			return true;
		}

		return false;
	}
	
	public String getID(){
		return entityFrom.getID() + entityTo.getID() + type + name;
	}
}
