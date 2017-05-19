***********
DataOutputter for CASL simulations

***********

Database structure:
/SimulationName/Execution/ExecutionDocuments

*******
##This is hideous
##Put interactions in their own place
JSON Entity Schema:
{
	"system-name": String,
	"description": String,
	"execution-ID": String,
	"output-ID": String,
	"current-step": Number,
	"total-steps": Number,
	"time-since-last-step": Number,
	"environments": [
		{
			"environment-ID": String,
			"environment-Type": String,
			"life-time": Number,
			"parameters": [
				{
					"name": String,
					"type": String,
					"value": String (OR Number OR Binary....)
				}
			]
		}
	],
	"groups": [
		{
		"group-ID": String,
		"group-Type": String,
		"life-time": Number,
		"parameters": [
			{
				"name": String,
				"type": String,
				"value": String (OR Number OR Binary....)
			}
		}
	],
	"agents": [
		{	
			"agent-ID": String,
			"agent-Type": String,
			"life-time": Number,
			"parameters":[
				{
					"name": String,
					"type": String,
					"value": String (OR Number OR Binary....)
				}
			],
			"interactions": [
			
			]
		}
	]
}