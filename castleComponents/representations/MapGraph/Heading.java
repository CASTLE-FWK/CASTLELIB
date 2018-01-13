package castleComponents.representations.MapGraph;

public enum Heading {
	N, S, E, W, SE, SW, NE, NW, NONE;

	public static Heading getHeadingFromInts(int x, int y) {
		Heading h = null;
		if (x > 0) {
			if (y > 0) {
				h = SE;
			} else if (y < 0) {
				h = NE;
			} else {
				h = E;
			}
		} else if (x < 0) {
			if (y > 0) {
				h = SW;
			} else if (y < 0) {
				h = NW;
			} else {
				h = W;
			}
		} else {
			if (y > 0) {
				h = S;
			} else if (y < 0) {
				h = N;
			} else {
				h = NONE;
			}
		}
		return h;
	}
}