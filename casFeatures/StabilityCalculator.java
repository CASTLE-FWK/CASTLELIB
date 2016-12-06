package casFeatures;

public class StabilityCalculator {
	
	private int initialWindowSize = 5;
	private int currentWindowSize = initialWindowSize;
	private int windowShifter = 1;
	private int windowCounter = 0;
	private int numberOfTrackedStates = 1;
	private double threshold = 0.025; //This is extremely difficult to set
	private double[] currentWindowValues;
	
	//Tracking the increased window start
	private int currentStepNumber = 0;
	private int startOfCurrentWindow = 0;
	private boolean increasing = false;
	
	public StabilityCalculator(int numberOfTrackedStates){
		setNumberOfTrackedStates(numberOfTrackedStates);
		currentWindowValues = new double[initialWindowSize];
		System.out.println("Threshold is set at "+threshold);
	}
	
	public void setNumberOfTrackedStates(int n){
		this.numberOfTrackedStates = n;
//		System.out.println("Number of tracked states (check if correct): " + this.numberOfTrackedStates);
	}
	
	//This can't return a boolean
	public void newValue(double value, int step) {
		currentStepNumber = step;
		if (windowCounter < (currentWindowSize - 1) ) {
			currentWindowValues[windowCounter] = value;
			windowCounter++;			
		} else if (windowCounter == currentWindowSize){
			//uh-oh
			System.out.println("StabilityCalc: ERROR");
		} else {
			//Run The Adaptation Stability Calculation
			
			//Sum all numbers in window
			double sum = 0;
			for (int i = 0; i < currentWindowSize; i++){
				sum += currentWindowValues[i];
			}
			//Take average
			double avg = sum / currentWindowSize;
			
			//Subtract average from current
			double sub = value - avg;
			
			//Divide by tracked states
			double div = sub / numberOfTrackedStates;
			
			//Check for threshold
//			System.out.println("div: " + div);
			if (div <= threshold && div >= -threshold){				
				increaseWindowSize();
				newWindow(value);
//				System.out.println("Increasing Window");
				if (!increasing){
					increasing = true;
					startOfCurrentWindow = currentStepNumber;
					System.out.println("New window starting at step " + startOfCurrentWindow);
				}
				return;
			} else {			
				increasing = false;
//				System.out.println("Decreasing Window");
				reset(value);
				return;
			}
		}		
	}
	
	public void newWindow(double newValue){
		windowCounter = 0;
		currentWindowValues = new double[currentWindowSize];
		currentWindowValues[windowCounter] = newValue;
		windowCounter++;
	}
	
	public void reset(double newValue){
		currentWindowSize = initialWindowSize;
		newWindow(newValue);				
	}
	
	public void increaseWindowSize(){
		currentWindowSize++;
	}
}