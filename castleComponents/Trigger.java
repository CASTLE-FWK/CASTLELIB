package castleComponents;

import interLib.Utilities;

import java.util.function.BiFunction;
import java.util.function.Function;

public class Trigger {
	
	int startingLife = 0;
	int timeToLive = 0;
	Function<Entity, Void> functionToTrigger;

	//Bi
	BiFunction<Entity, Object, Void> bifunctionToTrigger;
	Object objectToRun;
	
	boolean repeatable = false;
	int fired = 0;
	Entity entity;
	String triggerID;
	
	String triggerName;
	
	int startTime = -1;
	
	boolean bi = false;
	
	public Trigger(int lifeTime, String triggerName, Function<Entity, Void> fn, boolean repeatable, Entity c){
		this.startingLife = lifeTime;
		this.timeToLive = this.startingLife;
		this.functionToTrigger = fn;
		this.repeatable = repeatable;
		this.entity = c;
		this.triggerName = triggerName;
		bi = false;
		triggerID = this.entity.getID()+Utilities.generateUID();
	}
	
	public Trigger(int lifeTime, String triggerName, BiFunction<Entity, Object, Void> fn, boolean repeatable, Entity c){
		this.startingLife = lifeTime;
		this.timeToLive = this.startingLife;
		this.bifunctionToTrigger = fn;
		this.triggerName = triggerName;
		this.repeatable = repeatable;
		this.entity = c;
		bi = true;
		triggerID = this.entity.getID()+Utilities.generateUID();
	}
	
	public Trigger(Trigger t){
		this.startingLife = t.startingLife;
		this.timeToLive = t.startingLife;
		this.bi = t.bi;
		if (!bi){
			this.functionToTrigger = t.functionToTrigger;
		} else {
			this.bifunctionToTrigger = t.bifunctionToTrigger;
		}				
		this.repeatable = t.repeatable;
		this.entity = t.entity;
		this.triggerName = t.triggerName;
		this.triggerID = this.entity.getID()+Utilities.generateUID();
	}
	
	public Trigger(Trigger t, boolean b){
		this.startingLife = t.startingLife;
		this.timeToLive = t.startingLife;
		this.bi = t.bi;
		if (!bi){
			this.functionToTrigger = t.functionToTrigger;
		} else {
			this.bifunctionToTrigger = t.bifunctionToTrigger;
		}				
		this.repeatable = t.repeatable;
		this.entity = t.entity;
		this.triggerName = t.triggerName;
		this.triggerID = this.entity.getID()+Utilities.generateUID();
		
		//
	}
	
	public void trigger(){
		if (bi){
			timeToLive -= 1;
			if (timeToLive == 0){
				bifunctionToTrigger.apply(entity,objectToRun); //Not sure how to do this
				fired++;
				if (repeatable){
					this.timeToLive = this.startingLife;
				}
			}
			
		} else {
			timeToLive -= 1;			
			if (timeToLive == 0){
				functionToTrigger.apply(entity); //Not sure how to do this
				fired++;
				if (repeatable){
					this.timeToLive = this.startingLife;
				}
			}
			
		}
	}
	
	public Trigger again(){
		triggerID = this.entity.getID()+Utilities.generateUID();
		return this;
	}
	
	public void setStartTime(int t){
		startTime = t;
	}
	
	public Trigger setTimeToLive(int t){
		timeToLive = t;
		return this;
	}
	
	public boolean isDead(){
		return (timeToLive <= 0 && !repeatable);
	}
	
	public String triggerStatus(){
		String str = "Trigger(";
		str += "triggerName: " + triggerName;
		str += ", triggerID: "+ triggerID;
		str +=", timeToLive: "+timeToLive;
		str += ", repeatable: "+repeatable;
		str += ", times fired: "+fired;
		str += ", bi: "+bi;
		str += ", dead: "+isDead()+")";
		
		return str;
	}
	
	public String getTriggerName(){
		return triggerName;
	}
	
	public String getTriggerID(){
		return triggerID;
	}
}
