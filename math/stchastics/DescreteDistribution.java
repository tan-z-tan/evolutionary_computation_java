package math.stchastics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.math3.util.Pair;

import random.RandomManager;

public class DescreteDistribution<E> implements HasDensity_Descrete<E>{
	private Map<E, Double> elements;
	private List<E> keys;
	private double[] probabilityRate;
	
	public DescreteDistribution()
	{
		elements = new HashMap<E, Double>();
		probabilityRate = new double[0];
	}
	
	public DescreteDistribution(List<E> data) {
		this();
		setData(data);
	}
	
	public DescreteDistribution(Map<E, Double> data) {
		this();
		setData(data);
	}
	
	public Set<E> getUniqueElements() {
		return elements.keySet();
	}
	
	public List<E> getUniqueElementList() {
		return keys;
	}
	public double[] getUniqueElementProbababilityList() {
		return probabilityRate;
	}
		
	public void setData(Map<E, Double> data)
	{
		elements.clear();
		
		double sum = 0;
		for(E e: data.keySet())
		{
			if( elements.containsKey(e) ) {
				elements.put(e, elements.get(e) + data.get(e));
			} else {
				elements.put(e, data.get(e));
			}
			sum += data.get(e);
		}
		for( E e: elements.keySet() )
		{
			elements.put(e, elements.get(e) / sum);
		}
		
		keys = new ArrayList<E>(elements.keySet());
		probabilityRate = new double[keys.size()];
		double cumulativeProbability = 0;
		
		for( int i = 0; i < keys.size(); i++ )
		{
			cumulativeProbability += elements.get( keys.get(i) );
			probabilityRate[i] = cumulativeProbability;
		}
	}
	
	public void setData(List<E> data)
	{
		elements.clear();
		double probabilityForOneAppear = 1.0 / data.size();
		
		for(E e: data)
		{
			if( elements.containsKey(e) ) {
				elements.put(e, elements.get(e) + probabilityForOneAppear);
			} else {
				elements.put(e, probabilityForOneAppear);
			}
		}
		
		keys = new ArrayList<E>(elements.keySet());
		probabilityRate = new double[keys.size()];
		double cumulativeProbability = 0;
		
		for( int i = 0; i < keys.size(); i++ )
		{
			cumulativeProbability += elements.get( keys.get(i) );
			probabilityRate[i] = cumulativeProbability;
		}
	}
		
	public E sample()
	{
		double rand = RandomManager.getRandom();
		int index = Arrays.binarySearch( probabilityRate, rand );
		if( index < 0 )
		{
			index = -index - 1;
		}
		return keys.get(index);
	}
	
	public double getProbability(E e) {
		if( elements.containsKey(e) ) {
			return elements.get(e);
		} else {
			return 0;
		}
	}
	
	@Override
	public double density(E x) {
		if( elements.containsKey(x) ) {
			return elements.get(x);
		}
		return 0;
	}
	
	public static <T> DescreteDistribution<Pair<T, T>> createJointDistribution(List<T> dataA, List<T> dataB) {
		List<Pair<T, T>> jointData = new ArrayList<Pair<T, T>>();
		
		for( int i = 0; i < dataA.size(); i++ )
		{
			jointData.add(new Pair<T, T>(dataA.get(i), dataB.get(i)));
		}
		
		DescreteDistribution<Pair<T, T>> model = new DescreteDistribution<Pair<T, T>>( jointData );
		return model;
	}
	
	/**
	 * modelAとmodelBの組み合わせを返す。
	 * @param modelA
	 * @param modelB
	 * @return
	 */
	public static <E> List<Pair<E, E>> createJointCombination(DescreteDistribution<E> modelA, DescreteDistribution<E> modelB) {
		List<Pair<E, E>> jointData = new ArrayList<Pair<E, E>>();
		
		for( E e1: modelA.getUniqueElements() ) {
			for( E e2: modelB.getUniqueElements() ) {
				jointData.add(new Pair<E, E>(e1, e2));
			}
		}
		return jointData;
	}
	
	public static void main(String[] args) {
		DescreteDistribution<Integer> prob1 = new DescreteDistribution<Integer>();
		DescreteDistribution<Integer> prob2 = new DescreteDistribution<Integer>();
		prob1.setData(Arrays.asList(0, 0, 0, 0, 0, 1, 1, 2, 2, 2, 3, 3, 4, 5, 6, 7, 7, 7, 7, 7, 8, 8, 9, 10, 11, 11, 11, 11, 11, 11));
		
		prob2.setData(Arrays.asList(1, 1, 2, 2, 3, 3, 4, 4));
		
		List<Pair<Integer, Integer>> jointData = createJointCombination(prob1, prob2);
		System.out.println(jointData);
		
		System.out.println( prob1.density(0) );
		System.out.println( prob1.density(1) );
		System.out.println( prob1.density(2) );
		System.out.println( prob1.density(3) );
		System.out.println( prob1.density(4) );
		System.out.println( prob1.density(4) );
		
		
		System.out.println();
		
		Map<Integer, Double> freq = new HashMap<Integer, Double>();
		freq.put(0, 0.0);
		freq.put(1, 0.0);
		freq.put(2, 0.0);
		freq.put(3, 0.0);
		freq.put(4, 0.0);
		freq.put(5, 0.0);
		freq.put(6, 0.0);
		freq.put(7, 0.0);
		freq.put(8, 0.0);
		freq.put(9, 0.0);
		freq.put(10, 0.0);
		freq.put(11, 0.0);
		for( int i = 0; i < 10000; i++ )
		{
			int v = prob1.sample();
			freq.put( v, freq.get(v) + 1.0 / 10000 );
		}
		
		System.out.println( freq );
	}
}
