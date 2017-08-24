package observationTool;

import interactionGraph.Edge;
import interactionGraph.InteractionGraph;
import interactionGraph.Node;
import stdSimLib.Interaction;
import stdSimLib.Parameter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.bson.Document;

import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Sorts.ascending;
import static java.util.Arrays.asList;

public class DataCollector {

	String db;
	MongoClient mongoClient;
	MongoDatabase theDatabase;
	MongoCollection<Document> currentCollection;

	public DataCollector(String db) {
		this.db = db;
		mongoClient = new MongoClient();
		theDatabase = mongoClient.getDatabase(db);
	}

	public void restart() {
		mongoClient.close();
		mongoClient = new MongoClient();
		theDatabase = mongoClient.getDatabase(db);
	}

	public void close() {
		mongoClient.close();
		mongoClient = null;
	}

	public void setCollection(String coll) {
		this.currentCollection = theDatabase.getCollection(coll);
	}

	public FindIterable<Document> getAllDataInCollection(String collectionID) {
		return currentCollection.find();
	}

	public FindIterable<Document> find(String field, String value) {
		return currentCollection.find(new Document(field, value));
	}

	public AggregateIterable<Document> aggTest() {
		return currentCollection.aggregate(
				asList(new Document("$match", new Document("_id", "step-0")), new Document("$unwind", "$agents"),
						new Document("$match",
								new Document("$and",
										asList(new Document("agents.parameters.parameter-name", "Alive"),
												new Document("agents.parameters.parameter-value", "false")))),
						new Document("$group", new Document("_id", 0).append("count", new Document("$sum", 1))))

		);
	}

	//TODO: Make flexible
	public AggregateIterable<Document> countAgentsWithParameterValues(int stepNumber) {
		return currentCollection.aggregate(asList(new Document("$match", new Document("_id", "step-" + stepNumber)),
				new Document("$unwind", "$agents"),
				new Document("$match",
						new Document("$and",
								asList(new Document("agents.parameters.parameter-name", "Alive"),
										new Document("agents.parameters.parameter-value", "false")))),
				new Document("$group", new Document("_id", 0).append("count", new Document("$sum", 1))))

		);
	}

	public Document getTerminationStatistics() {
		FindIterable<Document> ts = find("_id", "termination-statistics");
		return ts.first();
	}

	public ArrayList<Document> getInitialisationParameters() {
		FindIterable<Document> ip = find("_id", "system-initialisation");
		Document doc = ip.first();
		return doc.get("initialisation-parameters", ArrayList.class);
	}

	public AggregateIterable<Document> getParameterValueFromType(int stepNumber, String agentType,
			String parameterName) {
		return currentCollection.aggregate(asList(new Document("$match", new Document("_id", "step-" + stepNumber)),
				new Document("$unwind", "$agents"),
				new Document("$match", new Document("agents.agent-type", agentType)),
				new Document("$unwind", "$agents.parameters"),
				new Document("$match", new Document("agents.parameters.parameter-name", parameterName)),
				new Document("$project", new Document("_id", 0).append("agent-ID", "$agents.agent-ID")
						.append(parameterName, "$agents.parameters.parameter-value"))));
	}

	public AggregateIterable<Document> getParametersFromAgentID_Agg(int stepNumber, String agentID) {
		return currentCollection.aggregate(asList(new Document("$match", new Document("_id", "step-" + stepNumber)),
				new Document("$unwind", "$agents"), new Document("$match", new Document("agents.agent-ID", agentID)),
				new Document("$project", new Document("_id", 0).append("agent-ID", "$agents.agent-ID")
						.append("parameters", "$agents.parameters"))));
	}

	public AggregateIterable<Document> getAllAgentsFromStep(int stepNumber) {
		return currentCollection
				.aggregate(asList(new Document("$match", new Document("_id", "step-" + stepNumber)),
						new Document("$unwind", "$agents"), new Document("$project", new Document("_id", 0)
								.append("agent-ID", "$agents.agent-ID").append("agent-type", "$agents.agent-type")
								.append("agent-name", "$agents.agent-name").append("lifetime", "$agents.lifetime")
								.append("parameters", "$agents.parameters"))));
	}

	//Get All Interactions From Step
	public AggregateIterable<Document> getAllInteractionsFromStep_Agg(int stepNumber) {
		return currentCollection.aggregate(asList(new Document("$match", new Document("_id", "step-" + stepNumber)),
				new Document("$unwind", "$interactions"),
				new Document("$project",
						new Document("_id", 0).append("interaction-from", "$interactions.interaction-from")
								.append("interaction-to", "$interactions.interaction-to").append("interaction-type",
										"$interactions.interaction-type"))));
	}

	public int countInteractionsInStep(int stepNumber) {
		AggregateIterable<Document> agg = currentCollection
				.aggregate(asList(new Document("$match", new Document("_id", "step-" + stepNumber)),
						new Document("$unwind", "$interactions"), new Document("$group",
								new Document("_id", 0).append("count", new Document("$sum", 1)))));

		int x = 0;
		for (Document d : agg) {
			x = d.getInteger("count");
		}
		return x;
		//		agg.forEach(new Block<Document>() {
		//		    @Override
		//		    public void apply(final Document document) {
		//		    	x = document.getInteger("count");
		////		    	params.add(new Interaction(document.getString("interaction-from"), document.getString("interaction-to"), document.getString("interaction-type")));
		//		    }
		//		});
		//		
		//		
		//		return x;

	}

