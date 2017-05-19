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
	protected HashMap<String,Agent> storedAgents;
	ArrayList<SemanticGroup> storedCapsules;
	protected Collection<Agent> agentsAsSet;
	
	int dummyAgents = 2;
	int dummySubCapsules = 0;
	
	//TODO: Need a way to store the lower and upper bounds of a group
	//This also needs to be defined somewhere in CASL. 
	//Possibly in the initialisation parameters section
	//Should default to L: 0, U: INF
	
	//Need ways to add multiple agents of differnet types
	//Have them interact correctly
	//Blablah
	public SemanticGroup(String capType, EntityID id){
		super(capType, id);
		storedAgents = new HashMap<String,Agent>();
		storedCapsules = new ArrayList<SemanticGroup>();


		//TEsting
		//1) Make some dummy agents, store them in storedAgents
		dummy(dummyAgents);

	}

	void dummy(int d){
		for (int i = 0; i < d; i++){
//			Agent tmpAgent = new Agent(getEntityID()+"agent_"+i);
			// storedAgents.put(tmpAgent.getID(),tmpAgent); //Ooooooops
		}
		agentsAsSet = storedAgents.values(); //w/ever
	}


	void step(){
		
	}

	public ArrayList<Agent> getAgentsAsSet(){
		return new ArrayList<Agent>(storedAgents.values());
	}
	@Override
	public void run(){
		simulate();
	}

	public void simulate(){

		
		if (getCurrentPhase() == Phase.SETUP) {
			//Broadcast phase to storedCapsules
			broadcast(MessageType.CLOCK,getCurrentStep());
			broadcast(MessageType.PHASE,getCurrentPhase());

			phase_Setup();

			//Wait for storedCapsule ACKS
		} else if (getCurrentPhase() == Phase.ACTION) {
			//Broadcast phase to storedCapsules
			broadcast(MessageType.PHASE,getCurrentPhase());
			phase_Action();

			// ExecutorService capExecutor = Executors.newFixedThreadPool(dummySubCapsules);
			// for (Capsule cap : storedCapsules){
			// 	capExecutor.execute(cap);
			// }
			
			// while (!capExecutor.isTerminated()){}

			//Wait for storedCapsule ACKS

			

		} else if (getCurrentPhase() == Phase.CLEANUP) {
			//Broadcast phase to storedCapsules
			broadcast(MessageType.PHASE,getCurrentPhase());

			phase_Cleanup();
			//Wait for storedCapsule ACKS

		}
	}

	@Override
	public void initialise(){
		logger.print(getEntityID().toString() + " performing phase_init at step " + getCurrentStep());
	}

	@Override
	public void phase_Setup(){
		logger.print(getEntityID().toString() + " performing phase_setup at step " + getCurrentStep());
	}

	@Override
	public void phase_Action(){
		// for (Agent agent : agentsAsSet){

		// }
		logger.print(getEntityID().toString() + " performing phase_action at step " + getCurrentStep());
	}

	@Override
	public void phase_Cleanup(){
		logger.print(getEntityID().toString() + " performing phase_cleanup at step " + getCurrentStep());
	}

	/**
	 * Sends a message to all tier 1 entities
	 * @param  messageType [description]
	 * @param  contents    [description]
	 * @return             [description]
	 */
	protected <T> void broadcast(MessageType messageType, T contents){
		Message<T> message = new Message<T>(messageType, contents, getCurrentStep());

		//Really you want generate a bunch of messages and put them in the queue

		for (SemanticGroup cap : storedCapsules){
			cap.receiveMessage(new Message<T>(messageType, contents, getCurrentStep(), cap));
		}
	}
	
	
	/**
	 * Data output section
	 */
	
	/****Repast Stuff****/
//	public Context<Agent> void build(){
		//Context<Agent> agentContext
		//
		//return context;
//	}
	
	@Override
	public String toString(){
		return "CAPSULE SUPERCLASS (OVERRIDE THIS)";
	}
	


	//Intra capsule communication
	//


	//Inter capsule communication 
		//C-C
		//Query
		
		//Comm


		//C-E
		//Query
		
		//Comm

}