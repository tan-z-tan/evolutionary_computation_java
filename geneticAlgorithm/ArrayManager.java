package geneticAlgorithm;


import java.util.ArrayList;
import java.util.List;

import random.RandomManager;

public class ArrayManager
{	
	public static <T> GaIndividual<T>[] crossoverrrr(GaIndividual<T> parentA, GaIndividual<T> parentB, double ratio)
	{
		// check
		if( parentA.getGene().size() != parentB.getGene().size() )
		{
			System.out.println("Crossover operation must be done on genes of same length.");
			return null;
		}
		
		GaIndividual<T> childA = new GaIndividual<T>();
		GaIndividual<T> childB = new GaIndividual<T>();
		List<T> childAList = new ArrayList<T>();
		List<T> childBList = new ArrayList<T>();
		for( int i = 0; i < parentA.getGene().size(); i++ )
		{
			if( RandomManager.getRandom() > ratio )
			{
				childAList.add(parentB.getGene().get(i));
				childBList.add(parentA.getGene().get(i));
			}
			else
			{
				childAList.add(parentA.getGene().get(i));
				childBList.add(parentB.getGene().get(i));
			}
		}
		childA.setGene(childAList);
		childB.setGene(childBList);
		GaIndividual<T>[] gaIndividuals = new GaIndividual[]{childA, childB};
		return gaIndividuals;
	}
	
	public static <T> GaIndividual<T>[] crossoverrrr(GaIndividual<T> parentA, GaIndividual<T> parentB)
	{
		// check
		if( parentA.getGene().size() != parentB.getGene().size() )
		{
			System.out.println("Crossover operation must be done on genes of same length.");
			return null;
		}
		
		GaIndividual<T> childA = new GaIndividual<T>();
		GaIndividual<T> childB = new GaIndividual<T>();
		List<T> childAList = new ArrayList<T>();
		List<T> childBList = new ArrayList<T>();		
		int crossoverPoint = (int)(RandomManager.getRandom() * parentA.getGene().size());
		int index = 0;
		
		for( ; index < crossoverPoint; index++ )
		{
			childAList.add(parentB.getGene().get(index));
			childBList.add(parentA.getGene().get(index));
		}
		for( ; index < parentA.getGene().size(); index++ )
		{
			childAList.add(parentA.getGene().get(index));
			childBList.add(parentB.getGene().get(index));
		}
		
		childA.setGene(childAList);
		childB.setGene(childBList);
		GaIndividual<T>[] gaIndividuals = new GaIndividual[]{childA, childB};
		return gaIndividuals;
	}
	
	private static <T> void mutationHelper(List<T> list, GaEnvironment<? extends GaIndividual<T>> environment)
	{
		environment.getGeneType();
	}
	
	public static <T> GaIndividual<T> mutation(GaIndividual<T> parent, GaEnvironment<? extends GaIndividual<T>> environment)
	{
		GaIndividual<T> child = new GaIndividual<T>();
		List<T> childList = new ArrayList<T>();
		try{	
			if( environment.getGeneType().equals(Integer.class) )
			{
				for( int i = 0; i < parent.getGene().size(); i++ )
				{
					if( RandomManager.getRandom() > 0.5)
					{
						((List<Integer>)childList).add(1);
					}
					else
					{
						((List<Integer>)childList).add(0);
					}
				}
			}
		} catch (Exception e) { e.printStackTrace(); }
		
		child.setGene(childList);
		return child;
	}
	
	public static <T> GaIndividual<T> mutation(GaIndividual<T> parent, double mutationRatio)
	{
		GaIndividual<T> child = new GaIndividual<T>();
		List childList = new ArrayList<T>();
		for( int index = 0; index < parent.getGene().size(); index++ )
		{			
			if( RandomManager.getRandom() < mutationRatio )
			{
				if( RandomManager.getRandom() > 0.5)
				{
					childList.add(1);
				}
				else
				{
					childList.add(0);
				}
			}
			else
			{
				childList.add(parent.getGene().get(index));
			}
		}
		
		child.setGene(childList);
		return child;
	}
}
