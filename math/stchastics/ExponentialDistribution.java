package math.stchastics;

public class ExponentialDistribution
{
  public static double getRandomValue(double lambda)
  {
    return -Math.log(1 - Math.random()) / lambda; 
  }
}
