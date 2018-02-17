package castleComponents;

/**
 * What does this do?
 * 
 */
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import dataGenerator.OutputToJSON_Mongo;
import simulator.ExecutionParameters;
import stdSimLib.Parameter;
import stdSimLib.utilities.Utilities;

public class CASSystem {
	int clock = 0;
	int numberOfSteps = 10;
	EntityID sysID;
	int dummyEnvs = 1;
	String systemName;

	// DataGeneration
	OutputToJSON_Mongo dbOutputter;
	String executionID;
	protected ArrayList<Parameter<?>> parameters;
	public ArrayList<Parameter<?>> getParameters() {
		return parameters;
	}

	String dbID;

	protected SimulationInfo simulationInfo;
	protected Logger logger;
	protected Output output;
	protected OutputToJSON_Mongo dbOut = null;

	protected long startTime = 0;
	protected long timeSinceLastStep = 0;
	protected long elapsedTime = 0;

	// Tier below
	ArrayList<Environment> storedEnvironments;
	ArrayList<SemanticGroup> storedCapsules;
	MessageQueue messageQueue;
	Phase currentPhase;

	// TESTING
	int numCaps;

	public CASSystem() {
		parameters = new ArrayList<Parameter<?>>();
	}
	
	public <T> void addParameter(T o, String name) {
		parameters.add(new Parameter<T>(o, name));
	}

	public void setLogger(Logger l) {
		logger = l;
	}

	public void setDBOut(OutputToJSON_Mongo d) {
		dbOut = d;
	}

	public boolean loggerIsNull() {
		return (logger == null);
	}

	public boolean dbIsNull() {
		return (dbOut == null);
	}

	public void simulate() {
		// Initialize clock
		clock = 0;
		startTime = System.currentTimeMillis();
		// Broadcast clock to tier1 entities
		broadcast(MessageType.CLOCK, clock);

		// Wait for tier1 ACKS

		// Begin the loop
		for (int i = 0; i < numberOfSteps; i++) {
			/******* SETUP PHASE ******/

			// Update currentPhase to SETUP
			currentPhase = Phase.SETUP;

			// Broadcast clock to tier1 entities
			broadcast(MessageType.CLOCK, clock);
			System.out.println("***************CURRENT STEP IS " + clock + "***************");

			// Broadcast Phase.SETUP to tier1 entities
			broadcast(MessageType.PHASE, currentPhase);

			// Wait for tier1 ACKS (diff from thread acks)
			ExecutorService envExecutor = Executors.newFixedThreadPool(dummyEnvs);
			for (Environment env : storedEnvironments) {
				envExecutor.execute(env);
			}
			// Caps as well
			envExecutor.shutdown();
			while (!envExecutor.isTerminated()) {
				// HAHAHAHAH EWWWWWWW WHAT IS THIS? THIS CAN"T BE ACTUAL JAVA
			}

			/******* ACTION PHASE ******/

			// Update currentPhase to ACTION
			currentPhase = Phase.ACTION;

			// Broadcast Phase.ACTION to tier1 entities
			broadcast(MessageType.PHASE, currentPhase);

			// Wait for tier1 ACKS
			envExecutor = Executors.newFixedThreadPool(dummyEnvs);
			for (Environment env : storedEnvironments) {
				envExecutor.execute(env);
			}
			// Caps as well
			envExecutor.shutdown();
			while (!envExecutor.isTerminated()) {
				// HAHAHAHAH EWWWWWWW WHAT IS THIS? THIS CAN"T BE ACTUAL JAVA
			}

			/******* CLEANUP PHASE ******/

			// Update currentPhase to CLEANUP
			currentPhase = Phase.CLEANUP;

			// Broadcast Phase.CLEANUP to tier1 entities
			broadcast(MessageType.PHASE, currentPhase);

			// Wait for tier1 ACKS (diff from thread acks)
			envExecutor = Executors.newFixedThreadPool(dummyEnvs);
			for (Environment env : storedEnvironments) {
				envExecutor.execute(env);
			}
			// Caps as well
			envExecutor.shutdown();
			while (!envExecutor.isTerminated()) {
				// HAHAHAHAH EWWWWWWW WHAT IS THIS? THIS CAN"T BE ACTUAL JAVA
			}

			long currTime = System.currentTimeMillis();
			timeSinceLastStep = currTime - startTime;
			elapsedTime += timeSinceLastStep;
			startTime = currTime;

			// Send data to database
			// TODO: Thread this (like the honours one) because it will become a BIG problem
			updateDatabase();

			// incrementClock
			clock = clock + 1;

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
	<T> void broadcast(MessageType messageType, T contents) {
		Message<T> message = new Message<T>(messageType, contents, clock);

		// Really you want generate a bunch of messages and put them in the queue

		for (Environment env : storedEnvironments) {
			env.receiveMessage(new Message<T>(messageType, contents, clock, env));
		}

		for (SemanticGroup cap : storedCapsules) {
			cap.receiveMessage(new Message<T>(messageType, contents, clock, cap));
		}

		// notifyAll(); //??
	}
	
	
	public void initialDatabaseSend() {
		if (dbOutputter == null) {
			errLog("outputter is null");
		}
		dbOutputter.storeInitValues(parameters, Utilities.generateNiceTimeStamp());
	}

	// Send output to database
	public void updateDatabase() {
		// Fill System section
		System.out.println("THE CLOCK: " + clock);
		if (dbOutputter == null) {
			errLog("outputter is null");
		}
		dbOutputter.exportSystemStep(clock, numberOfSteps, timeSinceLastStep, elapsedTime);

		// Fill Environment section

		// Fill Group section

		// Fill Agent section
	}

	public String systemSpecsToString(String sysName, String sysDescription, List<Parameter<?>> params) {
		String out = "Simulation Initialization Details: \n";
		out += "Name: " + sysName + "\n";
		out += "Description: " + sysDescription + "\n";
		out += "Execution Start Time: " + Utilities.generateNiceTimeStamp() + "\n";
		out += "Initialization Parameters:\n";
		for (Parameter<?> p : params) {
			out += "\t" + p.toString() + "\n";
		}
		out += "----------------------------------------";
		return out;
	}

	public void buildClone(ExecutionParameters ep) {
		
	}
	public void runClone() {
		
	}
	
	public void errLog(Object o) {
		System.err.println(getClass().getSimpleName() + " Warning: " + o.toString());
	}
}