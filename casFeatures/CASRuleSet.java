package casFeatures;
import casFeatures.CASLib.*;

public class CASRuleSet {

	CAS_Inspection_Level checking;
	CAS_Rule_Type type;
	CAS_Rule_Exception[] exceptions;
	
	public CASRuleSet(CAS_Inspection_Level checking, CAS_Rule_Type type, CAS_Rule_Exception[] exceptions){
		this.checking = checking;
		this.type = type;
		this.exceptions = exceptions;
	}

	public CAS_Inspection_Level getChecking() {
		return checking;
	}

	public CAS_Rule_Type getType() {
		return type;
	}

	public CAS_Rule_Exception[] getExceptions() {
		return exceptions;
	}
	
	@Override
	public String toString(){
		String str = "";
		str += "Inspection Level: "+checking.toString();
		str += "\nRule Type: " + type.toString();
		str += "\nExceptions: ";
		for (CAS_Rule_Exception ex : exceptions){
			str += ex.toString()+" ";
		}
		return str;
	}

	
	
}
