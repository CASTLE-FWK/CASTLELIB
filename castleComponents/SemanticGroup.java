package castleComponents;

/**
 * What does this do?
 * 
 */

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonValue;

public class SemanticGroup extends Entity {
	protected ArrayList<Agent> storedAgents;
	ArrayList<SemanticGroup> storedCapsules;
	protected Collection<Agent> agentsAsSet;

	int dummyAgents = 2;
	int dummySubCapsules = 0;

	// TODO: Need a way to store the lower and upper bounds of a group
	// This also needs to be defined somewhere in CASL.
	// Possibly in the initialisation parameters section
	// Should default to L: 0, U: INF

	// Need ways to add multiple agents of differnet types
	// Have them interact correctly
	// Blablah
	public SemanticGroup(String capType, EntityID id) {
		super(capType, id);
		storedAgents = new ArrayList<Agent>();
		storedCapsules = new ArrayList<SemanticGroup>();

	}

	public void addStoredAgents(ArrayList<Agent> ags) {
		storedAgents.addAll(ags);
	}

	void step() {

	}

	@Override
	public void run() {
		simulate();
	}

	public void simulate() {

		if (getCurrentPhase() == Phase.SETUP) {
			// Broadcast phase to storedCapsules
			broadcast(MessageType.CLOCK, getCurrentStep());
			broadcast(MessageType.PHASE, getCurrentPhase());

			phase_Setup();

			// Wait for storedCapsule ACKS
		} else if (getCurrentPhase() == Phase.ACTION) {
			// Broadcast phase to storedCapsules
			broadcast(MessageType.PHASE, getCurrentPhase());
			phase_Action();
		} else if (getCurrentPhase() == Phase.CLEANUP) {
			// Broadcast phase to storedCapsules
			broadcast(MessageType.PHASE, getCurrentPhase());

			phase_Cleanup();
			// Wait for storedCapsule ACKS

		}
	}

	/**
	 * Sends a message to all tier 1 entities
	 * 
	 * @param messageType
	 *            [description]
	 * @param contents
	 *            [description]
	 * @return [description]
	 */
	protected <T> void broadcast(MessageType messageType, T contents) {
		Message<T> message = new Message<T>(messageType, contents, getCurrentStep());

		// Really you want generate a bunch of messages and put them in the queue

		for (SemanticGroup cap : storedCapsules) {
			cap.receiveMessage(new Message<T>(messageType, contents, getCurrentStep(), cap));
		}
	}

	@Override
	public String toString() {
		return "CAPSULE SUPERCLASS (OVERRIDE THIS)";
	}

}