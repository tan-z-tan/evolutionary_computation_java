package math.functions;

public class SigmoidFunction
{
  /** 0 < f(x) < 1 となるシグモイド関数 */
  public static double apply(double value)
  {
    return 1.0 / (1 + Math.exp(-value));
  }
  
  /** min < f(x) < max となるシグモイド関数 */
  public static double apply(double value, double max, double min)
  {
    if( max < 0 )
    {
      return apply(value, min, max);
    }
    return (max - min) * apply(value) + min;
  }
  
  public static void main(String args[])
  {
    System.out.println( apply(-30, -14323, 123) );
  }
}
