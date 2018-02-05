package dataGenerator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import org.bson.Document;

import castleComponents.Environment;
import castleComponents.Feature;
import castleComponents.SemanticGroup;
import castleComponents.SimulationInfo;
import stdSimLib.Parameter;
import castleComponents.Agent;
import castleComponents.Entity;
import castleComponents.Interaction;
import castleComponents.Output;

public class OutputToJSON_Mongo {

	Document theOutput;
	Document system;
	Document environments;
	Document groups;
	Document agents;
	Document initValues;
	Document logs;
	ArrayList<Document> interactions;

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

	String DBName = "default_DB_Name";

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
		DBName = systemName;
		currentPath = URL + DBName;
		// this.dbID = dbID;

		mongoClient = new MongoClient();
		db = mongoClient.getDatabase(databaseName);
		if (db == null) {
			System.err
					.println("db is null, which means the database called " + databaseName + " probably doesn't exist");
		}
		DBName = DBName + "_" + executionID;
		DBName = DBName.replaceAll("\\s+", "_");
		this.currentCollection = getCurrentCollectionFromDB(DBName);
		if (this.currentCollection == null) {
			System.err.println("cc is null");
		}
		System.out.println("MongoDB collection is at " + DBName);
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
	}

	public void storeInitValues(ArrayList<Parameter<?>> params, String startTimeAsDate) {
		ArrayList<Document> paramDocs = new ArrayList<Document>();
		for (Parameter<?> param : params) {
			Document paramDoc = new Document().append("parameter-name", param.getName())
					.append("parameter-type", param.getType()).append("parameter-value", param.getCurrentValue());

			paramDocs.add(paramDoc);
		}
		initValues.append("_id", "system-initialisation");
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
			Document paramDoc = new Document().append("parameter-name", param.getName())
					.append("parameter-type", param.getType()).append("parameter-value", param.getCurrentValue());

			paramDocs.add(paramDoc);
		}

		List<Interaction> entityInteractions = e.publishInteractions();
		if (entityInteractions != null) {
			for (Interaction inter : entityInteractions) {
				Document interDoc = new Document().append("interaction-from", inter.getEntityFrom().getID())
						.append("interaction-to", inter.getEntityTo().getID())
						.append("interaction-type", inter.getType().toString());
				interactions.add(interDoc);
			}
		}

		

		List<Feature> entityFeatureCalls = e.publishFeatures();
		if (entityFeatureCalls != null) {
			for (Feature f : entityFeatureCalls) {
				Document fCallDoc = new Document().append("feature-name", f.getName())
						.append("feature-type", f.getFeatureType().toString()).append("feature-call#", f.getOccurrence());
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
	}

	public void endOfStep() {
		currentCollection.insertOne(new Document("_id", "step-" + currentStep).append("system-info", system)
				.append("environments", environmentsDocuments).append("groups", groupsDocuments)
				.append("agents", agentsDocuments).append("interactions", interactions));
	}

	public Document getCompleteDocument() {
		return new Document("_id", "step-" + currentStep).append("system-info", system)
				.append("environments", environmentsDocuments).append("groups", groupsDocuments)
				.append("agents", agentsDocuments).append("interactions", interactions);
	}

	public Document exportParameters(String name, String parameterValue, String type) {
		Document param = new Document();
		param.append("parameter-name", name);
		param.append("parameter-type", type);
		param.append("parameter-value", parameterValue);
		return param;
	}

	// Complete JSON and send to DB
	public void endOfSimulation(int finalStep, long elapsedTime, int totalSteps) {
		currentCollection.insertOne(new Document("_id", "termination-statistics").append("termination-step", finalStep)
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