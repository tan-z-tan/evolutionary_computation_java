package math.gaussianProcess;

import org.apache.commons.math3.linear.RealVector;


public class GPKernel implements Kernel<RealVector>
{
    private final double theta0;
    private final double theta1;
    private final double theta2;
    private final double theta3;
        
    public GPKernel(double theta0, double theta1, double theta2, double theta3)
    {
        this.theta0 = theta0;
        this.theta1 = theta1;
        this.theta2 = theta2;
        this.theta3 = theta3;
    }
    
    @Override
    public double k(RealVector x1, RealVector x2)
    {
        //return theta0 * Math.exp(-theta1 * Math.pow(norm(diff(x1,x2)), 2) / 2.0) + theta2 + theta3 * innerProduct(x1, x2);
    	return theta0 * Math.exp(-theta1 * Math.pow(x1.subtract(x2).getNorm(), 2.0) / 2) + theta2 + theta3 * x1.dotProduct(x2);
    }
    
    public double getTheta0()
    {
        return theta0;
    }

    public double getTheta1()
    {
        return theta1;
    }

    public double getTheta2()
    {
        return theta2;
    }

    public double getTheta3()
    {
        return theta3;
    }
}
