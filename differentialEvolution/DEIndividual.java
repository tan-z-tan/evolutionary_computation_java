package differentialEvolution;

import org.apache.commons.math3.linear.RealVector;

import ecCore.Individual;

/**
 * The Individual class of GP.
 * @author tanji
 */
public class DEIndividual extends Individual
{
	protected RealVector _genom;
	
	public DEIndividual()
	{
		_genom = null;
		_fitnessValue = 0;
	}
	
	public RealVector getGene()
	{
		return _genom;
	}
	
	public void setGene(RealVector gene)
	{
		_genom = gene;
	}
}