package stdSimLib;

public class State<T> implements java.io.Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1366472396542306010L;
	T[] allValues;
	T currentState;
	T initialState;
	String stateName;
	int numberOfPossibleValues;
	
	/**
	 * 
	 * @param stateName
	 * @param all possible values this state can have of type T. First entry is default initial state.
	 */
	@SafeVarargs
	public State(String stateName, T... allPossibleValues){
		allValues = allPossibleValues;
		numberOfPossibleValues = allValues.length;
		this.stateName = stateName;
		
		initialState = allValues[0];
		currentState = initialState;
	}
	
	public boolean setState(T newState){
		if (checkValueExists(newState)){
			currentState = newState;
			return true;
		} 
		return false;	
	}
	
	public void setInitialState(T initialState){
		if (checkValueExists(initialState)){
			this.initialState = initialState;
		}
	}
	
	public void initialiseState(){
		currentState = initialState;
	}
	
	public String listStateValues(String separator){
		String out = "";
		for (T value : allValues){
			out += value + separator;
		}
		
		return out.substring(0,out.length()-1);
	}
	
	public String publishState(String open, String close, String separator){		
		return open + stateName + " " + listStateValues(separator) + close;
	}
	
	public String publishCurrentState(String open, String close, String separator){		
		return open + "name=\"" + stateName+"\" type=\"" + getStateTypeAsString() + "\" value=\"" + currentState + "\"" + close;
	}
	
	public String publishStateWithType(String open, String close, String separator){
		return open + "name=\"" + stateName+"\" type=\"" + getStateTypeAsString() + "\" values=\"" + listStateValues(separator) + "\"" + close;
	}
	
	public String getStateTypeAsString(){
		return currentState.getClass().getName().toLowerCase().replace("java.lang.", "");
	}
	
	public Class<? extends Object> getStateType(){
		return currentState.getClass();
	}
	
	private boolean checkValueExists(T newValue){
		for (T value : allValues){
			if (newValue == value){
				return true;
			}
		}
		return false;
	}
	
	public T getCurrentState(){
		return currentState;
	}

	
	public boolean stateEquals(T compare){
		return compare == currentState;
	}
	

	/**
	 * @return the stateName
	 */
	public String getStateName() {
		return stateName;
	}
	
	public boolean compareState(State<?> otherState){
		return (otherState.getStateTypeAsString().compareToIgnoreCase(getStateTypeAsString()) == 0) 
			&& (otherState.getCurrentState() == getCurrentState());
	}
}
