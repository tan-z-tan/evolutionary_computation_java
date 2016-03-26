package math.stchastics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.crypto.Data;

import org.apache.commons.math3.util.Pair;

public class EntropyFamily
{
	public static void main(String[] args)
	{
		List<Integer> listA = Arrays.asList(0,0,1,1,2,3,4);
		List<Integer> listB = Arrays.asList(0,1,2,2,3,4,5);
		DescreteDistribution<Integer> modelA = new DescreteDistribution<Integer>(listA);
		DescreteDistribution<Integer> modelB = new DescreteDistribution<Integer>(listB);
		
		System.out.println("Entropy " + calculateEntropy( listA ));
		System.out.println("KL(modelA, modelB) = " + calculateKLDivergence(modelA, modelB));
		
		modelA = new DescreteDistribution<Integer>(Arrays.asList(1,1,1,0,1,1,0,1,0,0,1,1,0,0,1,1,1,0,1,1,0,1));
		modelB = new DescreteDistribution<Integer>(Arrays.asList(1,0,1,0,1,0,1,0,1,1,0,1,1,0,1,1,0,1,1,0,0,1));
		
		System.out.println("Mutual Information = " + calculateMutualInformation(
				Arrays.asList(1,1,1,0,1,1,0,1,0,0,1,1,0,0,1,1,1,0,1,1,0,1),
				Arrays.asList(1,0,1,0,1,0,1,0,1,1,0,1,1,0,1,1,0,1,1,0,0,1)));
		System.out.println("Transfer Entropy A->B = " + calculateTransferEntropy(
				Arrays.asList(1,1,1,0,1,1,0,1,0,0,1,1,0,0,1,1,1,0,1,1,0,1),
				Arrays.asList(1,0,1,0,1,0,1,0,1,1,0,1,1,0,1,1,0,1,1,0,0,1)));
		System.out.println("Transfer Entropy B->A = " + calculateTransferEntropy(
				Arrays.asList(1,0,1,0,1,0,1,0,1,1,0,1,1,0,1,1,0,1,1,0,0,1),
				Arrays.asList(1,1,1,0,1,1,0,1,0,0,1,1,0,0,1,1,1,0,1,1,0,1)));
	}
	
	public static <E> double calculateTransferEntropy(List<E> dataA, List<E> dataB) {
		DescreteDistribution<E> modelA = new DescreteDistribution<E>(dataA);
		DescreteDistribution<Pair<E,E>> jointModel = DescreteDistribution.createJointDistribution(dataA, dataB);
		
		List<Pair<E,E>> chainDataA = new ArrayList<Pair<E,E>>();
		List<List<E>> joint3Data = new ArrayList<List<E>>();
		
		for( int i = 0; i < dataA.size() -1; i++ )
		{
			joint3Data.add(Arrays.asList(dataA.get((i+1) % dataA.size()), dataA.get(i),	dataB.get(i)));
			chainDataA.add(new Pair<E, E>(dataA.get((i+1) % dataA.size()), dataA.get(i)));
		}
		
		DescreteDistribution<List<E>> joint3Model = new DescreteDistribution<List<E>>(joint3Data);
		DescreteDistribution<Pair<E,E>> chainModelA = new DescreteDistribution<Pair<E,E>>(chainDataA);
		
		double te = 0;
		for( List<E> state: joint3Model.getUniqueElements() )
		{
			double p_x_1_x_y = joint3Model.density(state);
			double p_x_y = jointModel.density(new Pair<E,E>(state.get(1), state.get(2)));
			double p_x_1_x = chainModelA.density(new Pair<E,E>(state.get(0), state.get(1)));
			double p_x = modelA.density(state.get(1));
			double p_x_1_bar_x_y = p_x_1_x_y / p_x_y;
			double p_x_1_bar_x = p_x_1_x / p_x;
			
			te += joint3Model.density(state) * Math.log(p_x_1_bar_x_y / p_x_1_bar_x) / Math.log(10);
		}
		
		return te;
	}
	
	/**
	 * @param listA
	 * @param listB
	 * @return */
	public static <E> double calculateMutualInformation(List<E> dataA, List<E> dataB) {
		DescreteDistribution<E> modelA = new DescreteDistribution<E>(dataA);
		DescreteDistribution<E> modelB = new DescreteDistribution<E>(dataB);
		DescreteDistribution<Pair<E,E>> jointModel = DescreteDistribution.createJointDistribution(dataA, dataB);
		
		double mi = 0;
		for( Pair<E,E> e: jointModel.getUniqueElements() )
		{
			double p_x_y = jointModel.density(e);
			double p_x = modelA.density(e.getKey());
			double p_y = modelB.density(e.getValue());
			mi += p_x_y * (Math.log(p_x_y / (p_x * p_y)) / Math.log(10));
		}
		
		return mi;
	}
	
	/**
	 * Calculate the entropy of the specified list.
	 * E(list) = \sigma_x p(x) log(p(x))
	 * @param listA
	 * @return
	 */
	public static <E> double calculateEntropy(List<E> list)
	{
		DescreteDistribution<E> probModel = new DescreteDistribution<E>(list);
		return calculateEntropy(probModel);
	}
	
	/**
	 * Calculate the entropy of the specified model.
	 * E(list) = \sigma_x p(x) log(p(x))
	 * @param listA
	 * @return
	 */
	public static <E> double calculateEntropy(DescreteDistribution<E> probModel)
	{
		double entropy = 0;
		for( E e: probModel.getUniqueElements() )
		{
			double p = probModel.density(e);
			entropy += - p * Math.log(p);
		}
		
		return entropy;
	}
	
	public static <E> Set<Pair<E, E>> listPair(Set<E> listA, Set<E> listB) {
		Set<Pair<E, E>> pairList = new HashSet<Pair<E, E>>();
		for( E e1: listA ) {
			for( E e2: listB ) {
				pairList.add( new Pair<E, E>(e1, e2) );
			}
		}
		
		return pairList;
	}
	
	/**
	 * 離散型のKLダイバージェンス
	 * @param listA
	 * @param listB
	 * @return
	 */
	public static <E> double calculateKLDivergence(HasDensity_Descrete<E> modelA, HasDensity_Descrete<E> modelB) {
		double diversity = 0;
		for( E e: modelA.getUniqueElements() ) {
			double p_A = modelA.density(e);
			double p_B = modelB.density(e);
			
			if( p_B != 0 )
			{
				diversity += p_A * Math.log(p_A / p_B);
			}
		}
		
		return diversity;
	}
	
  /** 分布Pと分布QのKullback-Leibler Divergenceを返す */
  public static double calculateKLDivergence(HasDensity<Double> P, HasDensity<Double> Q, double[] domain)
  {
    double diversity = 0;
    try{
      for( int i = 0; i < domain.length; i++ )
      {
        diversity += P.density(domain[i]) * Math.log( (P.density(domain[i]) / Q.density(domain[i])) );
      }
    }catch(Exception e) {e.printStackTrace();}
    
    return diversity;
  }
  
  /** データtargetDataと分布PとのKullback-Leibler Divergenceを返す */
  public static double calculateKLDivergence(double[] targetData, HasDensity<Double> P, double[] domain)
  {
    double diversity = 0;
    try{
      for( int i = 0; i < domain.length; i++ )
      {
        diversity += P.density(domain[i]) * Math.log( (P.density(domain[i]) / targetData[i]) );
        //diversity += targetData[i] * Math.log( targetData[i] / P.density(domain[i]));
      }
    }catch(Exception e) {e.printStackTrace();}
    
    return diversity;
  }
}
