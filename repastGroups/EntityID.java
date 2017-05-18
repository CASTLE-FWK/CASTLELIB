package repastGroups;

public class EntityID{
	String entityType;
	String entityUID;
	long idnumber;
	String parentID;

	public EntityID(String et, long uid) {
		entityType = et;
		idnumber = uid;
		entityUID = entityType + "_" + idnumber;
	}
	
	public EntityID(String et, long uid, String parentID){
		this.entityType = et;
		idnumber = uid;
		this.parentID = parentID;
		entityUID = entityType + "_" + uid+"("+parentID+")";
	}
	public EntityID(EntityID id){
		this.entityType = id.entityType;
		this.entityUID = id.entityUID;
		this.idnumber = id.idnumber;
		this.parentID = id.parentID;
	}
	
	public EntityID addParent(String parent){
		parentID = parent;
		entityUID = entityType + "_" + idnumber+"("+parentID+")";
		return this;
	}
	
	
	public EntityID(String id){
		entityUID = id;
	}

	@Override
	public String toString(){
		return entityUID;
	}
	
}