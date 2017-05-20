package stdSimLib;

/**
 * Simple class to represent scores from a metric
 * @author lollbirdsey
 *
 */

public class MetricScore{
	final String QUOTE = "\"";
	final String EQUALS = "=";
	final String SPACE = " ";
	final String METRIC_MM = "metric";
	final String CONFIG_MM = "config";
	final String REFER_MM = "referenceSS";
	final String RESUL_MM = "result";	
	
	String metricName = "";
	String metricConfiguration = "";
	String referenceSSID = "";
	String result = "";
	
	public MetricScore(String metricName, String metricConfiguration, String referenceID, String result){
		this.metricName = metricName;
		this.metricConfiguration = metricConfiguration;
		this.referenceSSID = referenceID;
		this.result = result;
	}
	
	public boolean compare(MetricScore ms){
		return (metricName.compareToIgnoreCase(ms.metricName) == 0) && (metricConfiguration.compareToIgnoreCase(ms.metricConfiguration) == 0)
				&& (referenceSSID.compareToIgnoreCase(ms.referenceSSID) == 0);
	}
	
	@Override
	public String toString(){
		return "Metric: "+ metricName + " Metric Configuration: " + metricConfiguration + " Reference SS ID: " + referenceSSID + " Result: "+ result;
	}		
	
	public String toMetaModel(String open, String close){
		return open + METRIC_MM + EQUALS + QUOTE + metricName + QUOTE + SPACE + CONFIG_MM + EQUALS + QUOTE + metricConfiguration + 
				QUOTE + SPACE + REFER_MM + EQUALS + QUOTE + referenceSSID + QUOTE + SPACE + RESUL_MM + EQUALS + QUOTE + result + QUOTE + close; 
	}
}
