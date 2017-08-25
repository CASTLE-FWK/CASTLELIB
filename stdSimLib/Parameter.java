package stdSimLib;

public class Parameter<T> {
	
	String name;
	T value;
	String type;
	String valueAsString;
	boolean usingPBR = false;
	
	public Parameter(T value, String name){
		this.value = value;
		valueAsString = this.value.toString();
		this.name = name;
		type = this.value.getClass().getName();
	}
	
	public Parameter(T value, String name, String type){
		this.value = value;
		valueAsString = this.value.toString();
		this.name = name;
		this.type = type;
	}
	
	public Parameter(T[] value, String name){
		usingPBR = true;
		this.value = value[0];
		valueAsString = this.value.toString();
		this.name = name;
		type = this.value.getClass().getName();
	}
	
	
	public Parameter(T[] value, String name, String type){
		usingPBR = true;
		this.value = value[0];
		valueAsString = this.value.toString();
		this.name = name;
		this.type = type;
	}


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public T getValue() {
		return value;
	}

	public void setValue(T value) {
		this.value = value;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public void updateValue(T value2){
		this.value = value2;
	}
	
	public String getCurrentValue(){
		return value.toString();
	}
	
	@Override
	public String toString(){
		return getType()+" "+getName()+" = "+getValue();
	}
	
	public boolean compare(Parameter<?> other){
		if (this.name.compareTo(other.getName()) == 0){
			if (this.type.compareTo(other.getType()) == 0){
				if (this.value.toString().compareTo(other.getValue().toString()) == 0){
					return true;
				}
			}
		}
		return false;
	}
}
