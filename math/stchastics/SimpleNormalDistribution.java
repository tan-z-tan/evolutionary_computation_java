package math.stchastics;


public class SimpleNormalDistribution implements HasDensity<Double>
{
  private static final double sqrt2Pi = Math.sqrt(2 * Math.PI);
  private double mean;
  private double sigma2;
  private double sigma;
  
  public SimpleNormalDistribution(double mean, double sigma2)
  {
    this.mean = mean;
    this.sigma2 = sigma2;
    this.sigma = Math.sqrt(sigma2);
  }

  @Override
  public double density(Double x)
  {
    return Math.exp(-(x - mean) * (x - mean) / (2 * sigma2)) / (sigma * sqrt2Pi);
  }
  
  public double getMean()
  {
    return mean;
  }

  public void setMean(double mean)
  {
    this.mean = mean;
  }

  public double getSigma2()
  {
    return sigma2;
  }

  public void setSigma2(double sigma2)
  {
    this.sigma2 = sigma2;
  }
  
  // --- static methods ---
  public static double getRandomValue()
  {
    double a = Math.random();
    double b = Math.random();
    return Math.sqrt(-2 * Math.log(a)) * Math.sin(2 * Math.PI * b);
  }
	
  public static double getProbabilityDensity(double x, double mean, double sigma)
	{
		double p = Math.exp( - (x - mean) * (x - mean) / (2 * sigma * sigma) ) / (sqrt2Pi* sigma);
		return p;
	}
	
  public static void main(String args[])
  {
    System.out.println( getRandomValue() );
    System.out.println( getRandomValue() );
    System.out.println( getRandomValue() );
    System.out.println( getRandomValue() );
    System.out.println( getRandomValue() );
    System.out.println( getRandomValue() );
    System.out.println( getRandomValue() );
    System.out.println( getRandomValue() );
    System.out.println( getRandomValue() );
    System.out.println( getRandomValue() );
  }
}
