
package math.gaussianProcess;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JFrame;

import math.stchastics.CorrelationCoefficient;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

import visualization.PlotPanel2D;
import arrayHandle.ArrayProcessor;

public class GradientOptimizer
{
    public static void main(String[] args)
    {
        //List<double[]> dataX = new ArrayList<double[]>();
    	List<RealVector> dataX = new ArrayList<RealVector>();
        //List<Double> dataY = Arrays.asList(0.349486,-0.30839,1.007332,0.166823,-0.348307, -0.8529323);
        //List<Double> dataY = Arrays.asList(new Double[]{0.0, 0.0, 0.4, 0.42, 0.41, 2.1, 0.8});
    	double[] dataY = new double[]{0.0, 0.0, 0.4, 0.42, 0.41, 2.1, 0.8};
    	List<RealVector> dataYList = new ArrayList<RealVector>();
    	
        //double[][] tmpX = new double[][]{new double[]{0.0, 0.0}, new double[]{0.8, 0.8}, new double[]{0.35, 0.35}, new double[]{0.6, 0.6}, new double[]{0.7, 0.7}, new double[]{0.9, 0.9}};
        //double[] tmpX = new double[]{0.0,0.1,0.15,0.6,0.7};
        //double[][] tmpX = new double[][]{{0.0, 0.0}, {0.1, 0.0}, {0.15, 0.0}, {0.6, 0.0}, {0.7, 0.0}, {0.9, 0.0}};
        //double[][] tmpX = new double[][]{{0.02, 0.0}, {0.16, 0.0}, {0.23, 0.0}, {0.24, 0.0}, {0.31, 0.0}, {0.65, 0.0}, {0.80, 0}};
        double[][] tmpX = new double[][]{{0.02}, {0.16}, {0.23}, {0.24}, {0.31}, {0.65}, {0.80}};
        
        //List<Double> dataY = Arrays.asList(0.349486,0.830839,1.007332,0.971507,0.133066,0.166823,-0.848307,-0.445686,-0.563567,0.261502);        
        //double[] tmpX = new double[]{0.000000,0.111111,0.222222,0.333333,0.444444,0.555556,0.666667,0.777778,0.888889,1.000000};
        double start = 0;
        double end = 1;
        
        for( double[] x: tmpX )
        {
            dataX.add(MatrixUtils.createRealVector(x));
        }
        for( double y: dataY )
        {
        	dataYList.add(MatrixUtils.createRealVector(new double[]{y}));
        }
        
        GaussianProcessor processor = new GaussianProcessor();
        processor.setDataXList(dataX);
        processor.setDataY(dataY);
        //processor.setBeta( 0.01 * CorrelationCoefficient.getVariance(dataY) );
        processor.setBeta( 0.01 * CorrelationCoefficient.getVariance(dataY) );
        //processor.setKernel(new GaussKernel(1));
        processor.setKernel(new GPKernel(30, 40, 10.0, 0));
        //processor.setRange(-1, 1, 0);
        //processor.setRange(-1, 1, 1);
        
        processor.calculateWeight();
        
        GaussianProcessor.validate(dataX, 0, 0, dataYList, processor, start, end, 100);
        
        List<Point2D> dataDiff = new ArrayList<Point2D>();
        List<Point2D> dataEI = new ArrayList<Point2D>();
        List<Point2D> horizontalLine = new ArrayList<Point2D>();
        horizontalLine.add(new Point2D.Double(start, 0));
        horizontalLine.add(new Point2D.Double(end,  0));
        
        for( double i = start; i < end; i += Math.abs(start -end) / 100 )
        {
            dataDiff.add(new Point2D.Double(i, calculateGradientOfEI(processor, new double[]{i}, 0)[0]));
            dataEI.add(new Point2D.Double(i, calculateEI(processor, new double[]{i}, 2.1 - ArrayProcessor.getAverage(dataY)) ));
            //System.out.println("i = " + i + " " + dataDiff.get(dataDiff.size()-1).getY());
            //System.out.println("i = " + i + " " + dataEI.get(dataEI.size()-1).getY());
        }
        
        PlotPanel2D panel = new PlotPanel2D();
        //panel.addPlotData(dataDiff, PlotPanel2D.PLOT_TYPE_LINE);
        panel.addPlotData(dataEI, PlotPanel2D.PLOT_TYPE_LINE);
        panel.addPlotData(horizontalLine, PlotPanel2D.PLOT_TYPE_LINE);
        
        JFrame frame = new JFrame("");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(panel);
        frame.setSize(500, 400);
        frame.setLocation(0, 400);
        frame.setVisible(true);
        
        //System.out.println(data);
        System.out.println();
        //System.out.println( "Gradient at 0.2 = " + calculateGradientOfEI(processor, new double[]{0.2}).getEntry(0, 0) );
        
        steepestDescentAlgorithm(processor, new double[]{0.0});
        steepestDescentAlgorithm(processor, new double[]{0.1});
        steepestDescentAlgorithm(processor, new double[]{0.2});
        steepestDescentAlgorithm(processor, new double[]{0.3});
        steepestDescentAlgorithm(processor, new double[]{0.4});
        steepestDescentAlgorithm(processor, new double[]{0.5});
        steepestDescentAlgorithm(processor, new double[]{0.6});
        steepestDescentAlgorithm(processor, new double[]{0.7});
        steepestDescentAlgorithm(processor, new double[]{0.8});
        steepestDescentAlgorithm(processor, new double[]{0.9});
        steepestDescentAlgorithm(processor, new double[]{1.0});
    }
    
