package visualisation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import observationTool.DataCollector_FileSystem;
import observationTool.VEntity;

public class ViewInConsole {

	String datasetID;
	String db = "simulations";
	HashMap<String, VAgentSpec> specs; // <AgentName, VAgentSpec>
	int numberOfSteps;
	int currentTime = 0;
	DataCollector_FileSystem collector;
	int startTime = 0;

	public static void main(String[] args) {
		ViewInConsole vic = new ViewInConsole();
		if (args.length < 1) {
			System.err.println("No args. Dying");
			System.exit(0);
		}
		String dsid = args[0];
		vic.newSimulation(dsid);
		vic.begin();

		Scanner keyboard = new Scanner(System.in);
		char keyGet = 'x';
		while (keyGet != 'q') {
			String nextLine = keyboard.nextLine();
			if (nextLine.length() <= 0) {
				continue;
			}
			keyGet = nextLine.charAt(0);
			if (keyGet == 'd') {
				vic.stepForward();
			} else if (keyGet == 'a') {
				vic.stepBack();
			}
		}
	}

	public ViewInConsole() {

	}

	public void newSimulation(String datasetID) {
		this.datasetID = datasetID;
		// Connect to DB
		collector = new DataCollector_FileSystem(db);
		// Get access to the desired dataset
		collector.setCollection(this.datasetID);

		// Get useful information about the system
		numberOfSteps = collector.getTerminationStep();
		HashMap<String, String> params = collector.getInitialisationParameters();
	}

	public void begin() {
		System.out.println("Begin vis of " + datasetID);
		stepToTime(startTime);
	}

	public void stepForward() {
		stepToTime(currentTime + 1);
	}

	public void stepBack() {
		stepToTime(currentTime - 1);
	}

	public void restart() {
		currentTime = 0;
		stepToTime(0);
	}

	public void stepToTime(int time) {
		currentTime = time;
		if (currentTime > numberOfSteps) {
			currentTime = numberOfSteps;
		}
		if (currentTime < startTime) {
			currentTime = startTime;
		}

		// Hardcoding for SN
		// Get subcomms and comm
//		ArrayList<VEntity> comms = new ArrayList<VEntity>(collector.buildVEnvMap(time).values());
		ArrayList<VEntity> subComms = new ArrayList<VEntity>(collector.buildVGroupMap(time).values());

		// Now we display stuff from them
		System.out.println("Step: " + time);
//		for (VEntity v : comms) {
//
//		}

		for (VEntity v : subComms) {

		}

	}
}
