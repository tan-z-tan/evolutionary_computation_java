package math.stchastics;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.ldap.HasControls;

import org.apache.commons.math3.distribution.RealDistribution;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

class Indices
{
	int[] indices;
	public Indices(int[] indices)
	{
		this.indices = indices;
	}
	
	public boolean equals(Object obj)
	{
		if( obj != null && obj instanceof Indices && ((Indices)obj).indices.length == indices.length )
		{
			for( int i = 0; i < indices.length; i++ )
			{
				if( indices[i] != ((Indices)obj).indices[i] )
				{
					return false;
				}
			}
			return true;
    	}
		return false;		
	}
	
    public int hashCode()
    {
    	int hashCode = 1;
    	for( int i: indices )
    	{
    		hashCode = 31 * hashCode + i;
    	}
    	return hashCode;
    }
}

/**
 * 正規分布を表すクラス．確率密度や条件付き確率の計算を行う．
 * @author tanji
 *
 */
public class NormalDistribution
{
	private int n;
	private RealVector mean;
	private RealMatrix sigma;
	private double sigma_det;
	private RealMatrix sigma_inverse;
	private Map<Indices, List<RealMatrix>> conditionalMatrixMap;
	public static final NormalDistribution regularNormalDistribution;
	static{
		regularNormalDistribution = new NormalDistribution(0, 1);
	}
	
	public NormalDistribution(double mean, double sigma)
	{
		this(MatrixUtils.createRealVector(new double[]{mean}), MatrixUtils.createRealMatrix(new double[][]{{sigma}}));
	}
	
	public NormalDistribution(double[] mean, double[][] sigma)
	{
		this(MatrixUtils.createRealVector(mean), MatrixUtils.createRealMatrix(sigma));
	}
	
	public NormalDistribution(RealVector mean, RealMatrix sigma)
	{
		if( mean.getDimension() != sigma.getRowDimension() || mean.getDimension() != sigma.getColumnDimension() )
		{
			throw new IllegalArgumentException("The mean and sigma must have the same dimension.");
		}
		
		this.n = mean.getDimension();
		this.mean = mean;
		this.sigma = sigma;
		LUDecomposition decomp = new LUDecomposition(sigma);
		this.sigma_det = decomp.getDeterminant();
		this.sigma_inverse = decomp.getSolver().getInverse();
		this.conditionalMatrixMap = new HashMap<Indices, List<RealMatrix>>(4, 0.75f);
	}
	
	public double getLogProbability(double[] x)
	{
		return getLogProbability(MatrixUtils.createRealVector(x));
	}
	
	public double getLogProbability(RealVector x)
	{
		RealVector diff = x.subtract(mean);
		
		//double expContent = -diff.transpose().multiply(sigma_inverse).multiply(diff).getData()[0][0] / 2.0;
		double expContent = diff.dotProduct( sigma_inverse.operate(diff) );
		
		return Math.log(1 / (Math.pow((2 * Math.PI), n/2.0) * Math.sqrt(sigma_det) )) + expContent;
	}
	
	public double getProbability(double[] x)
	{
		return getProbability(MatrixUtils.createRealVector(x));
	}
	
	public double getProbability(double x)
	{
		return getProbability(MatrixUtils.createRealVector(new double[]{x}));
	}
	
	public double getProbability(RealVector x)
	{
		RealVector diff = x.subtract(mean);

		//RealMatrix diff = x.subtract(mean);
		//double expContent = -diff.transpose().multiply(sigma_inverse).multiply(diff).getData()[0][0] / 2.0;
		double expContent = -diff.dotProduct( sigma_inverse.operate(diff) ) / 2.0;
		
		return 1 / (Math.pow((2 * Math.PI), n/2.0) * Math.sqrt(sigma_det) ) * Math.exp(expContent);
	}
	
	/**
	 * return NormalDistribution(x|y)
	 * @param x
	 * @param y
	 * @return
	 */
	public double getConditionalProbability(int[] x_indices, double[] x, int[] y_indices, double[] y) // probability of x given y
	{
		return getConditionalProbability(x_indices, MatrixUtils.createColumnRealMatrix(x), y_indices, MatrixUtils.createColumnRealMatrix(y));
	}
	
	public double getConditionalLogProbability(int[] x_indices, double[] x, int[] y_indices, double[] y) // probability of x given y
	{
		return getConditionalLogProbability(x_indices, MatrixUtils.createColumnRealMatrix(x), y_indices, MatrixUtils.createColumnRealMatrix(y));
	}
	
