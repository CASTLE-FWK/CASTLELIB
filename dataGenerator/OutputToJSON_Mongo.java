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
import castleComponents.SemanticGroup;
import stdSimLib.Parameter;
import castleComponents.Agent;
import castleComponents.Interaction;

public class OutputToJSON_Mongo{

	Document theOutput;
	Document system;
	Document environments;
	Document groups;
	Document agents;
	Document initValues;
	ArrayList<Document> interactions;

	//CouchDB stuff
	String URL = "http://127.0.0.1:5984/"; //this isn't correct but it's close
	String DB_NAME = "testdb_1";

	String executionID = "";
	int currentStep = 0;
	String currentPath;
	String dbID = "";
	
	MongoClient mongoClient;
	MongoDatabase db;
	MongoCollection<Document> currentCollection;
	
	ArrayList<Document> agentsDocuments;
	ArrayList<Document> groupsDocuments;
	ArrayList<Document> environmentsDocuments;


	public OutputToJSON_Mongo(String systemName, String executionID, String dbID){
		//Create DB
		this.executionID = executionID;
		DB_NAME = systemName;
		currentPath = URL+DB_NAME;
		this.dbID = dbID;
		System.out.println("current DB path for this execution: " + currentPath);
		
		mongoClient = new MongoClient();
		
		db = mongoClient.getDatabase("simulations");
		DB_NAME = DB_NAME + "_" + executionID;
		
		currentCollection = db.getCollection(DB_NAME);
		System.out.println("MongoDB collection is at "+DB_NAME);

		initValues = new Document();
	}
	
	//Clean and init things
	public void newStep(){
		//Need to do something to ensure that its actually called.
		theOutput = new Document();
		system = new Document();
		environments = new Document();
		groups = new Document();
		agents = new Document();	
		agentsDocuments = new ArrayList<Document>();
		interactions =  new ArrayList<Document>();
		environmentsDocuments = new ArrayList<Document>();
		groupsDocuments = new ArrayList<Document>();
	}
	
	public void storeInitValues(ArrayList<Parameter<?>> params, String startTimeAsDate){
		ArrayList<Document> paramDocs = new ArrayList<Document>();
		for (Parameter<?> param : params){
			Document paramDoc = new Document()
				.append("parameter-name", param.getName())
				.append("parameter-type", param.getType())
				.append("parameter-value", param.getCurrentValue());
				
			paramDocs.add(paramDoc);
		}
		initValues.append("_id","system-initialisation");
		initValues.append("initialisation-parameters",paramDocs);
		initValues.append("start-time",startTimeAsDate);
		initValues.append("execution-ID", executionID);
		initValues.append("notes","");
		
		//Upload to DB
		currentCollection.insertOne(
				initValues);
	}
	
	public void exportSystem(String systemName, String execID, int stepNumber,int totalSteps, long timeSinceLastStep, long elapsedTime){
		currentStep = stepNumber;
		//Ideally this would call system.getSystemName and so on but this is just for layout testing
		system.append("system-name",systemName);
		system.append("execution-ID", executionID);
		system.append("output-ID",execID+"-"+stepNumber);
		system.append("current-step",stepNumber);
		system.append("total-steps",totalSteps);
		system.append("time-since-last-step",timeSinceLastStep);
		system.append("elapsed-time", elapsedTime);
		system.append("notes", "");
	}

	
	public void exportEnvironment(Environment e){
		Document environment = new Document();
		environment.append("environment-ID", e.getID());
		environment.append("environment-type", e.getType());
		environment.append("environment-name", e.getID());
		environment.append("lifetime", -1);
		
		ArrayList<Document> paramDocs = new ArrayList<Document>();
		
		Iterator<Entry<String, Parameter<?>>> it = e.getParameters().entrySet().iterator();
		while (it.hasNext()){
			Map.Entry<String, Parameter<?>> pair = (Map.Entry<String, Parameter<?>>)it.next();
			Parameter<?> param = pair.getValue();
			Document paramDoc = new Document()
			.append("parameter-name", param.getName())
			.append("parameter-type", param.getType())
			.append("parameter-value", param.getCurrentValue());
			
			paramDocs.add(paramDoc);
		}
		
		List<Interaction> agentInteractions = e.publishInteractions();
		if (agentInteractions != null){
			for (Interaction inter : agentInteractions){
				Document interDoc = new Document()
					.append("interaction-from",inter.getEntityFrom().getID())
					.append("interaction-to", inter.getEntityTo().getID())
					.append("interaction-type",inter.getType());
				interactions.add(interDoc);
			}
		}
		
	
		environment.append("parameters",paramDocs);
		
		environmentsDocuments.add(environment);
	}
	
