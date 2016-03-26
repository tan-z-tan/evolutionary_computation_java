package math.stchastics;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

import math.ExMath;
import visualization.PlotPanel2D;

public class GammaDistribution
{
  public static double getProbabilityDensity(double x, double a, double b)
	{
  	double value = 1 / ExMath.gamma(a) * Math.pow(b, a) * Math.pow(x, a-1) * Math.exp(-b * x);
  	//double value = Math.pow(b, a) * Math.pow(x, a-1) * Math.exp(-b * x);
  	return value;
	}
  
  // main method for test
  public static void main(String args[])
  {
  	double a = 2;
  	double b = 1;
  	
  	List<Point2D> valueList = new ArrayList<Point2D>();
  	for( int i = 0; i < 1000; i++ )
  	{
  		double x = i / 100.0;
  		double value = GammaDistribution.getProbabilityDensity(x, a, b);
  		if( Double.isInfinite(value) )
  		{
  			value = 0;
  		}
  		valueList.add(new Point2D.Double(x, value));
  		System.out.println(value);
  	}
  	PlotPanel2D plot = new PlotPanel2D(valueList);
  	JFrame frame = new JFrame("Gamma Distribution");
  	frame.add(plot);
  	frame.setSize(600, 400);
  	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
  	frame.setVisible(true);
  }
}
