package math.stchastics;

import java.util.List;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealVector;

public class CorrelationCoefficient
{

    /**
     * @param args
     */
    public static void main(String[] args)
    {
        double[] dataX = new double[]{2,4,6,8,10};
        double[] dataY = new double[]{340,20,2390,23,24};
        //System.out.println( correlationCoefficient(dataX, dataY) );
        
        System.out.println(getVariance(new double[]{1,4,66,80}));
        System.out.println(getVariance(MatrixUtils.createRealVector(new double[]{1,4,66,80})));
    }
    public static double correlationCoefficient(double[] dataX, double[] dataY)
    {
        if( dataX.length != dataY.length )
        {
            throw new IllegalArgumentException("The length of data is different.");
        }
        
        double averageX = getAverage(dataX);
        double averageY = getAverage(dataY);
        double correlationCoefficient = 0;
        
        for( int i = 0; i < dataX.length; i++ )
        {
            correlationCoefficient += (dataX[i] - averageX) * (dataY[i] - averageY);
        }
        
        return correlationCoefficient / Math.sqrt(dataX.length * getVariance(dataX) * dataY.length * getVariance(dataY));
    }
    
    public static double correlationCoefficient(double[] dataX, List<Double> dataY)
    {
        if( dataX.length != dataY.size())
        {
            System.out.println(dataX.length + " " + dataY.size());
            throw new IllegalArgumentException("The length of data is different.");
        }
        
        double averageX = getAverage(dataX);
        double averageY = getAverage(dataY);
        double correlationCoefficient = 0;
        
        for( int i = 0; i < dataX.length; i++ )
        {
            correlationCoefficient += (dataX[i] - averageX) * (dataY.get(i)- averageY);
        }
        
        double denominator = Math.sqrt(dataX.length * getVariance(dataX) * dataY.size() * getVariance(dataY));
        if( denominator == 0 )
        {
            return 0;
        }
        else
        {
            return correlationCoefficient / denominator;
        }
    }
    
    public static double getAverage(List<Double> x)
    {
        if( x.size() == 0 )
        {
            return 0;
        }
        
        double sum = 0;
        for( double value: x )
        {
            sum += value;
        }
        return sum / x.size();
    }
    
    public static double getAverage(double[] x)
    {
        if( x.length == 0 )
        {
            return 0;
        }
        
        double sum = 0;
        for( double value: x )
        {
            sum += value;
        }
        return sum / x.length;
    }
    
    public static double getVariance(RealVector x)
    {
        double variance = 0;
        double average = x.getL1Norm() / x.getDimension();
        for( double value: x.toArray() )
        {
            variance += Math.pow(value - average, 2);
        }
        return variance / x.getDimension();
    }
    
    public static double getVariance(List<Double> x)
    {
        double variance = 0;
        double average = getAverage(x);
        for( double value: x )
        {
            variance += Math.pow(value - average, 2);
        }
        return variance / x.size();
    }
    
    public static double getVariance(double[] x)
    {
        double variance = 0;
        double average = getAverage(x);
        for( double value: x )
        {
            variance += Math.pow(value - average, 2);
        }
        return variance / x.length;
    }
}
