package castleComponents;

/**
 * What does this do?
 * 
 */
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import dataGenerator.OutputToJSON_Mongo;
import stdSimLib.Parameter;
import stdSimLib.utilities.Utilities;

public class CASSystem{
	int clock = 0;
	int numberOfSteps = 10;
	EntityID sysID;
	int dummyEnvs = 1;
	String systemName;

	//DataGeneration
	OutputToJSON_Mongo dbOutputter;
	String executionID;
	protected ArrayList<Parameter<?>> parameters;
	String dbID;
	
	long startTime = 0;
	long timeSinceLastStep = 0;
	long elapsedTime = 0;

	//Tier below
	ArrayList<Environment> storedEnvironments;
	ArrayList<SemanticGroup> storedCapsules;
	MessageQueue messageQueue;
	Phase currentPhase;

	//TESTING
	int numCaps;

	public CASSystem(int steps, int numCaps){
		//Basic inits
		storedEnvironments = new ArrayList<Environment>();
		storedCapsules = new ArrayList<SemanticGroup>();
		
		sysID = new EntityID("System",0);
		numberOfSteps = steps;
		currentPhase = Phase.SETUP;
		
		String name = "THESYSTEM";
		
		executionID = Utilities.generateTimeID();
		dbID = Utilities.generateID();
		parameters = new ArrayList<Parameter<?>>();
		
		dbOutputter = new OutputToJSON_Mongo(name.replaceAll(" ", "_").toLowerCase(), executionID, dbID);
		

		//TESTING
		this.numCaps = numCaps;

		//Parse init file with Environment & Capsule descriptors
		dummy();
		simulate();
	}

	void simulate(){
		//Initialize clock
		clock = 0;
		startTime = System.currentTimeMillis();
		//Broadcast clock to tier1 entities
		broadcast(MessageType.CLOCK,clock);

		//Wait for tier1 ACKS


		//Begin the loop
		for (int i = 0; i < numberOfSteps; i++){
			/*******SETUP PHASE******/

			//Update currentPhase to SETUP
			currentPhase = Phase.SETUP;

			//Broadcast clock to tier1 entities
			broadcast(MessageType.CLOCK, clock);
			System.out.println("***************CURRENT STEP IS "+clock+"***************");

			//Broadcast Phase.SETUP to tier1 entities
			broadcast(MessageType.PHASE, currentPhase);

			//Wait for tier1 ACKS (diff from thread acks)
			ExecutorService envExecutor = Executors.newFixedThreadPool(dummyEnvs);
			for (Environment env : storedEnvironments){
				envExecutor.execute(env);
			}
			//Caps as well
			envExecutor.shutdown();
			while(!envExecutor.isTerminated()){
				//HAHAHAHAH EWWWWWWW WHAT IS THIS? THIS CAN"T BE ACTUAL JAVA
			}

			/*******ACTION PHASE******/

			//Update currentPhase to ACTION
			currentPhase = Phase.ACTION;
			
			//Broadcast Phase.ACTION to tier1 entities
			broadcast(MessageType.PHASE, currentPhase);

			//Wait for tier1 ACKS
			envExecutor = Executors.newFixedThreadPool(dummyEnvs);
			for (Environment env : storedEnvironments){
				envExecutor.execute(env);
			}
			//Caps as well
			envExecutor.shutdown();
			while(!envExecutor.isTerminated()){
				//HAHAHAHAH EWWWWWWW WHAT IS THIS? THIS CAN"T BE ACTUAL JAVA
			}

			/*******CLEANUP PHASE******/
			
			//Update currentPhase to CLEANUP
			currentPhase = Phase.CLEANUP;
		
			//Broadcast Phase.CLEANUP to tier1 entities
			broadcast(MessageType.PHASE,currentPhase);

			//Wait for tier1 ACKS (diff from thread acks)
			envExecutor = Executors.newFixedThreadPool(dummyEnvs);
			for (Environment env : storedEnvironments){
				envExecutor.execute(env);
			}
			//Caps as well
			envExecutor.shutdown();
			while(!envExecutor.isTerminated()){
				//HAHAHAHAH EWWWWWWW WHAT IS THIS? THIS CAN"T BE ACTUAL JAVA
			}
			
			long currTime = System.currentTimeMillis();
			timeSinceLastStep = currTime - startTime;
			elapsedTime += timeSinceLastStep;
			startTime = currTime;
			
			//Send data to database
			//TODO: Thread this (like the honours one) because it will become a BIG problem
			updateDatabase();

			//incrementClock
			clock = clock + 1;


		}
	}

	void dummy(){
		//Each Env and capsule live on their own thread
		//Init some storedEnvironments
		
//		for (int i = 0; i < dummyEnvs; i++){
//			Environment tmpEnv = new Environment("EnvType"+i,0, this.numCaps);
//			storedEnvironments.add(tmpEnv);
//		}

		//Each Env goes in its own thread
		// ExecutorService envExecutor = Executors.newFixedThreadPool(dummyEnvs);
		// for (Environment env : storedEnvironments){
		// 	envExecutor.execute(env);
		// }

		

		//Init some storedCapsules
//		for (int i = 0; i < 3; i++){
//			SemanticGroup tmpCapsule = new SemanticGroup("CapsuleType"+1, 0);
//			storedCapsules.add(tmpCapsule);
//		}

		//Each tier1 Capsule goes in its own thread
	}

	/**
	 * Sends a message to all tier 1 entities
	 * @param  messageType [description]
	 * @param  contents    [description]
	 * @return             [description]
	 */
	<T> void broadcast(MessageType messageType, T contents){
		Message<T> message = new Message<T>(messageType, contents, clock);

		//Really you want generate a bunch of messages and put them in the queue

		for (Environment env : storedEnvironments){
			env.receiveMessage(new Message<T>(messageType, contents, clock, env));
		}

		for (SemanticGroup cap : storedCapsules){
			cap.receiveMessage(new Message<T>(messageType, contents, clock, cap));
		}

		// notifyAll(); //??
	}
	
	//Send output to database
	public void updateDatabase(){
		//Fill System section
		dbOutputter.dumpSystem(systemName, executionID, clock, numberOfSteps, timeSinceLastStep, elapsedTime);
		
		//Fill Environment section
		
		
		//Fill Group section
		
		
		//Fill Agent section
	}
	
/*	Context<Environment> build(Context<Environment> context){
		//Create environments
		//Environment env1 = new Environment;
		 * 
		//Build Capsules in the environments
		//env1.buildCapsules(initString);
		 * 
		//Add environment subcontext (that contains capsules)
		//context.addSubContext(env.getCapsuleContext());		
	}*/
}