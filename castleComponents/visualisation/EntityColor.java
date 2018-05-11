package castleComponents.visualisation;

import java.util.ArrayList;
import java.util.HashMap;

import castleComponents.objects.Vector2;

import java.awt.Color;

public class EntityColor {

	String targetParam;

	String positionParam;
	
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
			// TODO
		} else {
			pairs = new HashMap<String, ColorPair>();
		}

	}
	
	public void setPositionParam(String v) {
		positionParam = v;
	}
	
	public String getPositionParam() {
		return positionParam;
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
	
	
	public void addSet(String val, String col) {
		Color c = getColor(col);
		addSet(val, c);
	}

	public void setRange(int min, int max, Color minCol, Color maxCol) {
		colorRange = new ColorRange(min, max, minCol, maxCol);
	}
	
	public String getTargetParam() {
		return targetParam;
	}

	public Color getColorOfVal(String val) {
		if (colorType == ColorType.RANGE) {
			return colorRange.getCurrentColor(Integer.parseInt(val));
		} else {
			return pairs.get(val).getColor();
		}
	}
	
	

	Color getColor(String col) {
		Color color;
		switch (col.toLowerCase()) {
		case "black":
			color = Color.BLACK;
			break;
		case "blue":
			color = Color.BLUE;
			break;
		case "cyan":
			color = Color.CYAN;
			break;
		case "darkgray":
			color = Color.DARK_GRAY;
			break;
		case "gray":
			color = Color.GRAY;
			break;
		case "green":
			color = Color.GREEN;
			break;
		case "yellow":
			color = Color.YELLOW;
			break;
		case "lightgray":
			color = Color.LIGHT_GRAY;
			break;
		case "magneta":
			color = Color.MAGENTA;
			break;
		case "orange":
			color = Color.ORANGE;
			break;
		case "pink":
			color = Color.PINK;
			break;
		case "red":
			color = Color.RED;
			break;
		case "white":
			color = Color.WHITE;
			break;
		default:
			color = Color.WHITE;
			break;
		}
		return color;
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
	
	public Color getCurrentColor(int currVal) {
		float ratio = (float)currVal / (float)diff;
		 int red = (int) (maxColor.getRed() * ratio + minColor.getRed() * (1 - ratio));
         int green = (int) (maxColor.getGreen() * ratio + minColor.getGreen() * (1 - ratio));
         int blue = (int) (maxColor.getBlue() * ratio + minColor.getBlue() * (1 - ratio));
		
		return new Color(red, green, blue);
	}

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