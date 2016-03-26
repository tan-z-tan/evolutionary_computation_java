package geneticAlgorithm;

import java.util.List;

import ecCore.Individual;

/**
 * The Individual class of GP.
 * @author tanji
 */
public class GaIndividual <T> extends Individual
{
	//protected T[] _genom;
	protected List<T> _genom;
	
	public GaIndividual()
	{
		_genom = null;
		_fitnessValue = 0;
	}
	
	//public T[] getGene()
	{
		//return _genom;
	}
	
	public List<T> getGene()
	{
		return _genom;
	}
	
	//public void setGene(T[] gene)
	{
		//_genom = gene;
	}
	
	public void setGene(List<T> gene)
	{
		_genom = gene;
	}
	
	//@Override
	//public String toString()
	//{
		//return String.valueOf(_fitnessValue);
	//}
}