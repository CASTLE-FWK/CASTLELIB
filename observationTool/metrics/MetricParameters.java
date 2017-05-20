package observationTool.metrics;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class MetricParameters{
	HashMap<String, Object> params;
	public MetricParameters() {
		params = new HashMap<String, Object>();
	}
	
	public void addParameter(String name, Object value){
		params.put(name, value);
	}
	
	public Object getParameterValue(String name){
		return params.get(name);
	}

	@Override
	public String toString(){
		String str = "";
		Iterator<Entry<String, Object>> it = params.entrySet().iterator();
		while (it.hasNext()){
			Map.Entry<String, Object> pair = (Map.Entry<String, Object>)it.next();
			str += pair.getKey()+": "+pair.getValue().toString()+", ";
		}				
		return str;
	}
}
