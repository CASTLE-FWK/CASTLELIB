package stdSimLib.utilities;

import java.util.HashMap;

import castleComponents.EntityID;

public class EntityIDFactory {

	private static HashMap<String, Long> existingIDs;
	
	public static EntityID getNewID(String entityType){
		if (existingIDs == null){
			reset();
		}
		if (existingIDs.get(entityType) == null){
			existingIDs.put(entityType, -1l);
		}
		existingIDs.put(entityType, existingIDs.get(entityType)+1);
		
		return new EntityID(entityType, existingIDs.get(entityType));
	}
	
	public static void reset(){
		existingIDs = new HashMap<String, Long>();
	}
	
}
