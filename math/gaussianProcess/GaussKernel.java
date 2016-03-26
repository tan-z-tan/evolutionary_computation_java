package math.gaussianProcess;

import org.apache.commons.math3.linear.RealVector;

public class GaussKernel implements Kernel<RealVector>
{
    private final double sigma2;
    
    public GaussKernel(double sigma2)
    {
        this.sigma2 = sigma2;
    }
    
    @Override
    public double k(RealVector x1, RealVector x2)
    {
        return Math.exp(-Math.pow(x1.subtract(x2).getNorm(), 2) / (2 * sigma2) );
    }
    
    public static double norm(double[] x)
    {
        double value = 0;
        for( int i = 0; i < x.length; i++ )
        {
            value += x[i] * x[i];
        }
        return Math.sqrt(value);

    }
    public static double[] diff(double[] x1, double[] x2)
    {
        double[] diff = new double[x1.length];
        for( int i = 0; i < x1.length; i++ )
        {
            diff[i] = x1[i] - x2[i];
        }
        return diff;
    }
    
    public static double innerProduct(double[] x1, double[] x2)
    {
        double value = 0;
        for( int i = 0; i < x1.length; i++ )
        {
            value += x1[i] * x2[i];
        }
        return value;
    }

    public double getSigma2()
    {
        return sigma2;
    }
}
