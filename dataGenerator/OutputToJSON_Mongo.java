package dataGenerator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

public class OutputToJSON_Mongo implements Runnable {

	Document theOutput;
	Document system;
	Document environments;
	Document groups;
	Document agents;
	Document initValues;
	Document logs;

	protected final int INTERACTION_SIZE_LIMIT = 12500;
	protected final int AGENTS_SIZE_LIMIT = 1500;
	protected final int GROUPS_SIZE_LIMIT = 1500;
	protected final int ENVIRONMENTS_SIZE_LIMIT = 1500;
	protected final int LOGS_SIZE_LIMIT = 12500;

	protected final int MAX_CONCURRENT_THREADS = 10;

	protected final String EACH = "$each";
	protected final String SET = "$set";
	protected final String PUSH = "$push";
	protected final String ADD_TO_SET = "$addToSet";
	protected final String ID_STR = "_id";

	protected final String AGENTS = "agents";
	protected final String GROUPS = "groups";
	protected final String ENVIRONMENTS = "environments";
	protected final String INTERACTIONS = "interactions";

	String URL = "127.0.0.1:27017"; // this isn't correct but it's close
	String DB_NAME = "testdb_1";

	String executionID = "";
	int currentStep = 0;
	String currentPath;

	SimulationInfo simInfo;

	ArrayList<Document> agentsDocuments;
	ArrayList<ArrayList<Document>> agentMaxSizeDocumentStore;
	ArrayList<Document> groupsDocuments;
	ArrayList<ArrayList<Document>> groupsMaxSizeDocumentStore;
	ArrayList<Document> environmentsDocuments;
	ArrayList<ArrayList<Document>> environmentsMaxSizeDocumentStore;
	ArrayList<Document> logDocuments;
	ArrayList<ArrayList<Document>> logsMaxSizeDocumentStore;
	ArrayList<Document> interactions;
	ArrayList<ArrayList<Document>> interactionsSizeStore;

	final String GROUP = "group";
	final String AGENT = "agent";
	final String ENVIRONMENT = "environment";

	MongoCollection<Document> currentCollection;

	MongoClient mongoClient;
	MongoDatabase db;

	String collectionName = "default_DB_Name";
	String dbName = "";

	// The wall of clones
	ArrayList<Document> agentsDocumentsClone;
	ArrayList<ArrayList<Document>> agentMaxSizeDocumentStoreClone;
	ArrayList<Document> groupsDocumentsClone;
	ArrayList<ArrayList<Document>> groupsMaxSizeDocumentStoreClone;
	ArrayList<Document> environmentsDocumentsClone;
	ArrayList<ArrayList<Document>> environmentsMaxSizeDocumentStoreClone;
	ArrayList<Document> logDocumentsClone;
	ArrayList<ArrayList<Document>> logsMaxSizeDocumentStoreClone;
	ArrayList<Document> interactionsClone;
	ArrayList<ArrayList<Document>> interactionsSizeStoreClone;
	Document systemClone;
	Document environmentsClone;
	Document groupsClone;
	Document agentsClone;
	Document initValuesClone;
	Document logsClone;

	Output output;

	ArrayList<MongoWriteQueuer> mongoWriteQueue;

	public OutputToJSON_Mongo(Output output, SimulationInfo simInfo) {
		// Create DB
		this.simInfo = simInfo;
		this.output = output;

		initValues = new Document();
		mongoWriteQueue = new ArrayList<MongoWriteQueuer>();
		mongoWriteQueueRunner = new ArrayList<MongoWriteQueuer>();
	}