    // 最急降下法
    public static double[] steepestDescentAlgorithm(GaussianProcessor processor, double[] start)
    {
        double[] current = Arrays.copyOf(start, start.length);
        double ratio = 0.1;
        
        while( true )
        {
            //System.out.println("SDA: " + Arrays.toString(previous));
            double[] gradient = calculateGradientOfEI(processor, current, -2.0);
            double[] newPoint = Arrays.copyOf(current, current.length);
//            System.out.println("At " + newPoint[0] + " " + calculateEI(processor, newPoint));
//            System.out.println("        Gradient " + (-gradient[0]) * ratio);
            boolean isRangeIn = true;
            //System.out.println(Arrays.toString(gradient));
            
            for( int i = 0; i < newPoint.length; i++ )
            {
                newPoint[i] -= gradient[i] * ratio;
                // TODO 下の行はエラーのためにコメントアウトしているが，本当は必要な判定
                //if( !(processor.getRange(i)[0] <= newPoint[i] && newPoint[i] <= processor.getRange(i)[1]) )
                {
                    isRangeIn = false;
                    break;
                }
            }
            if(!isRangeIn)
            {
                break;
            }
            
//            System.out.println("updated " + newPoint[0] + " " + calculateEI(processor, newPoint));
            if( GaussKernel.norm( GaussKernel.diff(current, newPoint) ) < 0.00001 )
            {
                current = newPoint;
                break;
            }
            current = newPoint;
        }
        System.out.println("SDA: start=" + Arrays.toString(start) + ", result=" + Arrays.toString(current) + ", value=" + calculateEI(processor, current, 0.5) + ", from " + calculateEI(processor, start, 0.5));
        return current;
    }
    
    /** returns EI(X) */
    public static double calculateMean(GaussianProcessor processor, double[] p, double threshold)
    {
        return processor.estimateMAP(p).toArray()[0];
    }
    
    /** returns EI(X) */
    public static double calculateEI(GaussianProcessor processor, double[] p, double threshold, int reverse)
    {
        double s = Math.sqrt(processor.getSigma(p));
        double u = reverse * u(p, threshold, s, processor);
        
        try{
            double phiLarge = 1/2.0 * new NormalDistribution(0, 1).cumulativeProbability(u / Math.sqrt(2)) + 1/2.0;
            //double phiLarge = 1/2.0 * Erf.erf(u / Math.sqrt(2)) + 1/2;
            double phiSmall = (1/Math.sqrt(2.0 * Math.PI)) * Math.exp(-u * u / 2);
            //System.out.println("phiS = " + phiLarge + " " + phiSmall);
            
            return s * (u * phiLarge + phiSmall);
            //return u;
            //return Erf.erf( (threshold - processor.estimateMAP(p)) / s);
            //return (processor.estimateMAP(p) - 0.5) / s;
        }catch(Exception e) {e.printStackTrace();}
        return -1;
    }
    
    /** returns EI(X) */
    public static double calculateEI(GaussianProcessor processor, double[] p, double threshold)
    {
        return calculateEI(processor, p, threshold, 1);
    }
    
