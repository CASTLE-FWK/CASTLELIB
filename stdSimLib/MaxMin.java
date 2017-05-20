package stdSimLib;

public class MaxMin<T> {

	T max;
	T min;
	double maxCount;
	double minCount;
	double total;
	
	public MaxMin() {
		maxCount = -Double.MAX_VALUE;
		minCount = Double.MAX_VALUE;
		total = 0;
		max = null;
		min = null;
	}
	
	public void newValue(T candidate, double val){
		if (val > maxCount){
			max = candidate;
			maxCount = val;
		}
		
		if (val < minCount){
			min = candidate;
			minCount = val;
		}
	}
	
	public T getMax(){
		return max;
	}
	
	public T getMin(){
		return min;
	}

	public double getMaxCount() {
		return maxCount;
	}

	public double getMinCount() {
		return minCount;
	}

	public double getTotal() {
		return total;
	}
}
