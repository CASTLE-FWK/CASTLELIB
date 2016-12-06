package repastGroups;

/**
 * What does this do?
 * 
 */

import java.util.ArrayList;
import java.util.concurrent.ExecutorService; 
import java.util.concurrent.Executors;

public class Environment extends Entity {

	protected ArrayList<Capsule> storedCapsules;
	ArrayList<Environment> storedEnvironments;
	MessageQueue messageQueue;
	int numberOfCapsules = 1;


	ExecutorService capExecutor;

	//CELL CAPSULE STUFF
	int capsulesX = 10;
	int capsulesY = 10;

	//2D representation of capsules
	protected CapsuleGrid theGrid;

	public Environment(String envType, long uid, int numCaps){
		super(envType,uid);
		numberOfCapsules = numCaps;
		capsulesX = numCaps;
		capsulesY = numCaps;

		//Init communications stuff
		messageQueue = new MessageQueue();

		//Init capsules to be contained
		storedCapsules = new ArrayList<Capsule>(); //TODO: Switch to a HashMap or something

		//Init sub-environments
		storedEnvironments = new ArrayList<Environment>();

		//Init 2D representation
//		theGrid = new CapsuleGrid(capsulesX,capsulesY);


		//TESTING
//		dummyAdd(capsulesX * capsulesY);
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
			capExecutor = Executors.newFixedThreadPool(numberOfCapsules);
			for (Capsule cap : storedCapsules){
				capExecutor.execute(cap);
			}
			capExecutor.shutdown();
			while (!capExecutor.isTerminated()){
			}

			//Wait for storedCapsule ACKS
		} else if (getCurrentPhase() == Phase.ACTION) {
			//Broadcast phase to storedCapsules
			broadcast(MessageType.PHASE,getCurrentPhase());
			phase_Action();

			capExecutor = Executors.newFixedThreadPool(numberOfCapsules);
			for (Capsule cap : storedCapsules){
				capExecutor.execute(cap);
			}
			capExecutor.shutdown();
			while (!capExecutor.isTerminated()){
			}

			//Wait for storedCapsule ACKS

			

		} else if (getCurrentPhase() == Phase.CLEANUP) {
			//Broadcast phase to storedCapsules
			broadcast(MessageType.PHASE,getCurrentPhase());

			phase_Cleanup();
			ExecutorService capExecutor = Executors.newFixedThreadPool(numberOfCapsules);
			for (Capsule cap : storedCapsules){
				capExecutor.execute(cap);
			}
			capExecutor.shutdown();
			while (!capExecutor.isTerminated()){
			}
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

		for (Environment env : storedEnvironments){
			env.receiveMessage(new Message<T>(messageType, contents, getCurrentStep(), env));
		}

		for (Capsule cap : storedCapsules){
			cap.receiveMessage(new Message<T>(messageType, contents, getCurrentStep(), cap));
		}
	}

	<T> void addMessageToQueue(Message<?> msg){
		messageQueue.addNewMessage(msg);
	}

	void sendAllMessages(){
		while (!messageQueue.isEmpty()){
			Message<?> msg = messageQueue.removeMessage();
			Entity recipient = msg.getRecipient();
			recipient.receiveMessage(msg);
		}
	}


	
	public void dummyAdd(int c){
		// for (int i = 0; i < c; i++){
		// 	// Capsule tmpCapsule = new Capsule(getEntityID().toString()+"_capsule",i);
		// 	// storedCapsules.add(tmpCapsule);
		// 	Capsule tmpCC = new Capsule(getEntityID().toString()+"_Capsule",i, 100, 100, this);
		// 	storedCapsules.add(tmpCC);
		// }

		for (int i = 0; i < capsulesX; i++){
			for (int j = 0; j < capsulesY; j++){
//				Capsule tmpCC = new Capsule(getEntityID().toString()+"_Capsule",i, 100, 100, this,i,j);
				Capsule tmpC = new Capsule(getEntityID().toString()+"_Capsule",i);
				storedCapsules.add(tmpC);
				theGrid.addCell(tmpC, i, j);
			}
		}
		for (Capsule cc : storedCapsules){
			cc.initialize();
		}
	}
	

	//CapsuleGrid stuff
	public Capsule getNeighbour_U(int x, int y){
		return theGrid.getNeighbour_U(x,y);
	}

	public Capsule getNeighbour_UR(int x, int y){
		return theGrid.getNeighbour_UR(x,y);
	}

	public Capsule getNeighbour_R(int x, int y){
		return theGrid.getNeighbour_R(x,y);
	}

	public Capsule getNeighbour_DR(int x, int y){
		return theGrid.getNeighbour_DR(x,y);
	}

	public Capsule getNeighbour_D(int x, int y){
		return theGrid.getNeighbour_D(x,y);
	}

	public Capsule getNeighbour_DL(int x, int y){
		return theGrid.getNeighbour_DL(x,y);
	}

	public Capsule getNeighbour_L(int x, int y){
		return theGrid.getNeighbour_L(x,y);
	}

	public Capsule getNeighbour_UL(int x, int y){
		return theGrid.getNeighbour_UL(x,y);
	}
}

