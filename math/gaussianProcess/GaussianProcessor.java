package math.gaussianProcess;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JFrame;

import math.stchastics.NormalDistribution;

import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.linear.SingularMatrixException;
import org.apache.commons.math3.linear.SingularValueDecomposition;

import visualization.PlotPanel2D;

public class GaussianProcessor
{
    private List<RealVector> dataXList;
    private List<RealVector> dataY;
    private RealMatrix dataYMatrix; 
    private RealVector dataYAverage;
    private Kernel<RealVector> kernel;
    private RealMatrix gramMatrix;
    private RealMatrix inverseGramMatrix;
    private RealMatrix weightArray;
    private double beta;
    
    // main method for test
    public static void main(String[] args)
    {
    	//test_1dimensional();
    	test_multiDimensional();
    }
    
    private static void test_1dimensional()
    {
    	List<RealVector> dataX = new ArrayList<RealVector>();
        double[] tmpX = new double[]{  -3,  0.0,  0.1, 0.2,  0.3, 0.4,   0.7,  0.8,  0.9};
        List<RealVector> dataY = new ArrayList<RealVector>();
        List<Double> dataYArray = Arrays.asList(2.0, 0.34, 0.83, 1.0, 0.97, 0.13,-0.84,-0.44, -0.2);
        
        for( double x: tmpX )
        {
            dataX.add(MatrixUtils.createRealVector(new double[]{x}));
        }
        for( double y: dataYArray )
        {
            dataY.add(MatrixUtils.createRealVector(new double[]{y}));
        }
        GaussianProcessor processor = new GaussianProcessor();
        processor.setDataXList(dataX);
        processor.setDataY(dataY);
        
        processor.setBeta( 0.01 );
        processor.setKernel(new GaussKernel(1));
                
        processor.calculateWeight();
        
        RealVector estimatedValue = processor.estimateMAP( MatrixUtils.createRealVector(new double[]{0.5}) );
        System.out.println( "Estimated value = " + estimatedValue );
        
        //processor.getPosteriorDistribution(new double[]{0.5});
        processor.getPosteriorDistribution( MatrixUtils.createRealVector(new double[]{0.5}) );
        
        validate(dataX, 0, 0, dataY, processor, -3, 3, 200);
    }
    
    public static void test_multiDimensional()
    {
    	List<RealVector> dataX = new ArrayList<RealVector>();
        double[][] tmpX = new double[][]{{0, 0}, {1, 0}, {1, 1}, {0, 1}, {-1, 1}, {-1, 0}, {-1, -1}, {0, -1}, {1, -1}};
        List<RealVector> dataY = new ArrayList<RealVector>();
        //List<Double> dataYArray = Arrays.asList(0.34,0.83, 1.0, 0.97, 0.13,-0.84,-0.44, -0.2);
        double[][] dataYArray = new double[][]{{0, 0-1}, {1, 1-1}, {2, 2-1}, {1, 1-1}, {0, 0-1}, {-1, -1-1}, {-2, -2-1}, {-1, -1-1}, {0, 0-1}};
        for( double[] x: tmpX )
        {
            dataX.add(MatrixUtils.createRealVector(x));
        }
        for( double[] y: dataYArray )
        {
        	dataY.add(MatrixUtils.createRealVector(y));
        }
        GaussianProcessor processor = new GaussianProcessor();
        processor.setDataXList(dataX);
        processor.setDataY(dataY);
        
        processor.setBeta( 1 );
        processor.setKernel(new GaussKernel(1));
                
        processor.calculateWeight();
        
        RealVector estimatedValue = processor.estimateMAP( MatrixUtils.createRealVector(new double[]{0.5, 0.5}) );
        System.out.println( "Estimated value = " + estimatedValue );
        
        //processor.getPosteriorDistribution(new double[]{0.5});
        System.out.println( processor.getPosteriorDistribution( MatrixUtils.createRealVector(new double[]{0.5, 0.5}) ) );
        
        validate(dataX, 1, 1, dataY, processor, -5, 5, 200);
    }
    
    public GaussianProcessor(RealMatrix inverseGramMatrix)
    {
    	
    }
    
    public GaussianProcessor()
    {
        //rangeXList = new ArrayList<double[]>();
    }
    
    /** 使う前にデータをセットし，このメソッドを呼び出す */
    public void calculateWeight() throws SingularMatrixException
    {
        weightArray = calculateWeight(dataXList, dataY, kernel);
    }
    
    public RealVector estimateMAP(double[] x)
    {
        return estimateMAP( MatrixUtils.createRealVector(x) );
    }
    
    public RealVector estimateMAP(RealVector x)
    {
        RealMatrix vectorK = getVarianceVector(x);
        // c = (beta + kernel.k(data, data)). c - K^t * C^{-1}_N * K
        return vectorK.transpose().multiply(this.inverseGramMatrix).multiply(dataYMatrix).getRowVector(0);
    }
    
