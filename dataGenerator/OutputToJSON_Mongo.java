package dataGenerator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.UpdateResult;

import org.bson.Document;

import castleComponents.Environment;
import castleComponents.Feature;
import castleComponents.SemanticGroup;
import castleComponents.SimulationInfo;
import stdSimLib.HashMap;
import stdSimLib.Parameter;
import castleComponents.Agent;
import castleComponents.Entity;
import castleComponents.Interaction;
import castleComponents.Output;

public class OutputToJSON_Mongo implements Runnable {

	Document theOutput;
	Document system;
	Document environments;
	Document groups;
	Document agents;
	Document initValues;
	Document logs;
	ArrayList<Document> interactions;
	ArrayList<ArrayList<Document>> interactionsSizeStore;
	HashMap<Integer, ArrayList<Document>> maxInteractionsStore;
	
	
	private final int INTERACTION_SIZE_LIMIT = 12500;
	private final String EACH = "$each";
	private final String SET = "$set";
	private final String PUSH = "$push";
	private final String ADD_TO_SET = "$addToSet";
	private final String ID_STR = "_id";

	String URL = "http://127.0.0.1:5984/"; // this isn't correct but it's close
	String DB_NAME = "testdb_1";

	String executionID = "";
	int currentStep = 0;
	String currentPath;

	SimulationInfo simInfo;

	ArrayList<Document> agentsDocuments;
	ArrayList<Document> groupsDocuments;
	ArrayList<Document> environmentsDocuments;
	ArrayList<Document> logDocuments;

	final String GROUP = "group";
	final String AGENT = "agent";
	final String ENVIRONMENT = "environment";

	MongoCollection<Document> currentCollection;

	MongoClient mongoClient;
	MongoDatabase db;

	String collectionName = "default_DB_Name";
	String dbName = "";

	Output output;

	public OutputToJSON_Mongo(Output output, SimulationInfo simInfo) {
		// Create DB
		this.simInfo = simInfo;
		this.output = output;

		initValues = new Document();
	}

	public void setupDB(String systemName, String executionID, String databaseName) {
		System.out.println("Setting up db at");
		this.executionID = executionID;
		collectionName = systemName;
		currentPath = URL + collectionName;
		// this.dbID = dbID;
		dbName = databaseName;
		mongoClient = new MongoClient();
		db = mongoClient.getDatabase(databaseName);
		if (db == null) {
			System.err
					.println("db is null, which means the database called " + databaseName + " probably doesn't exist");
		}
		collectionName = collectionName + "_" + executionID;
		collectionName = collectionName.replaceAll("\\s+", "_");
		this.currentCollection = getCurrentCollectionFromDB(collectionName);
		if (this.currentCollection == null) {
			System.err.println("cc is null");
		}
		System.out.println("MongoDB collection is at " + collectionName);
		newStep();
	}

	// Clean and init things
	public void newStep() {
		// Need to do something to ensure that its actually called.
		theOutput = new Document();
		system = new Document();
		environments = new Document();
		groups = new Document();
		agents = new Document();
		logDocuments = new ArrayList<Document>();
		agentsDocuments = new ArrayList<Document>();
		interactions = new ArrayList<Document>();
		environmentsDocuments = new ArrayList<Document>();
		groupsDocuments = new ArrayList<Document>();
		interactionsSizeStore = new ArrayList<ArrayList<Document>>();
		maxInteractionsStore = new HashMap<Integer, ArrayList<Document>>();
	}

	public void storeInitValues(ArrayList<Parameter<?>> params, String startTimeAsDate) {
		ArrayList<Document> paramDocs = new ArrayList<Document>();
		for (Parameter<?> param : params) {
			Document paramDoc = new Document().append(PARAMETER_NAME, param.getName())
					.append(PARAMETER_TYPE, param.getType()).append(PARAMETER_VALUE, param.getCurrentValue());

			paramDocs.add(paramDoc);
		}
		initValues.append(ID_STR, "system-initialisation");
		initValues.append("initialisation-parameters", paramDocs);
		initValues.append("start-time", startTimeAsDate);
		initValues.append("execution-ID", executionID);
		initValues.append("notes", "");

		// Upload to DB
		// output.insertOneToDB(initValues);
		currentCollection.insertOne(initValues);
	}

