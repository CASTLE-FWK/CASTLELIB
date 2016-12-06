package repastGroups;

public class EntityID{
	String entityType;
	String entityUID;

	public EntityID(String et, long uid) {
		entityType = et;
		entityUID = entityType + "_" + uid;
	}

	@Override
	public String toString(){
		return entityUID;
	}
}