package castleComponents.visualisation;

import java.util.ArrayList;
import java.awt.Color;
import java.awt.color.*;

public class EntityColor {

	String targetParam;

	enum ColorType {
		SET, BOOLEAN, RANGE
	};

	ColorType colorType;

	// For Set and boolean (its a special set)
	ArrayList<ColorPair> pairs;

	// For Range
	ColorRange colorRange;

	public EntityColor(String targetParam, String colorTypeStr) {
		this.targetParam = targetParam;
		colorType = ColorType.valueOf(colorTypeStr);
		if (colorType == ColorType.RANGE) {

		} else {
			pairs = new ArrayList<ColorPair>();
		}

	}

	public void addSet(String value, Color c) {
		if (colorType == ColorType.SET) {
			pairs.add(new ColorPair(value, c));
		} else if (colorType == ColorType.BOOLEAN) {
			if (pairs.size() >= 2) {
				//clear and replace
				pairs.clear();
			}
			pairs.add(new ColorPair(value, c));
		}
	}
}

class ColorPair {
	String value;
	Color color;

	public ColorPair(String val, Color c) {
		this.value = val;
		this.color = c;
	}
	// getset
}

class ColorRange {
	int min;
	int max;
	int diff;
	Color minColor;
	Color maxColor;

	public ColorRange(int min, int max, Color minC, Color maxC) {
		this.min = min;
		this.max = max;
		diff = this.max - this.min;
		this.minColor = minC;
		this.maxColor = maxC;

	}
	// TODO getset
	// TODO gradient
}