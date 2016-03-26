package math.stchastics;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.linear.RealVector;

public class MixtureNormalDistribution implements math.stchastics.ContinuousDistribution<RealVector>
{
	private List<NormalDistribution> innerDistributionsList;
	private List<Double> weightList;
	
	public MixtureNormalDistribution()
	{
		this(new ArrayList<NormalDistribution>(), new ArrayList<Double>());
	}
	
	public MixtureNormalDistribution(List<NormalDistribution> normalDistributions, List<Double> weightList)
	{
		this.innerDistributionsList = normalDistributions;
		this.weightList = weightList;
	}
	
	@Override
	public double density(RealVector x)
	{
		double sum = 0;
		for( int i = 0; i < innerDistributionsList.size(); i++ )
		{
			sum += weightList.get(i) * innerDistributionsList.get(i).getProbability(x);
			//System.out.println( " " + x + " " + innerDistributionsList.get(i).getProbability(x) + " " + innerDistributionsList.get(i).getMean() + " " + innerDistributionsList.get(i).getSigma() );
		}
		return sum;
	}

	@Override
	public RealVector sample()
	{
		// TODO Auto-generated method stub
		return null;
	}
	
}
