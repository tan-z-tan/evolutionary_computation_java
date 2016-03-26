package math.stchastics;


public class KullbackLeiblerDivergence
{
  /** 分布Pと分布QのKullback-Leibler Divergenceを返す */
  public static double calculateKLDivergence(HasDensity<Double> P, HasDensity<Double> Q, double[] domain)
  {
    double diversity = 0;
    try{
      for( int i = 0; i < domain.length; i++ )
      {
        diversity += P.density(domain[i]) * Math.log( (P.density(domain[i]) / Q.density(domain[i])) );
      }
    }catch(Exception e) {e.printStackTrace();}
    
    return diversity;
  }
  
  /** データtargetDataと分布PとのKullback-Leibler Divergenceを返す */
  public static double calculateKLDivergence(double[] targetData, HasDensity<Double> P, double[] domain)
  {
    double diversity = 0;
    try{
      for( int i = 0; i < domain.length; i++ )
      {
        diversity += P.density(domain[i]) * Math.log( (P.density(domain[i]) / targetData[i]) );
        //diversity += targetData[i] * Math.log( targetData[i] / P.density(domain[i]));
      }
    }catch(Exception e) {e.printStackTrace();}
    
    return diversity;
  }
}
