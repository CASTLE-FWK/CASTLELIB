package casFeatures;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class CASStateOfInterest {
	//We have to put variables into maps, based on types :(
	HashMap<String, Integer> integerMap;
	HashMap<String, Boolean> booleanMap;
	HashMap<String, String> stringMap;
	HashMap<String, Double> doubleMap;
	HashMap<String, Enum> enumMap;
	
	
	
	public CASStateOfInterest(){
		//Init maps :(
		integerMap = new HashMap<String, Integer>();
		booleanMap = new HashMap<String, Boolean>();
		stringMap = new HashMap<String, String>();
		doubleMap = new HashMap<String, Double>();
		enumMap = new HashMap<String, Enum>();
	}

	
	//Do a deep copy (at least I think this should be deep)
	//Could spin this off into 4 threads
	public CASStateOfInterest(CASStateOfInterest csoi){
		integerMap = new HashMap<String, Integer>();
		booleanMap = new HashMap<String, Boolean>();
		stringMap = new HashMap<String, String>();
		doubleMap = new HashMap<String, Double>();
		enumMap = new HashMap<String, Enum>();
		
		HashMap<String, Integer> oldIntegerMap = csoi.getIntegerMap();
		HashMap<String, Boolean> oldBooleanMap = csoi.getBooleanMap();
		HashMap<String, String> oldStringMap = csoi.getStringMap();
		HashMap<String, Double> oldDoubleMap = csoi.getDoubleMap();
		HashMap<String, Enum> oldEnumMap = csoi.getEnumMap();
		
		Set<String> integerKeys = new HashSet<String>(oldIntegerMap.keySet());		
		for (String key : integerKeys){
			integerMap.put(key, new Integer(oldIntegerMap.get(key)));
		}
		
		Set<String> doubleKeys = new HashSet<String>(oldDoubleMap.keySet());		
		for (String key : doubleKeys){
			doubleMap.put(key, new Double(oldDoubleMap.get(key)));
		}
		
		Set<String> booleanKeys = new HashSet<String>(oldBooleanMap.keySet());		
		for (String key : booleanKeys){
			booleanMap.put(key, new Boolean(oldBooleanMap.get(key)));
		}
		
		Set<String> stringKeys = new HashSet<String>(oldStringMap.keySet());		
		for (String key : stringKeys){
			stringMap.put(key, new String(oldStringMap.get(key)));
		}
		
		Set<String> enumKeys = new HashSet<String>(oldEnumMap.keySet());		
		for (String key : enumKeys){
			enumMap.put(key, oldEnumMap.get(key));
		}
	}
	
	public <T> void registerNewState(String stateName, T state){
		if (state instanceof Integer){
			integerMap.put(stateName, (Integer)state);
		} else if (state instanceof Double){
			doubleMap.put(stateName, (Double)state);
		} else if (state instanceof String){
			stringMap.put(stateName, (String)state);
		} else if (state instanceof Boolean){
			booleanMap.put(stateName, (boolean)state);
		} else if (state instanceof Enum){
			enumMap.put(stateName, (Enum)state);
		}
		else {
			
		}
	}
	
	public boolean compareMap(CASStateOfInterest currState){
		//Integer Map Comparison
		HashMap<String,Integer> currentIntegerMap = currState.getIntegerMap();
		boolean integerMapBool = false; 
		if (currentIntegerMap.size() != integerMap.size()){
			integerMapBool =  false;
		}
		Set<String> integerKeys = new HashSet<String>(integerMap.keySet());
		int count = 0;
		for (String key : integerKeys){
			if (currentIntegerMap.get(key).intValue() == integerMap.get(key).intValue()){
				count++;
			}
		}
		//If all states are not the same in the previous step,
		//then clearly some adaptation has occurred 
		if (count != integerKeys.size()){
			integerMapBool = true;
		}
		
		//Double Map Comparison
		HashMap<String,Double> currentDoubleMap = currState.getDoubleMap();
		boolean doubleMapBool = false; 
		if (currentDoubleMap.size() != doubleMap.size()){
			doubleMapBool =  false;
		}
		Set<String> doubleKeys = new HashSet<String>(doubleMap.keySet());
		count = 0;
		for (String key : doubleKeys){
			if (currentDoubleMap.get(key).doubleValue() == doubleMap.get(key).doubleValue()){
				count++;
			}
		}
		//If all states are not the same in the previous step,
		//then clearly some adaptation has occurred 
		if (count != doubleKeys.size()){
			doubleMapBool = true;
		}
		
		
		//Boolean Map Comparison
		HashMap<String,Boolean> currentBooleanMap = currState.getBooleanMap();
		boolean booleanMapBool = false; 
		if (currentBooleanMap.size() != booleanMap.size()){
			booleanMapBool =  false;
		}
		Set<String> booleanKeys = new HashSet<String>(booleanMap.keySet());
		count = 0;
		for (String key : booleanKeys){
			if (currentBooleanMap.get(key).booleanValue() == (booleanMap.get(key).booleanValue())){
				count++;
			}
		}
		//If all states are not the same in the previous step,
		//then clearly some adaptation has occurred 
		if (count != booleanKeys.size()){
			booleanMapBool = true;
		}
		
		
		
		//String Map Comparison
		HashMap<String,String> currentStringMap = currState.getStringMap();
		boolean stringMapBool = false; 
		if (currentStringMap.size() != stringMap.size()){
			stringMapBool =  false;
		}
		Set<String> stringKeys = new HashSet<String>(stringMap.keySet());
		count = 0;
		for (String key : stringKeys){
			if (currentStringMap.get(key).compareTo(stringMap.get(key)) == 0){
				count++;
			}
		}
		
		//If all states are not the same in the previous step,
		//then clearly some adaptation has occurred 
		if (count != stringKeys.size()){
			stringMapBool = true;
		}	
		
		//Enum Map Comparison
		HashMap<String,Enum> currentEnumMap = currState.getEnumMap();
		boolean enumMapBool = false; 
		if (currentEnumMap.size() != enumMap.size()){
			enumMapBool =  false;
		}
		Set<String> enumKeys = new HashSet<String>(enumMap.keySet());
		count = 0;
		for (String key : enumKeys){
			if (currentEnumMap.get(key).compareTo(enumMap.get(key)) == 0){
				count++;
			}
		}
		//If all states are not the same in the previous step,
		//then clearly some adaptation has occurred 
		if (count != enumKeys.size()){
			enumMapBool = true;
		}	
	
		
		return (integerMapBool || doubleMapBool || booleanMapBool || stringMapBool || enumMapBool);
	}

	public HashMap<String, Integer> getIntegerMap() {
		return integerMap;
	}

	public HashMap<String, Boolean> getBooleanMap() {
		return booleanMap;
	}

	public HashMap<String, String> getStringMap() {
		return stringMap;
	}

	public HashMap<String, Double> getDoubleMap() {
		return doubleMap;
	}
	
	public HashMap<String, Enum> getEnumMap() {
		return enumMap;
	}
	
	@Override
	public String toString(){
		String str = "";
		
		
		return str;
	}
}