	public void exportSystemStep(int stepNumber, int totalSteps, long timeSinceLastStep, long elapsedTime) {
		currentStep = stepNumber;
		// Ideally this would call system.getSystemName and so on but this is just for
		// layout testing
		system.append("system-name", simInfo.getSystemName());
		system.append("execution-ID", simInfo.getExecutionID());
		system.append("output-ID", simInfo.getExecutionID() + "-" + stepNumber);
		system.append("current-step", stepNumber);
		system.append("total-steps", totalSteps);
		system.append("time-since-last-step", timeSinceLastStep);
		system.append("elapsed-time", elapsedTime);
		system.append("notes", "");
	}

	public void exportLog(Entity e, String str) {
		Document d = new Document();
		String entityType = getEntityType(e);
		d.append(entityType + "-ID", e.getID());
		d.append("log-message", str);
		logDocuments.add(d);
	}

	public String getEntityType(Entity e) {
		String entityType = "";
		if (e instanceof SemanticGroup) {
			entityType = GROUP;
		} else if (e instanceof Agent) {
			entityType = AGENT;
		} else if (e instanceof Environment) {
			entityType = ENVIRONMENT;
		} else {
			System.out.println("Entity type unknown. Something has gone very wrong.");
			entityType = null;
		}
		return entityType;
	}

	final String PARAMETER_NAME = "parameter_name";
	final String PARAMETER_TYPE = "parameter_type";
	final String PARAMETER_VALUE = "parameter_value";

	final String INTERACTION_FROM = "interaction_from";
	final String INTERACTION_TYPE = "interaction_type";
	final String INTERACTION_TO = "interaction_to";
	final String INTERACTION_NAME = "interaction_name";

	final String FEATURE_NAME = "feature-name";
	final String FEATURE_TYPE = "feature-type";
	final String FEATURE_CALL_NUM = "feature-call#";

	public void exportEntity(Entity e) {
		String entityType = getEntityType(e);

		Document entity = new Document();
		entity.append(entityType + "-ID", e.getID());
		entity.append(entityType + "-type", e.getType());
		entity.append(entityType + "-name", e.getID());
		entity.append("lifetime", -1);
		ArrayList<Document> paramDocs = new ArrayList<Document>();
		ArrayList<Document> fCallDocs = new ArrayList<Document>();

		Iterator<Entry<String, Parameter<?>>> it = e.getParameters().entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, Parameter<?>> pair = (Map.Entry<String, Parameter<?>>) it.next();
			Parameter<?> param = pair.getValue();
			Document paramDoc = new Document().append(PARAMETER_NAME, param.getName())
					.append(PARAMETER_TYPE, param.getType()).append(PARAMETER_VALUE, param.getCurrentValue());

			paramDocs.add(paramDoc);
		}

		List<Interaction> entityInteractions = e.publishInteractions();
		if (entityInteractions != null) {
			for (Interaction inter : entityInteractions) {
				Document interDoc = new Document().append(INTERACTION_FROM, inter.getEntityFrom().getID())
						.append(INTERACTION_TO, inter.getEntityTo().getID())
						.append(INTERACTION_TYPE, inter.getType().toString())
						.append(INTERACTION_NAME, inter.getInteractionName());
				// interactions.add(interDoc);
				storeNewInteractionDocument(interDoc);
			}
		}

		List<Feature> entityFeatureCalls = e.publishFeatures();
		if (entityFeatureCalls != null) {
			for (Feature f : entityFeatureCalls) {
				Document fCallDoc = new Document().append(FEATURE_NAME, f.getName())
						.append(FEATURE_TYPE, f.getFeatureType().toString())
						.append(FEATURE_CALL_NUM, f.getOccurrence());
				fCallDocs.add(fCallDoc);
			}
		}
		entity.append("parameters", paramDocs);
		entity.append("feature-calls", fCallDocs);