    /** returns a Gradient dEI(X)/dX */
    public static double[] calculateGradientOfEI(GaussianProcessor processor, double[] p, double threshold)
    {
        double s = Math.sqrt(processor.getSigma(p));
        double u = u(p, threshold, s, processor);
        
        try{
            double phiLarge = 1/2.0 * new NormalDistribution(0, 1).cumulativeProbability(u / Math.sqrt(2)) + 1/2.0;
            //double phiLarge = 1/2.0 * Erf.erf(u / Math.sqrt(2)) + 1/2;
            double phiSmall = (1/Math.sqrt(2.0 * Math.PI)) * Math.exp(-u * u / 2);
            
            RealMatrix deltaS = deltaS(p, s, processor);
            RealMatrix deltaU = deltaU(p, s, threshold, processor, deltaS);
            
            //return new double[]{s};
            //return new double[]{deltaS.getEntry(0, 0)};
            return deltaS.scalarMultiply(u * phiLarge + phiSmall).add(deltaU.scalarMultiply(s * phiLarge)).scalarMultiply(1).getColumn(0);
        }catch(Exception e) {e.printStackTrace();}
        return null;
    }
    
    public static double u(double[] data, double threshold, double s, GaussianProcessor processor)
    {
        double y = processor.estimateMAP(data).getEntry(0);
        return (y - threshold) / s;
    }
    
    // du/dx
    public static RealMatrix deltaU(double[] data, double s, double threshold, GaussianProcessor processor, RealMatrix deltaS)
    {
        RealMatrix vectorK = processor.getVarianceVector(data);
        RealMatrix diffK = diffVectorK(data, vectorK, processor);
        
        //return diffK.multiply(processor.getInverseGramMatrix().multiply(MyMatrixUtil.convertToRealMatrix(processor.getDataY()))).add(deltaS.scalarMultiply(-u(data, threshold, s, processor))).scalarMultiply(1/s);
        return diffK.multiply(processor.getInverseGramMatrix().multiply(processor.getDataYMatrix())).add(deltaS.scalarMultiply(-u(data, threshold, s, processor))).scalarMultiply(1/s);
    }
    
    // ds/dx
    public static RealMatrix deltaS(double[] data, double s, GaussianProcessor processor)
    {
        RealMatrix vectorK = processor.getVarianceVector(data);
        RealMatrix diffK = diffVectorK(data, vectorK, processor);
        return diffK.multiply(processor.getInverseGramMatrix().multiply(vectorK)).scalarMultiply(1/s);
    }
    
    public static RealMatrix diffVectorK(double[] data, RealMatrix vectorK, GaussianProcessor processor)
    {
        double[][] diffData = new double[data.length][processor.getDataXList().size()];
        for( int i = 0; i < data.length; i++ ) // D
        {
            for( int j = 0; j < processor.getDataXList().size(); j++ ) // N
            {
                diffData[i][j] = diffK(data, i, j, processor);
            }
        }
        return MatrixUtils.createRealMatrix(diffData);
    }
    
    // difference: (d kernel(X, X^j) / d xi)
    public static double diffK(double[] data, int i, int j, GaussianProcessor processor)
    {
        if( processor.getKernel() instanceof GaussKernel )
        {
            return processor.getKernel().k(MatrixUtils.createRealVector(data), processor.getDataXList().get(j)) * 2 / ((GaussKernel)processor.getKernel()).getSigma2() * (data[i] - processor.getDataXList().get(j).getEntry(i));
        }
        else if( processor.getKernel() instanceof GPKernel )
        {
            return processor.getKernel().k(MatrixUtils.createRealVector(data), processor.getDataXList().get(j)) * (- ((GPKernel)processor.getKernel()).getTheta1() * (data[i] - processor.getDataXList().get(j).getEntry(i))) + ((GPKernel)processor.getKernel()).getTheta3() * data[i] * processor.getDataXList().get(j).getEntry(i);
        }
         return 0;   
        //return processor.getKernel().k(data, processor.getDataXList().get(j)) * 2 * (data[i] - processor.getDataXList().get(j)[i]) / ((GaussKernel)processor.getKernel()).getSigma2();
    }
        
    public static double[] vectorK(double[] data, GaussianProcessor processor)
    {
        double[] vectorK = new double[data.length];
        for(int i = 0; i < data.length; i++ )
        {
        	vectorK[i] = processor.getKernel().k(processor.getDataXList().get(i), MatrixUtils.createRealVector(data));
        }
        return vectorK;
    }
}
