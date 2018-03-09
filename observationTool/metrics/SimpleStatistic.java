package observationTool.metrics;

import java.util.ArrayList;

import experimentExecution.MetricInfo;
import observationTool.DataCollector_FileSystem;
import observationTool.VEntity;
import observationTool.results.MetricResult;

public class SimpleStatistic extends MetricBase {

	DataCollector_FileSystem collector;
	private MetricResult mr;
	private int numberOfSteps = -1;

	private final String ENTC = "EntityCounter";
	private final String IC = "InteractionCounter";
	
	private boolean countingEntities = false;
	private boolean countingInteractions = false;
	
	public SimpleStatistic(MetricInfo mi) {
		super("SimpleStatistic", mi);
	}

	public void setCollector(DataCollector_FileSystem dc) {
		this.collector = dc;
	}

	public void setResultStore(MetricResult mr) {
		this.mr = mr;
	}

	public void setup(int nOS, boolean entiC, boolean interC) {
		this.numberOfSteps = nOS;
		this.countingEntities = entiC;
		this.countingInteractions = interC;
	}

	public void run() {
		if (mr == null || numberOfSteps < 0 || collector == null) {
			errLog("Something ain't right. Need to investiage. Exiting.");
		}

		if (countingEntities)
			mr.addResultType(ENTC);
		
		if (countingInteractions)
			mr.addResultType(IC);
		
		for (int time = 0; time < numberOfSteps; time++) {
			if (countingEntities) {
				int sum = collector.countAllEntitiesInStep(time);
				mr.addResultAtStep(ENTC, sum, time);
			}
			if (countingInteractions) {
				int inters = collector.countInteractionsInStep(time);
				mr.addResultAtStep(IC, inters, time);
			}
		}
	}

	public int entCount(ArrayList<VEntity> vs) {
		return vs.size();
	}

	public MetricResult getResults() {
		return mr;
	}

}