    public NormalDistribution getPosteriorDistribution(RealVector x)
    {
        double value = 0;
        for( int i = 0; i < weightArray.getRowDimension(); i++ )
        {
            value += weightArray.getEntry(i, 0) * kernel.k(dataXList.get(i), x);
        }
        
        RealVector mean = this.estimateMAP(x);
        double sigma = this.getSigma(x);
        double[] sigmaArray = new double[mean.getDimension()];
        for( int i = 0; i < sigmaArray.length; i++ )
        {
        	sigmaArray[i] = sigma;
        }
        return new NormalDistribution(mean, MatrixUtils.createRealDiagonalMatrix(sigmaArray));
    }
    
    public RealVector calculateVectorK(RealVector data)
    {
        //double[] kData = new double[dataXList.size()];
    	RealVector kData = MatrixUtils.createRealVector(new double[dataXList.size()]);
        for( int i = 0; i < kData.getDimension(); i++ )
        {
        	//kData[i] = kernel.k(dataXList.get(i), data);
        	kData.setEntry(i, kernel.k(dataXList.get(i), data));
        }
        return kData;
        //return MatrixUtils.createRealMatrix(data.getData());
    }
        
    public static void validate(List<RealVector> dataX, int indexX, int indexY, List<RealVector> dataY, GaussianProcessor processor, double min, double max, int length)
    {
        // validation
        List<Point2D> givenData = new ArrayList<Point2D>();
        for( int i = 0; i < dataX.size(); i++ )
        {
        	//givenData.add(new Point2D.Double(processor.getDataXList().get(i).getData()[indexX], processor.getDataY().get(i).getData()[0]));
        	givenData.add( new Point2D.Double(dataX.get(i).getEntry(indexX), dataY.get(i).getEntry(indexY)) );
        }
        List<Point2D> estimatedData = new ArrayList<Point2D>();
        List<Point2D> upperSData = new ArrayList<Point2D>();
        List<Point2D> lowerSData = new ArrayList<Point2D>();
        List<Point2D> grid = new ArrayList<Point2D>();
        grid.add(new Point2D.Double(min, 0));
        grid.add(new Point2D.Double(max, 0));
        
        for( double i = min; i < max; i += (max - min) / length)
        {
        	RealVector x = MatrixUtils.createRealVector(new double[dataX.get(0).getDimension()]);
            //double[] x = new double[dataX.get(0).getDimension()];
            //x[index] = i;
        	x.setEntry(indexX, i);
        	double y = processor.estimateMAP(x).toArray()[indexY];
        	double standardDeviation = Math.sqrt( processor.getSigma(x));
            //double standardDeviation = Math.sqrt( processor.getSigma(dataX.));
            
            estimatedData.add(new Point2D.Double(x.getEntry(indexX), y));
            upperSData.add(new Point2D.Double(x.getEntry(indexX), y + 2 * standardDeviation));
            lowerSData.add(new Point2D.Double(x.getEntry(indexX), y - 2 * standardDeviation));
            //System.out.println(x[0] + " " + y);
        }
        
        PlotPanel2D panel = new PlotPanel2D();
        panel.addPlotData(givenData, PlotPanel2D.PLOT_TYPE_POINT);
        panel.addPlotData(estimatedData, PlotPanel2D.PLOT_TYPE_LINE);
        panel.addPlotData(upperSData, PlotPanel2D.PLOT_TYPE_LINE);
        panel.addPlotData(lowerSData, PlotPanel2D.PLOT_TYPE_LINE);
        panel.addPlotData(grid, PlotPanel2D.PLOT_TYPE_LINE);
        panel.setPointSize( 1 + (int)Math.sqrt(1000.0 / givenData.size()) );
        
        JFrame frame = new JFrame("Gaussian Process");
        frame.add(panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 400);
        frame.setVisible(true);
    }
    
    public RealMatrix calculateWeight(List<RealVector> dataX, List<RealVector> dataY, Kernel<RealVector> kernel) throws SingularMatrixException
    {
        gramMatrix = calculateGramMatrix(dataX, kernel);
        RealMatrix covarianceMatrix = MatrixUtils.createRealIdentityMatrix(dataX.size()).scalarMultiply(beta);
        gramMatrix = gramMatrix.add(covarianceMatrix);
        dataYMatrix = MatrixUtils.createRealMatrix( dataY.size(), dataY.get(0).getDimension() );
        for( int i = 0; i < dataY.size(); i++ )
        {
        	dataYMatrix.setRowVector(i, dataY.get(i) );
        }
        //System.out.println(dataYMatrix);
        //RealVector weight = gramMatrix.inverse().multiply( MatrixUtils.createColumnRealMatrix(dataYArray) ).getColumnVector(0);
        LUDecomposition lUDec = new LUDecomposition( gramMatrix );
        
        try{
            if( !lUDec.getSolver().isNonSingular() )
            {
                SingularValueDecomposition svDec = new SingularValueDecomposition( gramMatrix );
                RealMatrix sigma = svDec.getS();
                for( int i = 0; i < sigma.getRowDimension(); i++ )
                {
                    for( int j = 0; j < sigma.getColumnDimension(); j++ )
                    {
                        if( sigma.getEntry(i, j) != 0 )
                        {
                            sigma.setEntry(i, j, 1/sigma.getEntry(i, j));
                        }
                    }
                }
                inverseGramMatrix = svDec.getV().transpose().multiply(sigma).multiply(svDec.getU().transpose());
                //inverseGramMatrix = svDec.getSolver().getInverse();
                System.err.println("Matrix is singular!");
            }
            else
            {
                inverseGramMatrix = lUDec.getSolver().getInverse();
            }
        }catch(SingularMatrixException e)
        {
            System.out.println(gramMatrix);
            e.printStackTrace();
        }
        //RealMatrix weight = inverseGramMatrix.multiply( MatrixUtils.createColumnRealMatrix(dataYArray) );
        RealMatrix weight = inverseGramMatrix.multiply( dataYMatrix );
        
        return weight;
    }
    
