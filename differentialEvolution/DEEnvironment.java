package differentialEvolution;

import ecCore.Environment;

public class DEEnvironment<T extends DEIndividual> extends Environment<T>
{
	protected int _chromosomeLength;
	protected Class<T> _geneType;
	
	public DEEnvironment()
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