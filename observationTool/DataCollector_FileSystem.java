package observationTool;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

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
	String initParamFilePath = "";
	String filepathStepsRoot = "";
	String terminationStatsFilePath = "";
	final String STEP = "Step";
	final String JSON = ".json";
	final String AGENTS = "Agent";
	final String ENVIRONMENTS = "Environment";
	final String GROUPS = "Group";

	// Prefixes
	final String AGENT_PRE = "agent";
	final String ENV_PRE = "environment";
	final String GRP_PRE = "group";
	final String PARAM_PRE = "parameter";
	final String D_NAME = "-name";
	final String D_ID = "-ID";
	final String D_TYPE = "-type";
	final String D_VALUE = "-value";

	// Agent
	final String AGENTNAME = AGENT_PRE + D_NAME;
	final String AGENTID = AGENT_PRE + D_ID;
	final String AGENTTYPE = AGENT_PRE + D_TYPE;

	// PARAMETERS
	final String PARAMNAME = PARAM_PRE + D_NAME;
	final String PARAMVAL = PARAM_PRE + D_VALUE;
	final String PARAMTYPE = PARAM_PRE + D_TYPE;

	// Some speed up things
	ConcurrentHashMap<Integer, Integer> totalNumberOfInteractionsInStep;

	public DataCollector_FileSystem(String fp) {
		setCollection(fp);
		totalNumberOfInteractionsInStep = new ConcurrentHashMap<Integer, Integer>();
	}
	
	public DataCollector_FileSystem(DataCollector_FileSystem c) {
		String fpr = c.getFilePathRoot();
		setCollection(fpr);
		totalNumberOfInteractionsInStep = new ConcurrentHashMap<Integer, Integer>();
	}

	public void setCollection(String fp) {
		filePathRoot = fp;
		filepathStepsRoot = filePathRoot + "/steps";
		initParamFilePath = filePathRoot + "/systemInitialization" + JSON;
		terminationStatsFilePath = filePathRoot + "/termination-statistics" + JSON;
	}

	public HashMap<String, String> getInitialisationParameters() {
		HashMap<String, String> ip = new HashMap<String, String>();
		JsonObject obj = parseFile(initParamFilePath);
		//TODO
		return ip;
	}

	public ArrayList<VEntity> buildVAgentList(int stepNumber) {
		ArrayList<VEntity> vAgents = new ArrayList<VEntity>();
		// Open file
		JsonObject file = parseFile(buildFilePath(stepNumber));
		JsonArray agents = file.get(AGENTS).asArray();
		for (int i = 0; i < agents.size(); i++) {
			JsonObject obj = agents.get(i).asObject();
			if (obj == null) {
				System.err.println("obj is null");
				System.err.println("file is " + buildFilePath(stepNumber));
				System.exit(0);
			}
			if (obj.get(AGENTNAME) == null) {
				System.err.println("agent-name is null");
				System.err.println("file is " + buildFilePath(stepNumber));
				System.exit(0);
			}
			String name = obj.get(AGENTNAME).asString();
			String id = obj.get(AGENTID).asString();
			String type = obj.get(AGENTTYPE).asString();
			VEntity tmpVA = new VEntity(name, type, id);
			JsonArray params = obj.get("parameters").asArray();
			for (int j = 0; j < params.size(); j++) {
				JsonObject d = params.get(j).asObject();
				String pName = d.get(PARAMNAME).asString();
				String pType = d.get(PARAMTYPE).asString();
				String pValue = d.get(PARAMVAL).asString();
				tmpVA.addParameterFromString(pName, pType, pValue);
			}
			vAgents.add(tmpVA);
		}
		return vAgents;
	}

	public HashMap<String, ArrayList<Interaction>> getAgentInteractionMap(int stepNumber) {
		HashMap<String, ArrayList<Interaction>> theMap = new HashMap<String, ArrayList<Interaction>>();
		JsonObject file = parseFile(buildFilePath(stepNumber));
		JsonArray agents = file.get(AGENTS).asArray();
		for (int i = 0; i < agents.size(); i++) {
			JsonObject obj = agents.get(i).asObject();
			String name = obj.get(AGENTNAME).asString();
			theMap.put(name, new ArrayList<Interaction>());
			ArrayList<Interaction> interactions = getInteractionsFromEntity(obj);
			for (Interaction inter : interactions) {
				String from = inter.getAgentFromAsString();
				String to = inter.getAgentToAsString();
				if (!theMap.containsKey(from)) {
					theMap.put(from, new ArrayList<Interaction>());
				}
				if (!theMap.containsKey(to)) {
					theMap.put(to, new ArrayList<Interaction>());
				}
				theMap.get(from).add(inter);
				theMap.get(to).add(inter);
			}
		}
		return theMap;
	}

	public HashMap<String, VEntity> buildVAgentMap(int stepNumber) {
		HashMap<String, VEntity> vAgents = new HashMap<String, VEntity>();
		JsonObject file = parseFile(buildFilePath(stepNumber));
		JsonArray agents = file.get(AGENTS).asArray();
		for (int i = 0; i < agents.size(); i++) {
			JsonObject obj = agents.get(i).asObject();
			if (obj == null) {
				System.err.println("obj is null");
				System.err.println("file is " + buildFilePath(stepNumber));
				System.exit(0);
			}
			if (obj.get(AGENTNAME) == null) {
				System.err.println("agent-name is null");
				System.err.println("file is " + buildFilePath(stepNumber));
				System.exit(0);
			}
			String name = obj.get(AGENTNAME).asString();
			String id = obj.get(AGENTID).asString();
			String type = obj.get(AGENTTYPE).asString();
			VEntity tmpVA = new VEntity(name, type, id);
			JsonArray params = obj.get("parameters").asArray();
			for (int j = 0; j < params.size(); j++) {
				JsonObject d = params.get(j).asObject();
				String pName = d.get(PARAMNAME).asString();
				String pType = d.get(PARAMTYPE).asString();
				String pValue = d.get(PARAMVAL).asString();

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
			if (groups.get(i).isObject()) {
				JsonObject obj = groups.get(i).asObject();
				interactions.addAll(getInteractionsFromEntity(obj));
			}
		}

		JsonArray environments = file.get(ENVIRONMENTS).asArray();
		for (int i = 0; i < environments.size(); i++) {
			if (environments.get(i).isObject()) {
				JsonObject obj = environments.get(i).asObject();
				interactions.addAll(getInteractionsFromEntity(obj));
			}
		}

		return interactions;
	}

	public int countInteractionsInStep(int stepNumber) {
		if (totalNumberOfInteractionsInStep.containsKey(stepNumber)) {
			return totalNumberOfInteractionsInStep.get(stepNumber);
		}
		int counter = 0;
		// Go through each entity and pull out the interactions list
		JsonObject file = parseFile(buildFilePath(stepNumber));
		// System.out.println("stepNumber: "+stepNumber);
		JsonArray agents = file.get(AGENTS).asArray();
		for (int i = 0; i < agents.size(); i++) {
			if (agents.get(i).isObject()) {
				JsonObject obj = agents.get(i).asObject();
				counter += countInteractionsFromEntity(obj);
			}
		}
		JsonArray groups = file.get(GROUPS).asArray();
		for (int i = 0; i < groups.size(); i++) {
			if (groups.get(i).isObject()) {
				JsonObject obj = groups.get(i).asObject();
				counter += countInteractionsFromEntity(obj);
			}
		}

		JsonArray environments = file.get(ENVIRONMENTS).asArray();
		for (int i = 0; i < environments.size(); i++) {
			if (environments.get(i).isObject()) {
				JsonObject obj = environments.get(i).asObject();
				counter += countInteractionsFromEntity(obj);
			}
		}
		totalNumberOfInteractionsInStep.put(stepNumber, counter);
		return counter;
	}

	public int countNumberOfAgentsInStep(int stepNumber) {
		return countEntityType(stepNumber, AGENTS);
	}

	public int countNumberOfEnvironmentsInStep(int stepNumber) {
		return countEntityType(stepNumber, ENVIRONMENTS);
	}

	public int countNumberOfGroupsInStep(int stepNumber) {
		return countEntityType(stepNumber, GROUPS);
	}

	public int countAllEntitiesInStep(int stepNumber) {
		return countNumberOfAgentsInStep(stepNumber) + countNumberOfEnvironmentsInStep(stepNumber)
				+ countNumberOfGroupsInStep(stepNumber);
	}

	public int getTerminationStep() {
		JsonObject obj = parseFile(terminationStatsFilePath);
		return obj.getInt("termination-step", -1);
	}

	/*****************************************************************/
	// All the class specific helper functions are below

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

	public int countEntityType(int stepNumber, String type) {
		JsonObject file = parseFile(buildFilePath(stepNumber));
		JsonArray ents = file.get(type).asArray();
		return ents.size();
	}

	// Unused
	public void restart() {
	}

	public String getFilePathRoot() {
		return filePathRoot;
	}

	public void setFilePathRoot(String filePathRoot) {
		this.filePathRoot = filePathRoot;
	}

	public void close() {
	}
}