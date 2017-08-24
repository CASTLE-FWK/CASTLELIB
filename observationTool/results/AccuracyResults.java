package observationTool.results;

public class AccuracyResults {

	int falsePositives = 0;
	int truePositives = 0;
	int falseNegatives = 0;
	int trueNegatives = 0;
	int totalHits = 0; //Should be a sum of the above 4 variables
	int realHits = 0;
	int goodHits = 0;
	int badHits = 0;
	int numberOfRealInstances = 0;

	public AccuracyResults() {

	}

	public boolean checkSanity() {
		if (sanitySum() == totalHits) {
			return true;
		}
		System.out.println("Error with Accuracy Calc");
		return false;
	}

	public int sanitySum() {
		return (falsePositives + truePositives + falseNegatives + trueNegatives);
	}

	public double getFalsePositivePercentage() {
		return (double) falsePositives / (double) totalHits;
	}

	public double getTruePositivePercentage() {
		return (double) truePositives / (double) totalHits;
	}

	public double getFalseNegativePercentage() {
		return (double) falseNegatives / (double) totalHits;
	}

	public double getTrueNegativePercentage() {
		return (double) trueNegatives / (double) totalHits;
	}

	@Override
	public String toString() {
		return (String.format("TP: %1$d TN: %2$d FP: %3$d FN: %4$d TH: %5$d", truePositives, trueNegatives,
				falsePositives, falseNegatives, totalHits));
	}

	public void addHit() {
		totalHits++;
	}

	public void addFalsePositive() {
		falsePositives++;
		addHit();
	}

	public void addTruePositive() {
		truePositives++;
		addHit();
	}

	public void addFalseNegative() {
		falseNegatives++;
		addHit();
	}

	public void addTrueNegative() {
		trueNegatives++;
		addHit();
	}

	public double calculateTPR() {
		return ((double) truePositives) / ((double) truePositives + falseNegatives);
	}

	public double calculateSPC() {
		return ((double) trueNegatives) / ((double) trueNegatives + falsePositives);
	}

	public double calculatePPV() {
		if (truePositives + falsePositives == 0) {
			return 0;
		}
		return ((double) truePositives) / ((double) truePositives + (double) falsePositives);
	}

	public double calculateNPV() {
		if (trueNegatives + falseNegatives == 0) {
			return 0;
		}
		return ((double) trueNegatives) / ((double) trueNegatives + (double) falseNegatives);
	}

	public double calculateACC() {
		return ((double) truePositives + trueNegatives)
				/ ((double) truePositives + falsePositives + trueNegatives + falseNegatives);
	}

	public double calculateMarkedness() {
		return (calculatePPV() + calculateNPV() - 1.0);
	}

	public double calculateTruePositiveRatio() {
		return (double) truePositives / totalHits;
	}

	public double F1Score() {
		return 2.0 * truePositives / (double) ((2.0 * truePositives) + falsePositives + falseNegatives);
	}

	public void setRealHits(int numRealHits) {
		realHits = numRealHits;
	}

	public double calculateRealHits() {
		if (realHits == 0) {
			//			System.out.println("is zero");
			return 0.0;
		}
		return (double) truePositives / (double) realHits;
	}

	public int getFalsePositives() {
		return falsePositives;
	}

	public void setFalsePositives(int falsePositives) {
		this.falsePositives = falsePositives;
	}

	public int getTruePositives() {
		return truePositives;
	}

	public void setTruePositives(int truePositives) {
		this.truePositives = truePositives;
	}

	public int getFalseNegatives() {
		return falseNegatives;
	}

	public void setFalseNegatives(int falseNegatives) {
		this.falseNegatives = falseNegatives;
	}

	public int getTrueNegatives() {
		return trueNegatives;
	}

	public void setTrueNegatives(int trueNegatives) {
		this.trueNegatives = trueNegatives;
	}

	public int getTotalHits() {
		return totalHits;
	}

	public void setTotalHits(int totalHits) {
		this.totalHits = totalHits;
	}

	public int getGoodHits() {
		return goodHits;
	}

	public void setGoodHits(int goodHits) {
		this.goodHits = goodHits;
	}

	public int getBadHits() {
		return badHits;
	}

	public void setBadHits(int badHits) {
		this.badHits = badHits;
	}

	public int getRealHits() {
		return realHits;
	}

	public void addBadHit() {
		addHit();
		badHits++;
	}

	public void addGoodHit() {
		addHit();
		goodHits++;
	}

	public void removeGoodHit() {
		goodHits--;
	}

	public int getNumberOfRealInstances() {
		return numberOfRealInstances;
	}

	public void setNumberOfRealInstances(int numberOfRealInstances) {
		this.numberOfRealInstances = numberOfRealInstances;
	}

	public AccuracyResults clone() {
		AccuracyResults nar = new AccuracyResults();
		nar.setFalseNegatives(falseNegatives);
		nar.setTrueNegatives(trueNegatives);
		nar.setFalsePositives(falsePositives);
		nar.setTruePositives(truePositives);
		nar.setTotalHits(totalHits);
		nar.setRealHits(realHits);
		nar.setGoodHits(goodHits);
		nar.setBadHits(badHits);
		nar.setNumberOfRealInstances(numberOfRealInstances);

		return nar;
	}
}