    public RealMatrix getVarianceVector(double[] data)
    {
    	return getVarianceVector(MatrixUtils.createRealVector(data));
    }
    
    public RealMatrix getVarianceVector(RealVector data)
    {
        double[] vectorData = new double[dataXList.size()];
        for(int i = 0; i < dataXList.size(); i++)
        {
            vectorData[i] = kernel.k(dataXList.get(i), data);
        }
        RealMatrix v = MatrixUtils.createRealMatrix(new double[][]{vectorData}).transpose();
        return v;
    }
    
    public static RealMatrix calculateGramMatrix(List<RealVector> data, Kernel<RealVector> kernel)
    {
        RealMatrix gramMatrix = MatrixUtils.createRealMatrix(data.size(), data.size());
        
        for( int i = 0; i < data.size(); i++ )
        {
            for( int j = 0; j < data.size(); j++ )
            {
                gramMatrix.setEntry(i, j, kernel.k(data.get(i), data.get(j)));
            }
        }
        return gramMatrix;
    }
    
    public double getSigma(double[] data)
    {
    	return getSigma( MatrixUtils.createRealVector(data) );
    }
    
    public double getSigma(RealVector data)
    {
        RealMatrix vectorK = getVarianceVector(data);
        // c = (beta + kernel.k(data, data)). c - K^t * C^{-1}_N * K
        return (beta + kernel.k(data, data)) - vectorK.transpose().multiply( inverseGramMatrix ).multiply(vectorK).getEntry(0, 0);
    }
    
    // getter and setter methods
    public List<RealVector> getDataXList()
    {
        return dataXList;
    }

    public Kernel<RealVector> getKernel()
    {
        return kernel;
    }

    public void setKernel(Kernel<RealVector> kernel)
    {
        this.kernel = kernel;
    }
    
    public void setDataXList(double[][] dataXList)
    {
        this.dataXList = new ArrayList<RealVector>();
        for( double[] dataX: dataXList )
        {
        	this.dataXList.add(MatrixUtils.createRealVector(dataX));
        }
    }
    
    public void setDataXList(List<RealVector> dataXList)
    {
        this.dataXList = dataXList;
    }
    
    public void setDataY(double[] dataYArray)
    {
        //this.dataY = new ArrayList<RealVector>(dataYArray);
    	this.dataY = new ArrayList<RealVector>();
    	for( double y: dataYArray )
    	{
    		dataY.add(MatrixUtils.createRealVector(new double[]{y}));
    	}
        dataYAverage = MatrixUtils.createRealVector(new double[]{0});
        
        dataYAverage = dataYAverage.mapDivide(dataY.size());
        for( int i = 0; i < dataY.size(); i++ )
        {
        	dataY.set(i, dataY.get(i).subtract(dataYAverage));
        }
    }
    
    public void setDataY(List<RealVector> dataYArray)
    {
        this.dataY = new ArrayList<RealVector>(dataYArray);
        dataYAverage = MatrixUtils.createRealVector(new double[dataYArray.get(0).getDimension()]);
        for( int i = 0; i < dataY.size(); i++ )
        {
        	//dataYAverage = dataYAverage.add( dataY.get(i) );
        }
        dataYAverage = dataYAverage.mapDivide(dataY.size());
        for( int i = 0; i < dataY.size(); i++ )
        {
        	dataY.set(i, dataY.get(i).subtract(dataYAverage));
        }
    }
    
    public RealVector getDataYAverage()
    {
        return dataYAverage;
    }

    public RealMatrix getWeightArray()
    {
        return weightArray;
    }

    public RealMatrix getGramMatrix()
    {
        return gramMatrix;
    }

    public RealMatrix getInverseGramMatrix()
    {
        return inverseGramMatrix;
    }
    
    public double getBeta()
    {
        return beta;
    }

    public void setBeta(double beta)
    {
        this.beta = beta;
    }

	public RealMatrix getDataYMatrix()
	{
		return dataYMatrix;
	}
}