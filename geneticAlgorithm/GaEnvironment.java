package geneticAlgorithm;

import ecCore.Environment;

//public class GaEnvironment<T extends GaIndividual<?>> extends Environment<T>
public class GaEnvironment<T extends GaIndividual<?>> extends Environment<T>
{
	protected int _chromosomeLength;
	protected Class<T> _geneType;
	
	public GaEnvironment()
	{
		
	}
	
	// --- getter and setter ---
	public int getChromosomeLength()
	{
		return _chromosomeLength;
	}

	public void setChromosomeLength(int chromosomeLength)
	{
		_chromosomeLength = chromosomeLength;
	}

	public Class<T> getGeneType()
	{
		return _geneType;
	}

	public void setGeneType(Class geneType)
	{
		_geneType = geneType;
	}
}