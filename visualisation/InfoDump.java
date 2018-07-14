package visualisation;

import java.util.ArrayList;
import java.util.HashMap;

import observationTool.DataCollector_FileSystem;
import observationTool.VEntity;
import stdSimLib.Parameter;

public class InfoDump {

	String datasetID;
	String db = "simulations";
	HashMap<String, VAgentSpec> specs; // <AgentName, VAgentSpec>
	int numberOfSteps;
	int currentTime = 0;
	DataCollector_FileSystem collector;
	int startTime = 0;

	public static void main(String[] args) {
		String dsid = args[0];
		InfoDump id = new InfoDump(dsid);
	}

	public InfoDump(String datasetID) {
		this.datasetID = datasetID;
		// Connect to DB
		collector = new DataCollector_FileSystem(db);
		// Get access to the desired dataset
		collector.setCollection(this.datasetID);

		// Get useful information about the system
		numberOfSteps = collector.getTerminationStep();
		HashMap<String, String> params = collector.getInitialisationParameters();
		//Get all steps
		int totalSteps = numberOfSteps;
		//For each step
		for (int i = 0; i < totalSteps; i++) {
			//Get the agents we want
			System.out.print("Step:"+i+",");
			ArrayList<VEntity> subComms = new ArrayList<VEntity>(collector.buildVGroupMap(i).values());
			double avgConsensus = 0;
			for (VEntity v : subComms) {
				//Print out the thing we want
//				String out = v.getID();
//				out += "(consensusLevel:";
				//TODO
				if (v.getParameterValue("consensusLevel") == null) {
					avgConsensus += -1.0;
				} else {
					avgConsensus += Double.parseDouble(((Parameter<?>)v.getParameterValue("consensusLevel")).getCurrentValue());
				}
//				out += ")";
//				System.out.println(out);
			}
			avgConsensus = avgConsensus / subComms.size();
			System.out.println("consensus:"+avgConsensus);
		}
	}


}
