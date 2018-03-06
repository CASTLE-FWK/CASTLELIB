package observationTool.metrics;
import java.util.ArrayList;
import java.util.HashMap;
import experimentExecution.TypeMap;
import stdSimLib.utilities.Utilities;

public class MetricBuilder {
	
	
	//What does each line look like?
	//Each line is a metric pool
	//typeMaps={(name, eType, eVar, cd)*}	exclude=(METRICNAME*)	metricParams={METRICNAME=([n,v]*) *}
	
	static ArrayList<TypeMap> typeMaps;
	static ArrayList<String> exclusions;
	static HashMap<String, MetricParameters> metricParams; 
	
	public static void main(String[] args) {
		typeMaps = new ArrayList<TypeMap>();
		exclusions = new ArrayList<String>();
		metricParams = new HashMap<String, MetricParameters>();
		String metricParamFile = args[0];
		ArrayList<String> fileAsList = new ArrayList<String>(Utilities.parseFileLineXLine(metricParamFile));
		for (String s : fileAsList) {
			s = s.trim();
			String[] spl = s.split("\t");
			for (String st : spl) {
				if (st.startsWith("typeMaps")) {
					st = st.replace("typeMaps=", "");
					st = st.replaceAll("{", "");
					st = st.replaceAll("}", "");
					String[] tmSpl = st.split(")");
					if (tmSpl.length == 0) {
						System.out.println("No type maps were provided");
					} else {
						for (String tms : tmSpl) {
							tms = tms.replaceAll("(", "");
							String[] theTm = tms.split(",");
							TypeMap tm = new TypeMap(theTm[1], theTm[2], theTm[3], theTm[0]);
							typeMaps.add(tm);
						}
					}
				} else if (st.startsWith("exclude")) {
					st = st.replace("exclude=", "");
					st = st.replace("(", "");
					st = st.replace(")", "");
					String[] exSpl = st.split(",");
					for (String sss : exSpl) {
						exclusions.add(sss);
					}
					
				} else if (st.startsWith("metricParams")){
					st = st.replace("metricParams=", "");
					st = st.replaceAll("{", "");
					st = st.replaceAll("}", "");
					String[] mps = st.split(" ");
					for (String mpss : mps) {
						String[] metSpl = mpss.split("=");
						String metName = metSpl[0];
						MetricParameters mp = new MetricParameters();
						String [] metNVPairs = metSpl[1].split("]");
						for (String mnvpV : metNVPairs) {
							mnvpV = mnvpV.replaceAll("([","");
							String[] thePair = mnvpV.split(",");
							mp.addParameter(thePair[0], thePair[1]);
						}
						metricParams.put(metName, mp);
					}
				}
			}
		}
		
		//Build the thing
	}
}
