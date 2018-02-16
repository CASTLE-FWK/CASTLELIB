package observationTool;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.lang.model.type.IntersectionType;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;

import interactionGraph.Edge;
import interactionGraph.InteractionGraph;
import interactionGraph.Node;
import stdSimLib.Interaction;

public class DataCollector_FileSystem {

	//
	String filePathRoot = "";
	String filepathStepsRoot = "";
	final String STEP = "Step";
	final String JSON = ".json";
	final String AGENTS = "Agents";
	final String ENVIRONMENTS = "Environments";
	final String GROUPS = "Groups";

	public DataCollector_FileSystem(String fp) {
		filePathRoot = fp;
		filepathStepsRoot = filePathRoot + "/steps";
	}
	
	public void setCollection(String fp) {
		filePathRoot = fp;
		filepathStepsRoot = filePathRoot + "/steps";
	}
	
	//TODO
	public HashMap<String, String> getInitialisationParameters(){
		HashMap<String, String> ip = new HashMap<String, String>();
		return ip;
	}

	public ArrayList<VEntity> buildVAgentList(int stepNumber) {
		ArrayList<VEntity> vAgents = new ArrayList<VEntity>();
		// Open file
		JsonObject file = parseFile(buildFilePath(stepNumber));
		JsonArray agents = file.get(AGENTS).asArray();
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
	
	//TODO
	public HashMap<String, ArrayList<Interaction>> getAgentInteractionMap(int stepNumber){
		return null;
	}

	public HashMap<String, VEntity> buildVAgentMap(int stepNumber) {
		HashMap<String, VEntity> vAgents = new HashMap<String, VEntity>();
		JsonObject file = parseFile(buildFilePath(stepNumber));
		JsonArray agents = file.get(AGENTS).asArray();
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

	public InteractionGraph buildInteractionGraph(int stepNumber) {
		InteractionGraph ig = new InteractionGraph();
		HashMap<String, VEntity> agMap = buildVAgentMap(stepNumber);
		ArrayList<Interaction> interactions = getAllInteractionsFromStep(stepNumber);
		Iterator<Entry<String, VEntity>> it = agMap.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, VEntity> pair = (Map.Entry<String, VEntity>) it.next();
			VEntity agt = pair.getValue();
			ig.addNode(new Node(agt));
		}

		for (Interaction inter : interactions) {
			Node start = ig.findNode(inter.getAgentFromAsString());
			Node end = ig.findNode(inter.getAgentToAsString());
			if (start != null && end != null) {
				ig.addEdge(new Edge(start, end, inter.getType(), inter.getOccurrence()));
			}
		}

		return ig;
	}

	public ArrayList<Interaction> getAllInteractionsFromStep(int stepNumber) {
		// Go through each entity and pull out the interactions list
		JsonObject file = parseFile(buildFilePath(stepNumber));
		JsonArray agents = file.get(AGENTS).asArray();
		ArrayList<Interaction> interactions = new ArrayList<Interaction>();
		for (int i = 0; i < agents.size(); i++) {
			JsonObject obj = agents.get(i).asObject();
			interactions.addAll(getInteractionsFromEntity(obj));
		}
		JsonArray groups = file.get(GROUPS).asArray();
		for (int i = 0; i < groups.size(); i++) {
			JsonObject obj = groups.get(i).asObject();
			interactions.addAll(getInteractionsFromEntity(obj));
		}

		JsonArray environments = file.get(ENVIRONMENTS).asArray();
		for (int i = 0; i < environments.size(); i++) {
			JsonObject obj = environments.get(i).asObject();
			interactions.addAll(getInteractionsFromEntity(obj));
		}

		return interactions;
	}

	public int countInteractionsInStep(int stepNumber) {
		int counter = 0;
		// Go through each entity and pull out the interactions list
		JsonObject file = parseFile(buildFilePath(stepNumber));
		JsonArray agents = file.get(AGENTS).asArray();
		ArrayList<Interaction> interactions = new ArrayList<Interaction>();
		for (int i = 0; i < agents.size(); i++) {
			JsonObject obj = agents.get(i).asObject();
			counter += countInteractionsFromEntity(obj);
		}
		JsonArray groups = file.get(GROUPS).asArray();
		for (int i = 0; i < groups.size(); i++) {
			JsonObject obj = groups.get(i).asObject();
			counter += countInteractionsFromEntity(obj);
		}

		JsonArray environments = file.get(ENVIRONMENTS).asArray();
		for (int i = 0; i < environments.size(); i++) {
			JsonObject obj = environments.get(i).asObject();
			counter += countInteractionsFromEntity(obj);
		}

		return counter;
	}

	// TODO
	public int getTerminationStep() {
		return -1;
	}
	
	public void restart() {}
	public void close() {}

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

	public int countInteractionsFromEntity(JsonObject obj) {
		return obj.get("interactions").asArray().size();
	}

	public ArrayList<Interaction> getInteractionsFromEntity(JsonObject obj) {
		ArrayList<Interaction> interactions = new ArrayList<Interaction>();
		JsonArray inters = obj.get("interactions").asArray();
		for (int j = 0; j < inters.size(); j++) {
			JsonObject iObj = inters.get(j).asObject();
			String from = iObj.get("interaction-from").asString();
			String to = iObj.get("interaction-to").asString();
			String type = iObj.get("interaction-type").asString();
			interactions.add(new Interaction(from, to, type));
		}
		return interactions;
	}
}