	/**
	 * return log( NormalDistribution(x|y) )
	 * @param x
	 * @param y
	 * @return
	 */
	public double getConditionalLogProbability(int[] x_indices, RealMatrix x, int[] y_indices, RealMatrix y) // probability of x given y
	{
		List<RealMatrix> matrixList = getConditionalMatrixList(x_indices, y_indices);
		RealMatrix precision_xx_inverse = matrixList.get(0);
		RealMatrix precision_xy = matrixList.get(1);
		RealMatrix mean_x = matrixList.get(2);
		RealMatrix mean_y = matrixList.get(3);
		RealMatrix sigma_x_given_y = matrixList.get(4);
		RealMatrix sigma_x_given_y_inverse = matrixList.get(5);
		
		RealMatrix mean_x_given_y = mean_x.subtract( precision_xx_inverse.multiply(precision_xy).multiply(y.subtract(mean_y)) );
		
		// calculate
		int dimension = x.getRowDimension();
		RealMatrix diff = x.subtract(mean_x_given_y);
		double expContent = -diff.transpose().multiply( sigma_x_given_y_inverse ).multiply(diff).getData()[0][0] / 2.0;
		
		return Math.log(1 / (Math.pow((2 * Math.PI), dimension/2.0) * Math.sqrt(new LUDecomposition(sigma_x_given_y).getDeterminant() ) )) + expContent;
	}
	
	/**
	 * return NormalDistribution(x|y)
	 * @param x
	 * @param y
	 * @return
	 */
	public double getConditionalProbability(int[] x_indices, RealMatrix x, int[] y_indices, RealMatrix y) // probability of x given y
	{
		List<RealMatrix> matrixList = getConditionalMatrixList(x_indices, y_indices);
		RealMatrix precision_xx_inverse = matrixList.get(0);
		RealMatrix precision_xy = matrixList.get(1);
		RealMatrix mean_x = matrixList.get(2);
		RealMatrix mean_y = matrixList.get(3);
		RealMatrix sigma_x_given_y = matrixList.get(4);
		RealMatrix sigma_x_given_y_inverse = matrixList.get(5);
				
		RealMatrix mean_x_given_y = mean_x.subtract( precision_xx_inverse.multiply(precision_xy).multiply(y.subtract(mean_y)) );
		
		// calculate
		int dimension = x.getRowDimension();
		RealMatrix diff = x.subtract(mean_x_given_y);
		double expContent = -diff.transpose().multiply( sigma_x_given_y_inverse ).multiply(diff).getData()[0][0] / 2.0;
		
		return 1 / (Math.pow((2 * Math.PI), dimension/2.0) * Math.sqrt(new LUDecomposition(sigma_x_given_y).getDeterminant())) * Math.exp(expContent);
	}
	
	/**
	 * 条件付き分布を計算するために必要なマトリックスのリストを返す．
	 * @param x_indices
	 * @param y_indices
	 * @return
	 */
	private List<RealMatrix> getConditionalMatrixList(int[] x_indices, int[] y_indices)
	{
		Indices yHashElement = new Indices(y_indices);
		
		if( this.conditionalMatrixMap.containsKey(yHashElement) )
		{
			return conditionalMatrixMap.get(yHashElement);
		}
		else
		{
			RealMatrix precision_xx = sigma_inverse.getSubMatrix(x_indices, x_indices); 
			RealMatrix precision_xx_inverse = new LUDecomposition(precision_xx).getSolver().getInverse();
			RealMatrix precision_xy = sigma_inverse.getSubMatrix(x_indices, y_indices);
			//RealMatrix mu_x = mean.getSubMatrix(x_indices, new int[]{0});
			//RealMatrix mu_y = mean.getSubMatrix(y_indices, new int[]{0});
			RealMatrix mu_x = MatrixUtils.createColumnRealMatrix(mean.toArray()).getSubMatrix(x_indices, new int[]{0});
			RealMatrix mu_y = MatrixUtils.createColumnRealMatrix(mean.toArray()).getSubMatrix(y_indices, new int[]{0});
			
			RealMatrix sigma_x_given_y = sigma.getSubMatrix(x_indices, x_indices);
			RealMatrix sigma_x_given_y_inverse = new LUDecomposition(sigma_x_given_y).getSolver().getInverse();
			
			List<RealMatrix> matrixList = Arrays.asList(precision_xx_inverse, precision_xy, mu_x, mu_y, sigma_x_given_y, sigma_x_given_y_inverse);
			conditionalMatrixMap.put(yHashElement, matrixList);
			return matrixList;
		}
	}
	