		switch (entityType) {
		case GROUP:
			groupsDocuments.add(entity);
			break;
		case AGENT:
			agentsDocuments.add(entity);
			break;
		case ENVIRONMENT:
			environmentsDocuments.add(entity);
			break;
		}
		e.clear();
	}

	public void storeNewInteractionDocument(Document interDoc) {
		if (interactions.size() >= INTERACTION_SIZE_LIMIT) {
//			int count = maxInteractionsStore.size();
//			maxInteractionsStore.put(count, new ArrayList<Document>(interactions));
			interactionsSizeStore.add(new ArrayList<Document>(interactions));
			interactions.clear();
			System.out.println("OTOOOO OBOOID");
		}
		interactions.add(interDoc);
	}

	final String STEP_DASH = "step-";
	@Override
	public void run() {
		String stepString = STEP_DASH + currentStep;
		Document qDoc = new Document(ID_STR, stepString);
		currentCollection.insertOne(qDoc);
		currentCollection.updateOne(qDoc, new Document(SET, new Document("system-info", system)));
		currentCollection.updateOne(qDoc, new Document(SET, new Document("environments", environmentsDocuments)));
		currentCollection.updateOne(qDoc, new Document(SET, new Document("groups", groupsDocuments)));
		currentCollection.updateOne(qDoc, new Document(SET, new Document("agents", agentsDocuments)));

		interactionsSizeStore.add(new ArrayList<Document>(interactions));
		
//		maxInteractionsStore.put(maxInteractions.size(), new ArrayList<Document>(interactions));
		
		for (int i = 0; i < interactionsSizeStore.size(); i++) {
			ArrayList<Document> d = interactionsSizeStore.get(i);
			String command = "";
			if (i == 0) {
				command = SET;
				currentCollection.updateOne(qDoc, new Document(command, new Document("interactions", d)));
			} else {
				command = PUSH;
				// new ThreadedDocumentWriter(dbName, collectionName, qDoc,
				// new Document(command, new Document("interactions", new Document(EACH,
				// d)))).run();
				currentCollection.updateOne(qDoc,
						new Document(command, new Document("interactions", new Document(EACH, d))));
			}

		}

	}

	public void endOfStep() {
		Thread t = new Thread(this);
		t.start();
		// String stepString = "step-" + currentStep;
		// Document qDoc = new Document(ID_STR, stepString);
		// currentCollection.insertOne(qDoc);
		// currentCollection.updateOne(qDoc, new Document("$set", new
		// Document("system-info", system)));
		// currentCollection.updateOne(qDoc, new Document("$set", new
		// Document("environments", environmentsDocuments)));
		// currentCollection.updateOne(qDoc, new Document("$set", new Document("groups",
		// groupsDocuments)));
		// currentCollection.updateOne(qDoc, new Document("$set", new Document("agents",
		// agentsDocuments)));
		//
		// interactionsSizeStore.add(interactions);
		// for (int i = 0; i < interactionsSizeStore.size(); i++) {
		// ArrayList<Document> d = interactionsSizeStore.get(i);
		// String command = "";
		// if (i == 0) {
		// command = SET;
		// currentCollection.updateOne(qDoc, new Document(command, new
		// Document("interactions", d)));
		// } else {
		// command = PUSH;
		// // new ThreadedDocumentWriter(dbName, collectionName, qDoc,
		// // new Document(command, new Document("interactions", new Document(EACH,
		// // d)))).run();
		// currentCollection.updateOne(qDoc,
		// new Document(command, new Document("interactions", new Document(EACH, d))));
		// }
		//
		// }

		// currentCollection.updateOne(new Document(ID_STR, stepString), new
		// Document("$set", new Document("interactions", interactions)));
	}

	public Document exportParameters(String name, String parameterValue, String type) {
		Document param = new Document();
		param.append(PARAMETER_NAME, name);
		param.append(PARAMETER_TYPE, type);
		param.append(PARAMETER_VALUE, parameterValue);
		return param;
	}

	// Complete JSON and send to DB
	public void endOfSimulation(int finalStep, long elapsedTime, int totalSteps) {
		currentCollection.insertOne(new Document(ID_STR, "termination-statistics").append("termination-step", finalStep)
				.append("%-of-execution-finished", (((double) finalStep) / ((double) totalSteps) * 100))
				.append("elapsed-time", elapsedTime));
	}

	public MongoCollection<Document> getCurrentCollectionFromDB(String name) {
		return this.db.getCollection(name);
	}

	public void errLog(Object o) {
		System.err.println(getClass().getSimpleName() + " Warning: " + o.toString());
	}
}

class ThreadedDocumentWriter implements Runnable {
	MongoCollection<Document> currentCollection;
	Document query;
	Document docToWrite;
	MongoClient mc;

	public ThreadedDocumentWriter(String dbName, String collName, Document que, Document dtw) {
		MongoClient mc = new MongoClient();
		MongoDatabase db = mc.getDatabase(dbName);
		if (db == null) {
			System.err.println("db is null, which means the database called " + dbName + " probably doesn't exist");
		}

		this.currentCollection = db.getCollection(collName);
		this.query = que;
		this.docToWrite = dtw;
	}

	@Override
	public void run() {
		writeDocument();
	}

	public void writeDocument() {
		UpdateResult ur = currentCollection.updateOne(query, docToWrite);
		if (!ur.wasAcknowledged()) {
			System.out.println("8989899821");
			System.exit(0);
		} else {
			if (ur.getModifiedCount() < 1) {
				System.out.println("pmasdpdaspdas");
				System.exit(0);
			}
		}
	}
}