	public void exportGroup(SemanticGroup g){
		Document group = new Document();
		group.append("group-ID", g.getID());
		group.append("group-type", g.getType());
		group.append("group-name", g.getID());
		group.append("lifetime", -1);
		ArrayList<Document> paramDocs = new ArrayList<Document>();
		
		Iterator<Entry<String, Parameter<?>>> it = g.getParameters().entrySet().iterator();
		while (it.hasNext()){
			Map.Entry<String, Parameter<?>> pair = (Map.Entry<String, Parameter<?>>)it.next();
			Parameter<?> param = pair.getValue();
			Document paramDoc = new Document()
			.append("parameter-name", param.getName())
			.append("parameter-type", param.getType())
			.append("parameter-value", param.getCurrentValue());
			
			paramDocs.add(paramDoc);
		}
		
		List<Interaction> agentInteractions = g.publishInteractions();
		if (agentInteractions != null){
			for (Interaction inter : agentInteractions){
				Document interDoc = new Document()
					.append("interaction-from",inter.getEntityFrom().getID())
					.append("interaction-to", inter.getEntityTo().getID())
					.append("interaction-type",inter.getType());
				interactions.add(interDoc);
			}
		}
	
		group.append("parameters",paramDocs);
		
		groupsDocuments.add(group);
	}

	public void exportAgent(Agent a){
		Document agent = new Document();
		agent.append("agent-ID", a.getID());
		agent.append("agent-type", a.getType());
		agent.append("agent-name", a.getID());
		agent.append("lifetime", -1);
		
		ArrayList<Document> paramDocs = new ArrayList<Document>();

		Iterator<Entry<String, Parameter<?>>> it = a.getParameters().entrySet().iterator();
		while (it.hasNext()){
			Map.Entry<String, Parameter<?>> pair = (Map.Entry<String, Parameter<?>>)it.next();
			Parameter<?> param = pair.getValue();
			Document paramDoc = new Document()
			.append("parameter-name", param.getName())
			.append("parameter-type", param.getType())
			.append("parameter-value", param.getCurrentValue());
			paramDocs.add(paramDoc);
		}
		
		List<Interaction> agentInteractions = a.publishInteractions();
		if (agentInteractions != null){
			for (Interaction inter : agentInteractions){
				Document interDoc = new Document()
					.append("interaction-from",inter.getEntityFrom().getID())
					.append("interaction-to", inter.getEntityTo().getID())
					.append("interaction-type",inter.getType());
				interactions.add(interDoc);
			}
		}
	
		agent.append("parameters",paramDocs);
		
		agentsDocuments.add(agent);
	}
	
	public void endOfStep(){
		currentCollection.insertOne(
				new Document("_id","step-"+currentStep)
				.append("system-info", system)
				.append("environments",environmentsDocuments)
				.append("groups", groupsDocuments)
				.append("agents", agentsDocuments)
				.append("interactions",interactions));	
	}
	
	public Document getCompleteDocument(){
		return new Document("_id","step-"+currentStep)
			.append("system-info", system)
			.append("environments",environmentsDocuments)
			.append("groups", groupsDocuments)
			.append("agents", agentsDocuments)
			.append("interactions",interactions);	
	}

	public Document exportParameters(String name, String parameterValue, String type){
		Document param = new Document();
		param.append("parameter-name",name);
		param.append("parameter-type",type);
		param.append("parameter-value",parameterValue);
		return param;
	}

	//Complete JSON and send to DB
	public void finished(int finalStep, long elapsedTime, int totalSteps){
		currentCollection.insertOne(
				new Document("_id","termination-statistics")
				.append("termination-step", finalStep)
				.append("%-of-execution-finished",(((double)finalStep) / ((double)totalSteps) * 100))
				.append("elapsed-time",elapsedTime));	
	}
}