package castleComponents.visualisation;

import java.util.ArrayList;
import java.util.HashMap;
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

	public EntityColor(String targetParam, String colorType) {
		this.targetParam = targetParam;

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
	//????
}