package math.stchastics;

import org.apache.commons.math3.distribution.GammaDistribution;

/** ディリクレ分布に従うサンプルを生成するクラス */
public class DirichletDistributionSampler
{
	private GammaDistribution[] gammaList;
	
	public DirichletDistributionSampler(double[] alpha)
	{
		gammaList = new GammaDistribution[alpha.length];
		for( int i = 0; i < alpha.length; i++ )
		{
			gammaList[i] = new GammaDistribution(alpha[i], 1) ;
		}
	}
	
	public double[] sample()
	{
		double[] sample = new double[gammaList.length];
		
		double sum = 0;
		for( int i = 0; i < gammaList.length; i++ )
		{
			double x_i = gammaList[i].sample();
			sample[i] = x_i;
			sum += x_i;
		}
		for( int i = 0; i < gammaList.length; i++ )
		{
			sample[i] = sample[i] / sum;
		}
		
		return sample;
	}
}
