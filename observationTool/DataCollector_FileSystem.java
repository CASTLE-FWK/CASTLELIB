package observationTool;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;

import interactionGraph.InteractionGraph;
import stdSimLib.Interaction;

public class DataCollector_FileSystem {

	//
	String filePathRoot = "";
	String filepathStepsRoot = "";
	final String STEP = "Step";
	final String JSON = ".json";

	public DataCollector_FileSystem(String fp) {
		filePathRoot = fp;
		filepathStepsRoot = filePathRoot + "/steps";
	}

	public ArrayList<VEntity> buildVAgentList(int stepNumber) {
		ArrayList<VEntity> vAgents = new ArrayList<VEntity>();
		// Open file
		JsonObject file = parseFile(buildFilePath(stepNumber));
		JsonArray agents = file.get("Agents").asArray();
		for (int i = 0; i < agents.size(); i++) {
			JsonObject obj = agents.get(i).asObject();
			String name = obj.get("agent-name").asString();
			String id = obj.get("agent-ID").asString();
			String type = obj.get("agent-type").asString();
			VEntity tmpVA = new VEntity(name, type, id);
			JsonArray params = obj.get("parameters").asArray();
			for (int j = 0; j < params.size(); j++) {
				JsonObject d = params.get(i).asObject();
				String pName = d.get("parameter-name").asString();
				String pType = d.get("parameter-type").asString();
				String pValue = d.get("parameter-value").asString();
				tmpVA.addParameterFromString(pName, pType, pValue);
			}
			vAgents.add(tmpVA);
		}

		return vAgents;
	}

	public HashMap<String, VEntity> buildVAgentMap(int stepNumber) {
		HashMap<String, VEntity> vAgents = new HashMap<String, VEntity>();
		JsonObject file = parseFile(buildFilePath(stepNumber));
		JsonArray agents = file.get("Agents").asArray();
		for (int i = 0; i < agents.size(); i++) {
			JsonObject obj = agents.get(i).asObject();
			String name = obj.get("agent-name").asString();
			String id = obj.get("agent-ID").asString();
			String type = obj.get("agent-type").asString();
			VEntity tmpVA = new VEntity(name, type, id);
			JsonArray params = obj.get("parameters").asArray();
			for (int j = 0; j < params.size(); j++) {
				JsonObject d = params.get(i).asObject();
				String pName = d.get("parameter-name").asString();
				String pType = d.get("parameter-type").asString();
				String pValue = d.get("parameter-value").asString();
				tmpVA.addParameterFromString(pName, pType, pValue);
			}
			vAgents.put(tmpVA.getName(), tmpVA);
		}
		return vAgents;
	}

	//TODO
	public InteractionGraph buildInteractionGraph(int stepNumber) {
		InteractionGraph ig = new InteractionGraph();
		HashMap<String, VEntity> agMap = buildVAgentMap(stepNumber);
		ArrayList<Interaction> interactions = getAllInteractionsFromStep(stepNumber);
		return null;
	}

	//TODO
	public ArrayList<Interaction> getAllInteractionsFromStep(int stepNumber) {

		return null;
	}

	//TODO
	public int countInteractionsInStep(int stepNumber) {
		return -1;
	}
	
	//TODO
	public int getTerminationStep() {
		return -1;
	}

	// All the helper functions are below

	public String buildFilePath(int stepNumber) {
		return filepathStepsRoot + "/" + STEP + stepNumber + JSON;
	}

	public JsonObject parseFile(String fp) {
		try {
			return Json.parse((new FileReader(fp))).asObject();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
