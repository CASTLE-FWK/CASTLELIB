package observationTool;

import java.util.ArrayList;
import java.util.Collection;

public class Universals {

	public static final String BIRD = "Bird";
	public static final String ANT = "Ant";
	public static final String NON_ADV = "NonAdvocate";
	public static final String ADV = "Advocate";

	public static int numberOfNeighbours(VEntity v, Collection<VEntity> ents, double dist) {
		int num = 0;
		for (VEntity ve : ents) {
			if (ve == v)
				continue;

			if (v.getPosition().calculateDistance(ve.getPosition()) < dist) {
				num++;
			}
		}

		return num;
	}
}