	//TODO: Add notes to Collection

	//The following functions all return Lists of Things that can be processed by the MetricRunner
	//ABSTRACT AWAY

	public ArrayList<Interaction> getAllInteractionsFromStep(int stepNumber) {
		ArrayList<Interaction> interactions = new ArrayList<Interaction>();
		AggregateIterable<Document> raw = getAllInteractionsFromStep_Agg(stepNumber);
		raw.forEach(new Block<Document>() {
			@Override
			public void apply(final Document document) {
				interactions.add(new Interaction(document.getString("interaction-from"),
						document.getString("interaction-to"), document.getString("interaction-type")));
			}
		});

		return interactions;
	}

	//TODO: This is still a test?
	public ArrayList<Parameter<?>> getParametersFromAgentID(int stepNumber, String agentID) {
		ArrayList<Parameter<?>> params = new ArrayList<Parameter<?>>();
		AggregateIterable<Document> raw = getParametersFromAgentID_Agg(stepNumber, agentID);
		raw.forEach(new Block<Document>() {
			@Override
			public void apply(final Document document) {
				System.out.println(document.getString("parameters"));
				//		    	params.add(new Interaction(document.getString("interaction-from"), document.getString("interaction-to"), document.getString("interaction-type")));
			}
		});

		return params;
	}

	public ArrayList<VEntity> buildVAgentList(int stepNumber) {
		ArrayList<VEntity> vAgents = new ArrayList<VEntity>();
		//Get all agents in the step
		AggregateIterable<Document> rawAgents = getAllAgentsFromStep(stepNumber);
		//Cycle through all of them and build VAgents
		rawAgents.forEach(new Block<Document>() {
			@Override
			public void apply(final Document document) {
				VEntity tmpVA = new VEntity(document.getString("agent-name"), document.getString("agent-type"),
						document.getString("agent-ID"));
				ArrayList<Document> params = (ArrayList<Document>) document.get("parameters");
				for (Document d : params) {
					String name = d.getString("parameter-name");
					String type = d.getString("parameter-type");
					String value = d.getString("parameter-value");
					tmpVA.addParameterFromString(name, type, value);
				}
				vAgents.add(tmpVA);
			}
		});

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
			//			ig.addEdge(new Edge(new Node(agMap.get(inter.getAgentFromAsString())), new Node(agMap.get(inter.getAgentToAsString())),inter.getType(),inter.getOccurrence()));
		}

		return ig;
	}

	////TODO: Build Interaction Graph from time t to time t+k 
	public InteractionGraph buildInteractionGraphWithInterval(int stepNumber, int k) {
		InteractionGraph ig = new InteractionGraph();
		int finalStep = stepNumber + k;
		for (int i = stepNumber; i <= finalStep; i++) {
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
		}

		return ig;

	}

	public HashMap<String, VEntity> buildVAgentMap(int stepNumber) {
		HashMap<String, VEntity> vAgents = new HashMap<String, VEntity>();
		//Get all agents in the step
		AggregateIterable<Document> rawAgents = getAllAgentsFromStep(stepNumber);
		//Cycle through all of them and build VAgents
		rawAgents.forEach(new Block<Document>() {
			@Override
			public void apply(final Document document) {
				VEntity tmpVA = new VEntity(document.getString("agent-name"), document.getString("agent-type"),
						document.getString("agent-ID"));

				ArrayList<Document> params = (ArrayList<Document>) document.get("parameters");
				for (Document d : params) {
					String name = d.getString("parameter-name");
					String type = d.getString("parameter-type");
					String value = d.getString("parameter-value");
					tmpVA.addParameterFromString(name, type, value);
				}
				vAgents.put(tmpVA.getName(), tmpVA);
			}
		});

		return vAgents;
	}

	public HashMap<String, ArrayList<Interaction>> getAgentInteractionMap(int stepNumber) {
		HashMap<String, ArrayList<Interaction>> theMap = new HashMap<String, ArrayList<Interaction>>();

		AggregateIterable<Document> rawAgents = getAllAgentsFromStep(stepNumber);
		//Cycle through all of them and build VAgents
		rawAgents.forEach(new Block<Document>() {
			@Override
			public void apply(final Document document) {
				String name = document.getString("agent-name");
				theMap.put(name, new ArrayList<Interaction>());
			}
		});
		AggregateIterable<Document> raw = getAllInteractionsFromStep_Agg(stepNumber);
		if (raw == null) {
			return theMap;
		}
		raw.forEach(new Block<Document>() {
			@Override
			public void apply(final Document document) {
				String from = document.getString("interaction-from");
				String to = document.getString("interaction-to");
				if (to != null && from != null) {
					if (theMap.get(to) != null && theMap.get(from) != null) {
						Interaction tmpInteraction = new Interaction(from, to, document.getString("interaction-type"));
						theMap.get(from).add(tmpInteraction);
						theMap.get(to).add(tmpInteraction);
					}
				}

			}
		});

		return theMap;
	}

	public int getTerminationStep() {
		return getTerminationStatistics().getInteger("termination-step");
	}

	//Rebuild system from data at step x
}