	public NormalDistribution getConditionalModel(int[] x_indices, int[] y_indices, double[] yArray)
	{
		RealMatrix y = MatrixUtils.createColumnRealMatrix(yArray);
		
		List<RealMatrix> matrixList = getConditionalMatrixList(x_indices, y_indices);
		RealMatrix precision_xx_inverse = matrixList.get(0);
		RealMatrix precision_xy = matrixList.get(1);
		RealMatrix mean_x = matrixList.get(2);
		RealMatrix mean_y = matrixList.get(3);
		RealMatrix sigma_x_given_y = matrixList.get(4);
		//RealMatrix sigma_x_given_y_inverse = matrixList.get(5);
				
		RealMatrix mu_x_given_y = mean_x.subtract( precision_xx_inverse.multiply(precision_xy).multiply(y.subtract(mean_y)) );
		
		return new NormalDistribution(mu_x_given_y.getColumnVector(0), sigma_x_given_y);
	}
	
	public RealVector getMean()
	{
		return mean;
	}
	
	public RealMatrix getSigma()
	{
		return sigma;
	}
	
	public String toString()
	{
		StringBuilder str = new StringBuilder();
		str.append("Normal Distribution: mean= " + this.mean + " sigma= " + sigma);
		return str.toString();
	}
	
	/** returns probability density at x. This method exists for convenience. */
	public static double getProbabilityDensity(double x, double mean, double sigma)
	{
		return 1 / (Math.sqrt(2 * Math.PI) * sigma) * Math.exp( - (x - mean) * (x - mean) / (2 * sigma * sigma));
	}
		
	// --- main function for test ---
	public static void main(String[] args)
	{
		System.out.println( new int[]{1,2,3}.hashCode() );
		System.out.println( new int[]{1,2,3}.hashCode() );
		System.out.println( new int[]{1,2,3}.hashCode() );
		
		NormalDistribution model = new NormalDistribution(new double[]{0, 0, 0}, new double[][]{{1, 0.3, 0.5}, {0.3, 1, 0.7}, {0.5, 0.7, 1}});
		
		System.out.println( "Probability     = " + model.getProbability(new double[]{0, 1, 0}) );
		System.out.println( "Log Probability = " + model.getLogProbability(new double[]{0, 1, 0}) + " " + Math.exp(model.getLogProbability(new double[]{0, 1, 0})) );
		System.out.println();
		System.out.println( "Conditional Probability     = " + model.getConditionalProbability(new int[]{1, 2}, new double[]{1, 1}, new int[]{0}, new double[]{0}) );
		System.out.println( "Conditional Log Probability = " + model.getConditionalLogProbability(new int[]{1, 2}, new double[]{1, 1}, new int[]{0}, new double[]{0}) + " " + Math.exp(model.getConditionalLogProbability(new int[]{1, 2}, new double[]{1, 1}, new int[]{0}, new double[]{0})));		
		System.out.println();
		
		
		//System.out.println("Conditional Probability ");
		//performanceTest(model);
	}
	
	public static void performanceTest(NormalDistribution model)
	{
		// probability density function
		
		double sum = 0;
		long startTime = System.currentTimeMillis();
		for( int i = 0; i < 100; i ++ )
		{
			double[] x = new double[]{Math.random(), Math.random(), Math.random()};
			sum += model.getProbability(x);
		}
		System.out.println(sum);
		System.out.println("Time = " + (System.currentTimeMillis() - startTime));
		
		// conditional probability density function
		sum = 0;
		startTime = System.currentTimeMillis();
		int[] indeces_x = new int[]{0, 1};
		int[] indeces_y = new int[]{2};
		
		for( int i = 0; i < 100; i ++ )
		{
			double[] x = new double[]{Math.random(), Math.random()};
			double[] y = new double[]{Math.random()};
			sum += model.getConditionalProbability(indeces_x, x, indeces_y, y);
		}
		System.out.println(sum);
		System.out.println("Time = " + (System.currentTimeMillis() - startTime));
	}
}
