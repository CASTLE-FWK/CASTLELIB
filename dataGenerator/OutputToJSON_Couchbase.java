package dataGenerator;



import java.util.ArrayList;

//import com.eclipsesource.json.Json;
//import com.eclipsesource.json.JsonObject;
//import com.eclipsesource.json.JsonArray;
//import com.eclipsesource.json.JsonValue;
//import com.eclipsesource.json.WriterConfig;



import com.mashape.unirest.http.*;
import com.mashape.unirest.http.exceptions.UnirestException;
//import com.couchbase.client.java.Bucket;
//import com.couchbase.client.java.Cluster;
//import com.couchbase.client.java.CouchbaseCluster;
//import com.couchbase.client.java.document.JsonDocument;
//import com.couchbase.client.java.document.json.JsonArray;
//import com.couchbase.client.java.document.json.JsonObject;

import interLib.Agent;
import interLib.Parameter;

public class OutputToJSON_Couchbase{

//	JsonObject theOutput;
//	JsonObject system;
//	JsonArray environments;
//	JsonArray groups;
//	JsonArray agents;
//	JsonArray theRun;

	//CouchDB stuff
	String URL = "http://127.0.0.1:5984/"; //this isn't correct but it's close
	String DB_NAME = "testdb_1";

	String executionID = "";
	int currentStep = 0;
	String currentPath;
	
//	Cluster cluster;
//	Bucket bucket;


	public OutputToJSON_Couchbase(String name, String executionID){
		//Create DB
		this.executionID = executionID;
		DB_NAME = name;
		currentPath = URL+DB_NAME;
		System.out.println("current DB path for this execution: " + currentPath);
//		theRun = JsonArray.create();
//		cluster = CouchbaseCluster.create("localhost");
//		bucket = cluster.openBucket(DB_NAME);

		
	}


	//Clean and init things
	public void newStep(){
		//Need to do something to ensure that its actually called.
//		theOutput = JsonObject.create();
////		theRun = JsonArray.create();
//		system = JsonObject.create();
//		environments = JsonArray.create();
//		groups = JsonArray.create();
//		agents = JsonArray.create();	
	}
	
	public void dumpSystem(String systemName, String execID, int stepNumber,int totalSteps, long timeSinceLastStep, long elapsedTime){
		currentStep = stepNumber;
		//Ideally this would call system.getSystemName and so on but this is just for layout testin
//		system.put("system-name",systemName);
//		system.put("execution-ID", executionID);
//		system.put("output-ID",execID+"-"+stepNumber);
//		system.put("current-step",stepNumber);
//		system.put("total-steps",totalSteps);
//		system.put("time-since-last-step",timeSinceLastStep);
//		system.put("elapsed-time", elapsedTime);
////		JsonArray parameters = new JsonArray();
////		ArrayList<Parameter<?>> params = a.getParameters();
////		for (Parameter<?> param : params){
////			parameters.put(new JsonObject().put("parameter-name", param.getName()));
////			parameters.put(new JsonObject().put("parameter-type", param.getType()));
////			parameters.put(new JsonObject().put("parameter-value", param.getCurrentValue()));
////		}
////		agents.put(parameters);
//		system.put("notes", "");
	}

	//Entities

	//Should take an environment (or a list of them)
	public void dumpEnvironments(){

	}
	//Should take a group (or a list of them)
	public void dumpGroups(){

	}

	//Should take an agent (or a list of them)
	public void dumpAgent(Agent a){
//		JsonObject agent = JsonObject.create();
//		agent.put("agent-ID", a.getID());
//		agent.put("agent-type", a.getType());
//		agent.put("agent-name", a.getID());
//		agent.put("lifetime", -1);
//		JsonArray parameters = JsonArray.create();
//		ArrayList<Parameter<?>> params = a.getParameters();
//		for (Parameter<?> param : params){
//			parameters.add(dumpParameters(param.getName(), param.getCurrentValue(), param.getType()));
//		}
//		agent.put("parameters",parameters);
//		
//		agents.add(agent);
	}
//
//	public JsonObject dumpParameters(String name, String parameterValue, String type){
//		JsonObject param =JsonObject.create();
//		param.put("parameter-name",name);
//		param.put("parameter-type",type);
//		param.put("parameter-value",parameterValue);
//		return param;
//	}
	
//	public void endOfStep(){
//		JsonObject obj = JsonObject.create().put("system-info",system).put("environments",environments).put("groups",groups).put("agents",agents);
//		theRun.add(obj);
//	}

	//Complete JSON and send to DB
	public void finished(){
		System.out.println("uploading run to database");
////		theOutput = JsonObject.create().put("system-info",system).put("environments",environments).put("groups",groups).put("agents",agents);
//		theOutput.put("execution-steps",theRun);
//		bucket.upsert(JsonDocument.create(this.executionID, theOutput));
////		bucket.upsert(JsonDocument.create(this.executionID+"-Step"+currentStep,(com.couchbase.client.java.document.json.JsonObject.create().put("", theOutput.toString()))));
////		try {
////			HttpResponse<String> response = Unirest.put(currentPath+"/Step-"+currentStep)
////				.body(theOutput.toString())
////				.asString();
////				System.out.println(response.getBody());
//
//		} catch (UnirestException ue){
//			ue.printStackTrace();			
//		}
		//May call newStep() here after data is sent to db.
		
	}

//	public String printToString(){
//		return theOutput.toString(WriterConfig.PRETTY_PRINT);
//	}


}