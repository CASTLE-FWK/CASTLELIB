package visualisation;

import java.util.HashMap;

import observationTool.DataCollector_FileSystem;

public class ViewInConsole {

	String datasetID;
	String db = "simulations";
	HashMap<String, VAgentSpec> specs; // <AgentName, VAgentSpec>
	int numberOfSteps;
	int currentTime = 0;
	DataCollector_FileSystem collector;
	int startTime = 0;

	public static void main(String[] args) {
		String dsid = args[0];
		
	}
	
	public void newSimulation(String datasetID) {
		this.datasetID = datasetID;
		// Connect to DB
		collector = new DataCollector_FileSystem(db);
		// Get access to the desired dataset
		collector.setCollection(this.datasetID);

		// Wouldn't it be great to grab the entire dataset and jam it into memory here?

		// Get useful information about the system
		numberOfSteps = collector.getTerminationStep();
		HashMap<String, String> params = collector.getInitialisationParameters();
	}
	
	public void begin() {
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

	
	}

	
}
