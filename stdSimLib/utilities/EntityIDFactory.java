package stdSimLib.utilities;

import java.util.HashMap;

import castleComponents.EntityID;

public class EntityIDFactory {

	private static HashMap<String, Long> existingIDs;
	
	public static EntityID getNewID(String entityType){
		if (existingIDs.get(entityType) == null){
			existingIDs.put(entityType, 0l);
		}
		return new EntityID(entityType, existingIDs.get(entityType));
	}
	
}
