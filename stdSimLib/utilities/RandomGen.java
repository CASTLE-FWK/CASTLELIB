package stdSimLib.utilities;

import java.util.Random;

import castleComponents.objects.Vector2;

public class RandomGen {

	private static Random random = new XSRandom(System.currentTimeMillis());
	private static final double FIFTY_PERCENT = 0.5;
	
	public static double generateRandomRangeDouble(double min, double max){
		if (min > max){
			double tmp = min;
			min = max;
			max = tmp;
		} else if (min == max){
			return min;
		}
		
		return (min + (random.nextDouble() * ((max - min) + 1))); 
	}
	
	public static int generateRandomRangeInteger(int min, int max){
		if (min > max){
			int tmp = min;
			min = max;
			max = tmp;
		} else if (min == max){
			return min;
		}
//		return CASRandom.random((max - min) + 1) + min;
		return random.nextInt((max - min) + 1) + min;
	}
	
	public static boolean generateWithProbabilty(double probability){
		return (random.nextDouble() < probability);
	}
	
	public static boolean generateCoinFlip(){
		return generateWithProbabilty(FIFTY_PERCENT);
	}
	
	public static boolean generateBiasedCoinFlip(double f){
		return generateWithProbabilty(f);
	}
	
	public static float generateRandomFloat(){
		return random.nextFloat();
	}
	
	public static Vector2 randomiseVectorDouble(double xMin, double xMax, double yMin, double yMax){	
		double X = generateRandomRangeDouble(xMin, xMax);
		double Y = generateRandomRangeDouble(yMin, yMax);
		
		return new Vector2(X,Y);
	}

	public static Vector2 randomiseVectorDouble(Vector2 xVec, Vector2 yVec){
		return randomiseVectorDouble(xVec.getX(), xVec.getY(), yVec.getX(), yVec.getY());
	}
	
	public static Vector2 randomiseVectorDouble(double min, double max){	
		return randomiseVectorDouble(min,max,min,max);
	}
	
	//TODO: This is probably more useless than originally thought.
	public static Vector2 randomiseVectorInteger(int xMin, int xMax, int yMin, int yMax){	
		double X = generateRandomRangeInteger(xMin, xMax);
		double Y = generateRandomRangeInteger(yMin, yMax);
		
		return new Vector2(X,Y);
	}
	
	//TODO: Explain this one well.
	public static Vector2 randomiseBooleanVector(double xMin, double xMax, double yMin, double yMax){
		double X, Y;
		if (generateCoinFlip()){
			X = xMin;
		} else {
			X = xMax;			
		}
		
		if (generateCoinFlip()){
			Y = yMin;
		} else {
			Y = yMax;
		}
		
		return new Vector2(X,Y);			
	}
	
	public static Vector2 randomiseVectorInteger(int min, int max){			
		return randomiseVectorInteger(min,max,min,max);
	}
}