	public void setupDB(String systemName, String executionID, String databaseName) {
		System.out.println("Setting up db at");
		this.executionID = executionID;
		collectionName = systemName;
		currentPath = URL + collectionName;
		// this.dbID = dbID;5984
		dbName = databaseName;
		// MongoClientOptions.Builder builder = new MongoClientOptions.Builder();
		// builder.threadsAllowedToBlockForConnectionMultiplier(50);
		// MongoClientOptions options = builder.build();
		//
		// ServerAddress sa = new ServerAddress(URL);
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
		agentMaxSizeDocumentStore = new ArrayList<ArrayList<Document>>();
		groupsMaxSizeDocumentStore = new ArrayList<ArrayList<Document>>();
		environmentsMaxSizeDocumentStore = new ArrayList<ArrayList<Document>>();
		logsMaxSizeDocumentStore = new ArrayList<ArrayList<Document>>();
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
			storeNewGroupDocument(entity);
			break;
		case AGENT:
			storeNewAgentDocument(entity);
			break;
		case ENVIRONMENT:
			storeNewEnvironmentDocument(entity);
			break;
		}
		e.clear();
	}

	public void storeNewInteractionDocument(Document interDoc) {
		// if (interactions.size() >= INTERACTION_SIZE_LIMIT) {
		// interactionsSizeStore.add(new ArrayList<Document>(interactions));
		// interactions.clear();
		// }
		interactions.add(new Document(interDoc));
	}

	public void storeNewAgentDocument(Document interDoc) {
		if (agentsDocuments.size() >= AGENTS_SIZE_LIMIT) {
			agentMaxSizeDocumentStore.add(new ArrayList<Document>(agentsDocuments));
			agentsDocuments.clear();
		}
		agentsDocuments.add(new Document(interDoc));
	}

	public void storeNewGroupDocument(Document interDoc) {
		// if (groupsDocuments.size() >= GROUPS_SIZE_LIMIT) {
		// agentMaxSizeDocumentStore.add(new ArrayList<Document>(groupsDocuments));
		// groupsDocuments.clear();
		// }
		groupsDocuments.add(new Document(interDoc));
	}

	public void storeNewEnvironmentDocument(Document interDoc) {
		// if (environmentsDocuments.size() >= ENVIRONMENTS_SIZE_LIMIT) {
		// environmentsMaxSizeDocumentStore.add(new
		// ArrayList<Document>(environmentsDocuments));
		// environmentsDocuments.clear();
		// }
		environmentsDocuments.add(new Document(interDoc));
	}

	public void storeNewLogDocument(Document interDoc) {
		// if (logDocuments.size() >= AGENTS_SIZE_LIMIT) {
		// logsMaxSizeDocumentStore.add(new ArrayList<Document>(logDocuments));
		// logDocuments.clear();
		// }
		logDocuments.add(new Document(interDoc));
	}

	final String STEP_DASH = "step-";
	private ArrayList<MongoWriteQueuer> mongoWriteQueueRunner;

	@Override
	public void run() {
		System.out.println("TRIGGER WRITER");
		runWriteQueue();

	}

	public void runWriteQueue() {
		ExecutorService e = Executors.newFixedThreadPool(mongoWriteQueueRunner.size());
		System.out.println("mongo queue size: " + mongoWriteQueueRunner.size());
		try {
			for (MongoWriteQueuer mwq : mongoWriteQueueRunner) {
				e.execute(mwq);
			}
			e.shutdown();
			// ArrayList<Future<Boolean>> futures = new
			// ArrayList<Future<Boolean>>(e.invokeAll(mongoWriteQueueRunner));
		} catch (Exception e1) {
			e1.printStackTrace();
			System.exit(0);
		}
		// mongoWriteQueueRunner = new ArrayList<MongoWriteQueuer>();
		System.out.println("wait");
		// for (MongoWriteQueuer mwq : mongoWriteQueue) {
		// e.execute(mwq);
		// }
		// e.awaitTermination();
		// return e.isTerminated();
	}

	public void endOfStep() {
		agentMaxSizeDocumentStore.add(agentsDocuments);
		MongoWriteQueuer mwq = new MongoWriteQueuer(agentsDocuments, agentMaxSizeDocumentStore, groupsDocuments, environmentsDocuments,
				logDocuments, interactions, system, initValues, logs, currentStep, currentCollection);
		// mwq.run();
		mongoWriteQueue.add(mwq);

		// if (this.currentStep % MAX_CONCURRENT_THREADS == 0) {
		// System.out.println("TRIGGER WRITER");
		// runWriteQueue();
		// mongoWriteQueue = new ArrayList<MongoWriteQueuer>();
		// }

		mongoWriteQueueRunner = new ArrayList<MongoWriteQueuer>(mongoWriteQueue);
		Thread t = new Thread(this);
		t.start();
		mongoWriteQueue.clear();
		// }
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
		runWriteQueue();
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

class MongoWriteQueuer implements Runnable {
	ArrayList<Document> agentsDocuments;
	ArrayList<ArrayList<Document>> agentMaxSizeDocumentStore;
	ArrayList<Document> groupsDocuments;
	ArrayList<ArrayList<Document>> groupsMaxSizeDocumentStore;
	ArrayList<Document> environmentsDocuments;
	ArrayList<ArrayList<Document>> environmentsMaxSizeDocumentStore;
	ArrayList<Document> logDocuments;
	ArrayList<ArrayList<Document>> logsMaxSizeDocumentStore;
	ArrayList<Document> interactions;
	ArrayList<ArrayList<Document>> interactionsSizeStore;
	Document system;
	Document initValues;
	Document logs;
	int currentStep = -1;
	private MongoCollection<Document> currentCollection;

	protected final int INTERACTION_SIZE_LIMIT = 12500;
	protected final int AGENTS_SIZE_LIMIT = 2000;
	protected final int GROUPS_SIZE_LIMIT = 2000;
	protected final int ENVIRONMENTS_SIZE_LIMIT = 2000;
	protected final int LOGS_SIZE_LIMIT = 12500;

	protected final int MAX_CONCURRENT_THREADS = 10;

	protected final String EACH = "$each";
	protected final String SET = "$set";
	protected final String PUSH = "$push";
	protected final String ADD_TO_SET = "$addToSet";
	protected final String ID_STR = "_id";

	protected final String AGENTS = "agents";
	protected final String GROUPS = "groups";
	protected final String ENVIRONMENTS = "environments";
	protected final String INTERACTIONS = "interactions";
	protected final String STEP_DASH = "step-";

	public MongoWriteQueuer(ArrayList<Document> agentsDocuments, ArrayList<ArrayList<Document>> agentMaxSizeDocuments, ArrayList<Document> groupsDocuments,
			ArrayList<Document> environmentsDocuments, ArrayList<Document> logDocuments,
			ArrayList<Document> interactions, Document system, Document initValues, Document logs, int currentStep,
			MongoCollection<Document> currentCollection) {
		super();
		this.agentsDocuments = new ArrayList<Document>(agentsDocuments);
		this.agentMaxSizeDocumentStore = new ArrayList<ArrayList<Document>>(agentMaxSizeDocuments);
		this.groupsDocuments = new ArrayList<Document>(groupsDocuments);
		this.groupsMaxSizeDocumentStore = new ArrayList<ArrayList<Document>>();
		this.environmentsDocuments = new ArrayList<Document>(environmentsDocuments);
		this.environmentsMaxSizeDocumentStore = new ArrayList<ArrayList<Document>>();
		this.logDocuments = new ArrayList<Document>(logDocuments);
		this.logsMaxSizeDocumentStore = new ArrayList<ArrayList<Document>>();
		this.interactions = new ArrayList<Document>(interactions);
		this.interactionsSizeStore = new ArrayList<ArrayList<Document>>();
		this.system = system;
		this.initValues = initValues;
		this.logs = logs;
		this.currentStep = currentStep;
		this.currentCollection = currentCollection;

		System.out.println("WRITE CS: " + this.currentStep);
	}

	public void run() {
		String stepString = STEP_DASH + currentStep;
		Document qDoc = new Document(ID_STR, stepString);
		currentCollection.insertOne(qDoc);
		currentCollection.updateOne(qDoc, new Document(SET, new Document("system-info", system)));

		// Finalise sends
		// documentChunkSender(INTERACTIONS, qDoc, interactionsSizeStore);
		// documentChunkSender(AGENTS, qDoc, agentMaxSizeDocumentStore);
		// documentChunkSender(GROUPS, qDoc, groupsMaxSizeDocumentStore);
		// documentChunkSender(ENVIRONMENTS, qDoc, environmentsMaxSizeDocumentStore);
		//
		aDocumentChunker(interactions, INTERACTION_SIZE_LIMIT, INTERACTIONS, qDoc);
//		aDocumentChunker(agentsDocuments, AGENTS_SIZE_LIMIT, AGENTS, qDoc);
		documentChunkSender(AGENTS, qDoc, agentMaxSizeDocumentStore);
		aDocumentChunker(groupsDocuments, GROUPS_SIZE_LIMIT, GROUPS, qDoc);
		aDocumentChunker(environmentsDocuments, ENVIRONMENTS_SIZE_LIMIT, ENVIRONMENTS, qDoc);
	}

	public void aDocumentChunker(ArrayList<Document> docs, int maxSize, String name, Document qDoc) {
		if (docs.size() > maxSize) {
			int multiple = docs.size() / maxSize;
			int realMax = maxSize * multiple;
			int remainder = docs.size() - realMax;
			for (int i = 0; i < multiple * maxSize; i += multiple) {
				if (i == 0) {
					currentCollection.updateOne(qDoc,
							new Document(SET, new Document(name, docs.subList(i, i * multiple))));
				} else {
					currentCollection.updateOne(qDoc,
							new Document(PUSH, new Document(name, new Document(EACH, docs.subList(i, i * multiple)))));
				}
			}
			currentCollection.updateOne(qDoc, new Document(PUSH,
					new Document(name, new Document(EACH, docs.subList(realMax, realMax + remainder)))));
		} else {
			currentCollection.updateOne(qDoc, new Document(SET, new Document(name, docs)));
		}
	}

	public void documentChunkSender(String name, Document qDoc, ArrayList<ArrayList<Document>> docMap) {
		for (int i = 0; i < docMap.size(); i++) {
			ArrayList<Document> d = docMap.get(i);
			if (i == 0) {
				currentCollection.updateOne(qDoc, new Document(SET, new Document(name, d)));
			} else {
				currentCollection.updateOne(qDoc, new Document(PUSH, new Document(name, new Document(EACH, d))));
			}
		}
	}

}