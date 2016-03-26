package ecCore;

import java.io.Serializable;

public abstract class Individual implements Comparable<Individual>, Serializable
{
	protected double _fitnessValue;
	
	/** 
	 * returns fitness value
	 * @return
	 */
	public double getFitnessValue()
	{
		return _fitnessValue;
	}
	/**
	 * sets fitness value
	 * @param value
	 */
	public void setFitnessValue(double value)
	{
		_fitnessValue = value;
	}
	
	/**
	 * Evaluates this individual.
	 */
	public Object evaluate()
	{
		return null;
	}
	  
	@Override
	public int compareTo(Individual ind)
	{
		if( ind.getFitnessValue() < this._fitnessValue )
		{
			return 1;
		}
		else if( ind.getFitnessValue() > this._fitnessValue )
		{
			return -1;
		}
		else
		{
			return 0;
		}
	}
	
	@Override
	public String toString()
	{
		return String.valueOf(_fitnessValue);
	}
}
