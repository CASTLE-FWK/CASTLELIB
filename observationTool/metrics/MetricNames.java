package observationTool.metrics;

public class MetricNames {
	public final static String SYSCOMP = "System Complexity";
	public final static String CHANGOL11 = "Chan GoL 11";
	public final static String OTOOLE14 = "OToole 14";
	public final static String OD = "Oscillation Detection";
	public final static String CLUSTER = "Tag & Track";
	public final static String MSSE = "Multi-Scale-Shannon-Entropy";
	public final static String LBR = "Limited Bandwidth Recognition";
	public final static String ENTROPY = "Entropy Over Time";
	public final static String WAT = "KaddoumWAT";
	public final static String VILLEGAS = "VillegasAU";
	public final static String PERFSIT = "PerfSit";
	public final static String COUNTER = "Counter";
	public final static String SIMPLESTATISTIC = "SimpleStatistic";
	
	public static String[] getListOfAllAvailableMetric() {
		return new String[]{SYSCOMP,CHANGOL11,OTOOLE14, OD, CLUSTER, MSSE, LBR, ENTROPY, WAT, VILLEGAS, PERFSIT, COUNTER, SIMPLESTATISTIC};
	}
}
