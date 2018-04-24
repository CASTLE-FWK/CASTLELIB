package castleComponents.visualisation;

import java.util.ArrayList;
import java.util.HashMap;
import java.awt.Color;

public class EntityColor {

	String targetParam;

	enum ColorType {
		SET, BOOLEAN, RANGE
	};

	ColorType colorType;

	// For Set and boolean (its a special set)
	HashMap<String, ColorPair> pairs;

	// For Range
	ColorRange colorRange;

	public EntityColor(String targetParam, String colorTypeStr) {
		this.targetParam = targetParam;
		colorType = ColorType.valueOf(colorTypeStr);
		if (colorType == ColorType.RANGE) {

		} else {
			pairs = new HashMap<String, ColorPair>();
		}

	}

	public void addSet(String value, int r, int g, int b) {
		Color c = new Color(r, g, b);
		addSet(value, c);
	}

	public void addSet(String value, Color c) {
		if (colorType == ColorType.SET) {
			pairs.put(value, new ColorPair(value, c));
		} else if (colorType == ColorType.BOOLEAN) {
			if (pairs.size() >= 2) {
				// clear and replace
				pairs.clear();
			}
			pairs.put(value, new ColorPair(value, c));
		}
	}
	
	public String getTargetParam() {
		return targetParam;
	}
	
	public Color getColor(String val) {
		return pairs.get(val).getColor();
	}
}

class ColorPair {
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	String value;
	Color color;

	public ColorPair(String val, Color c) {
		this.value = val;
		this.color = c;
	}
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
	// TODO gradient

	public int getMin() {
		return min;
	}

	public void setMin(int min) {
		this.min = min;
	}

	public int getMax() {
		return max;
	}

	public void setMax(int max) {
		this.max = max;
	}

	public int getDiff() {
		return diff;
	}

	public void setDiff(int diff) {
		this.diff = diff;
	}

	public Color getMinColor() {
		return minColor;
	}

	public void setMinColor(Color minColor) {
		this.minColor = minColor;
	}

	public Color getMaxColor() {
		return maxColor;
	}

	public void setMaxColor(Color maxColor) {
		this.maxColor = maxColor;
	}
}