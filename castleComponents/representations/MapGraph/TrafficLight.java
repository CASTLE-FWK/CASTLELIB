package castleComponents.representations.MapGraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import castleComponents.objects.Vector2;
import stdSimLib.utilities.RandomGen;

public class TrafficLight {

	Vector2 location;
	ArrayList<Vector2> lightPatterns;
	Vector2 currPattern;
	int timeLeftGreen = 0;
	// What type am I?
	ArrayList<Vector2> listOfExits;
	HashSet<Vector2> setOne;
	HashSet<Vector2> setTwo;
	HashSet<Vector2> activeSet;
	final String SETONE = "SETONE";
	final String SETTWO = "SETTWO";
	String activeSetState = "";
	int nextCounter = 0;

	public TrafficLight(Vector2 loc, ArrayList<Vector2> lp) {
		this.location = new Vector2(loc);
		lightPatterns = new ArrayList<Vector2>();
		for (Vector2 v : lp) {
			lightPatterns.add(new Vector2(v));
		}
		listOfExits = new ArrayList<Vector2>();
		setOne = new HashSet<Vector2>();
		setTwo = new HashSet<Vector2>();

	}

	public void addExits(ArrayList<Vector2> exits) {
		for (Vector2 v : exits) {
			listOfExits.add(new Vector2(v));
		}

		int[] hasMatch = new int[listOfExits.size()];
		for (int i = 0; i < hasMatch.length; i++) {
			hasMatch[i] = -1;
		}
		for (int i = 0; i < listOfExits.size(); i++) {
			for (int j = i + 1; j < listOfExits.size(); j++) {
				hasMatch[i] = j;
			}
		}
		for (int i = 0; i < hasMatch.length; i++) {
			if (hasMatch[i] == -1) {
				// No match occured
				setTwo.add(listOfExits.get(i));
			} else {
				setOne.add(listOfExits.get(i));
				setOne.add(listOfExits.get(hasMatch[i]));
			}
		}

	}

	public Vector2 getLocation() {
		return location;
	}

	public void setLocation(Vector2 location) {
		this.location = location;
	}

	public ArrayList<Vector2> getLightPatterns() {
		return lightPatterns;
	}

	public void setLightPatterns(ArrayList<Vector2> lightPatterns) {
		this.lightPatterns = lightPatterns;
	}

	public void start() {
		boolean initActiveSet = RandomGen.generateCoinFlip();
		if (initActiveSet) {
			activeSetState = SETONE;
			activeSet = setOne;
		} else {
			activeSetState = SETTWO;
			activeSet = setTwo;
		}
		currPattern = getNextPattern();
		timeLeftGreen = (int) currPattern.getY();
	}

	// TODO
	public boolean haveToStop(Vector2 currPos, int dist) {
		if (activeSet.size() > 0) {
			Vector2 aPos = (Vector2) activeSet.toArray()[0];
			if (currPos.sameX(aPos)) {
				if (Math.abs(currPos.getX() - aPos.getX()) <= dist) {
					return true;
				}
			} else if (currPos.sameY((Vector2) activeSet.toArray()[0])) {
				if (Math.abs(currPos.getY() - aPos.getY()) <= dist) {
					return true;
				}
			}
		} else {
			errLog("polly shouldn't be");
		}
		return false;
	}

	public void next() {
		timeLeftGreen--;
		if (timeLeftGreen == 0) {
			currPattern = getNextPattern();
			timeLeftGreen = (int) currPattern.getY();
			if (activeSetState == SETONE) {
				activeSetState = SETTWO;
				activeSet = setTwo;
			} else {
				activeSetState = SETONE;
				activeSet = setOne;
			}
			errLog("activeSet: " + activeSetState);
		}
	}

	public Vector2 getNextPattern() {
		int currNext = nextCounter;
		nextCounter++;
		if (nextCounter == lightPatterns.size() - 1) {
			nextCounter = 0;
		}
		return lightPatterns.get(currNext);
	}

	public void errLog(Object o) {
		System.out.println("TrafficLight: " + o.toString());
	}
}
