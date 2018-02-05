package castleComponents;

import castleComponents.Enums.FeatureType;

public class Feature {
	FeatureType ft;
	String n;
	int occurrence = 0;

	public Feature(String n, FeatureType ft) {
		this.n = n;
		this.ft = ft;
		occurrence = 1;
	}

	public FeatureType getFeatureType() {
		return ft;
	}

	public String getName() {
		return n;
	}

	public int getOccurrence() {
		return occurrence;
	}

	public void incrementOccurrence() {
		occurrence++;
	}

	public void setFeatureType(FeatureType ft) {
		this.ft = ft;
	}

	public void setName(String n) {
		this.n = n;
	}
}