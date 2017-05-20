package castleComponents;

/**
 * What does this do?
 * 
 */

import java.util.ArrayList;
import java.util.concurrent.ExecutorService; 
import java.util.concurrent.Executors;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonValue;

import castleComponents.representations.Grid;

public class Environment extends Entity {

	protected ArrayList<SemanticGroup> storedGroups;
	protected ArrayList<Environment> storedEnvironments;
	protected MessageQueue messageQueue;
	protected int numberOfGroups = 1;


	protected ExecutorService groupExecutor;

	//CELL CAPSULE STUFF
	int groupsX = 10;
	int groupsY = 10;

	//2D representation of groups
	protected Grid<SemanticGroup> theGrid;

	public Environment(String envType, EntityID eid){
		super(envType,eid);
//		numberOfCapsules = numCaps;
//		groupsX = numCaps;
//		groupsY = numCaps;

		//Init communications stuff
		messageQueue = new MessageQueue();

		//Init groups to be contained
		storedGroups = new ArrayList<SemanticGroup>(); //TODO: Switch to a HashMap or something

		//Init sub-environments
		storedEnvironments = new ArrayList<Environment>();

		//Init 2D representation
//		theGrid = new CapsuleGrid(groupsX,groupsY);


		//TESTING
//		dummyAdd(groupsX * groupsY);
	}

	@Override
	public void run(){
//		super.run();

		simulate();

		// try {
		// 	Thread.sleep(3000);
		// } catch (Exception e){
		// 	e.printStackTrace();
		// }

		// //Send messages to Capsules
		// for (Capsule cap : storedCapsules){
		// 	cap.receiveMessage("MEOW");
		// }

	}

	public void simulate(){	
		//Accept message

		if (getCurrentPhase() == Phase.SETUP) {
			//Broadcast phase to storedCapsules
			broadcast(MessageType.CLOCK,getCurrentStep());
			broadcast(MessageType.PHASE,getCurrentPhase());

			phase_Setup();
			groupExecutor = Executors.newFixedThreadPool(numberOfGroups);
			for (SemanticGroup cap : storedGroups){
				groupExecutor.execute(cap);
			}
			groupExecutor.shutdown();
			while (!groupExecutor.isTerminated()){
			}

			//Wait for storedCapsule ACKS
		} else if (getCurrentPhase() == Phase.ACTION) {
			//Broadcast phase to storedCapsules
			broadcast(MessageType.PHASE,getCurrentPhase());
			phase_Action();

			groupExecutor = Executors.newFixedThreadPool(numberOfGroups);
			for (SemanticGroup cap : storedGroups){
				groupExecutor.execute(cap);
			}
			groupExecutor.shutdown();
			while (!groupExecutor.isTerminated()){
			}

			//Wait for storedCapsule ACKS

			

		} else if (getCurrentPhase() == Phase.CLEANUP) {
			//Broadcast phase to storedCapsules
			broadcast(MessageType.PHASE,getCurrentPhase());

			phase_Cleanup();
			ExecutorService groupExecutor = Executors.newFixedThreadPool(numberOfGroups);
			for (SemanticGroup cap : storedGroups){
				groupExecutor.execute(cap);
			}
			groupExecutor.shutdown();
			while (!groupExecutor.isTerminated()){
			}
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
		logger.print(getEntityID().toString() + " performing phase_action at step " + getCurrentStep());
	}

	@Override
	public void phase_Cleanup(){
		logger.print(getEntityID().toString() + " performing phase_cleanup at step " + getCurrentStep());
	}


	public ArrayList<SemanticGroup> getContainedSemanticGroups(){
		return storedGroups;
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

		for (Environment env : storedEnvironments){
			env.receiveMessage(new Message<T>(messageType, contents, getCurrentStep(), env));
		}

		for (SemanticGroup cap : storedGroups){
			cap.receiveMessage(new Message<T>(messageType, contents, getCurrentStep(), cap));
		}
	}

	protected <T> void addMessageToQueue(Message<?> msg){
		messageQueue.addNewMessage(msg);
	}

	void sendAllMessages(){
		while (!messageQueue.isEmpty()){
			Message<?> msg = messageQueue.removeMessage();
			Entity recipient = msg.getRecipient();
			recipient.receiveMessage(msg);
		}
	}


	
//	public void dummyAdd(int c){
//		// for (int i = 0; i < c; i++){
//		// 	// Capsule tmpCapsule = new Capsule(getEntityID().toString()+"_capsule",i);
//		// 	// storedCapsules.add(tmpCapsule);
//		// 	Capsule tmpCC = new Capsule(getEntityID().toString()+"_Capsule",i, 100, 100, this);
//		// 	storedCapsules.add(tmpCC);
//		// }
//
//		for (int i = 0; i < groupsX; i++){
//			for (int j = 0; j < groupsY; j++){
////				Capsule tmpCC = new Capsule(getEntityID().toString()+"_Capsule",i, 100, 100, this,i,j);
//				SemanticGroup tmpC = new SemanticGroup(getEntityID().toString()+"_Capsule",i);
//				storedCapsules.add(tmpC);
//				theGrid.addCell(tmpC, i, j);
//			}
//		}
//		for (SemanticGroup cc : storedCapsules){
//			cc.initialise();
//		}
//	}
	

	//CapsuleGrid stuff
	//TODO: Make this generate from generic representations
	public SemanticGroup getNeighbour_U(int x, int y){
		return theGrid.getNeighbour_U(x,y);
	}

	public SemanticGroup getNeighbour_UR(int x, int y){
		return theGrid.getNeighbour_UR(x,y);
	}

	public SemanticGroup getNeighbour_R(int x, int y){
		return theGrid.getNeighbour_R(x,y);
	}

	public SemanticGroup getNeighbour_DR(int x, int y){
		return theGrid.getNeighbour_DR(x,y);
	}

	public SemanticGroup getNeighbour_D(int x, int y){
		return theGrid.getNeighbour_D(x,y);
	}

	public SemanticGroup getNeighbour_DL(int x, int y){
		return theGrid.getNeighbour_DL(x,y);
	}

	public SemanticGroup getNeighbour_L(int x, int y){
		return theGrid.getNeighbour_L(x,y);
	}

	public SemanticGroup getNeighbour_UL(int x, int y){
		return theGrid.getNeighbour_UL(x,y);
	}
}

