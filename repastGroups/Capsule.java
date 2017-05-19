package repastGroups;

/**
 * What does this do?
 * 
 */
 
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class Capsule extends Entity {
	HashMap<String,Agent> storedAgents;
	ArrayList<Capsule> storedCapsules;
	protected Collection<Agent> agentsAsSet;
	int dummyAgents = 2;
	int dummySubCapsules = 0;
	
	//Need ways to add multiple agents of differnet types
	//Have them interact correctly
	//Blablah
	public Capsule(String capType, long uid){
		super(capType, uid);
		storedAgents = new HashMap<String,Agent>();
		storedCapsules = new ArrayList<Capsule>();



		//TEsting
		//1) Make some dummy agents, store them in storedAgents
		dummy(dummyAgents);

	}

	void dummy(int d){
		for (int i = 0; i < d; i++){
			Agent tmpAgent = new Agent(getEntityID().toString()+"agent_"+i);
			// storedAgents.put(tmpAgent.getID(),tmpAgent); //Ooooooops
		}
		agentsAsSet = storedAgents.values(); //w/ever
	}


	void step(){
		
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
	public void initialize(){
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
	<T> void broadcast(MessageType messageType, T contents){
		Message<T> message = new Message<T>(messageType, contents, getCurrentStep());

		//Really you want generate a bunch of messages and put them in the queue

		for (Capsule cap : storedCapsules){
			cap.receiveMessage(new Message<T>(messageType, contents, getCurrentStep(), cap));
		}
	}
	
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