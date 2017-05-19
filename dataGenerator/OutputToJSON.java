package dataGenerator;

import com.eclipsesource.json.JsonObject;

import java.util.ArrayList;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonValue;
import com.eclipsesource.json.WriterConfig;

import com.mashape.unirest.http.*;
import com.mashape.unirest.http.exceptions.UnirestException;
//
//import com.couchbase.client.java.Bucket;
//import com.couchbase.client.java.Cluster;
//import com.couchbase.client.java.CouchbaseCluster;
//import com.couchbase.client.java.document.JsonDocument;

import interLib.Agent;
import interLib.Parameter;

public class OutputToJSON{

	JsonObject theOutput;
	JsonObject system;
	JsonArray environments;
	JsonArray groups;
	JsonArray agents;

	//CouchDB stuff
	String URL = "http://127.0.0.1:5984/"; //this isn't correct but it's close
	String DB_NAME = "testdb_1";

	String executionID = "";
	int currentStep = 0;
	String currentPath;
	
//	Cluster cluster;
//	Bucket bucket;


	public OutputToJSON(String name, String executionID){
		//Create DB
		this.executionID = executionID;
		DB_NAME = name;
		currentPath = URL+DB_NAME;
		System.out.println("current DB path for this execution: " + currentPath);
//		cluster = CouchbaseCluster.create("localhost");
//		bucket = cluster.openBucket("default");
//		try {
//			HttpResponse<String> response = Unirest.put(currentPath)
//				.asString();
//				System.out.println(response.getBody());
//
//		} catch (UnirestException ue){
//			System.out.println("DB ERROR");
//			ue.printStackTrace();			
//		}

		//Init things
		
	}


	//Clean and init things
	public void newStep(){
		//Need to do something to ensure that its actually called.
		theOutput = new JsonObject();
		system = new JsonObject();
		environments = new JsonArray();
		groups = new JsonArray();
		agents = new JsonArray();	
	}
	
	public void dumpSystem(String systemName, String execID, int stepNumber,int totalSteps, long timeSinceLastStep, long elapsedTime){
		currentStep = stepNumber;
		//Ideally this would call system.getSystemName and so on but this is just for layout testin
		system.add("system-name",systemName);
		system.add("execution-ID", executionID);
		system.add("output-ID",execID+"-"+stepNumber);
		system.add("current-step",stepNumber);
		system.add("total-steps",totalSteps);
		system.add("time-since-last-step",timeSinceLastStep);
		system.add("elapsed-time", elapsedTime);
//		JsonArray parameters = new JsonArray();
//		ArrayList<Parameter<?>> params = a.getParameters();
//		for (Parameter<?> param : params){
//			parameters.add(new JsonObject().add("parameter-name", param.getName()));
//			parameters.add(new JsonObject().add("parameter-type", param.getType()));
//			parameters.add(new JsonObject().add("parameter-value", param.getCurrentValue()));
//		}
//		agents.add(parameters);
		system.add("notes", "");
	}

	//Entities

	//Should take an environment (or a list of them)
	public void dumpEnvironments(){
//		JsonObject environment = new JsonObject();
//		environment.add("environment-ID","envID1");
//		environment.add("type","envType1");
////		environment.add("parameters",dumpParameters());
//
//		environments.add(environment);
//
//		environment = new JsonObject();
//		environment.add("environment-ID","envID2");
//		environment.add("type","envType2");
////		environment.add("parameters",dumpParameters());
//		environments.add(environment);
	}
	//Should take a group (or a list of them)
	public void dumpGroups(){

	}

	//Should take an agent (or a list of them)
	public void dumpAgent(Agent a){
//		JsonObject agent = new JsonObject();
//		agent.add("agent-ID", a.getID());
//		agent.add("agent-type", a.getType());
//		agent.add("agent-name", a.getID());
//		agent.add("lifetime", -1);
//		JsonArray parameters = new JsonArray();
//		ArrayList<Parameter<?>> params = a.getParameters();
//		for (Parameter<?> param : params){
//			parameters.add(new JsonObject().add("parameter-name", param.getName()));
//			parameters.add(new JsonObject().add("parameter-type", param.getType()));
//			parameters.add(new JsonObject().add("parameter-value", param.getCurrentValue()));
//		}
//		agents.add(parameters);
//		
//		
//		
//		agents.add(agent);

	}

	public JsonValue dumpParameters(String name, String parameterValue, String type){
		JsonObject param = new JsonObject();
		param.add("name",name);
		param.add("type",type);
		param.add("value",parameterValue);
		return param;
	}

	//Complete JSON and send to DB
	public void finished(){
		theOutput = Json.object().add("system-info",system).add("environments",environments).add("groups",groups).add("agents",agents);		
//		bucket.upsert(JsonDocument.create(this.executionID+"-Step"+currentStep,(com.couchbase.client.java.document.json.JsonObject.create().put("", theOutput.toString()))));
//		try {
//			HttpResponse<String> response = Unirest.put(currentPath+"/Step-"+currentStep)
//				.body(theOutput.toString())
//				.asString();
//				System.out.println(response.getBody());
//
//		} catch (UnirestException ue){
//			ue.printStackTrace();			
//		}
		//May call newStep() here after data is sent to db.
		
	}

	public String printToString(){
		return theOutput.toString(WriterConfig.PRETTY_PRINT);
	}


}