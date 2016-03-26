package random;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;
import java.util.RandomAccess;

public class PropotionalSampling
{
	/** returns random indices with the probability proportionate to given rateList */
	public static List<Integer> getRandomSample(int n, List<Double> rateList) {
		
		double sum = 0;
		for( int i = 0; i < rateList.size(); i++ ) {
			sum += rateList.get(i);
		}
		List<Integer> result = new ArrayList<Integer>();
		
		double[] randomValueLise = new double[n];
		for( int i = 0; i < n; i++ )
		{
			randomValueLise[i] = RandomManager.getRandom() * sum;
		}
		Arrays.sort(randomValueLise);
		int searchIndex = 0;
		
		double cumulativeValue = 0;
		for( int i = 0; i < rateList.size(); i++ ) {
			cumulativeValue += rateList.get(i);
			for( ; searchIndex < randomValueLise.length && randomValueLise[searchIndex] <= cumulativeValue; searchIndex++ ) {
				result.add( i );
			}
		}
		shuffle(result);
		return result;
	}
	
    public static void shuffle(List<Integer> list) {
        int size = list.size();
        
        Integer arr[] = list.toArray(new Integer[]{});
        
        // Shuffle array
        for (int i=size; i>1; i--) {
        	int randInt = RandomManager.getRandom(size);
        	//swap(arr, i-1, rnd.nextInt(i));
        	int tmp = arr[i-1];
        	arr[i-1] = arr[randInt];
        	arr[randInt] = tmp;
        }
        
        // Dump array back into list
        ListIterator it = list.listIterator();
        for (int i=0; i<arr.length; i++) {
        	it.next();
        	it.set(arr[i]);
        }
    }
    
	public static void main(String[] args)
	{
		List<Double> rateList = new ArrayList<Double>();
		double sum = 0;
		for( int i = 0; i < 100; i++ ) {
			rateList.add( (double)(i) );
			sum += rateList.get(i);
		}
		
		for( int i = 0; i < rateList.size(); i++ ) {
			rateList.set(i, rateList.get(i) / sum);
		}
		System.out.println( rateList );
		List<Integer> indices = getRandomSample(1000, rateList);
		System.out.println( indices );
		int count = 0;
		int pre = -1;
		for( int i = 0; i < indices.size(); i++ ) {
			if( indices.get(i) != pre ) {
				System.out.println( pre + " " + count );
				pre = indices.get(i);
				count = 1;
			} else {
				count++;
			}
		}
	}
}
