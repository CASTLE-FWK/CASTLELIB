package visualisation;

import java.util.ArrayList;
import java.util.HashMap;

import observationTool.DataCollector_FileSystem;
import observationTool.VEntity;
import stdSimLib.Parameter;
import visualisation.phorcys.Utilities;

public class InfoDump {

	String datasetID;
	String db = "simulations";
	HashMap<String, VAgentSpec> specs; // <AgentName, VAgentSpec>
	int numberOfSteps;
	int currentTime = 0;
	DataCollector_FileSystem collector;
	int startTime = 0;

	final String CHAOS = "CHAOS";
	final String MINORITY = "MINORITY";
	final String TOTAL = "TOTAL";
	final String MAJORITY = "MAJORITY";

	public static void main(String[] args) {

		// We could just look it
		String dsid = args[0];
		InfoDump id;
		ArrayList<String> paths = new ArrayList<String>(stdSimLib.utilities.Utilities.parseFileLineXLine(dsid));
		for (String s : paths) {
			if (s.startsWith("#"))
				continue;
			id = new InfoDump(s);
		}
		

		// Get ID to return a string and then we can dump to the realEvents.csv
		// EM AD CR ST
	}

	public boolean isSame(String a, String b) {
		return a.compareTo(b) == 0;
	}

	public InfoDump(String datasetID) {
		this.datasetID = datasetID;
		// Connect to DB
		collector = new DataCollector_FileSystem(db);
		// Get access to the desired dataset
		collector.setCollection(this.datasetID);

		// Get useful information about the system
		numberOfSteps = collector.getTerminationStep();
		HashMap<String, String> params = collector.getInitialisationParameters();
		// Get all steps
		int totalSteps = numberOfSteps;

		// Property variables
		// Stability
		String stab_prevLevel = CHAOS;
		ArrayList<PropertyRange> stabilityRanges = new ArrayList<PropertyRange>();
		PropertyRange stab_currRange = null;

		// Adaptability
		ArrayList<PropertyRange> adaptabilityRanges = new ArrayList<PropertyRange>();
		double adapt_prevLevel = Double.NEGATIVE_INFINITY;
		PropertyRange adapt_currRange = null;

		// Emergence
		ArrayList<PropertyRange> emergenceRanges = new ArrayList<PropertyRange>();
		PropertyRange em_currRange = null;

		// Criticality
		ArrayList<PropertyRange> critRanges = new ArrayList<PropertyRange>();
		PropertyRange crit_currRange = null;

		// For each step
		for (int i = 0; i < totalSteps; i++) {
			// Get the agents we want

			ArrayList<VEntity> subComms = new ArrayList<VEntity>(collector.buildVGroupMap(i).values());
			double avgConsensus = 0;

			for (VEntity v : subComms) {
				if (v.getParameterValue("consensusLevel") == null) {
					avgConsensus += -1.0;
				} else {
					avgConsensus += Double
							.parseDouble(((Parameter<?>) v.getParameterValue("consensusLevel")).getCurrentValue());
				}
			}

			String conLevel = CHAOS;
			double threshold = 0;
			avgConsensus = avgConsensus / (double) subComms.size();
			if (avgConsensus <= 0.0 + threshold) {
				conLevel = CHAOS;
			} else if (avgConsensus >= 1.00 - threshold) {
				conLevel = TOTAL;
			} else if (avgConsensus >= 0.0 + threshold && avgConsensus <= 0.5) {
				conLevel = MINORITY;
			} else if (avgConsensus > 0.5 && avgConsensus < 1.0 - threshold) {
				conLevel = MAJORITY;
			}

			// Lets find properties
			// Stability
			if (isSame(stab_prevLevel, conLevel)) {
				if (stab_currRange == null) {
					stab_currRange = new PropertyRange(i);
				}

			} else {
				if (stab_currRange != null) {
					stab_currRange.end(i);
					stabilityRanges.add(new PropertyRange(stab_currRange));
					stab_currRange = null;
				}
			}

			// Emergence
			if (isSame(conLevel, MAJORITY)) {
				if (em_currRange == null) {
					em_currRange = new PropertyRange(i);
				}
			} else {
				if (em_currRange != null) {
					em_currRange.end(i);
					emergenceRanges.add(new PropertyRange(em_currRange));
					em_currRange = null;
				}
			}

			// Criticality
			// Can't do this until the end

			// Adaptability
			if (avgConsensus != adapt_prevLevel) {
				if (adapt_currRange == null) {
					adapt_currRange = new PropertyRange(i);
				}
			} else {
				if (adapt_currRange != null) {
					adapt_currRange.end(i);
					adaptabilityRanges.add(new PropertyRange(adapt_currRange));
					adapt_currRange = null;
				}
			}

			// System.out.print("Step:" + i + ",");
			// System.out.println("consensus:"+avgConsensus+" | "+conLevel);
		}
		// Close the open ranges
		// EM
		if (em_currRange != null) {
			em_currRange.end(totalSteps);
			emergenceRanges.add(new PropertyRange(em_currRange));
			em_currRange = null;
		}
		// AD
		if (adapt_currRange != null) {
			adapt_currRange.end(totalSteps);
			adaptabilityRanges.add(new PropertyRange(adapt_currRange));
			adapt_currRange = null;
		}
		// ST
		if (stab_currRange != null) {
			stab_currRange.end(totalSteps);
			stabilityRanges.add(new PropertyRange(stab_currRange));
			stab_currRange = null;
		}

		// Do the Criticality
		// For EM
		for (PropertyRange pr : emergenceRanges) {
			critRanges.add(new PropertyRange(pr.getStart() - 1, pr.getStart() - 1));
			critRanges.add(new PropertyRange(pr.getEnd() + 1, pr.getEnd() + 1));
		}

		// For ST
		for (PropertyRange pr : stabilityRanges) {
			critRanges.add(new PropertyRange(pr.getStart() - 1, pr.getStart() - 1));
			critRanges.add(new PropertyRange(pr.getEnd() + 1, pr.getEnd() + 1));
		}

		//TEST SIZES
//		System.out.println(emergenceRanges.size());
//		System.out.println(adaptabilityRanges.size());
//		System.out.println(critRanges.size());
//		System.out.println(stabilityRanges.size());
//		
		
		// Print out a string
		int line = 0;
		String out = "";
		boolean printing = true;
		// Ew
		String COMMA = ",";
		while (printing) {
			String em = printRange(line, emergenceRanges);
			String ad = printRange(line, adaptabilityRanges);
			String cr = printRange(line, critRanges);
			String st = printRange(line, stabilityRanges);

			if ((em + ad + cr + st).length() == 0) {
				printing = false;
				break;
			}

			out += em + COMMA + ad + COMMA + cr + COMMA + st + "\n";
			line++;
		}

		System.out.println(out);
		System.out.println("DS:"+datasetID);
		String outPath = datasetID+"/realEvents.csv";
		Utilities.writeToFile(out, outPath);
		// Write that string to a file (datasetID+"/realEvents.csv");
	}

	public String printRange(int line, ArrayList<PropertyRange> prs) {
		if (prs.size() >= line + 1) {
			// Can print
			return prs.get(line).toString();
		} else {
			return "";
		}
	}

}

class PropertyRange {
	private int startTime;
	private int endTime;

	public PropertyRange(int s) {
		start(s);
	}

	public PropertyRange(int s, int e) {
		start(s);
		end(e);
	}

	public PropertyRange(PropertyRange pr) {
		start(pr.getStart());
		end(pr.getEnd());
	}

	public void start(int s) {
		if (s < 0) {
			s = 0;
		}
		this.startTime = s;
	}

	public void end(int s) {
		if (s < 0) {
			s = 0;
		}
		this.endTime = s;
	}

	public String toString() {
		if (startTime == endTime) {
			return ""+startTime;
		}
		return startTime + "-" + endTime;
	}

	public int getStart() {
		return startTime;
	}

	public int getEnd() {
		return endTime;
	}

}
